package com.rizky.ta.model

import scalikejdbc._

/**
  * Created by solehuddien on 09/06/17.
  */
case class Classes(id: Int, name: String, image: String)

object Classes extends SQLSyntaxSupport[Classes]{
  override val tableName = "classes"
  //  override val useSnakeCaseColumnName = false
  private val c = Classes.syntax("c")


  def apply(c: ResultName[Classes])(rs: WrappedResultSet): Classes = new Classes(
    id = rs.int(c.id),
    name = rs.string(c.name),
    image = rs.string(c.image)
  )
  def apply(c: SyntaxProvider[Classes])(rs: WrappedResultSet): Classes = apply(c.resultName)(rs)

  def create(name: String, image: String)
            (implicit session: DBSession = autoSession): Classes = {

    val id = withSQL {
      insert.into(Classes).namedValues(
        column.name -> name,
        column.image -> image
      )
    }.updateAndReturnGeneratedKey.apply().toInt
    Classes(id, name, image)

  }

  def get(id: String)(implicit session: DBSession = autoSession): Option[Classes] = withSQL {
    select.from(Classes as c).where.eq(c.id, id)
  }.map(Classes(c)).single.apply()

  def getByName(name: String)(implicit session: DBSession = autoSession): Option[Classes] = withSQL {
    select.from(Classes as c).where.eq(c.name, name)
  }.map(Classes(c)).single.apply()


  def list()(implicit session: DBSession = autoSession): List[Classes] = withSQL {
    select.from(Classes as c)
      .orderBy(c.id)
  }.map(Classes(c)).list.apply()

}
