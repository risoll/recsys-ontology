package com.rizky.ta.controller

import com.rizky.ta.model.{Feedback, Place}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import sun.plugin.javascript.navig5.JSObject

import scala.tools.reflect.WrappedProperties.AccessControl

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

  private val newFeedback = new Feedback(
    1,
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36",
    "Linux x86_64",
    "180.245.230.8",
    "Bandung",
    "Rizky Solechudin",
    "Male",
    "Cilacap",
    22,
    "Software Engineer",
    "Telkom University",
    "Computer Science",
    5.0
  )

  private val addFeedback =
    (apiOperation[Unit]("/add")
      summary "add feedback"
      parameter bodyParam[Feedback]("feedback").description("New feedback definition").required)
//      parameters(
//      bodyParam[String]("userAgent").defaultValue("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36").description("User agent header of the client"),
//      bodyParam[String]("platform").defaultValue("Linux x86_64").description("Platform/OS used by the client"),
//      bodyParam[String]("ip").defaultValue("180.245.230.8").description("Client's external IP address"),
//      bodyParam[String]("city").defaultValue("Bandung").description("Client's current city"),
//      bodyParam[String]("name").defaultValue("Rizky Solechudin").description("Full name of the user"),
//      bodyParam[String]("gender").defaultValue("Male").description("Gender of the user"),
//      bodyParam[String]("origin").defaultValue("Cilacap").description("User's state of origin"),
//      bodyParam[Int]("age").defaultValue(22).description("User's current age"),
//      bodyParam[String]("profession").defaultValue("Software Engineer").description("User's current employment"),
//      bodyParam[String]("univ").defaultValue("Telkom University").description("User's college"),
//      bodyParam[String]("majors").defaultValue("Computer Science").description("User's college majors"),
//      bodyParam[Double]("rating").defaultValue(5).description("User's rating for the recommendation after using the app")
//    ))
  post("/add", operation(addFeedback)) {
    val feedback = parsedBody.extract[Feedback]
    Feedback.create(
      feedback.user_agent, feedback.platform, feedback.ip, feedback.city,
      feedback.name, feedback.gender, feedback.origin, feedback.age,
      feedback.profession, feedback.univ, feedback.majors, feedback.rating
    )
//    val userAgent = (parsedBody \ "user_agent").extract[String]
//    val platform = (parsedBody \ "platform").extract[String]
//    val ip = (parsedBody \ "ip").extract[String]
//    val city = (parsedBody \ "city").extract[String]
//    val name = (parsedBody \ "name").extract[String]
//    val gender = (parsedBody \ "gender").extract[String]
//    val origin = (parsedBody \ "origin").extract[String]
//    val age = (parsedBody \ "age").extract[Int]
//    val profession = (parsedBody \ "profession").extract[String]
//    val univ = (parsedBody \ "univ").extract[String]
//    val majors = (parsedBody \ "majors").extract[String]
//    val rating = (parsedBody \ "rating").extract[Double]
//    Feedback.create(userAgent, platform, ip, city, name, gender, origin,
//      age, profession, univ, majors, rating)
  }

//  case class DefaultFeedback(user_agent: Option[String], platform: Option[String],
//                      ip: Option[String], city: Option[String],
//                      name: Option[String], gender: Option[String], origin: Option[String],
//                      age: Option[Int], profession: Option[String], univ: Option[String],
//                      majors: Option[String], rating: Option[Double])
}
