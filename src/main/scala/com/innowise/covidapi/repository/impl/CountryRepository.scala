package com.innowise.covidapi.repository.impl

import com.innowise.covidapi.entity.Country
import com.innowise.covidapi.repository.{FindAllRepository, RedisRepository}
import com.innowise.covidapi.util.{ApiConstant, JedisConnectionManager}
import redis.clients.jedis.Jedis
import spray.json.{enrichAny, enrichString}

import scala.jdk.CollectionConverters.*

class CountryRepository(database: Int) extends RedisRepository[Country] with FindAllRepository[Country] {
  
  private val connection: Jedis = JedisConnectionManager.getConnection(database)

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

  override def findAll(): List[Country] = {
    val countryKeys = connection.keys("*")
    val countryList = countryKeys.stream()
      .map(key => find(key))
      .filter(countryOption => countryOption.isDefined)
      .map(optionCountry => optionCountry.get)
      .toList
    countryList.asScala.toList
  }

}
