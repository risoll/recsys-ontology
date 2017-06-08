package com.rizky.ta.util

import java.io.File

import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
/**
  * Created by risol_000 on 3/12/2017.
  */
object OwlUtil {
  /**
    * functions related to OWL API
    */
  def createIri(path: String, fileName: String): IRI ={
    var newPath = path + """\"""
    newPath = newPath.replace("""\""", "/")
    val url = "file:///" + newPath + fileName
    IRI.create(url)
  }

  def createOntology(iri: IRI, manager: OWLOntologyManager): OWLOntology ={
    val ont = manager.createOntology(iri)
    manager.saveOntology(ont)
    ont
  }

  def loadOntology(): Unit = {
    val manager = OWLManager.createOWLOntologyManager()
    val file = new File("data/attractions.owl")
    // Now load the local copy
    val ont = manager.loadOntologyFromOntologyDocument(file)
    println("Loaded ontology: " + ont)
    // We can always obtain the location where an ontology was loaded from
    val documentIRI = manager.getOntologyDocumentIRI(ont)
    println("    from: " + documentIRI)
    // Remove the ontology again so we can reload it later
    manager.removeOntology(ont)
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
}
