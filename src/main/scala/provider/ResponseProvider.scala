package com.innowise
package provider

import dto.ResponseDto

import akka.http.scaladsl.model.StatusCode

import java.time.LocalDateTime

object ResponseProvider {
  def generateResponse[T](message: String, statusCode: StatusCode, body: T): ResponseDto[T] = {
    ResponseDto(LocalDateTime.now(), statusCode.intValue, statusCode.reason, message, body)
  }
  
}
