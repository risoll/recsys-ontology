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
  protected val applicationDescription = "The places API. It exposes operations for browsing and searching lists of places"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }


  val addPlaces =
    (apiOperation[Unit]("/addPlaces")
      summary "add bulk places"
      notes "add multiple place, optional in collabs with UI"
      parameter queryParam[Option[List[Result]]]("results").description("a list of result that will be added to the DB, from Google Place API"))
  post("/addPlaces", operation(addPlaces)) {
    val results = (parsedBody \ "results").extract[List[Result]]
    for (result <- results) {
      Place.create(result.place_id)
    }
  }

  post("/updatePlace") {
    val placeId = (parsedBody \ "placeId").extract[String]
    val name = (parsedBody \ "name").extract[String]
    val types = (parsedBody \ "types").extract[List[String]].mkString(";")
    val address = (parsedBody \ "address").extract[String]
    val phone = (parsedBody \ "phone").extract[String]
    val openHours = (parsedBody \ "openHours").extract[String]
    val lengthOfVisit = (parsedBody \ "lengthOfVisit").extract[String]
    val tariff = (parsedBody \ "tariff").extract[String]
    Place.updatePlace(placeId, name, types, address, phone, openHours, lengthOfVisit, tariff)
  }

  post("/bulkUpdatePlaces") {
    val places = Place.list()
    val result = PlacesUtil.createRowMap(places)
    println("places", result)
    result
  }

  get("/places") {
    val places = Place.list()
    val result = PlacesUtil.createRowMap(places)
    val jsonResult = Serialization.write(result)
    jsonResult
  }

  get("/createTablePlaces") {
    Place.createTablePlaces()
    Serialization.write("status" -> "table places created")
  }


}
