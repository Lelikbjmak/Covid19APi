package com.innowise.covidapi.service

import com.innowise.covidapi.dto.CovidMaxMinCasesDto

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

trait CovidDetailsService {

  def getMaxAndMinCovidCasesForCountryListForToday(countryList: List[String]): Future[CovidMaxMinCasesDto]
  def getMaxAndMinCovidCasesForCountryListForTerm(countryList: List[String], firstDay: LocalDate, lastDay: Option[LocalDate]): Future[CovidMaxMinCasesDto]
}
