package com.innowise.covidapi.util

import com.innowise.covidapi.config.RedisConfig

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object JedisConnectionManager {

  def getConnection(database: Int): Jedis = {
    val connection = RedisConfig.connectionPool.getResource
    connection.select(database)
    connection
  }

}
