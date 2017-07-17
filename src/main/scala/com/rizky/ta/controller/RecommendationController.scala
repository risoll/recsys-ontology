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

//  private var propagation = Map[String, Map[String, Any]]()
  private var propagation = ListBuffer[Map[String, Any]]()
  private var childBuffer = ListBuffer[Map[String, Any]]()
  private val nodesDown =
  """[
    {
      "name": "Rekreasi",
      "pref": 0.6,
      "conf": 1
    },
    {
      "name": "Alam",
      "pref": 0.8,
      "conf": 1
    }
  ]"""
  private val downwardPropagation =
    (apiOperation[List[String]]("/propagation/downward")
      summary "down propagation, update preference and confidence value of each children nodes"
      parameter bodyParam[String]("nodes").defaultValue(nodesDown).description("The nodes which inherit the children"))
  post("/propagation/downward", operation(downwardPropagation)) {
    val nodes = parsedBody.extract[List[Map[String, Any]]]
    println("nodes", nodes)
//    propagation = Map()
    propagation = ListBuffer()
    childBuffer = nodes.to[ListBuffer]
    bfsPropagation(nodes)
    val askedNodes = RecommendationUtil.filterLeafNodes(OWL_MODEL, propagation.map(_("name").toString).toList)
    Map(
      "data" -> propagation,
      "askedNodes" -> askedNodes.map(node=>{
        Map("name" -> node, "image" -> Classes.getByName(node).get.image)
      })
    )
  }

  def bfsPropagation(nodes: List[Map[String, Any]]): Unit ={
    var newNodes = ListBuffer[Map[String, Any]]()
    nodes.foreach(node=>{
      //dapatkan children dari node yg dipilih
      val children = RecommendationUtil.getChildren(OWL_MODEL, node("name").asInstanceOf[String])
      children.foreach(child=>{
        //cek parents dari tiap child, karena bisa saja satu child punya 2 atau lebih parent
        val parents = RecommendationUtil.getParent(OWL_MODEL, child)
        val newParents = ListBuffer[Map[String, Any]]()
        newParents.append(node)
        parents.foreach(parent => {
          //cek jika child punya lebih dari satu parent
          childBuffer.foreach(child=>{
            if(child("name") == parent){
              //jika parent sudah ada sebelumnya, tidak usah ditambah ke buffer
              if(!newParents.map(_("name").toString).exists(parent.contentEquals))
                newParents.append(child)
            }
          })
        })
        val values = RecommendationUtil.calcValues(newParents.toList)
        val newValues = Map(
          "name" -> child,
          "parents" -> newParents,
          "pref" -> values("pref"),
          "conf" -> values("conf")
        )
        childBuffer.append(newValues)
        newNodes.append(newValues)
      })
    })
    propagation ++= newNodes
    if(newNodes.nonEmpty)
      bfsPropagation(newNodes.toList)
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
