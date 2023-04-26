package com.innowise
package service.impl

import dto.CountryDto
import mapper.CountryMapper
import repository.impl.CountryRepository

class CountryRetrieveService(countryRepository: CountryRepository){

  def fetchAllCountries(): List[CountryDto] = {
    CountryMapper.mapToDtoList(countryRepository.getAllCountries)
  }

}
