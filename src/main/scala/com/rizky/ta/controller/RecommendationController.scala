package com.rizky.ta.controller

import com.rizky.ta.model.Classes
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import com.rizky.ta.util.RecommendationUtil
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import net.liftweb.json._

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON

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

  private val OWL_FILE = "data/attractions.owl"
  private val OWL_MODEL = ModelFactory.createDefaultModel()
  private val inputStream = FileManager.get().open(OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File $OWL_FILE not found")
  }
  OWL_MODEL.read(inputStream, null)


  private val individualCategory =
    (apiOperation[List[String]]("/individual/category")
      summary "get list of individuals within certain category"
      parameter queryParam[String]("category").defaultValue("Edukasi").description("The lowest class right before individual"))
  get("/individual/category", operation(individualCategory)) {
    RecommendationUtil.getIndividualByCategory(
      params.get("category").getOrElse(""),
      OWL_MODEL
    )
  }

  private val individuals =
    (apiOperation[List[String]]("/individual/bulk")
      summary "get a list of all available individuals")
  get("/individual/bulk", operation(individuals)) {
    RecommendationUtil.getAllIndividual(OWL_MODEL)
  }

  private val children =
    (apiOperation[List[String]]("/class/children")
      summary "get a list of children from the current parent node"
      parameter queryParam[String]("node").defaultValue("Tempat Wisata").description("The parent node which inherit the children"))
  get("/class/children", operation(children)) {
    val node = params("node")
    val result = ListBuffer[Map[String, String]]()
    RecommendationUtil.getChildren(OWL_MODEL, node).foreach(child=>{
      result.append(Map("name" -> child, "image" -> Classes.getByName(child).get.image))
    })
    result
  }

  private val bulkChildren =
    (apiOperation[List[String]]("/class/bulk/children")
      summary "get a list of children from multiple parent node at once"
      parameter bodyParam[String]("nodes").defaultValue("[\"Alam\", \"Kuliner\"]").description("The parent nodes which inherit the children"))
  post("/class/bulk/children", operation(bulkChildren)) {
    val nodes = parsedBody.extract[List[String]]
    val result = ListBuffer[Map[String, String]]()
    nodes.foreach(node=>{
      RecommendationUtil.getChildren(OWL_MODEL, node).foreach(child=>{
        result.append(Map("name" -> child, "image" -> Classes.getByName(child).get.image))
      })
    })
    result
  }

  private val parents =
    (apiOperation[List[String]]("/class/parents")
      summary "get a list of parent from the current child node"
      parameter queryParam[String]("node").defaultValue("Edukasi").description("The children node"))
  get("/class/parents", operation(parents)) {
    val node = params("node")
    val result = ListBuffer[Map[String, String]]()
    RecommendationUtil.getParent(OWL_MODEL, node).foreach(parent=>{
      result.append(Map("name" -> parent, "image" -> Classes.getByName(parent).get.image))
    })
    result
  }

  private val bulkParents =
    (apiOperation[List[String]]("/class/bulk/parents")
      summary "get a list of parents from multiple child node at once"
      parameter bodyParam[String]("nodes").defaultValue("[\"Pemandangan Alam\", \"Edukasi\"]").description("The children nodes"))
  post("/class/bulk/parents", operation(bulkParents)) {
    val nodes = parsedBody.extract[List[String]]
    val result = ListBuffer[Map[String, String]]()
    nodes.foreach(node=>{
      RecommendationUtil.getParent(OWL_MODEL, node).foreach(parent=>{
        result.append(Map("name" -> parent, "image" -> Classes.getByName(parent).get.image))
      })
    })
    result
  }
}
