package common

import owl.OwlConst

/**
  * Created by solehuddien on 23/05/17.
  */
object CommonTest extends App{
//  println("OWL_PREFIX", OwlConst.OWL_PREFIX)
  var list = List("pemandangan", "b", "c")
  list ++= List("gg")
  println("==========", list)
//  val res = list.exists("Pemandangan".contentEquals)
//  println(res)
//  var m = Map("gg" -> 1, "gg2" -> 2)
//  val m2 = Map("gg3" -> 3)
//  m ++= m2
//  val m3 = m.filterKeys(Set("gg").contains)
//  println(m3)
}
