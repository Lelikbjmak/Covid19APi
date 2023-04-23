package com.innowise
package util

import dto.{ CountryDto, CovidMaxMinCasesDto}
import entity.Country
import json.JsonSupport
import repository.RedisRepository
import service.ApiService

import com.innowise.mapper.CountryMapper
import org.slf4j.{Logger, LoggerFactory}
import spray.json.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ApplicationInitializer extends JsonSupport {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def initCountries(countryRepository: RedisRepository[Country], apiService: ApiService): Unit = {

    val possibleCountriesStringList: Future[String] = apiService.call(ApiConstant.ApiRequest.POSSIBLE_COUNTRIES_ROUTE)

    possibleCountriesStringList.onComplete {
      case Failure(exception) =>
        logger.error(s"$exception")
      case Success(countryStringList) =>
        val jsonCountryList = countryStringList.parseJson
        val countryDtoList = jsonCountryList.convertTo[List[CountryDto]]
        CountryMapper.toEntityList(countryDtoList).map(country => countryRepository.save(country))
    }
  }
}
