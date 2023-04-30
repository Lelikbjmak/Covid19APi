package com.innowise.covidapi.repository

import com.innowise.covidapi.entity.Country
import com.innowise.covidapi.repository.impl.CountryRepository
import com.innowise.covidapi.util.{ApiConstant, JedisConnectionManager}
import org.scalatest.{BeforeAndAfterEach, color}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual
import org.scalatest.wordspec.AnyWordSpecLike
import redis.clients.jedis.Jedis

class CountryRepositorySpec extends AnyWordSpecLike with Matchers with BeforeAndAfterEach {

  private val countryRepository: RedisRepository[Country] = new CountryRepository(ApiConstant.Redis.TEST_DATABASE)

  private val testDatabaseConnection: Jedis = JedisConnectionManager.getConnection(ApiConstant.Redis.TEST_DATABASE)

  override def beforeEach(): Unit = {
    testDatabaseConnection.flushDB()
  }

  override def afterEach(): Unit = {
    testDatabaseConnection.flushDB()
  }

  "CountryRepository" when {

    "Saving a Country" should {
      "Store the Country in the database" in {

        val country = Country("United States", "us", "USA")
        val savedCountry = countryRepository.save(country)

        savedCountry shouldEqual country

        countryRepository.find("us").isDefined shouldEqual true
        countryRepository.find("us").get shouldEqual country
      }
    }

    "Saving a List of Countries" should {
      "Store the List of Countries in the database" in {

        val countryList: List[Country] = List(
          Country("United States", "us", "USA"),
          Country("United Kingdom", "uk", "UK"),
          Country("Belarus", "belarus", "BY")
        )

        val savedCountries = countryRepository.saveAll(countryList)

        savedCountries shouldEqual countryList

        countryList.forall(country =>
          countryRepository.find(country.slug).isDefined) shouldEqual true
      }
    }

    "Find a Country" should {
      "Return the Country from database" in {
        val country = Country("United States", "us", "USA")
        countryRepository.save(country)

        countryRepository.find("us").isDefined shouldEqual true
        countryRepository.find("us").get shouldEqual country
      }
    }

    "Find all Countries" should {
      "Retrieve all Countries from database" in {

        val countryList: List[Country] = List(
          Country("United States", "us", "USA"),
          Country("United Kingdom", "uk", "UK"),
          Country("Belarus", "belarus", "BY")
        )

        countryRepository.saveAll(countryList)

        countryRepository.asInstanceOf[CountryRepository].findAll().size shouldEqual 3
      }
    }
  }

}
