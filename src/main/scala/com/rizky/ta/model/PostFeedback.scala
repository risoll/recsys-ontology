package com.rizky.ta.model

import com.rizky.ta.model.PostFeedback.{autoSession, column}
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

case class PostFeedback(id: Int, user_agent: String, platform: String,
                        ip: String, city: String, name: String, gender: String,
                        age: Int, more_informative: Int, easier: Int, more_useful: Int,
                        more_appropriate_result: Int, more_helpful_interaction: Int,
                        overall_preference: Int, time: Long, profession: String)

object PostFeedback extends SQLSyntaxSupport[PostFeedback] {

  override val tableName = "post_feedbacks"
  //  override val useSnakeCaseColumnName = false
  private val c = PostFeedback.syntax("c")


  def apply(c: ResultName[PostFeedback])(rs: WrappedResultSet): PostFeedback = new PostFeedback(
    id = rs.int(c.id),
    user_agent = rs.string(c.user_agent),
    platform = rs.string(c.platform),
    ip = rs.string(c.ip),
    city = rs.string(c.city),
    name = rs.string(c.name),
    gender = rs.string(c.gender),
    age = rs.int(c.age),
    more_informative = rs.int(c.more_informative), 
    easier = rs.int(c.easier),
    more_useful = rs.int(c.more_useful),
    more_appropriate_result = rs.int(c.more_appropriate_result),
    more_helpful_interaction = rs.int(c.more_helpful_interaction),
    overall_preference = rs.int(c.overall_preference),
    time = rs.long(c.time),
    profession = rs.string(c.profession)
  )
  def apply(c: SyntaxProvider[PostFeedback])(rs: WrappedResultSet): PostFeedback = apply(c.resultName)(rs)

  def create(user_agent: String, platform: String,
             ip: String, city: String, name: String, gender: String,
             age: Int, more_informative: Int, easier: Int, more_useful: Int,
             more_appropriate_result: Int, more_helpful_interaction: Int,
             overall_preference: Int, time: Long, profession: String)
            (implicit session: DBSession = autoSession): PostFeedback = {

    val id = withSQL {
      insert.into(PostFeedback).namedValues(
        column.user_agent -> user_agent,
        column.platform -> platform,
        column.ip -> ip,
        column.city -> city,
        column.name -> name,
        column.gender -> gender,
        column.age -> age,
        column.more_informative -> more_informative,
        column.easier -> easier,
        column.more_useful -> more_useful,
        column.more_appropriate_result -> more_appropriate_result,
        column.more_helpful_interaction -> more_helpful_interaction,
        column.overall_preference -> overall_preference,
        column.time -> time,
        column.profession -> profession
      )
    }.updateAndReturnGeneratedKey.apply().toInt
    PostFeedback(id, user_agent, platform, ip, city, name, gender,
      age, more_informative, easier, more_useful,
      more_appropriate_result, more_helpful_interaction,
      overall_preference, time, profession)
  }

  def get(id: String)(implicit session: DBSession = autoSession): Option[PostFeedback] = withSQL {
    select.from(PostFeedback as c).where.eq(c.id, id)
  }.map(PostFeedback(c)).single.apply()

  def getByName(name: String)(implicit session: DBSession = autoSession): Option[PostFeedback] = withSQL {
    select.from(PostFeedback as c).where.eq(c.name, name)
  }.map(PostFeedback(c)).single.apply()


  def list()(implicit session: DBSession = autoSession): List[PostFeedback] = withSQL {
    select.from(PostFeedback as c)
      .orderBy(c.id)
  }.map(PostFeedback(c)).list.apply()

  def listPagination(limit: Int, offset: Int)(implicit session: DBSession = autoSession): List[PostFeedback] = withSQL {
    select.from(PostFeedback as c)
      .orderBy(c.id).limit(limit).offset(offset)
  }.map(PostFeedback(c)).list.apply()
}
