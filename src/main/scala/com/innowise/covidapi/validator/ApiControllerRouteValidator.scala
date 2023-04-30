package com.innowise.covidapi.validator

import com.innowise.covidapi.entity.Country
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.util.ApiConstant
import com.innowise.covidapi.util.ApiConstant.ApiRequest

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.temporal.TemporalAmount

class ApiControllerRouteValidator(countryRepository: RedisRepository[Country]) {

  def validateRequestBody(countryList: List[String]): Boolean = {
    val countryPresent = countryList.map(countrySlug => countryRepository.find(countrySlug).isDefined)
    countryPresent.forall(_ == true)
  }

  def validateRequestParamAndPathVariable(requestParam: String*): Boolean = { // check our days

    val validRequestParams = requestParam.map(f = requestParam => {
      try {
        val date = LocalDate.parse(requestParam)
        date.isBefore(LocalDate.now().plusDays(1)) && date.isAfter(ApiConstant.Api.FIRST_RECORDED_DAY)
      } catch {
        case _: DateTimeParseException => false
      }
    })

    validRequestParams.forall(_ == true)
  }
  
}
