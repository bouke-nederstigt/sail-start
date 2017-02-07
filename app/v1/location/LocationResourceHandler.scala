package v1.location

import javax.inject.{Inject, Provider}

import play.api.libs.json.{Json, JsValue, Writes}

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by bouke on 07/01/17.
 */

case class LocationResource(id: String, link: String, latitude: Double, longitude: Double)

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

class LocationResourceHandler @Inject()(routerProvider: Provider[LocationRouter], locationRepository: LocationRepository)
                                       (implicit ec: ExecutionContext) {

  def create(locationInput: LocationFormInput): Future[LocationResource] = {
    val data = Location(LocationId(locationInput.id), locationInput.latitude, locationInput.longitude)

    locationRepository. create(data).map({ id => createLocationResource(data) })
  }

  def lookup(id: String): Future[Option[LocationResource]] = {
    val locationFuture = locationRepository.get(LocationId(id))
    locationFuture.map { maybeLocation =>
      maybeLocation.map { location =>
        createLocationResource(location)
      }
    }
  }

  def find: Future[Iterable[LocationResource]] = {
    locationRepository.list().map { locationList =>
      locationList.map(location => createLocationResource(location))
    }
  }

  private def createLocationResource(l: Location): LocationResource = {
    LocationResource(l.id.toString, routerProvider.get.link(l.id), l.latitude, l.longitude)
  }

}
