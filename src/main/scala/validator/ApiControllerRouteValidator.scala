package com.innowise
package validator

import entity.Country
import repository.RedisRepository
import util.ApiConstant
import util.ApiConstant.ApiRequest

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}

class ApiControllerRouteValidator(countryRepository: RedisRepository[Country]) {

  def validateRequestBody(countryList: List[String]): Boolean = {
    val countryPresent = countryList.map(countrySlug => countryRepository.find(countrySlug).isDefined)
    countryPresent.forall(_ == true)
  }

  def validateRequestParamAndPathVariable(requestParam: String*): Boolean = { // check our days

    val validRequestParams = requestParam.map(f = requestParam => {
      try {
        val date = LocalDateTime.parse(requestParam, DateTimeFormatter.ISO_DATE_TIME)
        date.isBefore(LocalDateTime.now()) && date.isAfter(ApiConstant.Api.FIRST_RECORDED_DAY)
      } catch {
        case _: DateTimeParseException => false
      }
    })

    validRequestParams.forall(_ == true)
  }
  
}
