package com.innowise.covidapi.controller

import akka.actor.Status.Success
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.HttpMethods.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.innowise.covidapi.dto.CovidMaxMinCasesDto
import com.innowise.covidapi.mapper.CountryMapper
import com.innowise.covidapi.repository.impl.CountryRepository
import com.innowise.covidapi.service.CovidDetailsService
import com.innowise.covidapi.util.ApiConstant
import com.innowise.covidapi.validator.ApiControllerRouteValidator
import org.slf4j.{Logger, LoggerFactory}
import spray.json.{DefaultJsonProtocol, enrichAny}

import java.net.http.HttpHeaders
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class ApiController(covidDetailsService: CovidDetailsService, apiControllerRouteValidator: ApiControllerRouteValidator, countryRepository: CountryRepository) extends DefaultJsonProtocol {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val route: Route = pathPrefix("covid" / "countries") {
    pathEnd {
      get {
        logger.debug("Processing covid/countries GET request...")
        val countries = CountryMapper.mapToDtoList(countryRepository.findAll())
        complete(HttpEntity(ContentTypes.`application/json`, countries.toJson.toString))
      }
    } ~
      path("summary") {
        post {
          entity(as[List[String]]) { countryList => {
            validate(apiControllerRouteValidator.validateRequestBody(countryList),
              "Some country in CountryList is undefined. Check the accuracy of input data.") {

              logger.debug("Processing covid/countries/summary POST request...")

              val covidDetailsForToday: Future[CovidMaxMinCasesDto] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForToday(countryList)
              val po = Await.result(covidDetailsForToday, Duration.Inf)
              val headers = `Access-Control-Allow-Origin`.*

              complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, po.toJson.toString)).withHeaders(headers))
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
                s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDate.now()} ") {

                logger.debug("Processing covid/countries/day/{day} POST request...")

                val requestedDay: LocalDate = LocalDate.parse(dayString)

                val covidDetailsForParticularDay: Future[CovidMaxMinCasesDto] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForTerm(countryList, requestedDay, Option.empty)

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
                  s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDate.now()} ") {

                  logger.debug("Processing covid/countries/term?from=..&to=.. POST request...")

                  val firstDay: LocalDate = LocalDate.parse(from)
                  val lastDay: LocalDate = LocalDate.parse(to)

                  val covidDetailsForParticularDay: Future[CovidMaxMinCasesDto] = covidDetailsService.getMaxAndMinCovidCasesForCountryListForTerm(countryList, firstDay, Some(lastDay))

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