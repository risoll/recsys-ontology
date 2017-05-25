package com.rizky.ta.controller

import com.rizky.ta.util.GoogleUtil
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

/**
  * Created by solehuddien on 03/05/17.
  */
class GoogleController extends ScalatraServlet with JacksonJsonSupport with CorsSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  get("/radarSearch/:lat/:lng/:radius/:maxResult"){
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    val maxResult = params.get("maxResult").get.toInt
    GoogleUtil.radarSearch(lat, lng, radius, maxResult)
  }

  get("/photos"){
    val maxWidth = params.get("maxwidth")
    val photoRef = params.get("photoreference")
    val key = params.get("key")
  }
}
