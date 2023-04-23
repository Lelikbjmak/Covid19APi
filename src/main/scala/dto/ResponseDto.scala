package com.innowise
package dto


import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonWriter}

import java.time.LocalDateTime

case class ResponseDto[T](timestamp: LocalDateTime, code: Int, status: String, message: String, body: T)