package com.innowise
package dto

import spray.json.DefaultJsonProtocol
import upickle.implicits.key

import java.time.LocalDateTime

case class SummaryCovidDetailsDto(Slug: String, NewConfirmed: Long, Date: LocalDateTime)
