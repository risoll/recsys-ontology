package com.rizky.ta.controller

import com.rizky.ta.model.Feedback
import com.rizky.ta.util.{CommonUtil, RecommendationUtil}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

/**
  * Created by solehuddien on 02/06/17.
  */
class FeedbackController(implicit val swagger: Swagger)
  extends ScalatraServlet
    with JacksonJsonSupport
    with CorsSupport
    with SwaggerSupport {
  protected val applicationDescription = "The Feedback API. It manages the user's feedback after using the app"
  protected implicit val jsonFormats: Formats = DefaultFormats

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }


  private val feedbacks =
    (apiOperation[Unit]("/bulk")
      summary "get list of all feedbacks")
  get("/bulk", operation(feedbacks)) {
    Feedback.list()
  }

  private val addFeedback =
    (
      apiOperation[Unit]("/add")
        summary "add feedback"
        parameter bodyParam[Feedback]("feedback").description("New feedback definition").required)
  post("/add", operation(addFeedback)) {
    val feedback = parsedBody.extract[Feedback]
    Feedback.create(
      feedback.user_agent, feedback.platform, feedback.ip, feedback.city,
      feedback.name, feedback.gender, feedback.age, feedback.rating,
      feedback.eou, feedback.eou2, feedback.inf,
      feedback.etu, feedback.etu2, feedback.pe, feedback.prq, feedback.prq2,
      feedback.tr, feedback.tr2, feedback.mode, feedback.time, feedback.profession
    )
  }

  private val tResults =
    (
      apiOperation[Unit]("/results")
        summary "get results")
  get("/t-results", operation(tResults)) {
    val feedbacks = Feedback.list()
    val feedbacksMap = feedbacks.map(feedback => CommonUtil.getCCParams(feedback))
    val size = feedbacks.size.toDouble
    val keys = List("prq", "prq2", "pe", "tr", "tr2", "inf", "etu", "etu2", "eou", "eou2")
    val keysTwo = List("prq", "tr", "etu", "eou")
    val innerKeys = List("mean", "t", "df", "p")
    var results = Map[String, Map[String, Map[String, Double]]]()
    var result = Map[String, Double]()
    var buffer = Map[String, Double]()
    keys.foreach(key=>{
      result = Map()
      innerKeys.foreach(innerKey=>{
        println("INNER KEY", innerKey)
        if(innerKey == "mean"){
          for(i <- 1 to 2){
            val keyName = s"${key}Model${i}Count"
            val count = feedbacksMap.count(feedback => feedback(key) == 1 && feedback("mode") == i)
            println("KEY", key)
            if(!buffer.keys .exists(key.contains)){
//            if(i == 1){
              buffer ++= Map(keyName -> count.toDouble)
              result ++= Map(keyName -> count.toDouble)
              println("result1", result)
            }
            else{
              val countBefore = buffer(keyName)
              val keyBefore = key.dropRight(1)
              buffer ++= Map(keyBefore -> (count + countBefore))
              result ++= Map(keyBefore -> (count + countBefore))
              println("result2", result)
            }
          }
        }
      })
      println("result", result)
      results ++= Map(key -> Map("mean" -> result))
    })

    val prqModel1Count = feedbacksMap.count(feedback => feedback("prq") == 1 && feedback("mode") == 1)
    val prqModel2Count = feedbacksMap.count(feedback => feedback("prq") == 1 && feedback("mode") == 2)
    val prq2Model1Count = feedbacksMap.count(feedback => feedback("prq") == 1 && feedback("mode") == 2)
    val prq2Model2Count = feedbacksMap.count(feedback => feedback("prq2") == 1 && feedback("mode") == 2)
    val prqTotalModel1 = prqModel1Count + prq2Model1Count
    val prqTotalModel2 = prqModel2Count + prq2Model2Count
    val prqTotal = prqTotalModel1 + prqTotalModel2

    println("TOTAL PRQ MODEL 1", prqTotalModel1)
    println("TOTAL PRQ MODEL 2", prqTotalModel2)
    println("MEAN PRQ MODEL 1", prqTotalModel1.toDouble / size.toDouble)
    println("MEAN PRQ MODEL 2", prqTotalModel2.toDouble / size.toDouble)
    println("SIZE", size)

    Map(
      "prq" -> Map(
        "mean" -> Map(
          "model1" -> RecommendationUtil.round(prqTotalModel1.toDouble / size.toDouble, 2),
          "model2" -> RecommendationUtil.round(prqTotalModel2.toDouble / size.toDouble, 2)
        )
      )
    )

    results
  }


  private val results =
    (
      apiOperation[Unit]("/results")
        summary "get results")
  get("/results", operation(results)) {
    val feedbacks = Feedback.list()
    val feedbacksMap = feedbacks.map(feedback => CommonUtil.getCCParams(feedback))
    val size = feedbacks.size
    val keys = List("eou", "eou2", "inf", "etu", "etu2", "pe", "prq", "prq2", "tr", "tr2")
    val keysTwo = List("prq", "tr", "etu", "eou")
    val innerKeys = List("mean", "t", "df", "p")
    var results = Map[String, Map[String, Double]]()

    //negative question is etu and prq2
    keys.foreach(key => {
      var result = Map[String, Double]()
      for (i <- 1 to 2) {
        val agree = feedbacksMap.count(f => {
          f(key).toString.toInt == 1 && f("mode").toString.toInt == i
        })
        val disagree = feedbacksMap.count(f => {
          f(key).toString.toInt == 0 && f("mode").toString.toInt == i
        })
        val total = feedbacksMap.count(f => {
          f("mode").toString.toInt == i
        })
        var counter = agree.toDouble
        var norm = 1
        //        if(disagree > agree){
        if (key == "etu" || key == "prq2") {
          counter = disagree.toDouble
          norm = -1
        }
        result ++= Map(s"model$i" -> RecommendationUtil
          .round(counter / total.toDouble, 2) * norm)
      }
      results ++= Map(key -> result)
    })

    results
  }

}
