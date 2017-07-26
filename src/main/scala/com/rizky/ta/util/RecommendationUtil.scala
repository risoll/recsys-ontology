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
  // decay factor untuk confidence value per level
  // nilai 0.24 diambil karena total depth di ontology berjumlah 4
  private val alpha = 0.24

  // decay factor untuk update preference dan confidence value
  private val beta = 0.3

  // decay factor untuk normalisasi activation level
  private val normFactorActivation = 0.49
//  private val normFactorActivation = 0.2

  val fp = 0.6 // threshold untuk preference value
  val fc = 0.6 //threshold untuk confidence value
  val fa = 0.6 //threshold untuk activation value

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

  def updatePreference(pOld: Double, pAssign: Double): Double ={
    round(Math.min(1, pOld + beta * pAssign))
  }

  def updateConfidence(cOld: Double, cAssign: Double): Double = {
    round(beta * cOld + (1 - beta) * cAssign)
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

  def filterLeafNodes(OWL_MODEL: Model, nodes: List[String]): List[String] = {
    val newNodes = ListBuffer[String]()
    nodes.foreach(node=>{
      if(getChildren(OWL_MODEL, node).isEmpty)
        if(!newNodes.exists(node.contentEquals))
          newNodes.append(node)
    })
    newNodes.toList
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

  def calcValues(srcNodes: List[Map[String, Any]], agg: Boolean = false): Map[String, Double] ={
    var counter: Double = 0
    var denominator: Double = 0
    srcNodes.foreach(node=>{
      val pref = node("pref").toString.toDouble
      val conf = node("conf").toString.toDouble
      counter += pref * conf
      denominator += conf
    })
    val totalNode = srcNodes.size
    val preference = round(counter/denominator)
    var confidence: Double = 0.0
    if(!agg)
      confidence = round(denominator/totalNode - alpha)
    else
      confidence = round(denominator/totalNode)
    Map("pref" -> preference, "conf" -> Math.max(0, confidence))
  }

  def updateFromAgg(pOld: Double, pAgg: Double, cOld: Double, cAgg: Double): Map[String, Double] = {
    val pref = round(((1 - beta) * cOld * pOld + beta * cAgg * pAgg) / ((1-beta) * pOld + beta * pAgg))
    val conf = round(beta * cOld + (1 - beta) * cAgg)
    Map("pref" -> pref, "conf" -> conf)
  }

  def getActivation(neighbors: List[Map[String, Any]], a: Double = 0.0): Double ={
    //
    var newActivation = a
//    var newActivation = 0.0
    val linkWeight: Double = 1.0 / neighbors.size * normFactorActivation
//    val linkWeight: Double = neighbors.size * normFactorActivation
//    val linkWeight: Double = calcNILF(possibleNodesSize, neighbors.size)
    neighbors.foreach(neighbor=>{
      var value = 0.0
      neighbor.get("activation") match {
        case Some(activation) =>
          value = activation.toString.toDouble
        case None =>
          value = neighbor("pref").toString.toDouble
      }
      newActivation += value * linkWeight
    })
    println("LINK", linkWeight, "a", a, "newA", newActivation, "size", neighbors.size)
    Math.min(1, round(newActivation))
  }

  def calcNILF(possibleNodesSize: Int, neighborsSize: Int): Double = {
    val ILF = Math.log(possibleNodesSize/neighborsSize)
    ILF/Math.log(possibleNodesSize)
  }

  def mapToList(nodes: Map[String, Map[String, Double]]): List[Map[String, Any]] ={
    val res = ListBuffer[Map[String, Any]]()
    nodes.foreach(node => {
      res.append(Map(
        "name" -> node._1,
        "pref" -> node._2("pref"),
        "conf" -> node._2("conf")
      ))
    })
    res.toList
  }

  def listToMap(nodes: List[Map[String, Any]]): Map[String, Map[String, Double]] = {
    val res = mutable.HashMap[String, Map[String, Double]]()
    nodes.foreach(node=>{
      res.put(node("name").toString, Map(
        "pref" -> node("pref").asInstanceOf[Double],
        "conf" -> node("conf").asInstanceOf[Double]
      ))
    })
    res.toMap
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
