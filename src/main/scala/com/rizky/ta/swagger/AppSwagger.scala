package com.rizky.ta.swagger

/**
  * Created by solehuddien on 18/05/17.
  */
import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, NativeSwaggerBase, Swagger}


class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with NativeSwaggerBase

object AppApiInfo extends ApiInfo(
  "The Recsys Ontology API",
  "Docs for the Recsys Ontology API",
  "https://id.linkedin.com/in/rizkysolechudin",
  "rizky.solechudin@gmail.com",
  "MIT",
  "http://opensource.org/licenses/MIT")

class AppSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", AppApiInfo)