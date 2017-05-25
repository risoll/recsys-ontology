package com.rizky.ta.controller

import com.rizky.ta.model.Place
import com.rizky.ta.model.Place.Result
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import com.rizky.ta.model.owl.OwlConst
import com.rizky.ta.util.SparqlUtil
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

import scala.collection.mutable.ListBuffer

/**
  * Created by solehuddien on 25/05/17.
  */
class RecommendationController(implicit val swagger: Swagger)
  extends ScalatraServlet
    with JacksonJsonSupport
    with CorsSupport
    with SwaggerSupport {

  protected val applicationDescription = "The recommendation API. It will give the recommendation by querying with Sparql"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  private val OWL_MODEL = ModelFactory.createDefaultModel()
  private val inputStream = FileManager.get().open(OwlConst.OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File ${OwlConst.OWL_FILE} not found")
  }
  OWL_MODEL.read(inputStream, null)


  val attractions =
    (apiOperation[Unit]("/attractions/category")
      summary "get attractions based on lowest category"
      parameter queryParam[String]("category").description("category which is the lowest node before leaf in ontology hierarchy"))
  get("/attractions/:category", operation(attractions)) {
    SparqlUtil.getAttractionsByCategory(
      params.get("category").getOrElse(""),
      OWL_MODEL
    )
  }
}
