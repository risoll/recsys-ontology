package com.rizky.ta.controller

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.rizky.ta.model._
import org.json4s.jackson.Serialization
import com.rizky.ta.util.{PlacesUtil, RecommendationUtil}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
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

  private val OWL_FILE = "data/attractions.owl"
  private val OWL_MODEL = ModelFactory.createDefaultModel()
  private val inputStream = FileManager.get().open(OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File $OWL_FILE not found")
  }
  OWL_MODEL.read(inputStream, null)

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

  private val placesCategories =
    (apiOperation[Unit]("/bulk/categories")
      summary "get list of places with given classes"
      parameter bodyParam[String]("nodes").defaultValue("[\"Alam\", \"Edukasi\"]").description("The leaf nodes which inherit the individuals"))
  post("/bulk/categories", operation(placesCategories)) {
    val nodes = parsedBody.extract[List[String]]
    val result = ListBuffer[Option[Place]]()
    val resultBuffer = ListBuffer[String]()
    nodes.foreach(node=>{
      RecommendationUtil.getIndividualByCategory(node, OWL_MODEL).foreach(individual=>{
        if(!resultBuffer.exists(individual.contains))
          result.append(Place.getByName(individual))
        resultBuffer.append(individual)
      })
    })
    result

  }



}
