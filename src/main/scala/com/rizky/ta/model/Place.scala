package com.rizky.ta.model

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import scalikejdbc._

/**
  * Created by risol_000 on 1/30/2017.
  */
case class Place(place_id: String, name: String, formatted_address: String,
                 phone: String, length_of_visit: String, tariff: Int,
                 photo: String, lat: Double, lng: Double, rating: Double,
                 description: String,
                 monday: String,
                 tuesday: String,
                 wednesday: String,
                 thursday: String,
                 friday: String,
                 saturday: String,
                 sunday: String)

object Place extends SQLSyntaxSupport[Place] {

  case class Params(location: String, radius: Integer, kind: String, key: String)

  case class Result(geometry: Geometry, id: String, place_id: String, reference: String)

  case class Return(html_attributions: List[String], results: List[Result], status: String)

  override val tableName = "places"
  //  override val useSnakeCaseColumnName = false
  private val c = Place.syntax("c")


  def apply(c: ResultName[Place])(rs: WrappedResultSet): Place = new Place(
    place_id = rs.string(c.place_id),
    name = rs.string(c.name),
    formatted_address = rs.string(c.formatted_address),
    phone = rs.string(c.phone),
    length_of_visit = rs.string(c.length_of_visit),
    tariff = rs.int(c.tariff),
    rating = rs.int(c.rating),
    photo = rs.string(c.photo),
    lat = rs.double(c.lat),
    lng = rs.double(c.lng),
    description = rs.string(c.description),
    monday = rs.string(c.monday),
    tuesday = rs.string(c.tuesday),
    wednesday = rs.string(c.wednesday),
    thursday = rs.string(c.thursday),
    friday = rs.string(c.friday),
    saturday = rs.string(c.saturday),
    sunday = rs.string(c.sunday)
  )
  def apply(c: SyntaxProvider[Place])(rs: WrappedResultSet): Place = apply(c.resultName)(rs)

  def create(place_id: String,
             name: String, formatted_address: String = "",
             phone: String = "", length_of_visit: String = "", tariff: Int = 0,
             photo: String= "", lat: Double = 0, lng: Double = 0, rating: Double = 0,
             description: String = "",
             monday: String,
             tuesday: String,
             wednesday: String,
             thursday: String,
             friday: String,
             saturday: String,
             sunday: String)
            (implicit session: DBSession = autoSession): Place = {
    withSQL {
      insert.into(Place).values(
        place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
        lng, rating, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday
      )
    }.update().apply()
    Place(
      place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
      lng, rating, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday
    )
  }

  def get(id: String)(implicit session: DBSession = autoSession): Option[Place] = withSQL {
    select.from(Place as c).where.eq(c.place_id, id)
  }.map(Place(c)).single.apply()

  def getByName(name: String)(implicit session: DBSession = autoSession): Option[Place] = withSQL {
    select.from(Place as c).where.eq(c.name, name)
  }.map(Place(c)).single.apply()


  def updatePlace(place_id: String,
                  name: String = "", formatted_address: String = "",
                  phone: String = "", length_of_visit: String = "", tariff: Int = 0,
                  photo: String = "", lat: Double = 0, lng: Double = 0, rating: Double = 0,
                  description: String,
                  monday: String,
                  tuesday: String,
                  wednesday: String,
                  thursday: String,
                  friday: String,
                  saturday: String,
                  sunday: String)
                 (implicit session: DBSession = autoSession): Place = {
    withSQL{
      val c = Place.column
      update(Place).set(
        c.name -> name,
        c.formatted_address -> formatted_address,
        c.phone -> phone,
        c.length_of_visit -> length_of_visit,
        c.tariff -> tariff,
        c.photo -> photo,
        c.lat -> lat,
        c.lng -> lng,
        c.rating -> rating,
        c.description -> description,
        c.monday -> monday,
        c.tuesday -> tuesday,
        c.wednesday -> c.wednesday,
        c.thursday -> c.thursday,
        c.friday -> c.friday,
        c.saturday -> c.saturday,
        c.sunday -> c.sunday
      ).where.eq(c.place_id, place_id)
    }.update().apply()
    Place(
      place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
      lng, rating, description, monday, tuesday, wednesday, thursday, friday, saturday, sunday
    )
  }

  def list()(implicit session: DBSession = autoSession): List[Place] = withSQL {
    select.from(Place as c)
      .orderBy(c.place_id)
  }.map(Place(c)).list.apply()

  def listPagination(limit: Int, offset: Int)(implicit session: DBSession = autoSession): List[Place] = withSQL {
    select.from(Place as c).where.ne(c.photo, "")
      .orderBy(c.place_id).limit(limit).offset(offset)
  }.map(Place(c)).list.apply()
}
