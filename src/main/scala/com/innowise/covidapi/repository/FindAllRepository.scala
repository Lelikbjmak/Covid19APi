package com.innowise.covidapi.repository

trait FindAllRepository[A] {

  self: RedisRepository[_] =>

  def findAll(): List[A]
}
