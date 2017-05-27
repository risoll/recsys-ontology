package com.rizky.ta.controller

import com.rizky.ta.util.GoogleUtil
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

/**
  * Created by solehuddien on 03/05/17.
  */
class GoogleController(implicit val swagger: Swagger)
  extends ScalatraServlet
    with JacksonJsonSupport
    with CorsSupport
    with SwaggerSupport {
  protected val applicationDescription = "The Google API. Use this instead of its web service to avoid CORS"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }


  private val radarSearch =
    (apiOperation[List[String]]("/radarSearch")
      summary "get list of places with Google radar search API"
      parameters(
        queryParam[Double]("lat").defaultValue(-6.973984).description("Latitude"),
        queryParam[Double]("lng").defaultValue(107.630385).description("Longitude"),
        queryParam[Double]("radius").defaultValue(50).description("Radius in Km")
    ))
  get("/radarSearch", operation(radarSearch)){
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    val res = GoogleUtil.radarSearch(lat, lng, radius)
    res
  }

  get("/photos"){
    val maxWidth = params.get("maxwidth")
    val photoRef = params.get("photoreference")
    val key = params.get("key")
  }
}
