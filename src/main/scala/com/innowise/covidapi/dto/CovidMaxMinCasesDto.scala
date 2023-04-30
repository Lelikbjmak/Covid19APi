package com.innowise.covidapi.dto

import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsObject, JsValue, RootJsonFormat, enrichAny}

case class CovidMaxMinCasesDto(max: List[SummaryCovidDetailsDto], min: List[SummaryCovidDetailsDto])

object CovidMaxMinCasesDto extends DefaultJsonProtocol:

  implicit val covidDetailsDto: RootJsonFormat[CovidMaxMinCasesDto] = new RootJsonFormat[CovidMaxMinCasesDto]:

    override def read(json: JsValue): CovidMaxMinCasesDto = json match {
      case JsObject(fields) =>
        val max = fields.get("max").map(_.convertTo[List[SummaryCovidDetailsDto]])
          .getOrElse(throw DeserializationException("Error in deserialize CovidMaxMinCasesDto"))
        val min = fields.get("min").map(_.convertTo[List[SummaryCovidDetailsDto]])
          .getOrElse(throw DeserializationException("Error in deserialize CovidMaxMinCasesDto"))
        CovidMaxMinCasesDto(max = max, min = min)
      case _ => throw DeserializationException("Error in deserialize CovidMaxMinCasesDto")
    }

    override def write(obj: CovidMaxMinCasesDto): JsValue = JsObject(
      "max" -> JsArray(Vector(obj.max.map(_.toJson): _*)),
      "min" -> JsArray(Vector(obj.min.map(_.toJson): _*))
    )

end CovidMaxMinCasesDto
