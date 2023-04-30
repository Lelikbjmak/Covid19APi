package com.innowise.covidapi.repository

import redis.clients.jedis.Jedis

trait RedisRepository[A] {
  
  def save(entity: A): A

  def saveAll(entityCollection: Iterable[A]): Iterable[A]

  def find(key: String): Option[A]
}
