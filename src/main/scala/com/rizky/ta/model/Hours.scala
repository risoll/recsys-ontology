package com.rizky.ta.model

import scalikejdbc._
/**
  * Created by solehuddien on 01/06/17.
  */

case class Hours(id: Int, monday: String, tuesday: String,
                 wednesday: String, thursday: String,
                 friday: String, saturday: String,
                 sunday: String)
object OpenHours extends SQLSyntaxSupport[Hours]{
  override val tableName = "open_hours"
  private val c = OpenHours.syntax("c")

  def apply(c: ResultName[Hours])(rs: WrappedResultSet): Hours = new Hours(
    id = rs.int(c.id),
    monday = rs.string(c.monday),
    tuesday = rs.string(c.tuesday),
    wednesday = rs.string(c.wednesday),
    thursday = rs.string(c.thursday),
    friday = rs.string(c.friday),
    saturday = rs.string(c.saturday),
    sunday = rs.string(c.sunday)
  )
  def apply(c: SyntaxProvider[Hours])(rs: WrappedResultSet): Hours = apply(c.resultName)(rs)

  def create(id: Int, monday: String, tuesday: String, wednesday: String, thursday: String,
             friday: String, saturday: String, sunday: String)
            (implicit session: DBSession = autoSession): Hours = {
    withSQL {
      insert.into(OpenHours).values(
        id, monday, tuesday, wednesday, thursday, friday, saturday, sunday
      )
    }.update().apply()
    Hours(
      id, monday, tuesday, wednesday, thursday, friday, saturday, sunday
    )
  }

  def get(id: Int)(implicit session: DBSession = autoSession): Option[Hours] = withSQL {
    select.from(OpenHours as c).where.eq(c.id, id)
  }.map(OpenHours(c)).single.apply()

  def list()(implicit session: DBSession = autoSession): List[Hours] = withSQL {
    select.from(OpenHours as c)
      .orderBy(c.id)
  }.map(OpenHours(c)).list.apply()

}
object CloseHours extends SQLSyntaxSupport[Hours]{
  override val tableName = "close_hours"
  private val c = CloseHours.syntax("c")

  def apply(c: ResultName[Hours])(rs: WrappedResultSet): Hours = new Hours(
    id = rs.int(c.id),
    monday = rs.string(c.monday),
    tuesday = rs.string(c.tuesday),
    wednesday = rs.string(c.wednesday),
    thursday = rs.string(c.thursday),
    friday = rs.string(c.friday),
    saturday = rs.string(c.saturday),
    sunday = rs.string(c.sunday)
  )
  def apply(c: SyntaxProvider[Hours])(rs: WrappedResultSet): Hours = apply(c.resultName)(rs)

  def create(id: Int, monday: String, tuesday: String, wednesday: String, thursday: String,
             friday: String, saturday: String, sunday: String)
            (implicit session: DBSession = autoSession): Hours = {
    withSQL {
      insert.into(CloseHours).values(
        id, monday, tuesday, wednesday, thursday, friday, saturday, sunday
      )
    }.update().apply()
    Hours(
      id, monday, tuesday, wednesday, thursday, friday, saturday, sunday
    )
  }

  def get(id: Int)(implicit session: DBSession = autoSession): Option[Hours] = withSQL {
    select.from(CloseHours as c).where.eq(c.id, id)
  }.map(OpenHours(c)).single.apply()

  def list()(implicit session: DBSession = autoSession): List[Hours] = withSQL {
    select.from(CloseHours as c)
      .orderBy(c.id)
  }.map(CloseHours(c)).list.apply()


}
