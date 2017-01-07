package v1.location

import play.api.libs.json.{Json, JsValue, Writes}

/**
 * Created by bouke on 07/01/17.
 */

case class LocationResource(id: String, latitude: Double, longitude: Double)

/**
 * Mapping to convert location resource to json
 */
object LocationResource {
  implicit val implicitWrites = new Writes[LocationResource] {
    override def writes(location: LocationResource): JsValue = {
      Json.obj("id" -> location.id, "latitude" -> location.latitude, "longitude" -> location.longitude)
    }
  }
}
