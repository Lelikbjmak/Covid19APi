package com.innowise.covidapi.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.IllegalUriException
import com.innowise.covidapi.dto.CountryDto
import com.innowise.covidapi.mapper.ApiResponseMapper
import com.innowise.covidapi.service.impl.ApiServiceImpl
import com.innowise.covidapi.util.{ApiConstant, CovidRequestUtil}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual
import spray.json.enrichString

import java.time.LocalDate
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class ApiServiceSpec extends AnyFunSpec with Matchers {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  private implicit val executionContext: ExecutionContextExecutor = system.executionContext

  private val apiService: ApiService = new ApiServiceImpl

  describe("Api call") {

    it("Should return a possible list of countries") {
      val response: Future[String] = apiService.call(ApiConstant.ApiRequest.POSSIBLE_COUNTRIES_ROUTE)

      response.onComplete {
        case Success(data) =>
          val jsonCountryList = data.parseJson
          val countryDtoList = jsonCountryList.convertTo[List[CountryDto]]
          countryDtoList.size shouldEqual 248
        case Failure(exception) =>
          println(exception)
      }
    }

    it("Should return a summary CovidDetails") {
      val response: Future[String] = apiService.call(ApiConstant.ApiRequest.COVID_DETAILS_FOR_CURRENT_DAY_ROUTE)

      response.onComplete {
        case Success(value) =>
          val covidInfoFromApi = ApiResponseMapper.mapToSummaryCovidDetailsDtoList(value)
          assert(covidInfoFromApi.nonEmpty)
        case Failure(exception) =>
          println(exception)
      }
    }

    it("Should return covid details for particular term") {
      val firstDay: LocalDate = LocalDate.of(2021, 4, 21)
      val lastDay: LocalDate = LocalDate.of(2021, 4, 23)
      val request = CovidRequestUtil.generateRequest("belarus", firstDay, lastDay)
      val response: Future[String] = apiService.call(request)
      
      response.onComplete {
        case Success(value) =>
          val covidTermDetails = ApiResponseMapper.mapToTermCovidDetailsDtoList(value)
          assert(covidTermDetails.size == 3)
        case Failure(exception) =>
          println(exception)
      }
    }

    it("Sls for particular term") {

      assertThrows[IllegalUriException] {
        apiService.call("/undefined")
      }
    }
  }
}
