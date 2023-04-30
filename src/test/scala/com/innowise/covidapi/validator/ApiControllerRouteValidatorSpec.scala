package com.innowise.covidapi.validator

import com.innowise.covidapi.entity.Country
import com.innowise.covidapi.repository.RedisRepository
import com.innowise.covidapi.repository.impl.CountryRepository
import com.innowise.covidapi.util.{ApiConstant, JedisConnectionManager}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual
import redis.clients.jedis.Jedis

class ApiControllerRouteValidatorSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  private val testDatabaseConnection: Jedis = JedisConnectionManager.getConnection(ApiConstant.Redis.TEST_DATABASE)
  private val countryRepository: RedisRepository[Country] = new CountryRepository(ApiConstant.Redis.TEST_DATABASE)
  private val routeValidator: ApiControllerRouteValidator = new ApiControllerRouteValidator(countryRepository)

  override def beforeEach(): Unit = {
    countryRepository.saveAll(List(
      Country("Belarus", "belarus", "BY"),
      Country("Bolivia", "bolivia", "BV")))
  }

  override def afterEach(): Unit = {
    testDatabaseConnection.flushDB()
  }

  "Validator" should "approve current request body" in {
    val countrySlugList = List("belarus", "bolivia")
    routeValidator.validateRequestBody(countrySlugList) shouldEqual true
  }

  "Validator" should "reject current request body (Countries not found)" in {
    val countrySlugList = List("belarus", "united-states")
    routeValidator.validateRequestBody(countrySlugList) shouldEqual false
  }

  "Validator" should "approve current request params" in {
    val firstDay = "2020-10-10"
    val lastDay = "2022-10-10"
    routeValidator.validateRequestParamAndPathVariable(firstDay, lastDay) shouldEqual true
  }

  "Validator" should "reject current request params" in {
    val firstDay = "2029-10-10"
    val lastDay = "2018-10-10"
    routeValidator.validateRequestParamAndPathVariable(firstDay, lastDay) shouldEqual false
  }
}