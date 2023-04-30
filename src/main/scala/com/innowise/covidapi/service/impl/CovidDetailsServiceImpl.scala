package com.innowise.covidapi.service.impl

import akka.http.scaladsl.model.StatusCodes
import com.innowise.covidapi.dto.{CovidMaxMinCasesDto, SummaryCovidDetailsDto, TermCovidDetailsDto}
import com.innowise.covidapi.entity.CountryCovidDetails
import com.innowise.covidapi.mapper.{ApiResponseMapper, CovidDetailsMapper}
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.service.{ApiService, CovidDetailsService}
import com.innowise.covidapi.util.{ApiConstant, CovidRequestUtil}
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

  override def getMaxAndMinCovidCasesForCountryListForToday(countryList: List[String]): Future[CovidMaxMinCasesDto] = {

    val promise = Promise[CovidMaxMinCasesDto]()

    val foundCovidDetailsForCountries = retrieveService.retrieveCovidDetailsForCountryByDay(LocalDate.now(), countryList)

    if (foundCovidDetailsForCountries.length != countryList.length) {
      // Since our summary info has expiration (2 hours) if we don't find ONE of all countries therefore we'll not found any

      logger.debug(s"Covid details not found in db for all countries $countryList. Send request to external API")

      val request: String = ApiConstant.ApiRequest.COVID_DETAILS_FOR_CURRENT_DAY_ROUTE
      val covidDetailsFromApiResponse: Future[String] = apiService.call(request)

      covidDetailsFromApiResponse.onComplete {
        case Failure(exception) =>
          logger.error(s"Response from api executed with exception for request $request . Cause: $exception")
          promise.success(new CovidMaxMinCasesDto(List.empty, List.empty))
        case Success(covidDataFromApi) =>
          val covidInfoFromApi = ApiResponseMapper.mapToSummaryCovidDetailsDtoList(covidDataFromApi)

          saveNonRecognizedCovidDetails(covidInfoFromApi)

          val correspondedToCountriesCovidInfoList: List[SummaryCovidDetailsDto] = covidInfoFromApi
            .filter(element => countryList.contains(element.slug))

          val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(correspondedToCountriesCovidInfoList)
          promise.success(covidInfo)
      }
    } else {
      logger.debug(s"Covid details for countries $countryList found in redis.")
      val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(foundCovidDetailsForCountries)
      promise.success(covidInfo)
    }

    promise.future
  }

  override def getMaxAndMinCovidCasesForCountryListForTerm(countryList: List[String], firstDay: LocalDate, optionalLastDay: Option[LocalDate]): Future[CovidMaxMinCasesDto] = {

    val lastDay = if optionalLastDay.isDefined then
      optionalLastDay.get
    else
      firstDay.plusDays(2)

    val promise = Promise[CovidMaxMinCasesDto]()
    logger.debug(s"Finding CovidDetails for term $firstDay - $lastDay. Countries: $countryList")

    val summaryCovidDetailsDtoForFoundCountriesForTerm = if optionalLastDay.isEmpty then
      retrieveService.retrieveCovidDetailsForCountryByDay(firstDay, countryList)
    else
      retrieveService.retrieveCovidDetailsForCountryByTerm(firstDay, lastDay, countryList)

    val foundCountries = summaryCovidDetailsDtoForFoundCountriesForTerm.map(dto => dto.slug)
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
          val summaryDetails = summaryCovidDetailsFromApi.filter(p => p.isDefined).map(p => p.get)

          if summaryDetails.nonEmpty then
            saveNonRecognizedCovidDetails(summaryDetails)

          val allPossibleResults = summaryDetails ++ summaryCovidDetailsDtoForFoundCountriesForTerm // unite data we obtain from redis with data we obtain from API
          val allActualResults = allPossibleResults.filter(p => p != null)
          val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(allActualResults)
          promise.success(covidInfo)
        case Failure(exception) =>
          logger.error(s"At least one request executed with exception: $exception")
          promise.success(new CovidMaxMinCasesDto(List.empty, List.empty))
      }

    } else {
      val covidInfo = evaluateMaxAndMinConfirmedCasesForCountryList(summaryCovidDetailsDtoForFoundCountriesForTerm)
      promise.success(covidInfo)
    }

    promise.future
  }

  private def evaluateMaxAndMinConfirmedCasesForCountryList(countryCasesList: List[SummaryCovidDetailsDto]): CovidMaxMinCasesDto = {

    if countryCasesList.nonEmpty then
      val maxConfirmed = countryCasesList.map(p => p.newConfirmed).max
      val minConfirmed = countryCasesList.map(p => p.newConfirmed).min

      val maxConfirmedCovidCasesCountryList = countryCasesList.filter(p => p.newConfirmed == maxConfirmed)
      val minConfirmedCovidCasesCountryList = countryCasesList.filter(p => p.newConfirmed == minConfirmed)

      CovidMaxMinCasesDto(maxConfirmedCovidCasesCountryList, minConfirmedCovidCasesCountryList)
    else
      CovidMaxMinCasesDto(List.empty[SummaryCovidDetailsDto], List.empty[SummaryCovidDetailsDto])
  }

  private def saveNonRecognizedCovidDetails(nonRecognizedCovidDetailsList: List[SummaryCovidDetailsDto]): Iterable[CountryCovidDetails] = {
    val entityList = nonRecognizedCovidDetailsList.map(dto => CovidDetailsMapper.mapToEntity(dto))
    redisRepository.saveAll(entityList) // save all summary data to database
  }

  private def mapApiResponseToSummaryCovidDetailsDtoForCountry(jsonResponse: Future[String], country: String): Future[Option[SummaryCovidDetailsDto]] = {
    jsonResponse.map { value =>
      val covidInfoList = ApiResponseMapper.mapToTermCovidDetailsDtoList(value)
      if covidInfoList.isEmpty then
        Option.empty[SummaryCovidDetailsDto]
      else
        val newCasesForCurrentDay = covidInfoList(covidInfoList.length - 2).totalCases - covidInfoList.head.totalCases
        val countryDayCovidInfo: SummaryCovidDetailsDto = SummaryCovidDetailsDto(country, newCasesForCurrentDay, covidInfoList.head.totalCases, covidInfoList.head.date)
        Option.apply(countryDayCovidInfo)
    }
  }

}
