package com.innowise
package dto

import json.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol.jsonFormat3
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.LocalDateTime

case class TermCovidDetailsDto(Cases: Long, Country: String, Date: LocalDateTime)

object TermCovidDetailsDto extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val covidDetailsFormat: RootJsonFormat[TermCovidDetailsDto] = jsonFormat3(TermCovidDetailsDto.apply)
end TermCovidDetailsDto
