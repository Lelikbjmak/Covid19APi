package com.innowise
package dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class CountryDto(Country: String, Slug: String, ISO2: String)

object CountryDto extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val countryDtoFormat: RootJsonFormat[CountryDto] = jsonFormat3(CountryDto.apply)
end CountryDto
