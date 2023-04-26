package com.innowise
package service

import dto.SummaryCovidDetailsDto
import entity.CountryCovidDetails

import java.time.LocalDateTime

trait RedisCovidDetailsRetrieveService {

  def retrieveCovidDetailsForCountryByDay(day: LocalDateTime, countryList: List[String]): List[SummaryCovidDetailsDto]
  
  def retrieveCovidDetailsForCountryByTerm(firstDay: LocalDateTime, lastDay: LocalDateTime, countryList: List[String]): List[SummaryCovidDetailsDto]
}
