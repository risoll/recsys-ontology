package common

import owl.OwlConst
import util.control.Breaks._


/**
  * Created by solehuddien on 23/05/17.
  */
object CommonTest extends App{
//  println("OWL_PREFIX", OwlConst.OWL_PREFIX)
//  var list = List("pemandangan", "b", "c")
//  list ++= List("gg")
//  println("==========", list)
//  val res = list.exists("Pemandangan".contentEquals)
//  println(res)
//  var m = Map("gg" -> 1, "gg2" -> 2)
//  val m2 = Map("gg3" -> 3)
//  m ++= m2
//  val m3 = m.filterKeys(Set("gg").contains)
//  println(m3)
//  var m = Map(
//    "name" -> "gg",
//    "parents" -> "oi"
//  )
//  val n = Map(
//    "name" -> "woe"
//  )
//  m ++= n
//  println(m)
//  println("1 apahayo".substring(2))
//  var m2 = m.head
//  m2 = m2 + ("name" -> "gg2")
//  println(m2)
//  if(m.map(_("name").toString).exists("gg".contentEquals)){
//    println("gg")
//  }
//  for(i <- 1 until 2){
//    println("xx")
//  }
//  val exes = List("2")
//  var x = List(Map("name" -> "1"), Map("name" -> "2"), Map("name" -> "3"))
//  breakable{
//    x.foreach(y=>{
//      println(y("name"))
//      if(y("name") == "2") break
//      println("ganteng")
//    })
//  }
  val x = List("prqModel1Count").map(x=>x.substring(0, x.indexOf("M")))
  println(x)
  println(List("prqModel1Count").map(x=>x.substring(0, x.indexOf("M"))).exists("prq2".contains))
}
