package com.innowise.covidapi.util

import java.time.{LocalDate, LocalDateTime}

object ApiConstant {

  val CONFIGURATION_FILE_NAME = "application.conf"

  object Redis {
    val HOST_PROPERTY_NAME = "redis.host"
    val PORT_PROPERTY_NAME = "redis.port"
    val TOTAL_CONNECTIONS_PROPERTY_NAME = "redis.connection-pool.total-connections"
    val MAX_IDLE_CONNECTIONS_PROPERTY_NAME = "redis.connection-pool.max-idle"
    val MIN_IDLE_CONNECTIONS_PROPERTY_NAME = "redis.connection-pool.min-idle"

    val COUNTRY_DATABASE = 0
    val COVID_DETAILS_DATABASE = 1
    val TEST_DATABASE = 2
  }

  object ApiRequest {
    val COVID_DETAILS_MAIN_ROUTE = "https://api.covid19api.com/total/country/" // after this add country
    val COVID_DETAILS_ADDITIONAL_ROUTE = "/status/confirmed" // after this add request params
    val COVID_DETAILS_FOR_CURRENT_DAY_ROUTE = "https://api.covid19api.com/summary" // summary for current date
    val POSSIBLE_COUNTRIES_ROUTE = "https://api.covid19api.com/countries"
  }

  object Header {
    val ATTEMPTS_LIMIT_HEADER_NAME = "X-Ratelimit-Remaining"
    val RATE_LIMIT_RESET_HEADER_NAME = "X-Ratelimit-Reset"
  }

  object Application {
    val PORT_PROPERTY_NAME = "application.port"
    val HOST_PROPERTY_NAME = "application.host"
  }

  object Api {
    val FIRST_RECORDED_DAY: LocalDate = LocalDate.of(2020, 3, 5)
  }
}
