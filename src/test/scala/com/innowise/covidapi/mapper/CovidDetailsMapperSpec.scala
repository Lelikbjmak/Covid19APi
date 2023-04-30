package com.innowise.covidapi.mapper

import com.innowise.covidapi.dto.SummaryCovidDetailsDto
import com.innowise.covidapi.entity.CountryCovidDetails
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.shouldBe

import java.time.LocalDateTime

class CovidDetailsMapperSpec extends AnyFlatSpec {

  "Map to entity" should "Map SummaryCovidDetailsDto to CountryCovidDetails entity we store in database" in {
    val date: LocalDateTime = LocalDateTime.of(2020,1,1,1, 1, 1, 1)
    val dto = new SummaryCovidDetailsDto("belarus", 1, 2, date)
    val expectedEntity: CountryCovidDetails = new CountryCovidDetails(1, 2, "belarus", date.toLocalDate)
    val actualEntity = CovidDetailsMapper.mapToEntity(dto)
    expectedEntity shouldBe actualEntity
  }

  "Map to dto" should "Map CountryCovidDetails to SummaryCovidDetailsDto we send to user" in {
    val date: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)
    val entity: CountryCovidDetails = new CountryCovidDetails(1, 2, "belarus", date.toLocalDate)
    val expectedDto: SummaryCovidDetailsDto = new SummaryCovidDetailsDto("belarus", 1, 2, date)
    val actualDto = CovidDetailsMapper.mapToSummaryDto(entity)
    expectedDto shouldBe actualDto
  }
  
}
