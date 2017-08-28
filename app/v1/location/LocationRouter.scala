package v1.location

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
 * Created by bouke on 07/01/17.
 */
class LocationRouter @Inject()(controller: LocationController) extends SimpleRouter{
  val prefix="/v1/locations"

  def link(id: LocationId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)

    case POST(p"/onderboei") =>
      controller.startschip
  }
}
