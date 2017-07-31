package com.rizky.ta.controller

import com.rizky.ta.model.{Feedback}
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
      feedback.pu1, feedback.eou1, feedback.tr1, feedback.pe1,
      feedback.bi1
    )
  }
}
