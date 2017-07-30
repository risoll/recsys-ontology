package com.rizky.ta.controller

import com.rizky.ta.model.{Classes, Place}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import com.rizky.ta.util.{CommonUtil, GoogleUtil, RecommendationUtil}

import util.control.Breaks._
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
    RecommendationUtil.getChildren(OWL_MODEL, node).foreach(child => {
      val cls = Classes.getByName(child).get
      result.append(Map("name" -> child, "image" -> cls.image, "description" -> cls.description, "root" -> cls.root))
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
    nodes.foreach(node => {
      RecommendationUtil.getChildren(OWL_MODEL, node).foreach(child => {
        if (!resultBuffer.exists(child.contentEquals)) {
          val cls = Classes.getByName(child).get
          result.append(Map("name" -> child, "image" -> cls.image, "description" -> cls.description, "root" -> cls.root))
        }
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
    parents.foreach(parent => {
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
    nodes.foreach(node => {
      RecommendationUtil.getParent(OWL_MODEL, node).foreach(parent => {
        if (!resultBuffer.exists(parent.contentEquals))
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
    nodes.foreach(node => {
      tmpClass = ListBuffer()
      categories = RecommendationUtil.getCategory(OWL_MODEL, node).to[ListBuffer]
      categories -= ("NamedIndividual", "Class")
      traverse(categories.toList)
      temp = Map("name" -> node, "parents" -> tmpClass)
      result.append(temp)
    })
    result
  }

  def traverse(categories: List[String]): Unit = {
    categories.foreach(category => {
      val _categories = RecommendationUtil.getParent(OWL_MODEL, category)
      if (category != "Tempat Wisata") {
        tmpClass.append(Map(
          "child" -> category,
          "image" -> Classes.getByName(category).get.image,
          "parents" -> _categories
        ))
      }
      traverse(_categories)
    })
  }

  private var downPropResult = ListBuffer[Map[String, Any]]()
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
    downPropResult = ListBuffer()
    childBuffer = nodes.to[ListBuffer]
    downwardPropagation(nodes)
    val askedNodes = RecommendationUtil.filterLeafNodes(OWL_MODEL, downPropResult.map(_ ("name").toString).toList)
    Map(
      "data" -> downPropResult,
      "askedNodes" -> askedNodes.map(node => {
        val cls = Classes.getByName(node).get
        Map("name" -> node, "image" -> cls.image, "description" -> cls.description, "root" -> cls.root)
      })
    )
  }

  private val downwardPropagationV2 =
    (apiOperation[List[String]]("/propagation/downwardv2")
      summary "down propagation, update preference and confidence value of each children nodes"
      parameter bodyParam[String]("nodes").defaultValue(nodesDown).description("The nodes which inherit the children"))
  post("/propagation/downwardv2", operation(downwardPropagationV2)) {
    val nodes = parsedBody.extract[List[Map[String, Any]]]
    println("nodes", nodes)
    downPropResult = ListBuffer()
    childBuffer = nodes.to[ListBuffer]
    val askedNodes = downwardPropagationV2(nodes)
    Map(
      "data" -> downPropResult,
      "askedNodes" -> askedNodes.map(node => {
        val nodeName = node("name").toString
        val cls = Classes.getByName(nodeName).get
        Map("name" -> nodeName, "image" -> cls.image, "description" -> cls.description, "root" -> cls.root)
      })
    )
  }

  private var upPropResult = mutable.HashMap[String, Map[String, Any]]()
  private var parentBuffer = mutable.HashMap[String, Map[String, Double]]()
  //tmpInverse digunakan untuk menyimpan inverse parents dari parent, inverse parent adalah child node pada ontology dengan perspektif terbalik
  private var tmpInverse = mutable.HashMap[String, Map[String, Map[String, Double]]]()
  private var tmpContent = Map[String, Map[String, Double]]()
  private var tmpLevel = 0.0

  private val nodesUp =
    """{
       "distance": 55,
       "userLocation": {"lat":-6.9780761,"lng":107.6319546},
       "assigned":[
            {
              "name": "Kebun Binatang",
              "pref": 0.8,
              "conf": 1
            }
       ],
       "old":[
     {
       "name": "Spa",
       "activation": 0.294,
       "parents": [
         {
           "name": "Rekreasi",
           "pref": 0.6,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.6
     },
     {
       "name": "Keluarga",
       "activation": 0.294,
       "parents": [
         {
           "name": "Rekreasi",
           "pref": 0.6,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.6
     },
     {
       "name": "Hiburan Malam",
       "activation": 0.294,
       "parents": [
         {
           "name": "Rekreasi",
           "pref": 0.6,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.6
     },
     {
       "name": "Belanja",
       "activation": 0.294,
       "parents": [
         {
           "name": "Rekreasi",
           "pref": 0.6,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.6
     },
     {
       "name": "Taman",
       "activation": 0.392,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.8
     },
     {
       "name": "Pemandian Air Panas",
       "activation": 0.56203,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         },
         {
           "name": "Spa",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.64,
       "pref": 0.71364
     },
     {
       "name": "Pemandangan",
       "activation": 0.392,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.8
     },
     {
       "name": "Pegunungan",
       "activation": 0.392,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.8
     },
     {
       "name": "Museum Alam",
       "activation": 0.392,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.8
     },
     {
       "name": "Kebun Binatang",
       "activation": 0.56203,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         },
         {
           "name": "Keluarga",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.64,
       "pref": 0.71364
     },
     {
       "name": "Camping",
       "activation": 0.68404,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         },
         {
           "name": "Pegunungan",
           "activation": 0.392,
           "parents": [
             {
               "name": "Alam",
               "pref": 0.8,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.8
         }
       ],
       "conf": 0.64,
       "pref": 0.8
     },
     {
       "name": "Cagar Alam",
       "activation": 0.392,
       "parents": [
         {
           "name": "Alam",
           "pref": 0.8,
           "conf": 1
         }
       ],
       "conf": 0.76,
       "pref": 0.8
     },
     {
       "name": "Taman Bermain",
       "activation": 0.43806,
       "parents": [
         {
           "name": "Keluarga",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.52,
       "pref": 0.6
     },
     {
       "name": "Edukasi",
       "activation": 0.43806,
       "parents": [
         {
           "name": "Keluarga",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.52,
       "pref": 0.6
     },
     {
       "name": "Shopping Mall",
       "activation": 0.43806,
       "parents": [
         {
           "name": "Belanja",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.52,
       "pref": 0.6
     },
     {
       "name": "Oleh-oleh",
       "activation": 0.43806,
       "parents": [
         {
           "name": "Belanja",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.52,
       "pref": 0.6
     },
     {
       "name": "Fashion",
       "activation": 0.43806,
       "parents": [
         {
           "name": "Belanja",
           "activation": 0.294,
           "parents": [
             {
               "name": "Rekreasi",
               "pref": 0.6,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.6
         }
       ],
       "conf": 0.52,
       "pref": 0.6
     },
     {
       "name": "Pemandangan Kota",
       "activation": 0.58408,
       "parents": [
         {
           "name": "Pemandangan",
           "activation": 0.392,
           "parents": [
             {
               "name": "Alam",
               "pref": 0.8,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.8
         }
       ],
       "conf": 0.52,
       "pref": 0.8
     },
     {
       "name": "Pemandangan Alam",
       "activation": 0.58408,
       "parents": [
         {
           "name": "Pemandangan",
           "activation": 0.392,
           "parents": [
             {
               "name": "Alam",
               "pref": 0.8,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.8
         }
       ],
       "conf": 0.52,
       "pref": 0.8
     },
     {
       "name": "Hiking",
       "activation": 0.58408,
       "parents": [
         {
           "name": "Pegunungan",
           "activation": 0.392,
           "parents": [
             {
               "name": "Alam",
               "pref": 0.8,
               "conf": 1
             }
           ],
           "conf": 0.76,
           "pref": 0.8
         }
       ],
       "conf": 0.52,
       "pref": 0.8
     },
     {
       "name": "Waterpark",
       "activation": 0.65271,
       "parents": [
         {
           "name": "Taman Bermain",
           "activation": 0.43806,
           "parents": [
             {
               "name": "Keluarga",
               "activation": 0.294,
               "parents": [
                 {
                   "name": "Rekreasi",
                   "pref": 0.6,
                   "conf": 1
                 }
               ],
               "conf": 0.76,
               "pref": 0.6
             }
           ],
           "conf": 0.52,
           "pref": 0.6
         }
       ],
       "conf": 0.28,
       "pref": 0.6
     },
     {
       "name": "ATV",
       "activation": 0.65271,
       "parents": [
         {
           "name": "Taman Bermain",
           "activation": 0.43806,
           "parents": [
             {
               "name": "Keluarga",
               "activation": 0.294,
               "parents": [
                 {
                   "name": "Rekreasi",
                   "pref": 0.6,
                   "conf": 1
                 }
               ],
               "conf": 0.76,
               "pref": 0.6
             }
           ],
           "conf": 0.52,
           "pref": 0.6
         }
       ],
       "conf": 0.28,
       "pref": 0.6
     },
     {
       "name": "Museum",
       "activation": 0.65271,
       "parents": [
         {
           "name": "Edukasi",
           "activation": 0.43806,
           "parents": [
             {
               "name": "Keluarga",
               "activation": 0.294,
               "parents": [
                 {
                   "name": "Rekreasi",
                   "pref": 0.6,
                   "conf": 1
                 }
               ],
               "conf": 0.76,
               "pref": 0.6
             }
           ],
           "conf": 0.52,
           "pref": 0.6
         }
       ],
       "conf": 0.28,
       "pref": 0.6
     },
     {
       "name": "Museum Sejarah",
       "activation": 0.97254,
       "parents": [
         {
           "name": "Museum",
           "activation": 0.65271,
           "parents": [
             {
               "name": "Edukasi",
               "activation": 0.43806,
               "parents": [
                 {
                   "name": "Keluarga",
                   "activation": 0.294,
                   "parents": [
                     {
                       "name": "Rekreasi",
                       "pref": 0.6,
                       "conf": 1
                     }
                   ],
                   "conf": 0.76,
                   "pref": 0.6
                 }
               ],
               "conf": 0.52,
               "pref": 0.6
             }
           ],
           "conf": 0.28,
           "pref": 0.6
         }
       ],
       "conf": 0.04,
       "pref": 0.6
     },
     {
       "name": "Museum Budaya",
       "activation": 0.97254,
       "parents": [
         {
           "name": "Museum",
           "activation": 0.65271,
           "parents": [
             {
               "name": "Edukasi",
               "activation": 0.43806,
               "parents": [
                 {
                   "name": "Keluarga",
                   "activation": 0.294,
                   "parents": [
                     {
                       "name": "Rekreasi",
                       "pref": 0.6,
                       "conf": 1
                     }
                   ],
                   "conf": 0.76,
                   "pref": 0.6
                 }
               ],
               "conf": 0.52,
               "pref": 0.6
             }
           ],
           "conf": 0.28,
           "pref": 0.6
         }
       ],
       "conf": 0.04,
       "pref": 0.6
     }
   ]}"""
  private val upwardPropagationV2 =
    (apiOperation[List[String]]("/propagation/upwardv2")
      summary "upward propagation, update preference and confidence value of each parent nodes"
      parameter bodyParam[String]("nodes").defaultValue(nodesUp).description("The children nodes"))
  post("/propagation/upwardv2", operation(upwardPropagationV2)) {
    val nodes = parsedBody.extract[Map[String, Any]]
    val old = nodes("old").asInstanceOf[List[Map[String, Any]]]
    val assigned = nodes("assigned").asInstanceOf[List[Map[String, Any]]]
    val location = nodes("userLocation").asInstanceOf[Map[String, Double]]
    val distance = nodes("distance").toString.toDouble
    println("old", old)
    println("assigned", assigned)

    val updated = ListBuffer[Map[String, Any]]()
    upPropResult = mutable.HashMap()
    parentBuffer = mutable.HashMap()
    tmpInverse = mutable.HashMap()
    tmpContent = Map()
    tmpLevel = 0.0
    //update values
    assigned.foreach(a => {
      //ambil old value
      val matched = old.filter(_ ("name") == a("name")).head
      //update preference dan confidence
      updated.append(Map(
        "name" -> a("name"),
        "pref" -> RecommendationUtil.updatePreference(matched("pref").toString.toDouble, a("pref").toString.toDouble),
        "conf" -> RecommendationUtil.updateConfidence(matched("conf").toString.toDouble, a("conf").toString.toDouble)
      ))
    })

    //hasil update value dari nilai yang diassign oleh user, dimasukkan ke map lama (hasil propagasi kebawah)
    val updatedOld = ListBuffer[Map[String, Any]]()
    old.foreach(o => {
      var value = o
      if (updated.map(_ ("name").toString).exists(o("name").toString.contentEquals)) {
        value = value ++ updated.filter(_ ("name").toString == o("name")).head
      }
      updatedOld.append(value)
    })

    //mulai propagasi, hanya restrukturisasi hierarki agar mudah diolah
    upwardPropagation(updatedOld.toList, 0)

    //update value dengan agregasi dan update propagasi keatas, lalu filter hanya root node
    val roots = RecommendationUtil.getChildren(OWL_MODEL, "tempat wisata")
    val updatedNew = aggregateUpdateValues(tmpLevel, tmpInverse).filter(x => roots.exists(x._1.contentEquals))

    //propagasi kebawah lagi dengan nilai root node yang baru
    val listUpdateNew = RecommendationUtil.mapToList(updatedNew)
    downPropResult = ListBuffer()
    childBuffer = listUpdateNew.to[ListBuffer]
    downwardPropagation(listUpdateNew, true)

    //  filter hasil propagasi terakhir dengan threshold tertentu
    val recommendedClasses = recommendClasses(downPropResult)
    val recommendedPlaces = ListBuffer[Place]()
    recommendedClasses.foreach(recomm => {
      val category = recomm("name").toString
      println("CATEGORY", category)
      val places = RecommendationUtil.getIndividualByCategory(category, OWL_MODEL)
      places.foreach(place => {
        val p = Place.getByName(place)
        p match {
          case Some(value) =>
            recommendedPlaces.append(value)
          case None =>
        }
      })
    })

    println("DISTANCES", recommendedPlaces)
    val mergedPlaces = ListBuffer[Map[String, Any]]()
    if (recommendedPlaces.nonEmpty) {
      val placesDistance = getPlacesDistance(location, distance, recommendedPlaces.toList)
      placesDistance.foreach(place => {
        val matched = CommonUtil.getCCParams(recommendedPlaces.filter(_.name == place("name")).head)
        mergedPlaces.append(place ++ matched)
      })
    }

    val askedNodes = assigned.map(node=>{
      val nodeName = node("name").toString
      val children = RecommendationUtil.getChildren(OWL_MODEL, nodeName)
      children.map(child => {
        val cls = Classes.getByName(child).get
        Map("name" -> cls.name, "image" -> cls.image, "description" -> cls.description, "root" -> cls.root)
      })
    }).reduceLeft((a, b) => a ++ b)

    Map(
      "places"-> mergedPlaces,
      "old" -> downPropResult,
      "askedNodes" -> askedNodes
    )
  }

  private val upwardPropagation =
    (apiOperation[List[String]]("/propagation/upward")
      summary "upward propagation, update preference and confidence value of each parent nodes"
      parameter bodyParam[String]("nodes").defaultValue(nodesUp).description("The children nodes"))
  post("/propagation/upward", operation(upwardPropagation)) {
    val nodes = parsedBody.extract[Map[String, Any]]
    val old = nodes("old").asInstanceOf[List[Map[String, Any]]]
    val assigned = nodes("assigned").asInstanceOf[List[Map[String, Any]]]
    val location = nodes("userLocation").asInstanceOf[Map[String, Double]]
    val distance = nodes("distance").toString.toDouble
    println("old", old)
    println("assigned", assigned)

    val updated = ListBuffer[Map[String, Any]]()
    upPropResult = mutable.HashMap()
    parentBuffer = mutable.HashMap()
    tmpInverse = mutable.HashMap()
    tmpContent = Map()
    tmpLevel = 0.0
    //update values
    assigned.foreach(a => {
      //ambil old value
      val matched = old.filter(_ ("name") == a("name")).head
      //update preference dan confidence
      updated.append(Map(
        "name" -> a("name"),
        "pref" -> RecommendationUtil.updatePreference(matched("pref").toString.toDouble, a("pref").toString.toDouble),
        "conf" -> RecommendationUtil.updateConfidence(matched("conf").toString.toDouble, a("conf").toString.toDouble)
      ))
    })

    //hasil update value dari nilai yang diassign oleh user, dimasukkan ke map lama (hasil propagasi kebawah)
    val updatedOld = ListBuffer[Map[String, Any]]()
    old.foreach(o => {
      var value = o
      if (updated.map(_ ("name").toString).exists(o("name").toString.contentEquals)) {
        value = value ++ updated.filter(_ ("name").toString == o("name")).head
      }
      updatedOld.append(value)
    })

    //mulai propagasi, hanya restrukturisasi hierarki agar mudah diolah
    upwardPropagation(updatedOld.toList, 0)

    //update value dengan agregasi dan update propagasi keatas, lalu filter hanya root node
    val roots = RecommendationUtil.getChildren(OWL_MODEL, "tempat wisata")
    val updatedNew = aggregateUpdateValues(tmpLevel, tmpInverse).filter(x => roots.exists(x._1.contentEquals))

    //propagasi kebawah lagi dengan nilai root node yang baru
    val listUpdateNew = RecommendationUtil.mapToList(updatedNew)
    downPropResult = ListBuffer()
    childBuffer = listUpdateNew.to[ListBuffer]
    downwardPropagation(listUpdateNew, true)

    //  filter hasil propagasi terakhir dengan threshold tertentu
    val recommendedClasses = recommendClasses(downPropResult)
    val recommendedPlaces = ListBuffer[Place]()
    recommendedClasses.foreach(recomm => {
      val category = recomm("name").toString
      println("CATEGORY", category)
      val places = RecommendationUtil.getIndividualByCategory(category, OWL_MODEL)
      places.foreach(place => {
        val p = Place.getByName(place)
        p match {
          case Some(value) =>
            recommendedPlaces.append(value)
          case None =>
        }
      })
    })

    println("DISTANCES", recommendedPlaces)
    val mergedPlaces = ListBuffer[Map[String, Any]]()
    if (recommendedPlaces.nonEmpty) {
      val placesDistance = getPlacesDistance(location, distance, recommendedPlaces.toList)
      placesDistance.foreach(place => {
        val matched = CommonUtil.getCCParams(recommendedPlaces.filter(_.name == place("name")).head)
        mergedPlaces.append(place ++ matched)
      })
    }
    mergedPlaces
    //    downPropResult

  }

  def getPlacesDistance(origins: Map[String, Double], distance: Double, places: List[Place]): List[Map[String, Any]] = {
    val newPlaces = ListBuffer[Map[String, Any]]()
    places.foreach(place => {
      println(place)
      if (!newPlaces.map(_ ("name").toString).exists(place.name.contentEquals))
        newPlaces.append(Map(
          "name" -> place.name,
          "lat" -> place.lat,
          "lng" -> place.lng
        ))
    })
    GoogleUtil.distanceMatrix(origins, newPlaces.toList).filter(_ ("distance").asInstanceOf[Map[String, Any]]("value").toString.toDouble <= distance * 1000)
  }

  def recommendClasses(nodes: ListBuffer[Map[String, Any]]): List[Map[String, Any]] = {
    val recommended = ListBuffer[Map[String, Any]]()
    val decay = 0.8
    val fp = nodes.map(_ ("pref").toString.toDouble).sum / nodes.size * decay // threshold untuk preference value
    val fc = nodes.map(_ ("conf").toString.toDouble).sum / nodes.size * decay //threshold untuk confidence value
    val fa = nodes.map(_ ("activation").toString.toDouble).sum / nodes.size * decay //threshold untuk activation value

    println("FP", fp, "FC", fc, "FA", fa)

    var nodeToAppend = Map[String, Any]()
    var passed = false

    nodes.foreach(node => {
      passed = false
      breakable {
        val pref = node("pref").toString.toDouble
        val conf = node("conf").toString.toDouble
        val activation = node("activation").toString.toDouble

        nodeToAppend = Map(
          "name" -> node("name"),
          "pref" -> pref,
          "conf" -> conf,
          "activation" -> activation
        )

        //        cek confidence,preference dan activation value > threshold
        //        if(conf > fc){
        //          passed = true
        //          break
        //        }
        //
        //        if(pref > fp){
        //          if(activation > fa){
        //            passed = true
        //            break
        //          }
        //        }
        if (conf > fc) {
          if (pref > fp) {
            if (activation > fa) {
              passed = true
              break
            }
          }
        }

        //cek confidence, preference dan activation value tiap parent
        val parents = node("parents").asInstanceOf[ListBuffer[Map[String, Any]]]
        parents.foreach(parent => {
          parent.get("activation") match {
            case Some(value) =>
              val parentActivation = value.toString.toDouble
              val parentConf = parent("conf").toString.toDouble
              val parentPref = parent("pref").toString.toDouble
              //              if(parentConf > fc){
              //                passed = true
              //                break
              //              }
              //              if (parentPref > fp) {
              //                if(parentActivation > fa){
              //                  passed = true
              //                  break
              //                }
              //              }
              if (parentConf > fc) {
                if (parentPref > fp) {
                  if (parentActivation > fa) {
                    passed = true
                    break
                  }
                }
              }
            case None =>
          }
        })
      }
      if (passed)
        recommended.append(nodeToAppend)
    })
    println("RECOMMENDED SIZE", recommended.size)
    println("NODES SIZE", nodes.size)
    recommended.toList
  }

  def aggregateUpdateValues(tmpLevel: Double, tmpInverse: mutable.HashMap[String, Map[String, Map[String, Double]]]): Map[String, Map[String, Double]] = {
    var tmpUpdated = Map[String, Map[String, Double]]()
    for (level <- 0 to tmpLevel.toInt) {
      //filter node per level
      val filteredInv = tmpInverse.filter(_._2("metadata")("level") == level)
      filteredInv.foreach(f => {
        //hapus key yang tidak diperlukan
        var truncInv = f._2 - "values" - "metadata"

        //update value dari nilai node sebelumnya yang sudah di aggregasi dan update
        val updatedTrunc = mutable.HashMap[String, Map[String, Double]]()
        truncInv.foreach(tr => {
          if (tmpUpdated.keys.exists(tr._1.contentEquals)) {
            updatedTrunc.put(tr._1, tmpUpdated(tr._1))
          }
        })
        truncInv ++= tmpUpdated
        //hitung nilai agregasi inverse parent dan update nilai tersebut
        val aggs = RecommendationUtil.calcValues(RecommendationUtil.mapToList(truncInv), true)
        val upds = RecommendationUtil.updateFromAgg(f._2("values")("pref"), aggs("pref"), f._2("values")("conf"), aggs("conf"))

        //masukkan value ke temporary variable untuk ditambahkan ke pengecekan level selanjutnya
        tmpUpdated ++= Map(f._1 -> Map("pref" -> upds("pref"), "conf" -> upds("conf")))
      })

    }
    tmpUpdated
  }

  def upwardPropagation(nodes: List[Map[String, Any]], level: Double): Unit = {
    //iterasi per level
    nodes.foreach(node => {
      val nodeName = node("name").toString
      val nodePref = node("pref").toString.toDouble
      val nodeConf = node("conf").toString.toDouble
      //cek apakah node punya key parent
      if (node.keys.exists("parents".contentEquals)) {
        val parents = node("parents").asInstanceOf[List[Map[String, Any]]]
        parents.foreach(parent => {
          val parentName = parent("name").toString

          //untuk tiap parent, simpan inverse parentnya (childnya)
          var content = Map(nodeName -> Map("pref" -> nodePref, "conf" -> nodeConf),
            "metadata" -> Map("level" -> level),
            "values" -> Map(
              "pref" -> parent("pref").toString.toDouble,
              "conf" -> parent("conf").toString.toDouble
            )
          )
          if (tmpInverse.keys.exists(parentName.contentEquals)) {
            //tambahkan inverse node
            var contentOrg = tmpInverse(parentName)
            contentOrg ++= content
            content ++= contentOrg
          }
          tmpInverse.put(parentName, content)
        })

        //telusuri ontology dengan bfs disertai level selanjutnya
        tmpLevel = level
        val newLevel = level + 1
        upwardPropagation(parents, newLevel)
      }
    })
  }

  def downwardPropagationV2(nodes: List[Map[String, Any]], fromAgg: Boolean = false): List[Map[String, Any]] = {
    var newNodes = ListBuffer[Map[String, Any]]()
    nodes.foreach(node => {
      //dapatkan children dari node yg dipilih
      val children = RecommendationUtil.getChildren(OWL_MODEL, node("name").asInstanceOf[String])
      children.foreach(child => {
        //cek parents dari tiap child, karena bisa saja satu child punya 2 atau lebih parent
        val parents = RecommendationUtil.getParent(OWL_MODEL, child)
        var currentActivation = 0.0

        val newParents = ListBuffer[Map[String, Any]]()
        newParents.append(node)
        parents.foreach(parent => {
          //cek jika child punya lebih dari satu parent
          childBuffer.foreach(child => {
            if (child("name") == parent) {
              //jika parent sudah ada sebelumnya, tidak usah ditambah ke buffer
              if (!newParents.map(_ ("name").toString).exists(parent.contentEquals))
                newParents.append(child)

              //set previous activation ke current activation, hal ini seharusnya dilakukan segera setelah spreading
              child.get("activation") match {
                case Some(value) =>
                  currentActivation = value.toString.toDouble
                case None =>
              }
            }
          })
        })
        val values = RecommendationUtil.calcValues(newParents.toList, fromAgg)

        //buat activation level
        println("NEIGHBOR", child)
        val activation = RecommendationUtil.getActivation(newParents.toList, currentActivation)

        //cek jika node sudah ditraverse sebelumnya, jika iya tidak usah dimasukkan lagi
        if (!childBuffer.map(_ ("name").toString).exists(child.contentEquals)) {
          val newValues = Map(
            "name" -> child,
            "activation" -> activation,
            "pref" -> values("pref"),
            "conf" -> values("conf"),
            "parents" -> newParents
          )
          childBuffer.append(newValues)
          newNodes.append(newValues)
        }
      })
    })
    downPropResult ++= newNodes
    newNodes.toList

  }

  def downwardPropagation(nodes: List[Map[String, Any]], fromAgg: Boolean = false): Unit = {
    var newNodes = ListBuffer[Map[String, Any]]()
    nodes.foreach(node => {
      //dapatkan children dari node yg dipilih
      val children = RecommendationUtil.getChildren(OWL_MODEL, node("name").asInstanceOf[String])
      children.foreach(child => {
        //cek parents dari tiap child, karena bisa saja satu child punya 2 atau lebih parent
        val parents = RecommendationUtil.getParent(OWL_MODEL, child)
        var currentActivation = 0.0

        val newParents = ListBuffer[Map[String, Any]]()
        newParents.append(node)
        parents.foreach(parent => {
          //cek jika child punya lebih dari satu parent
          childBuffer.foreach(child => {
            if (child("name") == parent) {
              //jika parent sudah ada sebelumnya, tidak usah ditambah ke buffer
              if (!newParents.map(_ ("name").toString).exists(parent.contentEquals))
                newParents.append(child)

              //set previous activation ke current activation, hal ini seharusnya dilakukan segera setelah spreading
              child.get("activation") match {
                case Some(value) =>
                  currentActivation = value.toString.toDouble
                case None =>
              }
            }
          })
        })
        val values = RecommendationUtil.calcValues(newParents.toList, fromAgg)

        //buat activation level
        println("NEIGHBOR", child)
        val activation = RecommendationUtil.getActivation(newParents.toList, currentActivation)

        //cek jika node sudah ditraverse sebelumnya, jika iya tidak usah dimasukkan lagi
        if (!childBuffer.map(_ ("name").toString).exists(child.contentEquals)) {
          val newValues = Map(
            "name" -> child,
            "activation" -> activation,
            "pref" -> values("pref"),
            "conf" -> values("conf"),
            "parents" -> newParents
          )
          childBuffer.append(newValues)
          newNodes.append(newValues)
        }
      })
    })
    downPropResult ++= newNodes
    if (newNodes.nonEmpty)
      downwardPropagation(newNodes.toList)
  }
}