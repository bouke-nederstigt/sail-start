package v1.wind

import javax.inject.Inject

import play.api.libs.json.{Reads, JsResult, JsValue}
import v1.location.{LocationId, Location}
import play.api.libs.ws._


import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by bouke on 07/08/17.
 */

trait WindService {
  def getWindForLocation(location: Location): Future[Wind]
}

class OpenApiWindService @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends WindService {

  val url = "http://api.openweathermap.org/data/2.5/"

  val apiKey = sys.env("OPENWIND_APIKEY")

  private def createRequest(action: String, args: Tuple2[String, String]*): WSRequest = {
    ws.url(url + action).withQueryString("APPID" -> apiKey).withQueryString(args.toList: _*)
  }

  override def getWindForLocation(location: Location): Future[Wind] = {
    val request: WSRequest = createRequest("weather", Tuple2("lat", location.latitude.toString), Tuple2("lon", location.longitude.toString))

    val futureResult: Future[Wind] = request.get().map {
      response => Wind(location, (response.json \ "wind" \ "speed").get.as[Float], (response.json \ "wind" \ "deg").get.as[Int])
    }

    futureResult
  }
}
