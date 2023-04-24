package com.innowise
package dto

import json.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.LocalDateTime

case class SummaryCovidDetailsDto(Slug: String, NewConfirmed: Long, TotalConfirmed: Long, Date: LocalDateTime)

object SummaryCovidDetailsDto extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val todayCovidDetailsDtoFormat: RootJsonFormat[SummaryCovidDetailsDto] = jsonFormat4(SummaryCovidDetailsDto.apply)
end SummaryCovidDetailsDto
