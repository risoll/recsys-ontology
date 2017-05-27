package data

import com.rizky.ta.util.MavenToSbtUtil

import scala.collection.mutable.ListBuffer

/**
  * Created by risol_000 on 3/13/2017.
  */
object CsvTest extends App {
  //  val lines = "Kawah Putih;Ranca Upas;Situ Patenggang;TWA Cimanggu;Walini;Curug Tilu;Kawah Rengganis;Kolam Renang Valley Ciwidey;Perkebunan Rancabali Ciwidey;Situ "
  //  val lines = "ganteng"
  //  val lines = "Kec. Paseh, Curug Eli;Curug Salamanja;Ranca Saladah Waterboom,Situs Cikahuripan;Situs Makam Eyang Pakujaga,-"
  //  val Array(lokasi, alam, budaya, minatKhusus) = lines.split(",").map(_.trim)
  //  val list = new ListBuffer[String]()
  //  println(lokasi)
  //  println(alam)
  //  val alams = alam.split(";").map(_.trim)
  //  println(budaya)
  //  println(minatKhusus)
  //  alams.foreach(x=>{
  //    println(x)
  //  })
  //  splitted.foreach{ x=>
  //    println(x)
  //    list += x
  //  }
  //  println(list)
  val xml =
  <dependencies>
    <dependency>
      <groupId>se.walkercrou</groupId>
      <artifactId>google-places-api-java</artifactId>
      <version>2.1.2</version>
    </dependency>
  </dependencies>


  println(MavenToSbtUtil.createSbtString(xml))
}

