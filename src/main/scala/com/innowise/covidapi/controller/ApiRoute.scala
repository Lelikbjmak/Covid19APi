package com.innowise.covidapi.controller

import akka.http.scaladsl.model.headers.HttpOrigin
import akka.http.scaladsl.model.{HttpMethod, HttpMethods}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Route, ValidationRejection}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.model.{HttpHeaderRange, HttpOriginMatcher}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.innowise.covidapi.repository.impl.CountryRepository
import com.innowise.covidapi.service.CovidDetailsService
import com.innowise.covidapi.validator.ApiControllerRouteValidator

class ApiRoute(covidDetailsService: CovidDetailsService, apiControllerRouteValidator: ApiControllerRouteValidator, countryRepository: CountryRepository) {

  private val apiController: ApiController = new ApiController(covidDetailsService, apiControllerRouteValidator, countryRepository)

  private val corsSettings = CorsSettings.defaultSettings
    .withAllowedOrigins(HttpOriginMatcher("http://localhost:4200"))
    .withAllowCredentials(true)
    .withAllowedHeaders(
      HttpHeaderRange(
        "Accept",
        "Authorization",
        "Content-Type",
        "X-Requested-With"
      )
    )
    .withExposedHeaders(List("Authorization"))
    .withAllowedMethods(Seq(HttpMethods.GET, HttpMethods.POST))

  val route: Route = cors(corsSettings) {
    pathPrefix("api" / "v1") {
      apiController.getRoute
    }
  }
}
