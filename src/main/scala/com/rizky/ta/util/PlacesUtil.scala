package com.rizky.ta.util

import com.rizky.ta.model.Place

import scala.collection.mutable.ListBuffer

/**
  * Created by risol_000 on 1/30/2017.
  */
object PlacesUtil {
  def createRowMap(places: List[Place]): ListBuffer[Map[String, Any]] ={
    var result = new ListBuffer[Map[String, Any]]()
    var row = Map[String, Any]()
    var counter: Int = 1

    for (place <- places) {
      row += ("counter" -> counter)
      row += ("place_id" -> place.place_id)
      row += ("name" -> place.name)
      row += ("types" -> place.types)
      row += ("address" -> place.address)
      row += ("phone" -> place.phone)
      row += ("open_hours" -> place.open_hours)
      row += ("length_of_visit" -> place.length_of_visit)
      row += ("tariff" -> place.tariff)
      counter += 1
      result += row
    }
    result
  }
}
