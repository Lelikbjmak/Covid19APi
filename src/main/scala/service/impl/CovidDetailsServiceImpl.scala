package com.innowise
package service.impl

import dto.{CovidMaxMinCasesDto, ResponseDto, SummaryCovidDetailsDto, TermCovidDetailsDto}
import entity.CountryCovidDetails
import mapper.{ApiResponseMapper, CovidDetailsMapper}
import provider.ResponseProvider
import repository.RedisRepository
import service.{ApiService, CovidDetailsService}
import util.{ApiConstant, CovidRequestUtil}

import akka.http.scaladsl.model.StatusCodes
import org.slf4j.{Logger, LoggerFactory}
import spray.json.{enrichAny, enrichString}

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success}

case class CovidDetailsServiceImpl(apiService: ApiService, redisRepository: RedisRepository[CountryCovidDetails])
                                  (implicit executionContext: ExecutionContext) extends CovidDetailsService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val retrieveService: RedisCovidDetailsRetrieveServiceImpl = new RedisCovidDetailsRetrieveServiceImpl(redisRepository)

  override def getMaxAndMinCovidCasesForCountryListForToday(countryList: List[String]): Future[ResponseDto[CovidMaxMinCasesDto]] = {

    val promise = Promise[ResponseDto[CovidMaxMinCasesDto]]()

    val foundCovidDetailsForCountries = retrieveService.retrieveCovidDetailsForCountryByDay(LocalDateTime.now(), countryList)

    if (foundCovidDetailsForCountries.length != countryList.length) {
      // Since our summary info has expiration (2 hours) if we don't find ONE of all countries therefore we'll not found any
      
      logger.debug(s"Covid details not found in db for all countries $countryList. Send request to external API")

      val request: String = ApiConstant.ApiRequest.covidDetailsForCurrentDayRoute
      val covidDetailsFromApiResponse: Future[String] = apiService.call(request)

      covidDetailsFromApiResponse.onComplete {
        case Failure(exception) =>
          logger.error(s"Response from api executed with exception for request $request . Cause: $exception")
          promise.success(ResponseProvider.generateResponse("Error in deriving data from API.", StatusCodes.BadRequest, null))
        case Success(covidDataFromApi) =>
          val covidInfoFromApi = ApiResponseMapper.mapToSummaryCovidDetailsDtoList(covidDataFromApi)
          saveNonRecognizedCovidDetails(covidInfoFromApi)

          val correspondedToCountriesCovidInfoList: List[SummaryCovidDetailsDto] = covidInfoFromApi
            .filter(element => countryList.contains(element.Slug))

          val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(correspondedToCountriesCovidInfoList)
          promise.success(ResponseProvider.generateResponse(s"""Successfully obtain Max and Min cases for countries $countryList . For current day. Date: ${LocalDateTime.now()}""", StatusCodes.OK, covidInfo))
      }
    } else {
      logger.debug(s"Covid details for countries $countryList found in redis.")
      val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(foundCovidDetailsForCountries)
      promise.success(ResponseProvider.generateResponse(s"""Successfully obtain Max and Min cases for countries $countryList . For current day. Date: ${LocalDateTime.now()}""", StatusCodes.OK, covidInfo))
    }

    promise.future
  }

  override def getMaxAndMinCovidCasesForCountryListForTerm(countryList: List[String], firstDay: LocalDateTime, optionalLastDay: Option[LocalDateTime]): Future[ResponseDto[CovidMaxMinCasesDto]] = {

    val lastDay = if optionalLastDay.isDefined then
      optionalLastDay.get
    else
      firstDay.plusDays(2)

    val promise = Promise[ResponseDto[CovidMaxMinCasesDto]]()
    logger.debug(s"Finding CovidDetails for term ${firstDay.toLocalDate} - ${lastDay.toLocalDate}. Countries: $countryList")

    val summaryCovidDetailsDtoForFoundCountriesForTerm = if optionalLastDay.isEmpty then
      retrieveService.retrieveCovidDetailsForCountryByDay(firstDay, countryList)
    else
      retrieveService.retrieveCovidDetailsForCountryByTerm(firstDay, lastDay, countryList)

    val foundCountries = summaryCovidDetailsDtoForFoundCountriesForTerm.map(dto => dto.Slug)
    val notFoundCountries = countryList.diff(foundCountries)

    if (notFoundCountries.nonEmpty) {

      val futures = for {
        country <- notFoundCountries
      } yield {
        val request = CovidRequestUtil.generateRequest(country, firstDay, lastDay)
        val jsonResponseFromAPi: Future[String] = apiService.call(request)
        mapApiResponseToSummaryCovidDetailsDtoForCountry(jsonResponseFromAPi, country)
      }

      Future.sequence(futures).onComplete {

        case Success(summaryCovidDetailsFromApi) =>
          saveNonRecognizedCovidDetails(summaryCovidDetailsFromApi)
          val allResults = summaryCovidDetailsFromApi ++ summaryCovidDetailsDtoForFoundCountriesForTerm // unite data we obtain from redis with data we obtain from API
          val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(allResults)
          promise.success(ResponseProvider.generateResponse(s"""Successfully obtain Max and Min cases for countries $countryList . For term $firstDay - $lastDay""", StatusCodes.OK, covidInfo))

        case Failure(exception) =>
          logger.error(s"At least one request executed with exception: $exception")
          promise.success(ResponseProvider.generateResponse("Error in deriving data from API.", StatusCodes.BadRequest, null))
      }

    } else {
      val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(summaryCovidDetailsDtoForFoundCountriesForTerm)
      promise.success(ResponseProvider.generateResponse(s"""Successfully obtain Max and Min cases for countries $countryList . For term $firstDay - $lastDay""", StatusCodes.OK, covidInfo))
    }

    promise.future
  }

  private def evaluateMaxAndMinConfirmedCasesForCountryList(countryCasesList: List[SummaryCovidDetailsDto]): CovidMaxMinCasesDto = {

    val maxConfirmed = countryCasesList.map(p => p.NewConfirmed).max
    val minConfirmed = countryCasesList.map(p => p.NewConfirmed).min

    val maxConfirmedCovidCasesCountryList = countryCasesList.filter(p => p.NewConfirmed == maxConfirmed)
    val minConfirmedCovidCasesCountryList = countryCasesList.filter(p => p.NewConfirmed == minConfirmed)

    CovidMaxMinCasesDto(maxConfirmedCovidCasesCountryList, minConfirmedCovidCasesCountryList)
  }

  private def saveNonRecognizedCovidDetails(nonRecognizedCovidDetailsList: List[SummaryCovidDetailsDto]): Iterable[CountryCovidDetails] = {
    val entityList = nonRecognizedCovidDetailsList.map(dto => CovidDetailsMapper.mapToEntity(dto))
    redisRepository.saveAll(entityList) // save all summary data to database
  }

  private def mapApiResponseToSummaryCovidDetailsDtoForCountry(jsonResponse: Future[String], country: String): Future[SummaryCovidDetailsDto] = {
    jsonResponse.map { value =>
      val covidInfoList = ApiResponseMapper.mapToTermCovidDetailsDtoList(value)
      val newCasesForCurrentDay = covidInfoList(covidInfoList.length - 2).Cases - covidInfoList.head.Cases
      val countryDayCovidInfo: SummaryCovidDetailsDto = SummaryCovidDetailsDto(country, newCasesForCurrentDay, covidInfoList.head.Cases, covidInfoList.head.Date)
      countryDayCovidInfo
    }
  }

}
