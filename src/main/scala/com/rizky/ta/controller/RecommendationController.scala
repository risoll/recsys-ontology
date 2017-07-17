package com.rizky.ta.controller

import com.rizky.ta.model.Classes
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import com.rizky.ta.util.RecommendationUtil
import grizzled.slf4j.Logger
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import net.liftweb.json._

import scala.collection.mutable
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
    val resultBuffer = ListBuffer[String]()
    nodes.foreach(node=>{
      RecommendationUtil.getChildren(OWL_MODEL, node).foreach(child=>{
        if(!resultBuffer.exists(child.contentEquals))
          result.append(Map("name" -> child, "image" -> Classes.getByName(child).get.image))
        resultBuffer.append(child)
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
    val parents = RecommendationUtil.getParent(OWL_MODEL, node).to[ListBuffer]
    parents -= "Tempat Wisata"
    parents.foreach(parent=>{
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
    val resultBuffer = ListBuffer[String]()
    nodes.foreach(node=>{
      RecommendationUtil.getParent(OWL_MODEL, node).foreach(parent=>{
        if(!resultBuffer.exists(parent.contentEquals))
          result.append(Map("name" -> parent, "image" -> Classes.getByName(parent).get.image))
        resultBuffer.append(parent)
      })
    })
    result
  }

  private var tmpClass = ListBuffer[Map[String, Any]]()
  private val traverseNodes =
    (apiOperation[List[String]]("/individual/traverse/bulk")
      summary "get a list of traversable class of the current individuals"
      parameter bodyParam[String]("nodes").defaultValue("[\"Kebun Binatang\", \"Kawah Rengganis\", \"Museum Yayasan Pangeran\"]").description("The individual nodes"))
  post("/individual/traverse/bulk", operation(traverseNodes)) {
    val nodes = parsedBody.extract[List[String]].to[ListBuffer]
    val result = ListBuffer[Map[String, Any]]()
    var categories = ListBuffer[String]()
    var temp = Map[String, Any]()
    tmpClass = ListBuffer()
    nodes.foreach(node=>{
      tmpClass = ListBuffer()
      categories = RecommendationUtil.getCategory(OWL_MODEL, node).to[ListBuffer]
      categories -= ("NamedIndividual", "Class")
      traverse(categories.toList)
      temp = Map("name" -> node, "parents" -> tmpClass)
      result.append(temp)
    })
    result
  }

  def traverse(categories: List[String]): Unit ={
    categories.foreach(category=>{
      val _categories = RecommendationUtil.getParent(OWL_MODEL, category)
      if(category != "Tempat Wisata"){
        tmpClass.append(Map(
          "child" -> category,
          "image" -> Classes.getByName(category).get.image,
          "parents" -> _categories
        ))
      }
      traverse(_categories)
    })
  }

  private var propagation = Map[String, Map[String, Any]]()
  private var childBuffer = mutable.HashMap[String, Map[String, Double]]()
  private val nodesDown =
  """{
    "Rekreasi": {
      "pref": 0.6,
      "conf": 1
    },
    "Alam": {
      "pref": 0.8,
      "conf": 1
    }
  }"""
  private val downwardPropagation =
    (apiOperation[List[String]]("/propagation/downward")
      summary "down propagation, update preference and confidence value of each children nodes"
      parameter bodyParam[String]("nodes").defaultValue(nodesDown).description("The nodes which inherit the children"))
  post("/propagation/downward", operation(downwardPropagation)) {
    val nodes = parsedBody.extract[Map[String, Map[String, Double]]]
    propagation = Map()
    childBuffer = mutable.HashMap()
    nodes.map(node => childBuffer.put(node._1, node._2))
    bfsPropagation(nodes)
    propagation
  }

  def bfsPropagation(nodes: Map[String, Map[String, Double]]): Unit ={
    var newNodes = Map[String, Map[String, Double]]()
    nodes.foreach(node=>{
      val children = RecommendationUtil.getChildren(OWL_MODEL, node._1)
      children.foreach(child=>{
        val parents = RecommendationUtil.getParent(OWL_MODEL, child)
        val newParents = mutable.HashMap[String, Map[String, Double]]()
        newParents.put(node._1, node._2)
        parents.foreach(parent => {
          if(childBuffer.keys.exists(parent.contentEquals)){
            newParents.put(parent, childBuffer(parent))
          }
        })
        val values = RecommendationUtil.calcValues(newParents.toMap)
        val newChildren = Map(child -> values)
        childBuffer.put(child, values)
        newNodes ++= newChildren
      })
    })
    propagation ++= newNodes
    if(newNodes.nonEmpty)
      bfsPropagation(newNodes)
  }

  def propagate(nodes: Map[String, Map[String, Any]], parent: String, children: List[String], kind: String): Unit ={
//    children.foreach(child=>{
//      val parents = RecommendationUtil.getParent(OWL_MODEL, child)
//      val fixedAncestors = nodes.filterKeys(parents.contains)
//      val newChildren = RecommendationUtil.getChildren(OWL_MODEL, child)
//      println("received ancestor", child, parents, fixedAncestors)
//
//      var values = RecommendationUtil.calcValues(fixedAncestors)
//      println("values", child, values)
//      if(childBuffer.keys.exists(child.contentEquals)){
//        values = RecommendationUtil.updateValues(childBuffer(child), values)
//        println("updated values", child, values)
//      }
//
//      //newNodeValues adalah hasil propagasi tiap child
//      val newNodeValues = Map(
//        "parents" -> parents,
//        "pref" -> values("pref"),
//        "conf" -> values("conf")
//      )
//      childBuffer.put(child, newNodeValues)
//      propagation ++= Map(child -> newNodeValues)
//      propagate(Map(child -> newNodeValues), child, newChildren, "down")
//    })
  }
}
