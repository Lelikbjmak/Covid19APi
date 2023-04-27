package com.innowise
package dto

import jsonformat.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat

import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import java.time.LocalDateTime

case class SummaryCovidDetailsDto(slug: String, newConfirmed: Long, totalConfirmed: Long, date: LocalDateTime)

object SummaryCovidDetailsDto extends DefaultJsonProtocol:

  implicit val format: RootJsonFormat[SummaryCovidDetailsDto] = new RootJsonFormat[SummaryCovidDetailsDto] {

    def write(summaryCovidDto: SummaryCovidDetailsDto): JsValue = JsObject(
      "slug" -> JsString(summaryCovidDto.slug),
      "newConfirmed" -> JsNumber(summaryCovidDto.newConfirmed),
      "totalConfirmed" -> JsNumber(summaryCovidDto.totalConfirmed),
      "date" -> LocalDateTimeJsonFormat.write(summaryCovidDto.date)
    )

    def read(jsonSummaryCovidDetailsDto: JsValue): SummaryCovidDetailsDto =
      jsonSummaryCovidDetailsDto.asJsObject.getFields("Slug", "NewConfirmed", "TotalConfirmed", "Date") match {
        case Seq(JsString(slugField), JsNumber(newConfirmedField), JsNumber(totalConfirmedField), JsString(dateField)) =>
          SummaryCovidDetailsDto(slugField, newConfirmedField.toLong, totalConfirmedField.toLong, LocalDateTimeJsonFormat.read(JsString(dateField)))
        case _ =>
          throw DeserializationException("Country expected")
      }
  }
  
end SummaryCovidDetailsDto
