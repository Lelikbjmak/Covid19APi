package com.innowise
package jsonformat

import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeJsonProtocol extends DefaultJsonProtocol {

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {
    def write(instant: LocalDateTime): JsValue = JsString(DateTimeFormatter.ISO_DATE_TIME.format(instant))

    def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
      case _ => throw DeserializationException("Expected string")
    }
  }

}
