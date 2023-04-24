package com.innowise
package json

import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateJsonProtocol extends DefaultJsonProtocol {

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {
    def write(instant: LocalDate): JsValue = JsString(DateTimeFormatter.ISO_DATE.format(instant))

    def read(json: JsValue): LocalDate = json match {
      case JsString(s) => LocalDate.parse(s, DateTimeFormatter.ISO_DATE)
      case _ => throw DeserializationException("Expected string")
    }
  }
  
}
