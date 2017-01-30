package com.rizky.ta.model

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import scalikejdbc._

/**
  * Created by risol_000 on 1/30/2017.
  */
case class Place(place_id: String, name: String, types: String, address: String,
                 phone: String, open_hours: String, length_of_visit: String, tariff: String)

object Place extends SQLSyntaxSupport[Place] {

  case class Params(location: String, radius: Integer, kind: String, key: String)

  case class Result(geometry: Geometry, id: String, place_id: String, reference: String)

  case class Return(html_attributions: List[String], results: List[Result], status: String)

  val c = Place.syntax("c")
  def apply(c: ResultName[Place])(rs: WrappedResultSet): Place = new Place(
    place_id = rs.string(c.place_id),
    name = rs.string(c.name),
    types = rs.string(c.types),
    address = rs.string(c.address),
    phone = rs.string(c.phone),
    open_hours = rs.string(c.open_hours),
    length_of_visit = rs.string(c.length_of_visit),
    tariff = rs.string(c.tariff)
  )
  def apply(c: SyntaxProvider[Place])(rs: WrappedResultSet): Place = apply(c.resultName)(rs)

  override val tableName = "places"
//  override val useSnakeCaseColumnName = false

  def create(placeId: String)
            (implicit session: DBSession = autoSession): Place = {
    withSQL {
      insert.into(Place).values(placeId)
    }.update().apply()
    Place(placeId, "", "", "", "", "", "", "")
  }

  def updatePlace(place_id: String, name: String, types: String, address: String,
             phone: String, open_hours: String, length_of_visit: String,
             tariff: String)(implicit session: DBSession = autoSession): Place = {
    withSQL{
      val c = Place.column
      update(Place).set(
        c.name -> name,
        c.types -> types,
        c.address -> address,
        c.phone -> phone,
        c.open_hours -> open_hours,
        c.length_of_visit -> length_of_visit,
        c.tariff -> tariff
      ).where.eq(c.place_id, place_id)
    }.update().apply()
    Place(place_id, name, types, address, phone, open_hours, length_of_visit, tariff)
  }

  def list()(implicit session: DBSession = autoSession): List[Place] = withSQL {
    select.from(Place as c)
      .orderBy(c.place_id)
  }.map(Place(c)).list.apply()
}
