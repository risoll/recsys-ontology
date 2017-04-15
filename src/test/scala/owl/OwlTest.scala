package owl

import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
/**
  * Created by risol_000 on 3/11/2017.
  */

object OwlTest extends App{
  def createIri(path: String, fileName: String): IRI ={
    var newPath = path + """\"""
    newPath = newPath.replace("""\""", "/")
    val url = "file:///" + newPath + fileName
    IRI.create(url)
  }

  def createOntology(manager: OWLOntologyManager): OWLOntology ={
    val ont = manager.createOntology(iri)
    manager.saveOntology(ont)
    ont
  }

  def addObjProp(iri: IRI, propName: String): OWLObjectProperty ={
    ObjectProperty(s"$iri#$propName")
  }

  def addClass(iri: IRI, className: String): OWLClass ={
    Class(s"$iri#$className")
  }

  def addIndividual(iri: IRI, individualName: String): OWLNamedIndividual = {
    Individual(s"$iri#$individualName")
  }

  def insertIndividual(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass, owlIndividual: OWLNamedIndividual): Unit ={
    manager.addAxiom(ontology, ClassAssertion(owlClass, owlIndividual))
  }

  def buildClasses(iri: IRI, listClass: List[String]): List[OWLClass] = {
    val classes = ListBuffer[OWLClass]()
    for(cls <- listClass){
      classes += addClass(iri, cls)
    }
    classes.toList
  }

  def buildCommonRelation(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass, owlClass2: OWLClass): Unit ={
    manager.addAxiom(ontology, owlClass SubClassOf owlClass2)
  }

  def buildCommonTree(iri: IRI, ontology: OWLOntology, manager: OWLOntologyManager, root: OWLClass, classes: List[OWLClass]): Unit ={
    for(cls <- classes){
      buildCommonRelation(ontology, manager, cls, root)
    }
  }

  def addClassInto(ontology: OWLOntology, manager: OWLOntologyManager, owlClass: OWLClass): Unit ={
    val factory = manager.getOWLDataFactory
    val axiom = factory.getOWLDeclarationAxiom(owlClass)
    manager.addAxiom(ontology, axiom)
  }

  val fileName = "attractions.owl"
  var path = System.getProperty("user.dir")
  val manager = OWLManager.createOWLOntologyManager()
  val factory = manager.getOWLDataFactory
  val iri = createIri(path, fileName)
  println("iri " + iri)
  val ont = createOntology(manager)
  val PartOf = addObjProp(iri, "PartOf")
  val TempatWisata = addClass(iri, "TempatWisata")

  val roots = buildClasses(iri, OwlConst.roots)
//  val alams = buildClasses(iri, OwlConst.alams)
//  val pegunungans = buildClasses(iri, OwlConst.pegunungans)
//  val pemandangans = buildClasses(iri, OwlConst.pemandangans)
//  val budayas = buildClasses(iri, OwlConst.budayas)
//  val kuliners = buildClasses(iri, OwlConst.kuliners)
//  val internasionals = buildClasses(iri, OwlConst.internasionals)
//  val jajanans = buildClasses(iri, OwlConst.jajanans)
//  val tradisionals = buildClasses(iri, OwlConst.tradisionals)
//  val olahragas = buildClasses(iri, OwlConst.olahragas)
//  val rekreasis = buildClasses(iri, OwlConst.rekreasis)
//  val belanjas = buildClasses(iri, OwlConst.belanjas)
//  val keluargas = buildClasses(iri, OwlConst.keluargas)
//  val edukasis = buildClasses(iri, OwlConst.edukasis)
//  val museums = buildClasses(iri, OwlConst.museums)
//  val tamanBermains = buildClasses(iri, OwlConst.tamanBermains)
//  val spas = buildClasses(iri, OwlConst.spas)
//  val rutes = buildClasses(iri, OwlConst.rutes)

  addClassInto(ont, manager, TempatWisata)
  buildCommonTree(iri, ont, manager, TempatWisata, roots)
//  buildCommonTree(iri, ont, manager, roots.head, alams)
//  buildCommonTree(iri, ont, manager, alams(4), pegunungans)
//  buildCommonTree(iri, ont, manager, alams(5), pemandangans)
//  buildCommonTree(iri, ont, manager, roots(1), budayas)
//  buildCommonTree(iri, ont, manager, roots(2), kuliners)
//  buildCommonTree(iri, ont, manager, kuliners.head, internasionals)
//  buildCommonTree(iri, ont, manager, kuliners(1), jajanans)
//  buildCommonTree(iri, ont, manager, kuliners(2), tradisionals)
//  buildCommonTree(iri, ont, manager, roots(3), olahragas)
//  buildCommonTree(iri, ont, manager, roots(4), rekreasis)
//  buildCommonTree(iri, ont, manager, rekreasis.head, belanjas)
//  buildCommonTree(iri, ont, manager, rekreasis(2), keluargas)
//  buildCommonTree(iri, ont, manager, keluargas.head, edukasis)
//  buildCommonTree(iri, ont, manager, keluargas(2), tamanBermains)
//  buildCommonTree(iri, ont, manager, rekreasis(3), spas)
//  buildCommonTree(iri, ont, manager, roots.last, rutes)

  println(s"before $ont")
  val thr = addIndividual(iri, "THR-Ir-H-Juanda")
  println(thr)
  insertIndividual(ont, manager, roots.head, thr)
  println(s"after $ont")
  manager.saveOntology(ont)
}