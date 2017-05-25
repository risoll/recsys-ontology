package owl

import java.io.{File, FileReader}

import com.rizky.ta.util.OwlUtil
import org.apache.jena.ontology.OntModelSpec
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import org.semanticweb.owlapi.apibinding.OWLManager

/**
  * Created by risol_000 on 3/13/2017.
  */
object OwlConst {
  val ROOT_PATH = System.getProperty("user.dir")
  val OWL_FILE = "attractions.owl"
  val museums: List[String] = List("MuseumAlam", "MuseumBudaya", "MuseumSejarah")

  val edukasis: List[String] = List("Museum")
  val tamanBermains: List[String] = List("ATV", "WaterPark")
  val internasionals: List[String] = List("Asia", "Jepang", "Korea", "TimurTengah")
  val jajanans: List[String] = List("EsKrim", "OlehOleh")
  val tradisionals: List[String] = List("Makassar", "Padang", "Sunda")

  val pemandangans: List[String] = List("PemandanganAlam", "PemandanganKota")
  val pegunungans: List[String] = List("Camping", "Hiking")
  val spas: List[String] = List("PemandianAirPanas")
  val belanjas: List[String] = List("Fashion",  "OlehOleh", "ShoppingMall")
  val keluargas: List[String] = List("Edukasi", "KebunBinatang", "TamanBermain")

  val alams: List[String] = List("CagarAlam", "Camping", "KebunBinatang", "MuseumAlam", "Pegunungan", "Pemandangan", "PemandianAirPanas", "Taman")
  val budayas: List[String] = List("Monumen", "MuseumBudaya")
  val kuliners: List[String] = List("Internasional", "Jajanan", "Tradisional")
  val olahragas: List[String] = List("Hiking", "OutBound", "Paragliding")
  val rekreasis: List[String] = List("Belanja", "HiburanMalam", "Keluarga", "Spa")
  val rutes: List[String] = List("Hiking", "RuteKota")


  val roots: List[String] = List("Alam", "Budaya", "Kuliner", "Olahraga", "Rekreasi", "Rute")

}
