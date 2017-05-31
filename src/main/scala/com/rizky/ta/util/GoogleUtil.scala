package com.rizky.ta.util


import java.net.URLDecoder

import org.json4s.jackson.Serialization

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON
import scalaj.http.{Http, HttpResponse}
import scala.util.control.Breaks._

/**
  * Created by solehuddien on 03/05/17.
  */
object GoogleUtil {

  private val replaceQuery = Map("dan" -> "and")

  private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place"
//  private val API_KEY = "AIzaSyC1a8GoIeNrSnLD0SrT7prEa1Cf_T_a2WA"
//  private val API_KEY = "AIzaSyB8N-RSVWGZseC1qQc7T1s85Z4kOtZYnN0"
//  private val API_KEY = "AIzaSyC_cBzLwY0pZhuI3kDUykrvgGj5gnGUP7Q"
//  private val API_KEY = "AIzaSyDglsvlpD_o8RPXbpI--KlLgTmz_tWJSyQ"
//  private val API_KEY = "AIzaSyBpsIj_ThKLWEs26Ibf70lfN3d9eX0DBak"
  private val API_KEY = "AIzaSyCJynwIXL7HAnw8p6WzqRKZ4EOgRakuu_o"

  def textSearch(query: String, lat: Double, lng: Double, radius: Double): Map[String, Any] ={
    var parsedQuery = s"${query.replace(" ", "+")}+bandung+attraction"
    replaceQuery.foreach(query=>{
      parsedQuery = parsedQuery.replace(query._1, query._2)
    })
    val params = Map("query" -> parsedQuery, "lat" -> lat, "lng" -> lng, "radius" -> radius, "key" -> API_KEY)
    val prefix = s"$PLACES_API_BASE/textsearch/json?"
    val response = requestHttp(prefix, params).asInstanceOf[Map[String, Any]]
    var result: Map[String, Any] = Map()
    response.get("results") match {
      case Some(value) =>
        value.asInstanceOf[List[Map[String, Any]]].foreach(v=>{
          if(result.keys.size < v.keys.size)
            result = v
        })
        result
      case None =>
        Map()
    }
  }

  def radarSearch(lat: Double, lng: Double, radius: Double): List[Map[String, Any]] = {
    val params = Map("location" -> s"$lat,$lng", "radius" -> radius, "type" -> "point_of_interest", "key" -> API_KEY)
    val prefix = s"$PLACES_API_BASE/radarsearch/json?"
    val response = requestHttp(prefix, params).asInstanceOf[Map[String, Any]]
    response.get("results") match {
      case Some(value) =>
        value.asInstanceOf[List[Map[String, Any]]]
      case None =>
        List()
    }
  }

  def getPhoto(maxHeight: Int, maxWidth: Int, photoReference: String): String = {
    val params = Map("maxheight" -> maxHeight, "maxwidth" -> maxWidth, "photoreference" -> photoReference, "key" -> API_KEY)
    val prefix = s"$PLACES_API_BASE/photo?"
    requestHttp(prefix, params, "HEAD").asInstanceOf[String]
  }

  def getPhotos(maxHeight: Int, maxWidth: Int, ids: List[String]): List[Map[String, Any]] = {
    val res: ListBuffer[Map[String, Any]] = ListBuffer()
    ids.foreach(id => {
      val place = getPlaceDetails(id)
      var tmpInfo: Map[String, Any] = Map()
      var needAppend = true
      println("place", place)
      if(place.keys.nonEmpty){
        place.get("photos") match {
          case Some(photos) =>
            val photoRef = photos.asInstanceOf[List[Map[String, Any]]].head("photo_reference").toString
            val photo = getPhoto(maxHeight, maxWidth, photoRef)
            println(photoRef, photo)
            tmpInfo += "photo" -> photo
          case None =>
            needAppend = false
        }
        place.get("name") match {
          case Some(name) =>
            tmpInfo += "name" -> name
          case None =>
            needAppend = false
        }
        place.get("formatted_address") match {
          case Some(address) =>
            tmpInfo += "address" -> address
          case None =>
            needAppend = false
        }
        if(needAppend)
          res.append(tmpInfo)
      }
    })
    res.toList
  }

  def getPlaceIds(searchResults: List[Map[String, Any]]): List[String] = {
    val ids: ListBuffer[String] = ListBuffer()
    searchResults.foreach(result => {
      ids.append(result("place_id").toString)
    })
    ids.toList
  }

  def getPlaceDetails(placeId: String): Map[String, Any] = {
    val params = Map("placeid" -> placeId, "key" -> API_KEY)
    val prefix = s"$PLACES_API_BASE/details/json?"
    val response = requestHttp(prefix, params).asInstanceOf[Map[String, Any]]
    response.get("result") match {
      case Some(value) =>
        value.asInstanceOf[Map[String, Any]]
      case None =>
        Map()
    }
  }

  def getPhotoReference(placeId: String): String = {
    val res = getPlaceDetails(placeId)
    res.get("photos") match {
      case Some(photos) =>
        photos.asInstanceOf[List[Map[String, Any]]].head("photo_reference").toString
      case None =>
        ""
    }
  }

  def getPlaceInfo(placeId: String): String = {
    val res = getPlaceDetails(placeId)
    res.get("photos") match {
      case Some(photos) =>
        photos.asInstanceOf[List[Map[String, Any]]].head("photo_reference").toString
      case None =>
        ""
    }
  }

  def requestHttp(prefix: String, params: Map[String, Any], method: String = ""): Any = {
    val request = createRequestURI(prefix, params)
    println(request)
    method match {
      case "HEAD" =>
        val response = Http(request).method("HEAD").asParams
        response.headers.get("Location") match {
          case Some(value) =>
            value.head
          case None =>
            ""
        }
      case "" =>
        val response = Http(request).asString
        JSON.parseFull(response.body).get
    }
  }

  def createRequestURI(prefix: String, params: Map[String, Any]): String = {
    var request = s"$prefix"
    params.foreach(param => {
      request += s"&${param._1}=${param._2}"
    })
    request
  }

  case class PlaceDetail(place_id: String, name: String, types: String, address: String,
                         phone: String, open_hours: String, length_of_visit: String, tariff: String)

  case class Place(geometry: Geometry, id: String, place_id: String, reference: String)

  val placeKeys = List("geometry", "id", "place_id", "reference")

  case class AddressComponents(long_name: String, short_name: String, types: List[String])

  case class Location(lat: Float, lng: Float)

  case class Geometry(location: Location)

}
