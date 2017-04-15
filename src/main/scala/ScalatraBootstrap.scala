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
    }
}
