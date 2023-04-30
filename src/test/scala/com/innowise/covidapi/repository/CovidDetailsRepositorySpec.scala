package com.innowise.covidapi.repository

import com.innowise.covidapi.entity.CountryCovidDetails
import com.innowise.covidapi.repository.impl.CovidDetailsRepository
import com.innowise.covidapi.util.{ApiConstant, JedisConnectionManager}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldEqual
import org.scalatest.wordspec.AnyWordSpecLike
import redis.clients.jedis.Jedis

import java.time.LocalDate

class CovidDetailsRepositorySpec extends AnyWordSpecLike with Matchers with BeforeAndAfterEach {

  private val covidRepository: RedisRepository[CountryCovidDetails] = new CovidDetailsRepository(ApiConstant.Redis.TEST_DATABASE)

  private val testDatabaseConnection: Jedis = JedisConnectionManager.getConnection(ApiConstant.Redis.TEST_DATABASE)

  override def beforeEach(): Unit = {
    testDatabaseConnection.flushDB()
  }

  override def afterEach(): Unit = {
    testDatabaseConnection.flushDB()
  }

  "CovidDetailsRepository" when {

    "Saving CovidDetails" should {
      "Store CovidDetails in database" in {
        val date: LocalDate = LocalDate.of(2020, 2, 2)
        val covidDetails: CountryCovidDetails = new CountryCovidDetails(1, 2, "us", date)

        val savedCovidDetails = covidRepository.save(covidDetails)

        covidDetails shouldEqual savedCovidDetails

        covidRepository.find(covidDetails.slug + " " + covidDetails.date).isDefined shouldEqual true
      }
    }

    "Saving CovidDetails List" should {
      "Store CovidDetails List in database" in {
        val date: LocalDate = LocalDate.of(2020, 2, 2)
        val covidDetailsList: List[CountryCovidDetails] = List(
          CountryCovidDetails(1, 2, "us", date),
          CountryCovidDetails(1, 2, "belarus", date)
        )

        val savedCovidDetailsList = covidRepository.saveAll(covidDetailsList)

        covidDetailsList shouldEqual savedCovidDetailsList

        covidDetailsList.forall(covidDetails =>
          covidRepository.find(covidDetails.slug + " " + covidDetails.date).isDefined) shouldEqual true
      }
    }

    "Find CovidDetails" should {
      "Return CovidDetails from database" in {
        val date: LocalDate = LocalDate.of(2020, 2, 2)
        val covidDetails: CountryCovidDetails = new CountryCovidDetails(1, 2, "us", date)

        val savedCovidDetails = covidRepository.save(covidDetails)

        covidDetails shouldEqual savedCovidDetails

        val retrievedCovidDetails = covidRepository.find(covidDetails.slug + " " + covidDetails.date)
        retrievedCovidDetails.get shouldEqual covidDetails
      }
    }
  }
}
