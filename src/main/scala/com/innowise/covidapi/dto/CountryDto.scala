package com.innowise.covidapi.dto

import spray.json.{DefaultJsonProtocol, DeserializationException, JsObject, JsString, JsValue, RootJsonFormat}

case class CountryDto(name: String, slug: String, code: String)

object CountryDto extends DefaultJsonProtocol:
  implicit val format: RootJsonFormat[CountryDto] = new RootJsonFormat[CountryDto] {

    def write(countryDto: CountryDto): JsValue = JsObject(
      "name" -> JsString(countryDto.name),
      "slug" -> JsString(countryDto.slug),
      "code" -> JsString(countryDto.code)
    )

    def read(jsonCountryDto: JsValue): CountryDto = jsonCountryDto.asJsObject.getFields("Country", "Slug", "ISO2") match {
      case Seq(JsString(name), JsString(slug), JsString(code)) =>
        CountryDto(name, slug, code)
      case _ =>
        throw DeserializationException("Country expected")
    }
  }
end CountryDto
