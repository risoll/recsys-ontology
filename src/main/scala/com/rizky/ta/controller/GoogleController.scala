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

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  private val bandungLat = -6.917464
  private val bandungLng = 107.619123
  private val radius = 5
  private val maxWidth = 200
  private val maxHeight = 200

  private val textSearch =
    (apiOperation[List[String]]("/search/text")
      summary "get places by querying fuzzy text"
      parameters(
      queryParam[String]("query").defaultValue("Tangkuban Perahu").description("Fuzzy place name"),
      queryParam[Double]("lat").defaultValue(bandungLat).description("Latitude"),
      queryParam[Double]("lng").defaultValue(bandungLng).description("Longitude"),
      queryParam[Double]("radius").defaultValue(radius).description("Radius in meter")
    ))
  get("/search/text", operation(textSearch)) {
    val query = params.get("query").get
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    GoogleUtil.textSearch(query, lat, lng, radius)
  }

  private val radarSearch =
    (apiOperation[List[String]]("/search/radar")
      summary "get list of places with Google radar search API"
      parameters(
      queryParam[Double]("lat").defaultValue(bandungLat).description("Latitude"),
      queryParam[Double]("lng").defaultValue(bandungLng).description("Longitude"),
      queryParam[Double]("radius").defaultValue(radius).description("Radius in meter")
    ))
  get("/search/radar", operation(radarSearch)) {
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    GoogleUtil.radarSearch(lat, lng, radius)
  }

  private val photos =
    (apiOperation[List[String]]("/place/photo/bulk")
      summary "get list of photos from radar search API"
      parameters(
      queryParam[Double]("lat").defaultValue(bandungLat).description("Latitude"),
      queryParam[Double]("lng").defaultValue(bandungLng).description("Longitude"),
      queryParam[Double]("radius").defaultValue(radius).description("Radius in meter"),
      queryParam[Int]("maxWidth").defaultValue(maxWidth).description("Max width of photo in pixel"),
      queryParam[Int]("maxHeight").defaultValue(maxHeight).description("Max height of photo in pixel")
    ))
  get("/place/photo/bulk", operation(photos)) {
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    val maxWidth = params.get("maxWidth").get.toInt
    val maxHeight = params.get("maxHeight").get.toInt
    val searchResults = GoogleUtil.radarSearch(lat, lng, radius)
    val ids = GoogleUtil.getPlaceIds(searchResults)
    val photos = GoogleUtil.getPhotos(maxHeight, maxWidth, ids)
    photos
  }

  private val photo =
    (apiOperation[List[String]]("/place/photo")
      summary "get a place photo"
      parameters(
      queryParam[Int]("maxWidth").defaultValue(maxWidth).description("Max image width in pixel"),
      queryParam[Int]("maxHeight").defaultValue(maxHeight).description("Max image height in pixel"),
      queryParam[String]("photoReference").defaultValue("CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU").description("Reference of photo from place api")
    ))
  get("/place/photo", operation(photo)) {
    val maxWidth = params.get("maxWidth").get.toInt
    val maxHeight = params.get("maxHeight").get.toInt
    val photoReference = params.get("photoReference").get.toString
    val photo = GoogleUtil.getPhoto(maxHeight, maxWidth, photoReference)
    Map("location" -> photo)
  }

  private val placeDetails =
    (apiOperation[List[String]]("/place/details")
      summary "get details of a place"
      parameter queryParam[String]("placeId").defaultValue("ChIJN1t_tDeuEmsRUsoyG83frY4").description("Place ID"))
  get("/place/details", operation(placeDetails)) {
    val placeId = params.get("placeId").get
    GoogleUtil.getPlaceDetails(placeId)
  }


  private val distance =
    (apiOperation[Double]("/distance")
      summary "get distance between two points"
      parameters(
      queryParam[Double]("defaultLat").defaultValue(bandungLat).description("Source Latitude"),
      queryParam[Double]("defaultLng").defaultValue(bandungLng).description("Source longitude"),
      queryParam[Double]("lat").defaultValue(bandungLat).description("Destination Latitude"),
      queryParam[Double]("lng").defaultValue(bandungLng).description("Destination longitude")
    ))
  get("/distance", operation(distance)) {
    val defaultLat = params.get("defaultLat").get.toDouble
    val defaultLng = params.get("defaultLng").get.toDouble
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    var distance: Map[String, Double] = Map("distance" -> 0.0)
    val origins = Map("lat" -> defaultLat, "lng" -> defaultLng)
    val dests = List(Map(
      "name" -> "dest",
      "lat" -> lat,
      "lng" -> lng
    ))
    val distances = GoogleUtil.distanceMatrix(origins, dests)
    if(distances.nonEmpty){
      distance = Map("distance" ->
        distances.head("distance")
          .asInstanceOf[Map[String, Any]]("value")
          .toString
          .toDouble
      )
    }

    distance
  }
}
