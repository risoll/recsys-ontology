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
    val query = QueryFactory.create(queryString)
    val qe = QueryExecutionFactory.create(query, OWL_MODEL)
    val results = qe.execSelect()
    val attractions = ListBuffer[String]()
    while(results.hasNext){
      attractions.append(results.next().getResource("name").getLocalName)
    }
    attractions.toList
  }
}
