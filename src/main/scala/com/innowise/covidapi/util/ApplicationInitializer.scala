package com.innowise.covidapi.util

import com.innowise.covidapi.dto.{CountryDto, CovidMaxMinCasesDto}
import com.innowise.covidapi.entity.Country
import com.innowise.covidapi.mapper.CountryMapper
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.service.ApiService

import org.slf4j.{Logger, LoggerFactory}
import spray.json.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ApplicationInitializer {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def initCountries(countryRepository: RedisRepository[Country], apiService: ApiService): Unit = {

    val possibleCountriesStringList: Future[String] = apiService.call(ApiConstant.ApiRequest.POSSIBLE_COUNTRIES_ROUTE)

    possibleCountriesStringList.onComplete {
      case Failure(exception) =>
        logger.error(s"$exception")
      case Success(countryStringList) =>
        val jsonCountryList = countryStringList.parseJson
        val countryDtoList = jsonCountryList.convertTo[List[CountryDto]]
        CountryMapper.mapToEntityList(countryDtoList).map(country => countryRepository.save(country))
    }
  }
}
