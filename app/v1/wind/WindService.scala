package v1.wind

import javax.inject.Inject

import play.api.libs.json.{JsResult, JsValue}
import v1.location.Location
import play.api.libs.ws._


import scala.concurrent.Future

/**
 * Created by bouke on 07/08/17.
 */

trait WindService {
  def getWindForLocation(location: Location): Future[Option[Wind]]
}

class OpenApiWindService @Inject()(ws: WSClient) extends WindService {

  case class OpenApiWind(speed : Float, degree: Int)

  val url = "http://api.openweathermap.org/data/2.5/"

  val apiKey = sys.env("OPENWIND_APIKEY")

  private def createUrl(action: String, args: Tuple2[String, String]*): String = {
    url + action + "?APPID=" + apiKey + args.foreach { i => "&" + i._1 + "=" + i._2 }
  }

  override def getWindForLocation(location: Location): Future[Wind] = {
    val url = createUrl("weather", Tuple2("lat", location.latitude.toString), Tuple2("lon", location.longitude.toString))
    val request: WSRequest = ws.url(url)

    val futureResult : Future[JsResult[OpenApiWind]] = request.get().map {
      response => (response.json \ "wind").validate[OpenApiWind]
    }
  }
}
