package com.innowise
package mapper

import dto.CountryDto
import entity.Country

object CountryMapper {
  
  def mapToEntity(countryDto: CountryDto): Country = {
    Country(name = countryDto.Country, slug = countryDto.Slug, code = countryDto.ISO2)
  }
  
  def mapToEntityList(countryDtoList: List[CountryDto]): List[Country] = {
    countryDtoList.map(countryDto => mapToEntity(countryDto))
  }

  private def mapToDto(country: Country) = {
    CountryDto(Country = country.name, Slug = country.slug, ISO2 = country.code)
  }

  def mapToDtoList(countryList: List[Country]): List[CountryDto] = {
    countryList.map(country => mapToDto(country))
  }
}
