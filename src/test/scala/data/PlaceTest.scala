package data

import com.rizky.ta.config.DBInit
import com.rizky.ta.model.Place
import com.rizky.ta.util.{CommonUtil, GoogleUtil, RecommendationUtil}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager

import scala.collection.mutable.ListBuffer

/**
  * Created by solehuddien on 30/05/17.
  */
object PlaceTest extends App {
  private val OWL_FILE = "attractions.owl"
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
    "lng", "rating", "open_hours_monday", "open_hours_tuesday", "open_hours_wednesday", "open_hours_thursday",
    "open_hours_friday", "open_hours_saturday", "open_hours_sunday", "close_hours_monday", "close_hours_tuesday",
    "close_hours_wednesday", "close_hours_thursday", "close_hours_friday", "close_hours_saturday",
    "close_hours_sunday"
  )

  private val numericKeys = List(
    "tariff", "lat", "lng", "rating"
  )

  //  private val places = List(
  //    "Seni Calung dan Jaipongan",
  //    "Desa Wisata Rawakalong",
  //    "Situ Cileunca",
  //    "Kampung Toga",
  //    "Kolam Tirta Riang",
  //    "Gunung Kunci",
  //    "Strawberry 65",
  //    "Green Hill Park",
  //    "Curug Tilu",
  //    "Peneropongan Bintang Boscha",
  //    "Bandung Giri Gahana",
  //    "Maribaya",
  //    "Desa Wisata Lebak Muncang",
  //    "Terbang Buhun Pusaka Medal Laksana",
  //    "Kin Strawberry",
  //    "Curug Salamanja",
  //    "Tebing Keraton",
  //    "Ranca Saladah Waterboom",
  //    "Museum Asia Afrika",
  //    "Wisata Belanja Cibaduyut",
  //    "Museum SriBaduga",
  //    "Makam Dayeuh Luhur",
  //    "Komunitas Batar Ulin",
  //    "Wisata Belanja Ciwalk",
  //    "Makam Marangge",
  //    "Curug Eli",
  //    "Wisata Lebah Madu",
  //    "Situ Lembang",
  //    "Makam Kabuyutan",
  //    "Yasmin Kartika Sari",
  //    "Kawah Kamojang",
  //    "Museum Yayasan",
  //    "Tamada All Adventure Team",
  //    "Petik Strawberry Family",
  //    "Fragaria Strawberry",
  //    "Gua Pawon",
  //    "Agrowisata Gambung",
  //    "Curug Cinulang",
  //    "Situ Dano",
  //    "Danau Ciharus",
  //    "Situ Cisanti",
  //    "Cipanas Sekarwangi",
  //    "THR. Ir. H. Juanda",
  //    "Air Terjun Cipanji",
  //    "Situs Bumi Alit Kabuyutan",
  //    "Bulu Ngampar",
  //    "Pondok Strawberry",
  //    "Waduk Saguling",
  //    "Situs Cikahuripan",
  //    "Pak Ale Strawberry",
  //    "Situs Makam Eyang Pakujaga",
  //    "Museum Yayasan Pangeran",
  //    "Masigit Kareumbi",
  //    "Museum Siliwangi",
  //    "Cipanas Cileungsing",
  //    "Bale Bambu",
  //    "Museum Pos Indonesia",
  //    "Kolam Renang Valley Ciwidey",
  //    "Curug Cilengkrang",
  //    "Wisata Rohani Daarut Tauhid",
  //    "Kawah Rengganis",
  //    "Geology Museum",
  //    "Taman Lalu Lintas Ade Lima",
  //    "Cibolang",
  //    "Kolam Renang Priangan Tirta",
  //    "Karang Setra",
  //    "Cibingbin",
  //    "Situ Ciburuy",
  //    "Gunung Tangkuban Perahu",
  //    "Sinar Asih Petik Strawberry",
  //    "Kebun Binatang",
  //    "Kolam Renang Oniba",
  //    "Kindy Strawberry",
  //    "Petik Strawberry The Oneng",
  //    "Batu Kuda",
  //    "Menara Mesjid Raya jabar",
  //    "Indi Strawberry",
  //    "Perkebunan Rancabali",
  //    "Ranca Upas",
  //    "Kawah Putih",
  //    "Jayagiri",
  //    "Kolam Renang Tirta Nadi",
  //    "Petik Strawberry Raffa",
  //    "Saung Angklung Udjo",
  //    "Waduk Cirata",
  //    "Petik Strawberry Mr. Dede",
  //    "Situ Patenggang",
  //    "Walini",
  //    "TWA Cimanggu",
  //    "Regar Orchid",
  //    "Puncak Bintang Moko",
  //    "Museum Gaeusan Ulun"
  //  )

  private val places = List(
    "Terbang Buhun Pusaka Medal Laksana",
    "Kin Strawberry",
    "Curug Salamanja",
    "Wisata Belanja Cibaduyut",
    "Komunitas Batar Ulin",
    "Curug Eli",
    "Wisata Lebah Madu",
    "Yasmin Kartika Sari",
    "Museum Yayasan",
    "Tamada All Adventure Team",
    "Agrowisata Gambung",
    "Cipanas Sekarwangi",
    "Air Terjun Cipanji",
    "Situs Bumi Alit Kabuyutan",
    "Bulu Ngampar",
    "Pak Ale Strawberry",
    "Situs Makam Eyang Pakujaga",
    "Museum Yayasan Pangeran",
    "Museum Pos Indonesia",
    "Kawah Rengganis",
    "Karang Setra",
    "Situ Ciburuy",
    "Sinar Asih Petik Strawberry",
    "Kindy Strawberry",
    "Petik Strawberry The Oneng",
    "Indi Strawberry",
    "Petik Strawberry Raffa",
    "Saung Angklung Udjo",
    "Petik Strawberry Mr. Dede",
    "Regar Orchid"
  )

  DBInit.config()
  createValues()
//  checkNull()

  def checkNull(): Unit = {
    val buffer: ListBuffer[String] = ListBuffer()
    places.foreach(place => {
      val x = Place.getByName(place)
      x match {
        case Some(value) =>
        case None =>
          buffer.append(place)
          println(place)
      }
    })
    println(buffer.size)
  }


  def createValues(): Unit = {
    val buffer: ListBuffer[String] = ListBuffer()

    var tmpResult: Map[String, Any] = Map()
    places.foreach(place => {
      tmpResult = Map()
      val textSearchResult = GoogleUtil.textSearch(place, bandungLat, bandungLng, radius)
      println("textsearchresult", textSearchResult)
      if (textSearchResult.keys.nonEmpty) {
        placeKeys.foreach(key => {
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
          //          else if (key.contains("open") || key.contains("close")) {
          //            if (key.contains("open"))
          //              textSearchResult.get("opening_hours") match {
          //                case Some(openingHours) =>
          //                  openingHours.asInstanceOf[Map[String, Any]].get("periods") match {
          //                    case Some(periods) =>
          //                      periods.asInstanceOf[List[Map[String, Any]]].foreach(period=>{
          //                        if(key.contains("open")){
          //                          period.get("open") match {
          //                            case Some(open) =>
          //                              val day = open.asInstanceOf[Map[String, Any]]("day")
          //
          //                          }
          //                        }
          //                      })
          //                    case None =>
          //                      tmpResult += key -> ""
          //                  }
          //                case None =>
          //                  tmpResult += key -> ""
          //              }
          //          }
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
        if (Place.get(placeId).isEmpty) {
          buffer.append(placeId)
          val name = tmpResult("name").toString
          val formattedAddress = tmpResult("formatted_address").toString
          val phone = tmpResult("phone").toString
          val lengthOfVisit = tmpResult("length_of_visit").toString
          val tariff = tmpResult("tariff").toString.toInt
          val photo = tmpResult("photo").toString
          val lat = tmpResult("lat").toString.toDouble
          val lng = tmpResult("lng").toString.toDouble
          val rating = tmpResult("rating").toString.toDouble
          val openHoursMonday = tmpResult("open_hours_monday").toString
          val openHoursTuesday = tmpResult("open_hours_tuesday").toString
          val openHoursWednesday = tmpResult("open_hours_wednesday").toString
          val openHoursThursday = tmpResult("open_hours_thursday").toString
          val openHoursFriday = tmpResult("open_hours_friday").toString
          val openHoursSaturday = tmpResult("open_hours_saturday").toString
          val openHoursSunday = tmpResult("open_hours_sunday").toString
          val closeHoursMonday = tmpResult("close_hours_monday").toString
          val closeHoursTuesday = tmpResult("close_hours_tuesday").toString
          val closeHoursWednesday = tmpResult("close_hours_wednesday").toString
          val closeHoursThursday = tmpResult("close_hours_thursday").toString
          val closeHoursFriday = tmpResult("close_hours_friday").toString
          val closeHoursSaturday = tmpResult("close_hours_saturday").toString
          val closeHoursSunday = tmpResult("close_hours_sunday").toString
          println("data", name, photo, tariff)
          println("buffer", buffer)
          Place.create(placeId, name, formattedAddress, phone, lengthOfVisit,
            tariff, photo, lat, lng, rating, openHoursMonday, openHoursTuesday,
            openHoursWednesday, openHoursThursday, openHoursFriday, openHoursSaturday,
            openHoursSunday, closeHoursMonday, closeHoursTuesday, closeHoursWednesday,
            closeHoursThursday, closeHoursFriday, closeHoursSaturday, closeHoursSunday)
        }
      }
    })
  }

  def checkValue(key: String): Any = {
    if (numericKeys.contains(key)) 0 else ""
  }
}
