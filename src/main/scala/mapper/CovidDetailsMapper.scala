package com.innowise
package mapper

import dto.{SummaryCovidDetailsDto, TermCovidDetailsDto}
import entity.CountryCovidDetails

import java.time.LocalDate

object CovidDetailsMapper {

  def mapToEntity(summaryCovidDetailsDto: SummaryCovidDetailsDto): CountryCovidDetails = {
    CountryCovidDetails(summaryCovidDetailsDto.newConfirmed, summaryCovidDetailsDto.totalConfirmed, summaryCovidDetailsDto.slug, summaryCovidDetailsDto.date.toLocalDate)
  }

  def mapToSummaryDto(countryCovidDetails: CountryCovidDetails): SummaryCovidDetailsDto = {
    SummaryCovidDetailsDto(countryCovidDetails.slug, countryCovidDetails.cases, countryCovidDetails.totalCases, countryCovidDetails.date.atStartOfDay())
  }
}
