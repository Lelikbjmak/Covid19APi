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
}
