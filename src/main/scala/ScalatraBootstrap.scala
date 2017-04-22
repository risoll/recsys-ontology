import org.scalatra.LifeCycle
import javax.servlet.ServletContext

import com.rizky.ta.config.DBInit
import com.rizky.ta.servlet.{DatasetServlet, PlaceServlet}

/**
  * Created by risol_000 on 1/30/2017.
  */
class ScalatraBootstrap extends LifeCycle{
    override def init(context: ServletContext){
      DBInit.config()
      context.mount(new DatasetServlet, "/*")
      context.mount(new PlaceServlet, "/place/*")


      //    context.initParameters("org.scalatra.cors.allowedOrigins") = "http://10.251.223.122:9200"
      context.initParameters("org.scalatra.cors.enable") = "false"
      context.initParameters("org.scalatra.cors.allowedOrigins") = "http://192.168.2.119:4200,http://jalan-depan.herokuapp.com,http://jalan-jalan.herokuapp.com"

      context.initParameters("org.scalatra.cors.allowedHeaders") = "Content-Type"
      context.initParameters("org.scalatra.cors.allowedMethods") = "POST, OPTIONS, GET"

    }
}
