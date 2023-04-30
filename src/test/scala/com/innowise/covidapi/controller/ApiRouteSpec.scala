package com.innowise.covidapi.controller

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.{Route, ValidationRejection}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.innowise.covidapi.Application
import com.innowise.covidapi.dto.{CovidMaxMinCasesDto, SummaryCovidDetailsDto}
import com.innowise.covidapi.util.ApiConstant
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{shouldBe, shouldEqual}
import org.scalatest.wordspec.AnyWordSpec
import spray.json.*
import spray.json.DefaultJsonProtocol.*

import java.time.{LocalDate, LocalDateTime}

class ApiRouteSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  private val routes: Route = Application.getRoute.route

  "GET /api/v1/covid/countries" should {
    "return a list of countries GET requests to /api/v1/covid/countries" in {
      Get("/api/v1/covid/countries") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "POST /api/v1/covid/countries/day/{day}" should {
    "Reject request due to incorrect date" in {
      val countries = List("belarus", "bolivia")
      Post("/api/v1/covid/countries/day/2019-01-01").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        rejection shouldEqual ValidationRejection(s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDate.now()} ")
      }
    }
    "Reject request due to incorrect requestBody of countries" in {
      val countries = List("invalidCountry", "bolivia")
      Post("/api/v1/covid/countries/day/2019-01-01").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        rejection shouldEqual ValidationRejection("Some country in CountryList is undefined. Check the accuracy of input data.")
      }
    }
    "Obtain MaxMinCovidDetails for list of countries for a certain day" in {
      val countries = List("belarus", "japan")
      val expected = CovidMaxMinCasesDto(List(
        SummaryCovidDetailsDto("japan", 3071, 239005, LocalDateTime.parse("2021-01-01T00:00:00"))
      ), List(
        SummaryCovidDetailsDto("belarus", 1902, 196223, LocalDateTime.parse("2021-01-01T00:00:00"))
      ))
      Post("/api/v1/covid/countries/day/2021-01-01").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val json = responseAs[String].parseJson
        json.convertTo[CovidMaxMinCasesDto] shouldEqual expected
      }
    }
  }

  "POST /api/v1/covid/countries/summary" should {
    "Reject request due to incorrect requestBody of countries" in {
      val countries = List("invalidCountry", "bolivia")
      Post("/api/v1/covid/countries/summary").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        rejection shouldEqual ValidationRejection("Some country in CountryList is undefined. Check the accuracy of input data.")
      }
    }
    "Obtain MaxMinCovidDetails for list of countries for today" in {
      val countries = List("belarus", "japan")
      Post("/api/v1/covid/countries/summary").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val json = responseAs[String].parseJson
        json.convertTo[CovidMaxMinCasesDto] != null shouldEqual true
      }
    }
  }


  "POST /api/v1/covid/countries/term" should {
    "Reject request due to incorrect date" in {
      val countries = List("belarus", "bolivia")
      Post("/api/v1/covid/countries/term?from=2019-01-01&to=2025-01-01").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        rejection shouldEqual ValidationRejection(s"Invalid day. Date should be between first recorded day ${ApiConstant.Api.FIRST_RECORDED_DAY} and current date ${LocalDate.now()} ")
      }
    }
    "Reject request due to incorrect requestBody of countries" in {
      val countries = List("invalidCountry", "bolivia")
      Post("/api/v1/covid/countries/term?from=2021-03-14&to=2021-03-20").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        rejection shouldEqual ValidationRejection("Some country in CountryList is undefined. Check the accuracy of input data.")
      }
    }
    "Obtain MaxMinCovidDetails for list of countries for a certain day" in {
      val countries = List("belarus", "japan")
      val expected = CovidMaxMinCasesDto(List(
        SummaryCovidDetailsDto("japan", 6321, 447830, LocalDateTime.parse("2021-03-14T00:00:00"))
      ), List(
        SummaryCovidDetailsDto("belarus", 5615, 302323, LocalDateTime.parse("2021-03-14T00:00:00"))
      ))
      Post("/api/v1/covid/countries/term?from=2021-03-14&to=2021-03-20").withEntity(ContentTypes.`application/json`, countries.toJson.toString) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        val json = responseAs[String].parseJson
        json.convertTo[CovidMaxMinCasesDto] shouldEqual expected
      }
    }
  }
}

