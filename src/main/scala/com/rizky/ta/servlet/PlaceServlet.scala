package com.rizky.ta.servlet

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.rizky.ta.model._
import org.json4s.jackson.Serialization
import com.rizky.ta.util.PlacesUtil
import scala.collection.mutable.ListBuffer

/**
  * Created by risol_000 on 1/30/2017.
  */


class PlaceServlet extends ScalatraServlet with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats
  protected implicit val apiKey: String = Common.apiKey

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  post("/addPlaces") {
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

  post("/bulkUpdatePlaces"){
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

  get("/createTablePlaces"){
    Place.createTablePlaces()
    Serialization.write("status" -> "table places created")
  }

}
