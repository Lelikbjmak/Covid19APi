package com.innowise.covidapi.service

import com.innowise.covidapi.dto.SummaryCovidDetailsDto
import com.innowise.covidapi.entity.CountryCovidDetails
import com.innowise.covidapi.mapper.CovidDetailsMapper
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.repository.impl.CovidDetailsRepository
import com.innowise.covidapi.service.impl.RedisCovidDetailsRetrieveServiceImpl
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual

import java.time.{LocalDate, LocalDateTime}
import scala.language.postfixOps

class RedisCovidDetailsRetrieveServiceSpec extends AnyFlatSpec with Matchers {

  val covidDetailsRepositoryMock: RedisRepository[CountryCovidDetails] = mock(classOf[RedisRepository[CountryCovidDetails]])
  val redisCovidDetailsRetrieveService: RedisCovidDetailsRetrieveService = new RedisCovidDetailsRetrieveServiceImpl(covidDetailsRepositoryMock)

  "RetrieveCovidService" should "return List of SummaryCovidDetailsDto for certain day" in {
    val date: LocalDate = LocalDate.of(2021, 4, 21)
    val firstKey = s"belarus $date"
    val covidDetails: CountryCovidDetails = new CountryCovidDetails(1, 10, "belarus", date)
    val summaryCovidDetailsDto: SummaryCovidDetailsDto = new SummaryCovidDetailsDto("belarus", 1, 10, date.atStartOfDay())

    when(covidDetailsRepositoryMock.find(firstKey)).thenReturn(Option.apply(covidDetails))

    val actualRetrievedCovidDetails = redisCovidDetailsRetrieveService.retrieveCovidDetailsForCountryByDay(date, List("belarus"))
    verify(covidDetailsRepositoryMock, times(1)).find(firstKey)

    List(summaryCovidDetailsDto) shouldEqual actualRetrievedCovidDetails
  }

  "RetrieveCovidService" should "return List of SummaryCovidDetailsDto for certain day (with filtration of non-present info)" in {
    val date: LocalDate = LocalDate.of(2021, 4, 21)
    val firstKey = s"belarus $date"
    val secondKey = s"japan $date"

    val covidDetails: CountryCovidDetails = new CountryCovidDetails(1, 10, "belarus", date)
    val summaryCovidDetailsDto: SummaryCovidDetailsDto = new SummaryCovidDetailsDto("belarus", 1, 10, date.atStartOfDay())

    when(covidDetailsRepositoryMock.find(firstKey)).thenReturn(Option.apply(covidDetails))
    when(covidDetailsRepositoryMock.find(secondKey)).thenReturn(Option.empty)

    val actualRetrievedCovidDetails = redisCovidDetailsRetrieveService.retrieveCovidDetailsForCountryByDay(date, List("belarus", "japan"))

    List(summaryCovidDetailsDto) shouldEqual actualRetrievedCovidDetails
  }

  "RetrieveCovidService" should "return List of SummaryCovidDetailsDto for term (firstDay - lastDay)" in {
    val firstDay: LocalDate = LocalDate.of(2021, 4, 21)
    val lastDay: LocalDate = LocalDate.of(2021, 4, 23)

    val firstKey = s"belarus $firstDay"
    val lastKey = s"belarus $lastDay"

    val firstCovidDetails: CountryCovidDetails = new CountryCovidDetails(1, 10, "belarus", firstDay)
    val lastCovidDetails: CountryCovidDetails = new CountryCovidDetails(3, 12, "belarus", lastDay)

    val expectedCovidSummaryDto = new SummaryCovidDetailsDto("belarus", 2, 12, firstDay.atStartOfDay())
    when(covidDetailsRepositoryMock.find(firstKey)).thenReturn(Option.apply(firstCovidDetails))
    when(covidDetailsRepositoryMock.find(lastKey)).thenReturn(Option.apply(lastCovidDetails))

    val actualRetrievedCovidDetails = redisCovidDetailsRetrieveService.retrieveCovidDetailsForCountryByTerm(firstDay, lastDay, List("belarus", "japan"))

    List(expectedCovidSummaryDto) shouldEqual actualRetrievedCovidDetails
  }

  "RetrieveCovidService" should "return List of SummaryCovidDetailsDto for term (firstDay - lastDay) with filtration of non-present info" in {
    val firstDay: LocalDate = LocalDate.of(2021, 4, 21)
    val lastDay: LocalDate = LocalDate.of(2021, 4, 23)

    val firstKey = s"belarus $firstDay"
    val lastKey = s"belarus $lastDay"
    val thirdKey = s"japan $firstDay"
    val fourthKey = s"japan $lastDay"

    val firstCovidDetails: CountryCovidDetails = new CountryCovidDetails(1, 10, "belarus", firstDay)
    val lastCovidDetails: CountryCovidDetails = new CountryCovidDetails(3, 12, "belarus", lastDay)

    val expectedCovidSummaryDto = new SummaryCovidDetailsDto("belarus", 2, 12, firstDay.atStartOfDay())
    when(covidDetailsRepositoryMock.find(firstKey)).thenReturn(Option.apply(firstCovidDetails))
    when(covidDetailsRepositoryMock.find(lastKey)).thenReturn(Option.apply(lastCovidDetails))
    when(covidDetailsRepositoryMock.find(thirdKey)).thenReturn(Option.empty)
    when(covidDetailsRepositoryMock.find(fourthKey)).thenReturn(Option.empty)

    val actualRetrievedCovidDetails = redisCovidDetailsRetrieveService.retrieveCovidDetailsForCountryByTerm(firstDay, lastDay, List("belarus", "japan"))

    List(expectedCovidSummaryDto) shouldEqual actualRetrievedCovidDetails
  }
}
