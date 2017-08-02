package com.rizky.ta.controller

import com.rizky.ta.model.PostFeedback
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.scalatra.{CorsSupport, ScalatraServlet}

/**
  * Created by solehuddien on 02/06/17.
  */
class PostFeedbackController(implicit val swagger: Swagger)
  extends ScalatraServlet
    with JacksonJsonSupport
    with CorsSupport
    with SwaggerSupport {
  protected val applicationDescription = "The PostFeedback API. It manages the user's feedback evaluating the app"
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
      summary "get list of all post feedbacks")
  get("/bulk", operation(feedbacks)) {
    PostFeedback.list()
  }

  private val addFeedback =
    (
      apiOperation[Unit]("/add")
      summary "add post feedback"
      parameter bodyParam[PostFeedback]("feedback").description("New feedback definition").required)
  post("/add", operation(addFeedback)) {
    val feedback = parsedBody.extract[PostFeedback]
    PostFeedback.create(
      feedback.user_agent, feedback.platform, feedback.ip, feedback.city,
      feedback.name, feedback.gender, feedback.age,
      feedback.more_informative, feedback.easier, feedback.more_useful,
      feedback.more_appropriate_result, feedback.more_helpful_interaction,
      feedback.overall_preference, feedback.time
    )
  }
}
