package com.rizky.ta.model

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import scalikejdbc._

/**
  * Created by risol_000 on 1/30/2017.
  */
//heroku pg:backups:restore 'https://dl.dropboxusercontent.com/s/1lgcg0vteu0trf2/recsys-ta.dump?dl=0' DATABASE_URL --app jalan-belakang
//pg_dump -Fc --no-acl --no-owner -h localhost -U postgres recsys-ta > recsys-ta.dump

case class Place(place_id: String, name: String, formatted_address: String,
                 phone: String, length_of_visit: String, tariff: Int,
                 photo: String, lat: Double, lng: Double, rating: Double,
                 open_hours_monday: String, open_hours_tuesday: String,
                 open_hours_wednesday: String, open_hours_thursday: String,
                 open_hours_friday: String, open_hours_saturday: String,
                 open_hours_sunday: String, close_hours_monday: String,
                 close_hours_tuesday: String, close_hours_wednesday: String,
                 close_hours_thursday: String, close_hours_friday: String,
                 close_hours_saturday: String, close_hours_sunday: String)

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
    open_hours_monday = rs.string(c.open_hours_monday),
    open_hours_tuesday = rs.string(c.open_hours_tuesday),
    open_hours_wednesday = rs.string(c.open_hours_wednesday),
    open_hours_thursday = rs.string(c.open_hours_thursday),
    open_hours_friday = rs.string(c.open_hours_friday),
    open_hours_saturday = rs.string(c.open_hours_saturday),
    open_hours_sunday = rs.string(c.open_hours_sunday),
    close_hours_monday = rs.string(c.close_hours_monday),
    close_hours_tuesday = rs.string(c.close_hours_tuesday),
    close_hours_wednesday = rs.string(c.close_hours_wednesday),
    close_hours_thursday = rs.string(c.close_hours_thursday),
    close_hours_friday = rs.string(c.close_hours_friday),
    close_hours_saturday = rs.string(c.close_hours_saturday),
    close_hours_sunday = rs.string(c.close_hours_sunday)
  )
  def apply(c: SyntaxProvider[Place])(rs: WrappedResultSet): Place = apply(c.resultName)(rs)

  def create(place_id: String,
             name: String, formatted_address: String = "",
             phone: String = "", length_of_visit: String = "", tariff: Int = 0,
             photo: String, lat: Double = 0, lng: Double = 0, rating: Double = 0,
             open_hours_monday: String = "", open_hours_tuesday: String = "",
             open_hours_wednesday: String = "", open_hours_thursday: String = "",
             open_hours_friday: String = "", open_hours_saturday: String = "",
             open_hours_sunday: String = "", close_hours_monday: String = "",
             close_hours_tuesday: String = "", close_hours_wednesday: String = "",
             close_hours_thursday: String = "", close_hours_friday: String = "",
             close_hours_saturday: String = "", close_hours_sunday: String = "")
            (implicit session: DBSession = autoSession): Place = {
    withSQL {
      insert.into(Place).values(
        place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
        lng, rating, open_hours_monday, open_hours_tuesday, open_hours_wednesday, open_hours_thursday,
        open_hours_friday, open_hours_saturday, open_hours_sunday, close_hours_monday, close_hours_tuesday,
        close_hours_wednesday, close_hours_thursday, close_hours_friday, close_hours_saturday,
        close_hours_sunday
      )
    }.update().apply()
    Place(
      place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
      lng, rating, open_hours_monday, open_hours_tuesday, open_hours_wednesday, open_hours_thursday,
      open_hours_friday, open_hours_saturday, open_hours_sunday, close_hours_monday, close_hours_tuesday,
      close_hours_wednesday, close_hours_thursday, close_hours_friday, close_hours_saturday,
      close_hours_sunday
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
                  open_hours_monday: String = "", open_hours_tuesday: String = "",
                  open_hours_wednesday: String = "", open_hours_thursday: String = "",
                  open_hours_friday: String = "", open_hours_saturday: String = "",
                  open_hours_sunday: String = "", close_hours_monday: String = "",
                  close_hours_tuesday: String = "", close_hours_wednesday: String = "",
                  close_hours_thursday: String = "", close_hours_friday: String = "",
                  close_hours_saturday: String = "", close_hours_sunday: String = "")
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
        c.photo -> lat,
        c.photo -> lng,
        c.photo -> rating,
        c.open_hours_monday -> open_hours_monday,
        c.open_hours_tuesday -> open_hours_tuesday,
        c.open_hours_wednesday -> open_hours_wednesday,
        c.open_hours_thursday -> open_hours_thursday,
        c.open_hours_friday -> open_hours_friday,
        c.open_hours_saturday -> open_hours_saturday,
        c.open_hours_sunday -> open_hours_sunday,
        c.close_hours_monday -> close_hours_monday,
        c.close_hours_tuesday -> close_hours_tuesday,
        c.close_hours_wednesday -> close_hours_wednesday,
        c.close_hours_thursday -> close_hours_thursday,
        c.close_hours_friday -> close_hours_friday,
        c.close_hours_saturday -> close_hours_saturday,
        c.close_hours_sunday -> close_hours_sunday
      ).where.eq(c.place_id, place_id)
    }.update().apply()
    Place(
      place_id, name, formatted_address, phone, length_of_visit, tariff, photo, lat,
      lng, rating, open_hours_monday, open_hours_tuesday, open_hours_wednesday, open_hours_thursday,
      open_hours_friday, open_hours_saturday, open_hours_sunday, close_hours_monday, close_hours_tuesday,
      close_hours_wednesday, close_hours_thursday, close_hours_friday, close_hours_saturday,
      close_hours_sunday
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
