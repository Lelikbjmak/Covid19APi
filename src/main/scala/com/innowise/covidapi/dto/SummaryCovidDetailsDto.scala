package com.innowise.covidapi.dto

import com.innowise.covidapi.jsonformat.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat
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

    def read(jsonSummaryCovidDetailsDto: JsValue): SummaryCovidDetailsDto = jsonSummaryCovidDetailsDto match {
      case JsObject(fields) =>
        val slug = fields.get("slug").orElse(fields.get("Slug"))
          .map(_.convertTo[String])
          .getOrElse(throw DeserializationException("slug/Slug field expected"))
        val newConfirmedCases = fields.get("newConfirmed").orElse(fields.get("NewConfirmed"))
          .map(_.convertTo[Long])
          .getOrElse(throw DeserializationException("newConfirmed/NewConfirmed field expected"))
        val totalConfirmedCases = fields.get("totalConfirmed").orElse(fields.get("TotalConfirmed"))
          .map(_.convertTo[Long])
          .getOrElse(throw DeserializationException("totalConfirmed/TotalConfirmed field expected"))
        val date = LocalDateTimeJsonFormat.read(fields.get("date").orElse(fields.get("Date"))
          .getOrElse(throw DeserializationException("date/Date field expected")))

        SummaryCovidDetailsDto(slug, newConfirmedCases, totalConfirmedCases, date)

      case _ => throw DeserializationException("Error in deserialize SummaryCovidDetailsDto")
    }
  }

end SummaryCovidDetailsDto
