package com.innowise
package controller

import service.CovidDetailsService
import service.impl.CountryRetrieveService
import validator.ApiControllerRouteValidator

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{Route, ValidationRejection}

class ApiRoute(covidDetailsService: CovidDetailsService, apiControllerRouteValidator: ApiControllerRouteValidator, countryRetrieveService: CountryRetrieveService) {

  private val apiController: ApiController = new ApiController(covidDetailsService, apiControllerRouteValidator, countryRetrieveService)

  val route: Route = {
    pathPrefix("api" / "v1") {
      apiController.getRoute
    }
  }
}
