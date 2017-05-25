package owl

import java.io.InputStream

import com.rizky.ta.config.DBInit
import org.apache.jena.query.{DatasetAccessor, DatasetAccessorFactory}
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource}

/**
  * Created by solehuddien on 23/05/17.
  */
object JenaTest extends App{
  //URI Declarations
  val attractionsUri = "http://attractions/"
  val relationUri = "http://relationship/"

  //create resource for each member, identified by URI
  val model = ModelFactory.createDefaultModel()
  val agroWisataGambung = model.createResource(s"${attractionsUri}Agro_Wisata_Gambung")
  val curugEli = model.createResource(s"${attractionsUri}Curug_Eli")
  val curugSalamanja = model.createResource(s"${attractionsUri}Curug_Salamanja")
  val alam = model.createResource(s"${attractionsUri}Alam")

  val childOf = model.createProperty(relationUri, "childOf")
  val parentOf = model.createProperty(relationUri, "parentOf")
  val siblingOf = model.createProperty(relationUri, "siblingOf")

  curugEli.addProperty(childOf, alam)
  curugSalamanja.addProperty(childOf, alam)
  agroWisataGambung.addProperty(childOf, alam)
  alam.addProperty(parentOf, curugEli)
  alam.addProperty(parentOf, curugSalamanja)
  alam.addProperty(parentOf, agroWisataGambung)

//  val parents = model.listSubjectsWithProperty(parentOf)
//  while(parents.hasNext){
//    val child = parents.nextResource()
//    println(child.getURI)
//  }
//
//  val children = model.listSubjectsWithProperty(childOf)
//  while(children.hasNext){
//    val child = children.nextResource()
//    println(child.getURI)
//  }

//  val moreParents = model.listObjectsOfProperty(childOf)
//  while (moreParents.hasNext){
//    println("moreParents", moreParents.nextNode().asResource().getURI)
//  }
//
//  val childrenOfAlam = alam.listProperties(parentOf)
//  while(childrenOfAlam.hasNext){
//    println("childrenOfAlarm", childrenOfAlam.nextStatement().getResource.getURI)
//  }

  val listStatement = model.listStatements(alam, parentOf, null)
  while(listStatement.hasNext){
    println(listStatement.nextStatement().getResource.getURI)
  }

  val serviceURI = "http://localhost:3030/ds/data"
  val accessor = DatasetAccessorFactory.createHTTP(serviceURI)

}
