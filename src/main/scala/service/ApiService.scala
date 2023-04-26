package com.innowise
package service

import dto.{SummaryCovidDetailsDto, TermCovidDetailsDto}

import akka.actor.typed.delivery.internal.ProducerControllerImpl.Request
import akka.http.scaladsl.model.HttpMethod

import scala.concurrent.Future

trait ApiService {

  def call(request: String): Future[String]

}
