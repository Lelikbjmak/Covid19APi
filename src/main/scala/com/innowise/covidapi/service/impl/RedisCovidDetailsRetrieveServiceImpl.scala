package com.innowise.covidapi.service.impl

import com.innowise.covidapi.dto.SummaryCovidDetailsDto
import com.innowise.covidapi.entity.CountryCovidDetails
import com.innowise.covidapi.mapper.CovidDetailsMapper
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.service.RedisCovidDetailsRetrieveService

import java.time.LocalDate

class RedisCovidDetailsRetrieveServiceImpl(redisRepository: RedisRepository[CountryCovidDetails]) extends RedisCovidDetailsRetrieveService {
  
  override def retrieveCovidDetailsForCountryByDay(day: LocalDate, countryList: List[String]): List[SummaryCovidDetailsDto] = {

    val possibleFoundCovidDetailsForCountriesFromRedis = for (country <- countryList) yield
      redisRepository.find(country + " " + day)

    possibleFoundCovidDetailsForCountriesFromRedis
      .filter(details => details.isDefined)
      .map(details => details.get)
      .map(covidDetails => CovidDetailsMapper.mapToSummaryDto(covidDetails))
  }

  override def retrieveCovidDetailsForCountryByTerm(firstDay: LocalDate, lastDay: LocalDate, countryList: List[String]): List[SummaryCovidDetailsDto] = {

    val possibleFoundCovidDetailsForCountriesFromRedis = for (country <- countryList) yield {
      val firstDayCountryCovidDetails = redisRepository.find(country + " " + firstDay)
      val lastDayCountryCovidDetails = redisRepository.find(country + " " + lastDay)
      (firstDayCountryCovidDetails, lastDayCountryCovidDetails)
    }

    val foundCovidDetailsTupleForCountriesFromRedis = possibleFoundCovidDetailsForCountriesFromRedis
      .filter(tuple => tuple._1.isDefined && tuple._2.isDefined)
      .map(tuple => (tuple._1.get, tuple._2.get))

    foundCovidDetailsTupleForCountriesFromRedis.map(tuple => {
      val casesForTerm = tuple._2.totalCases - tuple._1.totalCases
      val summaryCountryCovidDetailsDtoForTerm: SummaryCovidDetailsDto = SummaryCovidDetailsDto(tuple._1.slug, casesForTerm, tuple._2.totalCases, firstDay.atStartOfDay()) // this date matter nothing
      summaryCountryCovidDetailsDtoForTerm
    })
  }

}