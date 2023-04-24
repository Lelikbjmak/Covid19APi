package com.innowise
package repository.impl

import entity.Country
import repository.RedisRepository
import util.{ApiConstant, JedisConnectionManager}

import redis.clients.jedis.Jedis
import spray.json.{enrichAny, enrichString}

class CountryRepository extends RedisRepository[Country] {

  private val connection: Jedis = JedisConnectionManager.getConnection(ApiConstant.Redis.COUNTRY_DATABASE)

  override def save(entity: Country): Country = {
    val jsonCountry = entity.toJson.toString
    connection set(entity.slug, jsonCountry)
    entity
  }

  override def find(key: String): Option[Country] = {
    val jsonCountry = connection.get(key)
    if jsonCountry == null then
      Option.empty
    else
      Option.apply(jsonCountry.parseJson.convertTo[Country])
  }

  override def saveAll(entityCollection: Iterable[Country]): Iterable[Country] = {
    entityCollection.foreach(country => save(country))
    entityCollection
  }
}
