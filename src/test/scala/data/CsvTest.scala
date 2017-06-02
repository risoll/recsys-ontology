package data

import com.rizky.ta.config.DBInit
import com.rizky.ta.model.{CloseHours, OpenHours}
import com.rizky.ta.util.MavenToSbtUtil
import org.apache.commons.lang3.StringUtils

import scala.io.Source
import scala.collection.mutable.ListBuffer

/**
  * Created by risol_000 on 3/13/2017.
  */
object CsvTest extends App {
  DBInit.config()
  val openHours = OpenHours.list()
  val closeHours = CloseHours.list()
  println(openHours)
  println(closeHours)
  println(StringUtils.getLevenshteinDistance("Tangkuban Perahu", "Maribaya Hot Springs"))
//  val bufferedSource = Source.fromFile("data/open_hours.csv")
//  bufferedSource.getLines.drop(1).foreach(line=>{
//    val cols = line.split(",").map(_.trim)
//    println(cols.toList, cols.length)
//    OpenHours.create(cols.head.toInt, cols(1), cols(2), cols(3), cols(4), cols(5), cols(6), cols(7))
//  })
}

