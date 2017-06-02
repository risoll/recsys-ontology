package com.rizky.ta.model

import com.rizky.ta.model.Common.Geometry
import com.rizky.ta.model.Place.Result
import scalikejdbc._

/**
  * Created by risol_000 on 1/30/2017.
  */
//heroku pg:backups:restore 'https://dl.dropboxusercontent.com/s/1lgcg0vteu0trf2/recsys-ta.dump?dl=0' DATABASE_URL --app jalan-belakang
//pg_dump -Fc --no-acl --no-owner -h localhost -U postgres recsys-ta > recsys-ta.dump

//case class Place(place_id: String, name: String, formatted_address: String,
//                 phone: String, length_of_visit: String, tariff: Int,
//                 photo: String, lat: Double, lng: Double, rating: Double,
//                 open_hours_id: Int, close_hours_id: Int)

case class Feedback(id: Int, user_agent: String, platform: String,
                    ip: String, city: String, name: String, gender: String,
                    origin: String, age: Int, profession: String,
                    univ: String, majors: String, rating: Double)

object Feedback extends SQLSyntaxSupport[Feedback] {

  override val tableName = "user_feedbacks"
  //  override val useSnakeCaseColumnName = false
  private val c = Feedback.syntax("c")


  def apply(c: ResultName[Feedback])(rs: WrappedResultSet): Feedback = new Feedback(
    id = rs.int(c.id),
    user_agent = rs.string(c.user_agent),
    platform = rs.string(c.platform),
    ip = rs.string(c.ip),
    city = rs.string(c.city),
    name = rs.string(c.name),
    gender = rs.string(c.gender),
    origin = rs.string(c.origin),
    age = rs.int(c.age),
    profession = rs.string(c.profession),
    univ = rs.string(c.univ),
    majors = rs.string(c.majors),
    rating = rs.double(c.rating)
  )
  def apply(c: SyntaxProvider[Feedback])(rs: WrappedResultSet): Feedback = apply(c.resultName)(rs)

  def create(user_agent: String, platform: String,
             ip: String, city: String, name: String, gender: String,
             origin: String, age: Int, profession: String,
             univ: String, majors: String, rating: Double)
            (implicit session: DBSession = autoSession): Feedback = {

    val id = withSQL {
      insert.into(Feedback).namedValues(
        column.user_agent -> user_agent,
        column.platform -> platform,
        column.ip -> ip,
        column.city -> city,
        column.name -> name,
        column.gender -> gender,
        column.origin -> origin,
        column.age -> age,
        column.profession -> profession,
        column.univ -> univ,
        column.majors -> majors,
        column.rating -> rating
      )
    }.updateAndReturnGeneratedKey.apply().toInt
    Feedback(id, user_agent, platform, ip, city, name, gender, origin,
      age, profession, univ, majors, rating)

  }

  def get(id: String)(implicit session: DBSession = autoSession): Option[Feedback] = withSQL {
    select.from(Feedback as c).where.eq(c.id, id)
  }.map(Feedback(c)).single.apply()

  def getByName(name: String)(implicit session: DBSession = autoSession): Option[Feedback] = withSQL {
    select.from(Feedback as c).where.eq(c.name, name)
  }.map(Feedback(c)).single.apply()


  def list()(implicit session: DBSession = autoSession): List[Feedback] = withSQL {
    select.from(Feedback as c)
      .orderBy(c.id)
  }.map(Feedback(c)).list.apply()

  def listPagination(limit: Int, offset: Int)(implicit session: DBSession = autoSession): List[Feedback] = withSQL {
    select.from(Feedback as c)
      .orderBy(c.id).limit(limit).offset(offset)
  }.map(Feedback(c)).list.apply()
}
