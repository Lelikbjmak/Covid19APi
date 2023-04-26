package com.innowise
package service

import dto.{CovidMaxMinCasesDto, ResponseDto}

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

trait CovidDetailsService {

  def getMaxAndMinCovidCasesForCountryListForToday(countryList: List[String]): Future[ResponseDto[CovidMaxMinCasesDto]]
  def getMaxAndMinCovidCasesForCountryListForTerm(countryList: List[String], firstDay: LocalDateTime, lastDay: Option[LocalDateTime]): Future[ResponseDto[CovidMaxMinCasesDto]]
}
