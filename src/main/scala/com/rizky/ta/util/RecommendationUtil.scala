package com.rizky.ta.util

import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.mutable.ListBuffer

/**
  * Created by solehuddien on 25/05/17.
  */
object RecommendationUtil {

  private val PREFIX =
    """
      PREFIX data:<http://www.semanticweb.org/rizkysolechudin/ontologies/2017/1/wisata#>
      PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
      PREFIX owl:<http://www.w3.org/2002/07/owl#>
      PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
      PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
    """

  def getAttractionsByCategory(category: String, OWL_MODEL: Model): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        ?name rdf:type data:$category }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def generateGeneralQuestions(OWL_MODEL: Model): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        ?name rdfs:subClassOf data:Tempat_Wisata }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def getAllIndividual(OWL_MODEL: Model): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        ?name rdf:type owl:NamedIndividual }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def executeQuery(queryString: String, OWL_MODEL: Model): List[String] ={
    val query = QueryFactory.create(queryString)
    val qe = QueryExecutionFactory.create(query, OWL_MODEL)
    val results = qe.execSelect()
    val tmpResults = ListBuffer[String]()
    while(results.hasNext){
      tmpResults.append(results.next().getResource("name").getLocalName.replace("_", " "))
    }
    tmpResults.toList
  }
}