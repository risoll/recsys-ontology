package common

import owl.OwlConst

/**
  * Created by solehuddien on 23/05/17.
  */
object CommonTest extends App{
//  println("OWL_PREFIX", OwlConst.OWL_PREFIX)
  val list = List("a", "b", "c")
  val res = list.exists("a".contains)
  println(res)
}
