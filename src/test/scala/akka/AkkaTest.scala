//package akka
//import akka.actor.ActorSystem
//import akka.actor.Status.Success
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model.Uri.Query
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.unmarshalling.Unmarshal
//import akka.serialization.Serialization
//import akka.stream.ActorMaterializer
//import org.json4s.{DefaultFormats, Formats}
//import org.json4s.JsonDSL._
//import net.liftweb.json._
//import net.liftweb.json.Serialization.write
//
//import scala.concurrent.duration._
//import scala.concurrent.{Await, Future, duration}
//import scala.collection.immutable.Seq
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.util.Try
///**
//  * Created by risol_000 on 3/14/2017.
//  */
//object AkkaTest extends App{
//  protected implicit val jsonFormats: Formats = DefaultFormats
//
//  implicit val system = ActorSystem()
//
//  private implicit val materializer = ActorMaterializer()
////
////  val endpoint = "http://api.example.com/abc"
////
////  private case class Example(var1: Int, var2: String)
////
////  private def createRequest(example: Example): HttpRequest =
////    HttpRequest(
////      method = HttpMethods.POST,
////      uri = endpoint,
////      entity = HttpEntity(ContentTypes.`application/json`, write(example)),
////      headers = Seq()
////    )
////
////  Http().singleRequest(createRequest(Example(213, "test")))
//
//  val params = Map(
//    "placeid" -> "ChIJUehhlkrmaC4RhmRh45wK-Ww",
//    "key" -> "AIzaSyBcly9g2k3wE6bmDnCNTMWEa8R3MER-Aiw"
//  )
//
//  val httpResponse: Future[HttpResponse] =
//    Http().singleRequest(
//      HttpRequest(
//        HttpMethods.GET,
//        Uri("https://maps.googleapis.com/maps/api/place/details/json?")
//          .withQuery(Query(params))
//      )
//    )
//  Await.result(httpResponse, 10 seconds)
//  println(httpResponse.map(_.status))
//  httpResponse.onComplete({
//    case Success((HttpResponse)) => {
//
//    }
//  })
//}
