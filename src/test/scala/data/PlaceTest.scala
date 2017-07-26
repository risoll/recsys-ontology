package data

import com.rizky.ta.config.DBInit
import com.rizky.ta.model.Place
import com.rizky.ta.util.{CommonUtil, GoogleUtil, RecommendationUtil}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import org.json4s.jackson.Json
import org.json4s.native.Serialization

import scala.collection.mutable.ListBuffer

/**
  * Created by solehuddien on 30/05/17.
  */
object PlaceTest extends App {
  private val OWL_FILE = "data/attractions.owl"
  private val OWL_MODEL = ModelFactory.createDefaultModel()
  private val inputStream = FileManager.get().open(OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File $OWL_FILE not found")
  }
  OWL_MODEL.read(inputStream, null)

  private val bandungLat = -6.917464
  private val bandungLng = 107.619123
  private val radius = 5
  private val maxWidth = 200
  private val maxHeight = 200

  private val placeKeys = List(
    "place_id", "name", "formatted_address", "phone", "length_of_visit", "tariff", "photo", "lat",
    "lng", "rating", "open_hours_id", "close_hours_id"
  )

  private val numericKeys = List(
    "tariff", "lat", "lng", "rating", "open_hours_id", "close_hours_id"
  )


  private val textSearchKey = List(
    "place_id", "name", "formatted_address",
    "photo", "lat", "lng", "rating", "tariff"
  )

  private val detailsKey = List(
    "length_of_visit", "opening_hours", "formatted_phone_number"
  )

  private val places = List(
//    "Seni Calung dan Jaipongan",
//    "Desa Wisata Rawakalong",
//    "Situ Cileunca",
//    "Kampung Toga",
    "Kolam Tirta Riang",
    "Gunung Kunci",
//    "Strawberry 65",
//    "Green Hill Park",
//    "Curug Tilu",
//    "Peneropongan Bintang Boscha",
//    "Bandung Giri Gahana",
//    "Maribaya",
    "Desa Wisata Lebak Muncang",
    "Terbang Buhun Pusaka Medal Laksana",
    "Kin Strawberry",
    "Curug Salamanja",
//    "Tebing Keraton",
//    "Ranca Saladah Waterboom",
//    "Museum Asia Afrika",
//    "Wisata Belanja Cibaduyut",
//    "Museum SriBaduga",
//    "Makam Dayeuh Luhur",
    "Komunitas Batar Ulin",
    "Wisata Belanja Ciwalk",
//    "Makam Marangge",
    "Curug Eli",
//    "Wisata Lebah Madu",
//    "Situ Lembang",
    "Makam Kabuyutan",
    "Yasmin Kartika Sari",
    "Kawah Kamojang",
//    "Museum Yayasan",
    "Tamada All Adventure Team",
    "Petik Strawberry Family",
    "Fragaria Strawberry",
    "Gua Pawon",
//    "Agrowisata Gambung",
    "Curug Cinulang",
    "Situ Dano",
    "Danau Ciharus",
    "Situ Cisanti",
    "Cipanas Sekarwangi",
//    "THR. Ir. H. Juanda",
    "Air Terjun Cipanji",
//    "Situs Bumi Alit Kabuyutan",
    "Bulu Ngampar",
    "Pondok Strawberry",
    "Waduk Saguling",
    "Situs Cikahuripan",
    "Pak Ale Strawberry",
    "Situs Makam Eyang Pakujaga",
//    "Museum Yayasan Pangeran",
//    "Masigit Kareumbi",
    "Museum Siliwangi",
    "Cipanas Cileungsing",
//    "Bale Bambu",
//    "Museum Pos Indonesia",
    "Kolam Renang Valley Ciwidey",
    "Curug Cilengkrang",
    "Wisata Rohani Daarut Tauhid",
    "Kawah Rengganis",
    "Geology Museum",
    "Taman Lalu Lintas Ade Lima",
    "Cibolang",
//    "Kolam Renang Priangan Tirta",
//    "Karang Setra",
    "Cibingbin",
    "Situ Ciburuy",
    "Gunung Tangkuban Perahu",
    "Sinar Asih Petik Strawberry",
//    "Kebun Binatang",
//    "Kolam Renang Oniba",
    "Petik Strawberry The Oneng",
    "Batu Kuda",
//    "Menara Mesjid Raya jabar",
//    "Indi Strawberry",
    "Perkebunan Rancabali",
//    "Ranca Upas",
    "Kawah Putih",
    "Jayagiri",
//    "Kolam Renang Tirta Nadi",
    "Petik Strawberry Raffa",
//    "Saung Angklung Udjo",
    "Waduk Cirata",
    "Petik Strawberry Mr. Dede",
    "Situ Patenggang",
    "Walini",
//    "TWA Cimanggu",
    "Regar Orchid",
//    "Puncak Bintang Moko",
    "Museum Gaeusan Ulun"
  )

  DBInit.config()
  createValues()
//  checkNull()
//  println("place", Place.get("ChIJcVwN9hfRaC4RDogyRPU6Rvk"))

//  def checkNull(): Unit = {
//    val buffer: ListBuffer[String] = ListBuffer()
//    places.foreach(place => {
//      val x = Place.getByName(place)
//      x match {
//        case Some(value) =>
//        case None =>
//          buffer.append(place)
//          println(place)
//      }
//    })
//    println(buffer.size)
//  }


  def createValues(): Unit = {
    val buffer: ListBuffer[String] = ListBuffer()
    var tmpResult: Map[String, Any] = Map()
    var tmpResult2: Map[String, Any] = Map()
    places.foreach(place => {
      tmpResult = Map()
      tmpResult2 = Map()
      val textSearchResult = GoogleUtil.textSearch(place, bandungLat, bandungLng, radius)
      println("textsearchresult", textSearchResult)
      if (textSearchResult.keys.nonEmpty) {
        textSearchKey.foreach(key => {
          if (key == "photo") {
            textSearchResult.get("photos") match {
              case Some(photos) =>
                val photoRef = photos.asInstanceOf[List[Map[String, Any]]].head("photo_reference").toString
                val photo = GoogleUtil.getPhoto(200, 200, photoRef)
                tmpResult += key -> photo
              case None =>
                tmpResult += key -> ""
            }
          }
          else if (key == "lat" || key == "lng") {
            textSearchResult.get("geometry") match {
              case Some(geometry) =>
                geometry.asInstanceOf[Map[String, Any]].get("location") match {
                  case Some(location) =>
                    val parsedLocation = location.asInstanceOf[Map[String, Any]]
                    if (key == "lat")
                      parsedLocation.get("lat") match {
                        case Some(lat) =>
                          tmpResult += key -> lat
                        case None =>
                          tmpResult += key -> 0
                      }
                    else
                      parsedLocation.get("lng") match {
                        case Some(lng) =>
                          tmpResult += key -> lng
                        case None =>
                          tmpResult += key -> 0
                      }
                  case None =>
                    tmpResult += key -> 0
                }
              case None =>
                tmpResult += key -> 0
            }
          }
          else if (key == "name")
            tmpResult += key -> place
          else
            textSearchResult.get(key) match {
              case Some(value) =>
                tmpResult += key -> value
              case None =>
                tmpResult += key -> checkValue(key)
            }

        })
        println("tmpresult", tmpResult)
        val placeId = tmpResult("place_id").toString
        val placeDetailsResult = GoogleUtil.getPlaceDetails(placeId)
        println("placedetails", placeDetailsResult)
        if(placeDetailsResult.keys.nonEmpty){
          detailsKey.foreach(key=>{
            if(key == "opening_hours"){
              placeDetailsResult.get(key) match {
                case Some(openingHours) =>
                  openingHours.asInstanceOf[Map[String, Any]].get("weekday_text") match {
                    case Some(weekdayText) =>
                      tmpResult2 += "weekday_text" -> weekdayText.asInstanceOf[List[String]]
                    case None =>
                      tmpResult2 += "weekday_text" -> List()
                  }
                case None =>
                  tmpResult2 += "weekday_text" -> List()
              }
            }
            else{
              placeDetailsResult.get(key) match {
                case Some(value) =>
                  tmpResult2 += key -> value
                case None =>
                  tmpResult2 += key -> ""
              }
            }
          })
          if (Place.get(placeId).isEmpty) {
            val name = tmpResult("name").toString
            val formattedAddress = tmpResult("formatted_address").toString
            val phone = tmpResult2("formatted_phone_number").toString
            val lengthOfVisit = tmpResult2("length_of_visit").toString
            val tariff = tmpResult("tariff").toString.toInt
            val photo = tmpResult("photo").toString
            val lat = tmpResult("lat").toString.toDouble
            val lng = tmpResult("lng").toString.toDouble
            val rating = tmpResult("rating").toString.toDouble
            val weekdayText = tmpResult2("weekday_text").asInstanceOf[List[String]]
            val description = ""
            var monday = ""
            var tuesday = ""
            var wednesday = ""
            var thursday = ""
            var friday = ""
            var saturday = ""
            var sunday = ""
            if(weekdayText.nonEmpty){
              monday = weekdayText.head.substring(weekdayText.head.indexOf("day:") + 5, weekdayText.head.length)
              tuesday = weekdayText(1).substring(weekdayText(1).indexOf("day:") + 5, weekdayText(1).length)
              wednesday = weekdayText(2).substring(weekdayText(2).indexOf("day:") + 5, weekdayText(2).length)
              thursday = weekdayText(3).substring(weekdayText(3).indexOf("day:") + 5, weekdayText(3).length)
              friday = weekdayText(4).substring(weekdayText(4).indexOf("day:") + 5, weekdayText(4).length)
              saturday = weekdayText(5).substring(weekdayText(5).indexOf("day:") + 5, weekdayText(5).length)
              sunday = weekdayText(6).substring(weekdayText(6).indexOf("day:") + 5, weekdayText(6).length)

            }
            println("INSERTED", place, "\n")
            Place.create(placeId, name, formattedAddress, phone, lengthOfVisit,
              tariff, photo, lat, lng, rating, description, monday,
              tuesday, wednesday, thursday, friday, saturday, sunday)
          }
        }

      }
      buffer.append(place)
    })
    println("buffer", buffer.toString)
  }

  def checkValue(key: String): Any = {
    if (numericKeys.contains(key)) 0 else ""
  }
}
