package com.innowise
package mapper

import dto.{SummaryCovidDetailsDto, TermCovidDetailsDto}

import spray.json.DefaultJsonProtocol.listFormat
import spray.json.{enrichAny, enrichString}

object ApiResponseMapper {
  def mapToSummaryCovidDetailsDtoList(jsonResponse: String): List[SummaryCovidDetailsDto] = {
    jsonResponse.parseJson.asJsObject.fields("Countries").convertTo[List[SummaryCovidDetailsDto]]
  }

  def mapToTermCovidDetailsDtoList(jsonResponse: String): List[TermCovidDetailsDto] = {
    jsonResponse.parseJson.convertTo[List[TermCovidDetailsDto]]
  }
}
