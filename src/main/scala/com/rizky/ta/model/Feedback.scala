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
                    age: Int, rating: Double, eou: Int, eou2: Int, inf: Int,
                    etu: Int, etu2: Int, pe: Int, prq: Int, prq2: Int,
                    tr: Int, tr2: Int, mode: Int, time: Long, profession: String)

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
    age = rs.int(c.age),
    rating = rs.double(c.rating),
    eou = rs.int(c.eou),
    eou2 = rs.int(c.eou2),
    inf = rs.int(c.inf),
    etu = rs.int(c.etu),
    etu2 = rs.int(c.etu2),
    pe = rs.int(c.pe),
    prq = rs.int(c.prq),
    prq2 = rs.int(c.prq2),
    tr = rs.int(c.tr),
    tr2 = rs.int(c.tr2),
    mode = rs.int(c.mode),
    time = rs.long(c.time),
    profession = rs.string(c.profession)
  )
  def apply(c: SyntaxProvider[Feedback])(rs: WrappedResultSet): Feedback = apply(c.resultName)(rs)

  def create(user_agent: String, platform: String,
             ip: String, city: String, name: String, gender: String,
             age: Int, rating: Double,  eou: Int, eou2: Int, inf: Int,
             etu: Int, etu2: Int, pe: Int, prq: Int, prq2: Int,
             tr: Int, tr2: Int, mode: Int, time: Long, profession: String)
            (implicit session: DBSession = autoSession): Feedback = {

    val id = withSQL {
      insert.into(Feedback).namedValues(
        column.user_agent -> user_agent,
        column.platform -> platform,
        column.ip -> ip,
        column.city -> city,
        column.name -> name,
        column.gender -> gender,
        column.age -> age,
        column.rating -> rating,
        column.eou -> eou,
        column.eou2 -> eou2,
        column.inf -> inf,
        column.etu -> etu,
        column.etu2 -> etu2,
        column.pe -> pe,
        column.prq -> prq,
        column.prq2 -> prq2,
        column.tr -> tr,
        column.tr2 -> tr2,
        column.mode -> mode,
        column.time -> time,
        column.profession -> profession
      )
    }.updateAndReturnGeneratedKey.apply().toInt
    Feedback(id, user_agent, platform, ip, city, name, gender,
      age, rating,  eou, eou2, inf,
      etu, etu2, pe, prq, prq2,
      tr, tr2, mode, time, profession)

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
