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
        if(!resultBuffer.exists(child.contains))
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
        if(resultBuffer.exists(_ != parent))
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
    var result = ListBuffer[Map[String, Any]]()
    var seqs = ListBuffer[String]()
    var categories = ListBuffer[String]()
    var temp = Map[String, Any]()
    var tmpResult = Map[String, Any]()
    var i = 0
    tmpClass = ListBuffer()
    var parents = List[String]()
    nodes.foreach(node=>{
      tmpClass = ListBuffer()
      seqs = ListBuffer()
      categories = RecommendationUtil.getCategory(OWL_MODEL, node).to[ListBuffer]
      categories -= ("NamedIndividual", "Class")
      println("node", node, categories)
      traverse(categories.toList)
      tmpClass.foreach(tmp => {
        println("tmp", tmp)
      })
//      parents = tmpClass.head("parents").asInstanceOf[List[String]]
//      parents.foreach(parent=>{
//        seqs.append(s"$parent, ${createClassTree(node, parent, parents.count(_ == parent))}")
//      })
//      temp = Map("name" -> node, "sequence" -> seqs, "parents" -> tmpClass)
      temp = Map("name" -> node, "parents" -> tmpClass)
      result.append(temp)
    })
    result
  }

//  def createClassTree(node: String, _parent: String, idx: Int): String ={
//    var seqString = node
//    val result = Map[String, Any]()
//    var tmpResult = mutable.HashMap[String, Any]()
//    var i = 1
//    var drop = 0
//    tmpClass.foreach(tmp=>{
//      if(tmp("child") == _parent){
//        if(i == idx)
//          drop = i
//        i += 1
//      }
//    })
//    val _tmpClass = tmpClass.drop(drop)
//    _tmpClass.foreach(tmp=>{
//      seqString += s", ${tmp("child")}"
//      tmp("parents").asInstanceOf[List[String]].foreach(parent=>{
//        seqString += s", $parent"
//      })
//    })
//    seqString
//  }

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
}
