package com.innowise.covidapi.util

import java.time.LocalDate

object CovidRequestUtil {

  def generateRequest(country: String, firstDay: LocalDate, lastDay: LocalDate): String = {

    val uriBuilder = StringBuilder(ApiConstant.ApiRequest.COVID_DETAILS_MAIN_ROUTE)
    uriBuilder.append(country)
    uriBuilder.append(ApiConstant.ApiRequest.COVID_DETAILS_ADDITIONAL_ROUTE)

    uriBuilder.append("?")
    uriBuilder.append("from=")
    uriBuilder.append(firstDay)

    uriBuilder.append("&")
    uriBuilder.append("to=")
    uriBuilder.append(lastDay)

    uriBuilder.toString()
  }
}