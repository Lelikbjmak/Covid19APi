package com.innowise.covidapi.entity

import com.innowise.covidapi.entity.Country.jsonFormat4
import com.innowise.covidapi.jsonformat.LocalDateJsonProtocol.LocalDateJsonFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.LocalDate

case class CountryCovidDetails(cases: Long, totalCases: Long, slug: String, date: LocalDate)

object CountryCovidDetails extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val countryCovidDetails: RootJsonFormat[CountryCovidDetails] = jsonFormat4(CountryCovidDetails.apply)
end CountryCovidDetails
