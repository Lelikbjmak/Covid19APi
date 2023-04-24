package com.innowise
package dto

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class CovidMaxMinCasesDto(max: List[SummaryCovidDetailsDto], min: List[SummaryCovidDetailsDto])

object CovidMaxMinCasesDto extends SprayJsonSupport with DefaultJsonProtocol:
  implicit val covidDetailsDto: RootJsonFormat[CovidMaxMinCasesDto] = jsonFormat2(CovidMaxMinCasesDto.apply)
end CovidMaxMinCasesDto
