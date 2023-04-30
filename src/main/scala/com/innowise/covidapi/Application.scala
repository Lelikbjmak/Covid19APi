package com.innowise.covidapi

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.innowise.covidapi.controller.ApiRoute
import com.innowise.covidapi.entity.{Country, CountryCovidDetails}
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.repository.impl.{CountryRepository, CovidDetailsRepository}
import com.innowise.covidapi.service.impl.{ApiServiceImpl, CovidDetailsServiceImpl}
import com.innowise.covidapi.service.{ApiService, CovidDetailsService}
import com.innowise.covidapi.util.{ApiConstant, ApplicationInitializer, PropertyUtil}
import com.innowise.covidapi.validator.ApiControllerRouteValidator
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}

object Application extends App {

  private val logger = LoggerFactory.getLogger(Application.getClass)

  private val APPLICATION_HOST = PropertyUtil.getProperty(ApiConstant.Application.HOST_PROPERTY_NAME).asInstanceOf[String]
  private val APPLICATION_PORT = PropertyUtil.getProperty(ApiConstant.Application.PORT_PROPERTY_NAME).asInstanceOf[Int]

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  private implicit val executionContext: ExecutionContextExecutor = system.executionContext

  private val apiService: ApiService = new ApiServiceImpl

  private val countryRepository: RedisRepository[Country] = new CountryRepository(ApiConstant.Redis.COUNTRY_DATABASE)
  private val covidRepository: RedisRepository[CountryCovidDetails] = new CovidDetailsRepository(ApiConstant.Redis.COVID_DETAILS_DATABASE)

  private val apiControllerRouteValidator: ApiControllerRouteValidator = ApiControllerRouteValidator(countryRepository)

  private val covidDetailsService: CovidDetailsService = CovidDetailsServiceImpl(apiService, covidRepository)


  private val apiRoute: ApiRoute = new ApiRoute(covidDetailsService, apiControllerRouteValidator, countryRepository.asInstanceOf[CountryRepository])

  private val serverBindingFuture: Future[Http.ServerBinding] = {
    Http().newServerAt(APPLICATION_HOST, APPLICATION_PORT).bind(apiRoute.route)
  }

  serverBindingFuture.onComplete { serverBinding =>
    if (serverBinding.isSuccess) {
      logger.debug(s"Server started on ${serverBinding.get.localAddress}")
      ApplicationInitializer.initCountries(countryRepository, apiService)
    } else {
      logger.error(s"Server could not be started: ${serverBinding.failed.get.getMessage}")
    }
  }

  def getRoute: ApiRoute = {
    this.apiRoute
  }

}
