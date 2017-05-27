package com.rizky.ta.util


import scala.util.parsing.json.JSON
import scalaj.http.{Http, HttpResponse}

/**
  * Created by solehuddien on 03/05/17.
  */
object GoogleUtil {

  private val API_KEY = "AIzaSyBcly9g2k3wE6bmDnCNTMWEa8R3MER-Aiw"
  private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place"

  def radarSearch(lat: Double, lng: Double, radius: Double): List[Map[String, Any]] = {
    val params = Map("location" -> s"$lat,$lng", "radius" -> radius, "type" -> "point_of_interest", "key" -> API_KEY)
    val response: HttpResponse[String] = Http(requestBuilder(s"$PLACES_API_BASE/radarsearch/json?", params)).asString
    val jsonRes = JSON.parseFull(response.body).get
    jsonRes.asInstanceOf[Map[String, Any]]("results").asInstanceOf[List[Map[String, Any]]]
  }

  def requestBuilder(prefix: String, params: Map[String, Any]): String ={
    var request = s"$prefix"
    params.foreach(param=>{
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
