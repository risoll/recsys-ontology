package com.rizky.ta.controller

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.rizky.ta.model._
import org.json4s.jackson.Serialization
import com.rizky.ta.util.PlacesUtil
import org.scalatra.swagger.{Swagger, SwaggerSupport}

import scala.collection.mutable.ListBuffer

/**
  * Created by risol_000 on 1/30/2017.
  */


class PlaceController(implicit val swagger: Swagger)
  extends ScalatraServlet
  with JacksonJsonSupport
  with CorsSupport
  with SwaggerSupport {

  protected implicit val apiKey: String = Common.apiKey
  protected val applicationDescription = "The Places API. It exposes operations for browsing and searching lists of places"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }


  private val places =
    (apiOperation[Unit]("/bulk")
      summary "get list of all places")
  get("/bulk", operation(places)) {
    Place.list()
  }

  private val placesPagination =
    (apiOperation[Unit]("/pagination")
      summary "get list of places with pagination"
      parameters(
      queryParam[Int]("limit").defaultValue(10).description("Number of content per page"),
      queryParam[Int]("offset").defaultValue(10).description("Number of skipped rows")
    ))
  get("/pagination", operation(placesPagination)) {
    val limit = params("limit").toInt
    val offset = params("offset").toInt
    Place.listPagination(limit, offset)
  }

//  post("/update") {
//    val placeId = (parsedBody \ "placeId").extract[String]
//    val name = (parsedBody \ "name").extract[String]
//    val formattedAddress = (parsedBody \ "formattedAddress").extract[String]
//    val phone = (parsedBody \ "phone").extract[String]
//    val lengthOfVisit = (parsedBody \ "lengthOfVisit").extract[String]
//    val tariff = (parsedBody \ "tariff").extract[String]
//    val photo = (parsedBody \ "photo").extract[String]
//    val lat = (parsedBody \ "lat").extract[Double]
//    val lng = (parsedBody \ "lng").extract[Double]
//    val rating = (parsedBody \ "rating").extract[Double]
//    val openHoursMonday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursTuesday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursWednesday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursThursday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursFriday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursSaturday = (parsedBody \ "openHoursMonday").extract[String]
//    val openHoursSunday = (parsedBody \ "openHoursMonday").extract[String]
//    val clsoeHoursMonday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursTuesday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursWednesday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursThursday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursFriday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursSaturday = (parsedBody \ "openHoursMonday").extract[String]
//    val closeHoursSunday = (parsedBody \ "openHoursMonday").extract[String]
//    Place.updatePlace(
//      placeId, name, formatted_address, phone, openHours,
//      lengthOfVisit, tariff
//    )
//  }

//  post("/update/bulk") {
//    val places = Place.list()
//    val result = PlacesUtil.createRowMap(places)
//    println("places", result)
//    result
//  }

//  get("/bulk") {
//    Place.list()
//    val places = Place.list()
//    val result = PlacesUtil.createRowMap(places)
//    val jsonResult = Serialization.write(result)
//    jsonResult
//  }



}
