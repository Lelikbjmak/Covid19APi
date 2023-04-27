package com.innowise
package dto

import jsonformat.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol.jsonFormat3
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import java.time.LocalDateTime

case class TermCovidDetailsDto(totalCases: Long, country: String, date: LocalDateTime)

object TermCovidDetailsDto extends DefaultJsonProtocol:

  implicit val format: RootJsonFormat[TermCovidDetailsDto] = new RootJsonFormat[TermCovidDetailsDto] {

    def write(termCovidDetails: TermCovidDetailsDto): JsValue = JsObject(
      "totalCases" -> JsNumber(termCovidDetails.totalCases),
      "country" -> JsString(termCovidDetails.country),
      "date" -> LocalDateTimeJsonFormat.write(termCovidDetails.date)
    )

    def read(jsonSummaryCovidDetailsDto: JsValue): TermCovidDetailsDto =
      jsonSummaryCovidDetailsDto.asJsObject.getFields("Cases", "Country", "Date") match {
        case Seq(JsNumber(casesField), JsString(countryField), JsString(dateField)) =>
          TermCovidDetailsDto(casesField.toLong, countryField, LocalDateTimeJsonFormat.read(JsString(dateField)))
        case _ =>
          throw DeserializationException("Country expected")
      }
  }

end TermCovidDetailsDto
