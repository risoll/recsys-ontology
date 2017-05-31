package com.rizky.ta.util

/**
  * Created by risol_000 on 3/13/2017.
  */
object CommonUtil {
  def dynamicField(org: String): Any = {
    var res: Any = org
    try {
      res = org.toInt
    } catch {
      case e: Exception => res = org
    }

    if (res == org)
      try {
        res = org.toFloat
      } catch {
        case e: Exception => res = org
      }

    if (res == org)
      try {
        res = org.toLong
      } catch {
        case e: Exception => res = org
      }

    if (res == org)
      try {
        res = org.toDouble
      } catch {
        case e: Exception => res = org
      }

    res
  }

  def createCaseClass[T](vals : Map[String, Object])(implicit cmf : ClassManifest[T]) = {
    val ctor = cmf.erasure.getConstructors().head
    val args = cmf.erasure.getDeclaredFields().map( f => vals(f.getName) )
    ctor.newInstance(args : _*).asInstanceOf[T]
  }
}

