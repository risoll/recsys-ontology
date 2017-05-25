package com.rizky.ta.controller
import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
/**
  * Created by risol_000 on 1/30/2017.
  */

private case class Message(greeting: String, to: String)
private case class AttractionsFormat()

class DatasetController extends ScalatraServlet with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/") {
    contentType = formats("json")
    Message("Hello", "World")
  }
}
