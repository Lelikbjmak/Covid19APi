package com.innowise.covidapi.mapper

import com.innowise.covidapi.dto.{SummaryCovidDetailsDto, TermCovidDetailsDto}
import com.innowise.covidapi.entity.CountryCovidDetails

import java.time.LocalDate

object CovidDetailsMapper {

  def mapToEntity(summaryCovidDetailsDto: SummaryCovidDetailsDto): CountryCovidDetails = {
    CountryCovidDetails(summaryCovidDetailsDto.newConfirmed, summaryCovidDetailsDto.totalConfirmed, summaryCovidDetailsDto.slug, summaryCovidDetailsDto.date.toLocalDate)
  }

  def mapToSummaryDto(countryCovidDetails: CountryCovidDetails): SummaryCovidDetailsDto = {
    SummaryCovidDetailsDto(countryCovidDetails.slug, countryCovidDetails.cases, countryCovidDetails.totalCases, countryCovidDetails.date.atStartOfDay())
  }
}
