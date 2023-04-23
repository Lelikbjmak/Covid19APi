package com.innowise
package json

import dto.*
import entity.{Country, CountryCovidDetails}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val countryFormat: RootJsonFormat[Country] = jsonFormat3(Country.apply)

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {
    def write(instant: LocalDateTime): JsValue = JsString(DateTimeFormatter.ISO_DATE_TIME.format(instant))

    def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
      case _ => throw DeserializationException("Expected string")
    }
  }

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {
    def write(instant: LocalDate): JsValue = JsString(DateTimeFormatter.ISO_DATE.format(instant))

    def read(json: JsValue): LocalDate = json match {
      case JsString(s) => LocalDate.parse(s, DateTimeFormatter.ISO_DATE)
      case _ => throw DeserializationException("Expected string")
    }
  }

  implicit val covidDetailsFormat: RootJsonFormat[TermCovidDetailsDto] = jsonFormat3(TermCovidDetailsDto.apply)
  implicit val todayCovidDetailsDtoFormat: RootJsonFormat[SummaryCovidDetailsDto] = jsonFormat3(SummaryCovidDetailsDto.apply)
  implicit val covidDetailsDto: RootJsonFormat[CovidMaxMinCasesDto] = jsonFormat2(CovidMaxMinCasesDto.apply)
  implicit val countryDtoFormat: RootJsonFormat[CountryDto] = jsonFormat3(CountryDto.apply)
  implicit val countryCovidDetails: RootJsonFormat[CountryCovidDetails] = jsonFormat3(CountryCovidDetails.apply)

  implicit def responseFormat[T: JsonFormat]: RootJsonFormat[ResponseDto[T]] =
    jsonFormat5(ResponseDto.apply[T])
}