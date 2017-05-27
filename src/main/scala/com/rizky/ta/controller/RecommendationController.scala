package com.rizky.ta.controller

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import com.rizky.ta.model.owl.OwlConst
import com.rizky.ta.util.RecommendationUtil
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

/**
  * Created by solehuddien on 25/05/17.
  */
class RecommendationController(implicit val swagger: Swagger)
  extends ScalatraServlet
    with JacksonJsonSupport
    with CorsSupport
    with SwaggerSupport {

  protected val applicationDescription = "The Recommendation API. It will give the recommendation by querying with Sparql"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  private val OWL_FILE = "attractions.owl"
  private val OWL_MODEL = ModelFactory.createDefaultModel()
  private val inputStream = FileManager.get().open(OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File $OWL_FILE not found")
  }
  OWL_MODEL.read(inputStream, null)


  private val attractions =
    (apiOperation[List[String]]("/attractions")
      summary "get list of attractions which the category is the lowest node (right before leaf) in ontology hierarchy"
      parameter queryParam[String]("category").defaultValue("Edukasi").description("category"))
  get("/attractions", operation(attractions)) {
    RecommendationUtil.getAttractionsByCategory(
      params.get("category").getOrElse(""),
      OWL_MODEL
    )
  }
}
