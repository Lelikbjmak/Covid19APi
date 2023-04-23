package com.innowise
package util

import java.time.LocalDateTime

object CovidRequestUtil {

  def generateRequest(country: String, firstDay: LocalDateTime, lastDay: LocalDateTime): String = {

    val uriBuilder = StringBuilder(ApiConstant.ApiRequest.mainUri)
    uriBuilder.append(country)
    uriBuilder.append(ApiConstant.ApiRequest.additionalUri)

    uriBuilder.append(ApiConstant.ApiRequest.requestParamPresentMark)
    uriBuilder.append(ApiConstant.ApiRequest.fromRequestParam.concat("="))
    uriBuilder.append(firstDay)

    uriBuilder.append(ApiConstant.ApiRequest.requestParamConcatenation)
    uriBuilder.append(ApiConstant.ApiRequest.toRequestParam.concat("="))
    uriBuilder.append(lastDay)

    uriBuilder.toString()
  }
}