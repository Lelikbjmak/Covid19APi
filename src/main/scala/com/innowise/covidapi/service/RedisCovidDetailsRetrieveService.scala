package com.innowise.covidapi.service

import com.innowise.covidapi.dto.SummaryCovidDetailsDto
import com.innowise.covidapi.entity.CountryCovidDetails

import java.time.LocalDate

trait RedisCovidDetailsRetrieveService {

  def retrieveCovidDetailsForCountryByDay(day: LocalDate, countryList: List[String]): List[SummaryCovidDetailsDto]
  
  def retrieveCovidDetailsForCountryByTerm(firstDay: LocalDate, lastDay: LocalDate, countryList: List[String]): List[SummaryCovidDetailsDto]
}
