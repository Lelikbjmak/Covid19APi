package com.innowise
package service.impl

import repository.impl.CountryRepository

import com.innowise.dto.CountryDto
import com.innowise.mapper.CountryMapper

class CountryRetrieveService(countryRepository: CountryRepository){
  
  def fetchAllCountries(): List[CountryDto] = {
    CountryMapper.mapToDtoList(countryRepository.getAllCountries())
  }
  
}
