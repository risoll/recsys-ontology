package com.rizky.ta.util

import com.rizky.ta.model.owl.OwlConst
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.mutable.ListBuffer

/**
  * Created by solehuddien on 25/05/17.
  */
object SparqlUtil {
  def getAttractionsByCategory(category: String, OWL_MODEL: Model): List[String] ={
    val queryString =
      s"""
       ${OwlConst.PREFIX}
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
