package com.innowise.covidapi.config

import com.innowise.covidapi.util.{ApiConstant, PropertyUtil}

import redis.clients.jedis.{JedisPool, JedisPoolConfig}

import scala.concurrent.duration.Duration


object RedisConfig {

  private val REDIS_HOST = PropertyUtil.getProperty(ApiConstant.Redis.HOST_PROPERTY_NAME).asInstanceOf[String]
  private val REDIS_PORT = PropertyUtil.getProperty(ApiConstant.Redis.PORT_PROPERTY_NAME).asInstanceOf[Int]
  private val TOTAL_CONNECTION_NUMBER = PropertyUtil.getProperty(ApiConstant.Redis.TOTAL_CONNECTIONS_PROPERTY_NAME).asInstanceOf[Int]
  private val MAX_IDLE_CONNECTIONS = PropertyUtil.getProperty(ApiConstant.Redis.MAX_IDLE_CONNECTIONS_PROPERTY_NAME).asInstanceOf[Int]
  private val MIN_IDLE_CONNECTIONS = PropertyUtil.getProperty(ApiConstant.Redis.MIN_IDLE_CONNECTIONS_PROPERTY_NAME).asInstanceOf[Int]

  val connectionPool: JedisPool = configureConnectionPool

  private def configureConnectionPool: JedisPool = {
    
    val jedisConnectionPoolConfig: JedisPoolConfig = new JedisPoolConfig
    
    jedisConnectionPoolConfig.setMaxTotal(TOTAL_CONNECTION_NUMBER)
    jedisConnectionPoolConfig.setMaxIdle(MAX_IDLE_CONNECTIONS)
    jedisConnectionPoolConfig.setMinIdle(MIN_IDLE_CONNECTIONS)
    jedisConnectionPoolConfig.setTestOnBorrow(true)
    jedisConnectionPoolConfig.setTestOnReturn(true)
    jedisConnectionPoolConfig.setTestWhileIdle(true)

    JedisPool(jedisConnectionPoolConfig, REDIS_HOST, REDIS_PORT)
  }
}
