package com.rizky.ta.util

import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Model

import scala.collection.mutable
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

  def getIndividualByCategory(category: String, OWL_MODEL: Model): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        ?name rdf:type data:${parseNode(category)} }
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

  def getChildren(OWL_MODEL: Model, node: String): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        ?name rdfs:subClassOf data:${parseNode(node)} }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def getParent(OWL_MODEL: Model, node: String): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
        data:${parseNode(node)} rdfs:subClassOf ?name }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def getCategory(OWL_MODEL: Model, node: String): List[String] ={
    val queryString =
      s"""
       $PREFIX
       select * where {
          data:${parseNode(node)} rdf:type ?name }
    """
    executeQuery(queryString, OWL_MODEL)
  }

  def parseNode(node: String): String = {
    node.split(" ").map(_.capitalize).mkString("_")
  }

  def round(num: Double): Double = {
    BigDecimal(num).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def createValues(destNodes: List[String], values: Map[String, Double]): Map[String, Map[String, Double]] ={
    val results = mutable.HashMap[String, Map[String, Double]]()
    destNodes.foreach(node=>{
      results.put(node, Map("pref" -> values("pref"), "conf" -> values("conf")))
    })
    results.toMap
  }

  def updateValues(oldValues: Map[String, Any], newValues: Map[String, Any]): Map[String, Double] ={
    val oldPref = oldValues("pref").asInstanceOf[Double]
    val oldConf = oldValues("conf").asInstanceOf[Double]
    val newPref = newValues("pref").asInstanceOf[Double]
    val newConf = newValues("conf").asInstanceOf[Double]
    println("counter formula update values", oldPref + "*" + oldConf + "+" + newPref + "*" + newConf)
    println("denom formula update values", oldConf + "*" + newConf)

    val counter = (oldPref * oldConf) + (newPref * newConf)
    val denominator = oldConf + newConf
    val a = 0.1
    val totalNode = 2
    val preference = round(counter/denominator)
    val confidence = round(denominator/totalNode - a)
    Map("pref" -> preference, "conf" -> confidence)
  }

  def calcValues(srcNodes: Map[String, Map[String, Any]]): Map[String, Double] ={
    var counter: Double = 0
    var denominator: Double = 0
    srcNodes.foreach(node=>{
      val pref = node._2("pref").asInstanceOf[Double]
      val conf = node._2("conf").asInstanceOf[Double]
      counter += pref * conf
      denominator += conf
    })
    val a = 0.1
    val totalNode = srcNodes.size
    val preference = round(counter/denominator)
    val confidence = round(denominator/totalNode - a)
    Map("pref" -> preference, "conf" -> confidence)
  }

  def createActivationValues(): Unit ={

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
