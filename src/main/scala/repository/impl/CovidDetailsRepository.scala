package com.innowise
package repository.impl

import entity.CountryCovidDetails
import repository.RedisRepository
import util.{ApiConstant, JedisConnectionManager}

import redis.clients.jedis.Jedis
import spray.json.{enrichAny, enrichString}

import java.time.LocalDate

class CovidDetailsRepository extends RedisRepository[CountryCovidDetails] {

  private val connection: Jedis = JedisConnectionManager.getConnection(ApiConstant.Redis.COVID_DETAILS_DATABASE)

  override def save(entity: CountryCovidDetails): CountryCovidDetails = {

    val jsonCovidDetails = entity.toJson.toString
    val key = entity.slug + " " + entity.date
    connection set(key, jsonCovidDetails)
    if entity.date == LocalDate.now() then
      connection expire(key, 60 * 60 * 3) // 3 hours

    entity
  }

  override def find(key: String): Option[CountryCovidDetails] = {
    val jsonCovidDetails = connection.get(key)
    if jsonCovidDetails == null then
      Option.empty
    else
      Option.apply(jsonCovidDetails.parseJson.convertTo[CountryCovidDetails])
  }

  override def saveAll(entityCollection: Iterable[CountryCovidDetails]): Iterable[CountryCovidDetails] = {
    entityCollection.foreach(covidDetails => save(covidDetails))
    entityCollection
  }
}
