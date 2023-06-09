package com.innowise.covidapi.util

import com.typesafe.config.{Config, ConfigFactory}

object PropertyUtil {

  private val config = ConfigFactory.load(ApiConstant.CONFIGURATION_FILE_NAME)

  def getProperty(propertyName: String): AnyRef = {
    config.getAnyRef(propertyName)
  }

}
