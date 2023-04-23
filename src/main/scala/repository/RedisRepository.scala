package com.innowise
package repository

trait RedisRepository[A] {

  def save(entity: A): A

  def saveAll(entityCollection: Iterable[A]): Iterable[A]

  def find(key: String): Option[A]
}
