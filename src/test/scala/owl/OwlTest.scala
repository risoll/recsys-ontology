package owl

import java.io.{File, FileReader}

import com.rizky.ta.config.DBInit
import com.rizky.ta.model.Classes
import com.rizky.ta.util.{OwlUtil, RecommendationUtil}
import org.apache.jena.ontology.{OntModel, OntModelSpec}
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, ResultSetFormatter, Syntax}
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.util.FileManager
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

/**
  * Created by risol_000 on 3/11/2017.
  */

object OwlTest extends App {
  //  def createIri(path: String, fileName: String): IRI ={
  //    var newPath = path + """\"""
  //    newPath = newPath.replace("""\""", "/")
  //    val url = "file:///" + newPath + fileName
  //    IRI.create(url)
  //  }
  //
  //  def createOntology(manager: OWLOntologyManager): OWLOntology ={
  //    val ont = manager.createOntology(iri)
  //    manager.saveOntology(ont)
  //    ont
  //  }
  //
  //  def addObjProp(iri: IRI, propName: String): OWLObjectProperty ={
  //    ObjectProperty(s"$iri#$propName")
  //  }
  //
  //  def addClass(iri: IRI, className: String): OWLClass ={
  //    Class(s"$iri#$className")
  //  }
  //
  //  def addIndividual(iri: IRI, individualName: String): OWLNamedIndividual = {
  //    Individual(s"$iri#$individualName")
  //  }
  //
  //  def insertIndividual(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass, owlIndividual: OWLNamedIndividual): Unit ={
  //    manager.addAxiom(ontology, ClassAssertion(owlClass, owlIndividual))
  //  }
  //
  //  def buildClasses(iri: IRI, listClass: List[String]): List[OWLClass] = {
  //    val classes = ListBuffer[OWLClass]()
  //    for(cls <- listClass){
  //      classes += addClass(iri, cls)
  //    }
  //    classes.toList
  //  }
  //
  //  def buildCommonRelation(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass, owlClass2: OWLClass): Unit ={
  //    manager.addAxiom(ontology, owlClass SubClassOf owlClass2)
  //  }
  //
  //  def buildCommonTree(iri: IRI, ontology: OWLOntology, manager: OWLOntologyManager, root: OWLClass, classes: List[OWLClass]): Unit ={
  //    for(cls <- classes){
  //      buildCommonRelation(ontology, manager, cls, root)
  //    }
  //  }
  //
  //  def addClassInto(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass): Unit ={
  //    val factory = manager.getOWLDataFactory
  //    val axiom = factory.getOWLDeclarationAxiom(owlClass)
  //    manager.addAxiom(ontology, axiom)
  //  }
  //
  //  val fileName = "attractions.owl"
  //  var path = System.getProperty("user.dir")
  //  val manager = OWLManager.createOWLOntologyManager()
  //  val factory = manager.getOWLDataFactory
  //  val iri = createIri(path, fileName)
  //  println("iri " + iri)
  //  val ont = createOntology(manager)
  //  val PartOf = addObjProp(iri, "PartOf")
  //  val TempatWisata = addClass(iri, "TempatWisata")
  //
  //  val roots = buildClasses(iri, OwlConst.roots)
  ////  val alams = buildClasses(iri, OwlConst.alams)
  ////  val pegunungans = buildClasses(iri, OwlConst.pegunungans)
  ////  val pemandangans = buildClasses(iri, OwlConst.pemandangans)
  ////  val budayas = buildClasses(iri, OwlConst.budayas)
  ////  val kuliners = buildClasses(iri, OwlConst.kuliners)
  ////  val internasionals = buildClasses(iri, OwlConst.internasionals)
  ////  val jajanans = buildClasses(iri, OwlConst.jajanans)
  ////  val tradisionals = buildClasses(iri, OwlConst.tradisionals)
  ////  val olahragas = buildClasses(iri, OwlConst.olahragas)
  ////  val rekreasis = buildClasses(iri, OwlConst.rekreasis)
  ////  val belanjas = buildClasses(iri, OwlConst.belanjas)
  ////  val keluargas = buildClasses(iri, OwlConst.keluargas)
  ////  val edukasis = buildClasses(iri, OwlConst.edukasis)
  ////  val museums = buildClasses(iri, OwlConst.museums)
  ////  val tamanBermains = buildClasses(iri, OwlConst.tamanBermains)
  ////  val spas = buildClasses(iri, OwlConst.spas)
  ////  val rutes = buildClasses(iri, OwlConst.rutes)
  //
  //  addClassInto(ont, manager, TempatWisata)
  //  buildCommonTree(iri, ont, manager, TempatWisata, roots)
  ////  buildCommonTree(iri, ont, manager, roots.head, alams)
  ////  buildCommonTree(iri, ont, manager, alams(4), pegunungans)
  ////  buildCommonTree(iri, ont, manager, alams(5), pemandangans)
  ////  buildCommonTree(iri, ont, manager, roots(1), budayas)
  ////  buildCommonTree(iri, ont, manager, roots(2), kuliners)
  ////  buildCommonTree(iri, ont, manager, kuliners.head, internasionals)
  ////  buildCommonTree(iri, ont, manager, kuliners(1), jajanans)
  ////  buildCommonTree(iri, ont, manager, kuliners(2), tradisionals)
  ////  buildCommonTree(iri, ont, manager, roots(3), olahragas)
  ////  buildCommonTree(iri, ont, manager, roots(4), rekreasis)
  ////  buildCommonTree(iri, ont, manager, rekreasis.head, belanjas)
  ////  buildCommonTree(iri, ont, manager, rekreasis(2), keluargas)
  ////  buildCommonTree(iri, ont, manager, keluargas.head, edukasis)
  ////  buildCommonTree(iri, ont, manager, keluargas(2), tamanBermains)
  ////  buildCommonTree(iri, ont, manager, rekreasis(3), spas)
  ////  buildCommonTree(iri, ont, manager, roots.last, rutes)
  //
  //  println(s"before $ont")
  //  val thr = addIndividual(iri, "THR-Ir-H-Juanda")
  //  println(thr)
  //  insertIndividual(ont, manager, roots.head, thr)
  //  println(s"after $ont")
  //  manager.saveOntology(ont)


  //  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM)
  //  try{
  //    val file = new File(OwlConst.OWL_FILE)
  //    val reader = new FileReader(file)
  //    model.read(reader, null)
  //  }
  //  catch{
  //    case e: Exception => e.printStackTrace()
  //    case _ =>
  //  }
  //  val builder = StringBuilder.newBuilder
  //
  //  builder.append(OwlConst.OWL_PREFIX)
  //  builder.append("SELECT * WHERE {")
  //  builder.append("?Alamat data:Alamat ?Alamat")
  //  builder.append("}")
  //  val sparqlQuery = builder.toString()
  //  val query = QueryFactory.create(sparqlQuery)
  //  val queryExecute = QueryExecutionFactory.create(query, OwlConst.OWL_MODEL)
  //  val results = queryExecute.execSelect()
  //  ResultSetFormatter.out(System.out, results, query)


  //todo OWL_MODEL sama seperti MODEL_WO_REASONER,
  // todo bedanya, MODEL_W_REASONER menghasilkan semua child dibawahnya dengan rekursif
  val OWL_MODEL = ModelFactory.createDefaultModel()
  val MODEL_W_REASONER = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF)
  val MODEL_WO_REASONER = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM)
  val inputStream = FileManager.get().open(OwlConst.OWL_FILE)
  if (inputStream == null) {
    throw new IllegalArgumentException(s"File ${OwlConst.OWL_FILE} not found")
  }
  val inputStream2 = FileManager.get().open(OwlConst.OWL_FILE)
  if (inputStream2 == null) {
    throw new IllegalArgumentException(s"File ${OwlConst.OWL_FILE} not found")
  }

  val inputStream3 = FileManager.get().open(OwlConst.OWL_FILE)
  if (inputStream3 == null) {
    throw new IllegalArgumentException(s"File ${OwlConst.OWL_FILE} not found")
  }
  OWL_MODEL.read(inputStream, null)
  MODEL_W_REASONER.read(inputStream2, null)
  MODEL_WO_REASONER.read(inputStream3, null)

  val OWL_PREFIX =
    "PREFIX data:<http://www.semanticweb.org/rizkysolechudin/ontologies/2017/1/wisata#>" +
    "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" +
    "PREFIX owl:<http://www.w3.org/2002/07/owl#>" +
    "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" +
    "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
    "PREFIX dc:<http://purl.org/dc/elements/1.1/>"

//  val res = RecommendationUtil.getChildrenWoReasoner(OWL_MODEL, "Rekreasi")
  val res = RecommendationUtil.getChildren(OWL_MODEL, "Rekreasi")
  val res2 = RecommendationUtil.getChildren(MODEL_W_REASONER, "Rekreasi")
  val res3 = RecommendationUtil.getChildren(MODEL_WO_REASONER, "Rekreasi")


  val res4 = RecommendationUtil.getChildrenWoReasoner(OWL_MODEL, "Rekreasi")
  val res5 = RecommendationUtil.getChildrenWoReasoner(MODEL_W_REASONER, "Rekreasi")
  val res6 = RecommendationUtil.getChildrenWoReasoner(MODEL_WO_REASONER, "Rekreasi")

  val q = "oleh-oleh"
  val r = q.capitalize
  var res7 = RecommendationUtil.getParent(MODEL_W_REASONER, q)
  val filters = List("Thing", "Resource", "Tempat Wisata")
  val nf = filters:+RecommendationUtil.parseNode(q)
  println(nf)
  res7 = res7.filterNot(x=>nf.exists(x.contentEquals))

  println("res7")
  res7.foreach(r=>println(r))

//  println("BIASA")
//  res.foreach(r=> println(r))
//  println("REASONER")
//  res2.foreach(r=> println(r))
//  println("GA PAKE REASONER")
//  res3.foreach(r=> println(r))
//
//
//  println("BIASA 2")
//  res4.foreach(r=> println(r))
//  println("REASONER 2")
//  res5.foreach(r=> println(r))
//  println("GA PAKE REASONER 2")
//  res6.foreach(r=> println(r))


  //  val queryString =
//    s"""
//      $OWL_PREFIX
//      select * where {
//      VALUES ?value
//      {
//
//      }
//      ?name rdfs:subClassOf data:Alam data:}
//    """

//  val queryString =
//    s"""
//          $OWL_PREFIX
//          select * where {
//         ?name rdfs:subClassOf data:Edukasi }
//    """


//  val queryString =
//    s"""
//          $OWL_PREFIX
//          select * where {
//            data:Museum_Yayasan_Pangeran rdf:type ?name
//
//          }
//    """
//
//
////    val queryString =
////    s"""
////          $OWL_PREFIX
////          select * where {
////            ?name owl:Class rdfs:subClassOf}
////    """
//  DBInit.config()
//  val query = QueryFactory.create(queryString)
//  val qe = QueryExecutionFactory.create(query, OWL_MODEL)
//  val results = qe.execSelect()
//  println("results")
//  var i = 0
//  var name = ""
//  while(results.hasNext){
//    name = results.next.getResource("name").getLocalName
//    name = name.replace("_", " ")
//    println(name)
////    if(name != "Tempat Wisata"){
////      val coba = Classes.getByName(name)
////      println(name, coba.get.image)
////    }
//    i += 1
//  }
//  println(i)
//  OwlUtil.loadOntology()
//  ResultSetFormatter.out(System.out, results, query)
  //  OWL_MODEL.write(System.out)
}
