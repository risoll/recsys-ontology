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
        queryParam[Double]("lat").defaultValue(-6.917464).description("Latitude"),
        queryParam[Double]("lng").defaultValue(107.619123).description("Longitude"),
        queryParam[Double]("radius").defaultValue(25000).description("Radius in meter")
    ))
  get("/radarSearch", operation(radarSearch)){
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    GoogleUtil.radarSearch(lat, lng, radius)
  }

  private val photos =
    (apiOperation[List[String]]("/photos")
      summary "get list of photos from radar search API"
      parameters(
        queryParam[Double]("lat").defaultValue(-6.917464).description("Latitude"),
        queryParam[Double]("lng").defaultValue(107.619123).description("Longitude"),
        queryParam[Double]("radius").defaultValue(5).description("Radius in meter"),
        queryParam[Int]("maxWidth").defaultValue(200).description("Max width of photo in pixel"),
        queryParam[Int]("maxHeight").defaultValue(200).description("Max height of photo in pixel")
    ))
  get("/photos", operation(photos)){
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

  private val placesDetails =
    (apiOperation[List[String]]("/placesDetails")
      summary "get list of place details from radar search API"
      parameters(
        queryParam[Double]("lat").defaultValue(-6.917464).description("Latitude"),
        queryParam[Double]("lng").defaultValue(107.619123).description("Longitude"),
        queryParam[Double]("radius").defaultValue(25000).description("Radius in meter"),
        queryParam[Int]("maxWidth").defaultValue(400).description("Max width of photo in pixel")
    ))
  get("/placesDetails", operation(placesDetails)){
    val lat = params.get("lat").get.toDouble
    val lng = params.get("lng").get.toDouble
    val radius = params.get("radius").get.toDouble
    val maxWidth = params.get("maxWidth").get.toInt
    val searchResults = GoogleUtil.radarSearch(lat, lng, radius)
    GoogleUtil.getPlacesDetails(searchResults)
  }

  private val photo =
    (apiOperation[List[String]]("/photo")
      summary "get a place photo"
      parameters(
        queryParam[Int]("maxWidth").defaultValue(200).description("Max image width in pixel"),
        queryParam[Int]("maxHeight").defaultValue(200).description("Max image height in pixel"),
        queryParam[String]("photoReference").defaultValue("CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU").description("Reference of photo from place api")
    ))
  get("/photo", operation(photo)){
    val maxWidth = params.get("maxWidth").get.toInt
    val maxHeight = params.get("maxHeight").get.toInt
    val photoReference = params.get("photoReference").get.toString
    val photo = GoogleUtil.getPhoto(maxHeight, maxWidth, photoReference)
    Map("location" -> photo)
  }

  private val placeDetails =
    (apiOperation[List[String]]("/placeDetails")
      summary "get details of a place"
      parameter queryParam[String]("placeId").defaultValue("ChIJN1t_tDeuEmsRUsoyG83frY4").description("Place ID"))
  get("/placeDetails", operation(placeDetails)){
    val placeId = params.get("placeId").get
    GoogleUtil.getPlaceDetails(placeId)
  }
}
