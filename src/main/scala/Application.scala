package com.innowise

import controller.{ApiRoute, CountryController}
import service.impl.{ApiServiceImpl, CovidDetailsServiceImpl}
import service.{ApiService, CovidDetailsService}
import util.{ApiConstant, ApplicationInitializer, PropertyUtil}

import akka.actor.*
import akka.actor.Status.Success
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.server.Directives.*
import com.innowise.entity.{Country, CountryCovidDetails}
import repository.RedisRepository
import repository.impl.{CountryRepository, CovidDetailsRepository}
import validator.ApiControllerRouteValidator

import akka.http.scaladsl.server.Route
import akka.pattern.Patterns.after
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn

object Application extends App {

  private val APPLICATION_HOST = PropertyUtil.getProperty(ApiConstant.Application.HOST_PROPERTY_NAME).asInstanceOf[String]
  private val APPLICATION_PORT = PropertyUtil.getProperty(ApiConstant.Application.PORT_PROPERTY_NAME).asInstanceOf[Int]

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  private implicit val executionContext: ExecutionContextExecutor = system.executionContext

  private val logger = LoggerFactory.getLogger(Application.getClass)

  private val apiService: ApiService = new ApiServiceImpl

  private val countryRepository: RedisRepository[Country] = new CountryRepository
  private val covidRepository: RedisRepository[CountryCovidDetails] = new CovidDetailsRepository

  private val apiControllerRouteValidator: ApiControllerRouteValidator = ApiControllerRouteValidator(countryRepository)

  private val covidDetailsService: CovidDetailsService = CovidDetailsServiceImpl(apiService, covidRepository)

  private val apiRoute: ApiRoute = new ApiRoute(covidDetailsService, apiControllerRouteValidator)

  private val serverBindingFuture: Future[Http.ServerBinding] = {
    Http().newServerAt(APPLICATION_HOST, APPLICATION_PORT).bind(apiRoute.route)
  }

  serverBindingFuture.onComplete { serverBinding =>
    if (serverBinding.isSuccess) {
      logger.info(s"Server started on ${serverBinding.get.localAddress}")
      ApplicationInitializer.initCountries(countryRepository, apiService)
    } else {
      logger.error(s"Server could not be started: ${serverBinding.failed.get.getMessage}")
    }
  }

}
