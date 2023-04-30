package com.innowise.covidapi.mapper

import com.innowise.covidapi.dto.CountryDto
import com.innowise.covidapi.entity.Country
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.*

class CountryMapperSpec extends AnyFunSpec {

  describe("CountryMapper") {

    it("Should map dto to entity") {
      val dto: CountryDto = new CountryDto("Belarus", "belarus", "BY")
      val expectedEntity: Country = new Country("Belarus", "belarus", "BY")
      val actualEntity = CountryMapper.mapToEntity(countryDto = dto)
      expectedEntity should be(actualEntity)
    }

    it("Should map List of dto's to List of entities") {
      val dtoList: List[CountryDto] = List(new CountryDto("Belarus", "belarus", "BY"))
      val expectedEntityList: List[Country] = List(new Country("Belarus", "belarus", "BY"))
      val actualEntityList = CountryMapper.mapToEntityList(dtoList)
      expectedEntityList should be(actualEntityList)
    }

    it("Should map List of dto's to List of entities") {
      val entityList: List[Country] = List(new Country("Belarus", "belarus", "BY"))
      val expectedDtoList: List[CountryDto] = List(new CountryDto("Belarus", "belarus", "BY"))
      val actualDtoList: List[CountryDto] = CountryMapper.mapToDtoList(entityList)
      expectedDtoList should be(actualDtoList)
    }

  }
}

