package com.innowise
package service.impl

import controller.ApiController
import dto.{SummaryCovidDetailsDto, TermCovidDetailsDto}
import entity.Country
import service.ApiService
import util.ApiConstant.Header

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}

class ApiServiceImpl(implicit system: ActorSystem[Nothing], executionContext: ExecutionContext) extends ApiService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  override def call(request: String): Future[String] = {

    logger.debug(s"Sending request to $request")

    val httpRequest: HttpRequest = HttpRequest(HttpMethods.GET, request)
    val futureResponse: Future[HttpResponse] = Http().singleRequest(request = httpRequest)
    val response = Await.result[HttpResponse](futureResponse, Duration.Inf)

    refreshRateLimit(response)

    response.entity.toStrict(1.second).map { strictEntity =>
      strictEntity.data.utf8String
    }
  }
  private def refreshRateLimit(httpResponse: HttpResponse): Unit = {

    val remainAttempts = httpResponse.headers.find(header => header.name == Header.ATTEMPTS_LIMIT_HEADER_NAME).get.value.toInt
    val resetTime = httpResponse.headers.find(header => header.name == Header.RATE_LIMIT_RESET_HEADER_NAME).get.value.toLong

    if remainAttempts == 0 then
      Thread.sleep((resetTime - System.currentTimeMillis() / 1000) * 1000)
  }
}
