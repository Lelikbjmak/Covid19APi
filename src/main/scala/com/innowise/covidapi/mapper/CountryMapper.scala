package com.innowise.covidapi.mapper

import com.innowise.covidapi.dto.CountryDto
import com.innowise.covidapi.entity.Country

object CountryMapper {

  def mapToEntity(countryDto: CountryDto): Country = {
    Country(countryDto.name, countryDto.slug, countryDto.code)
  }

  def mapToEntityList(countryDtoList: List[CountryDto]): List[Country] = {
    countryDtoList.map(countryDto => mapToEntity(countryDto))
  }

  private def mapToDto(country: Country) = {
    CountryDto(country.name, country.slug, country.code)
  }

  def mapToDtoList(countryList: List[Country]): List[CountryDto] = {
    countryList.map(country => mapToDto(country))
  }
}
