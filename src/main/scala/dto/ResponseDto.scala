package com.innowise
package dto


import json.LocalDateJsonProtocol.LocalDateJsonFormat
import json.LocalDateTimeJsonProtocol.LocalDateTimeJsonFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonFormat}

import java.time.LocalDateTime

case class ResponseDto[T](timestamp: LocalDateTime, code: Int, status: String, message: String, body: T)

object ResponseDto extends SprayJsonSupport with DefaultJsonProtocol:
  implicit def responseFormat[T: JsonFormat]: RootJsonFormat[ResponseDto[T]] =
    jsonFormat5(ResponseDto.apply[T])
end ResponseDto

