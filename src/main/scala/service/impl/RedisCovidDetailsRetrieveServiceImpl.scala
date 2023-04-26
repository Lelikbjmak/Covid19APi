package com.innowise
package service.impl

import dto.SummaryCovidDetailsDto
import entity.CountryCovidDetails
import mapper.CovidDetailsMapper
import repository.RedisRepository
import service.RedisCovidDetailsRetrieveService

import java.time.LocalDateTime

class RedisCovidDetailsRetrieveServiceImpl(redisRepository: RedisRepository[CountryCovidDetails]) extends RedisCovidDetailsRetrieveService {
  override def retrieveCovidDetailsForCountryByDay(day: LocalDateTime, countryList: List[String]): List[SummaryCovidDetailsDto] = {

    val possibleFoundCovidDetailsForCountriesFromRedis = for (country <- countryList) yield
      redisRepository.find(country + " " + day.toLocalDate)

    possibleFoundCovidDetailsForCountriesFromRedis
      .filter(details => details.isDefined)
      .map(details => details.get)
      .map(covidDetails => CovidDetailsMapper.mapToSummaryDto(covidDetails))
  }

  override def retrieveCovidDetailsForCountryByTerm(firstDay: LocalDateTime, lastDay: LocalDateTime, countryList: List[String]): List[SummaryCovidDetailsDto] = {

    val possibleFoundCovidDetailsForCountriesFromRedis = for (country <- countryList) yield {
      val firstDayCountryCovidDetails = redisRepository.find(country + " " + firstDay.toLocalDate)
      val lastDayCountryCovidDetails = redisRepository.find(country + " " + lastDay.toLocalDate)
      (firstDayCountryCovidDetails, lastDayCountryCovidDetails)
    }

    val foundCovidDetailsTupleForCountriesFromRedis = possibleFoundCovidDetailsForCountriesFromRedis
      .filter(tuple => tuple._1.isDefined && tuple._2.isDefined)
      .map(tuple => (tuple._1.get, tuple._2.get))

    foundCovidDetailsTupleForCountriesFromRedis.map(tuple => {
      val casesForTerm = tuple._2.totalCases - tuple._1.totalCases
      val summaryCountryCovidDetailsDtoForTerm: SummaryCovidDetailsDto = SummaryCovidDetailsDto(tuple._1.slug, casesForTerm, tuple._2.totalCases, firstDay) // this date matter nothing
      summaryCountryCovidDetailsDtoForTerm
    })
  }

}