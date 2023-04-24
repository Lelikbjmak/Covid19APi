package com.innowise
package controller

import dto.{CovidMaxMinCasesDto, ResponseDto}
import service.CovidDetailsService
import util.ApiConstant
import validator.ApiControllerRouteValidator

import akka.actor.Status.Success
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import spray.json.{DefaultJsonProtocol, enrichAny}

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Failure

class ApiController(covidDetailsService: CovidDetailsService, apiControllerRouteValidator: ApiControllerRouteValidator) extends DefaultJsonProtocol {

  private val route: Route = pathPrefix("covid" / "countries") {
    path("summary") {
      post {
        entity(as[List[String]]) { countryList => {
          validate(apiControllerRouteValidator.validateRequestBody(countryList),
            "Some country in CountryList is undefined. Check the accuracy of input data.") {

            val covidDetailsForToday: Future[ResponseDto[CovidMaxMinCasesDto]] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForToday(countryList)
            val po = Await.result(covidDetailsForToday, Duration.Inf)
            complete(HttpEntity(ContentTypes.`application/json`, po.toJson.toString))
          }
        }
        }
      }
    } ~
      path("day" / Segment) { dayString =>
        post {
          entity(as[List[String]]) { countryList => {
            validate(apiControllerRouteValidator.validateRequestBody(countryList),
              "Some country in CountryList is undefined. Check the accuracy of input data.") {
              validate(apiControllerRouteValidator.validateRequestParamAndPathVariable(dayString),
                s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDateTime.now()} ") {

                val requestedDay: LocalDateTime = LocalDateTime.parse(dayString, DateTimeFormatter.ISO_DATE_TIME)

                val covidDetailsForParticularDay: Future[ResponseDto[CovidMaxMinCasesDto]] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForTerm(countryList, requestedDay, Option.empty)

                val result = Await.result(covidDetailsForParticularDay, Duration.Inf)

                complete(HttpEntity(ContentTypes.`application/json`, result.toJson.toString))
              }
            }
          }
          }
        }
      } ~
      path("term") {
        parameters("from", "to") { (from, to) =>
          post {
            entity(as[List[String]]) { countryList => {
              validate(apiControllerRouteValidator.validateRequestBody(countryList),
                "Some country in CountryList is undefined. Check the accuracy of input data.") {
                validate(apiControllerRouteValidator.validateRequestParamAndPathVariable(from, to),
                  s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDateTime.now()} ") {

                  val firstDay: LocalDateTime = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME)
                  val lastDay: LocalDateTime = LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME)

                  val covidDetailsForParticularDay: Future[ResponseDto[CovidMaxMinCasesDto]] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForTerm(countryList, firstDay, Some(lastDay))

                  val result = Await.result(covidDetailsForParticularDay, Duration.Inf)

                  complete(HttpEntity(ContentTypes.`application/json`, result.toJson.toString))
                }
              }
            }
            }
          }
        }
      }
  }

  def getRoute: Route = route
}