package com.rizky.ta.util

import scala.xml.Elem

/**
  * Created by solehuddien on 16/04/17.
  */
object MavenToSbtUtil {

  def createSbtString(xml: Elem): String = {

    object scala {
      val version = "SCALA_VERSION$"
    }


    val data: Seq[(String, String, String)] = (xml \ "dependency") map { d =>
      val groupId = d \ "groupId" text
      val artifactId = d \ "artifactId" text
      val versionNum = d \ "version" text

      (groupId, artifactId, versionNum)
    }

    val CrossBuildArtifact = """([\w-]+)_\$SCALA_VERSION\$""".r

    def dep(a: String, g: String, v: String, cross: Boolean) = {
      val sep = if (cross) "%%" else "%"
      val ident = a.split("-").map(_.capitalize).mkString
      """val %s = "%s" %s "%s" %% "%s" """ format(ident, g, sep, a, v)
    }

    val m = data map {
      case (g, CrossBuildArtifact(a), v) => dep(a, g, v, true)
      case (g, a, v) => dep(a, g, v, false)
    } mkString ("\n")
    println(m)
    m.toString
  }
}
