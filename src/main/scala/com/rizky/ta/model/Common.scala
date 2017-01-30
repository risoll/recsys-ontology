package com.rizky.ta.model

/**
  * Created by risol_000 on 1/30/2017.
  */

object Common {
  case class AddressComponents(long_name: String, short_name: String, types: List[String])
  case class Location(lat: Float, lng: Float)
  case class Geometry(location: Location)
  val apiKey = "AIzaSyBcly9g2k3wE6bmDnCNTMWEa8R3MER-Aiw"
}
