package com.innowise
package entity

import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

case class CountryCovidDetails(cases: Long, slug: String, date: LocalDate)
