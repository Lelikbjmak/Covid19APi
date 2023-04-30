package com.innowise.covidapi.entity

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol.jsonFormat3
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Country(name: String, slug: String, code: String)

object Country extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val countryFormat: RootJsonFormat[Country] = jsonFormat3(Country.apply)
end Country
