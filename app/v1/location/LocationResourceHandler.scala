package v1.location

import javax.inject.{Inject, Provider}

import play.api.libs.json.{Json, JsValue, Writes}
import v1.wind.{WindService, WindRepository}

//import v1.wind.Wind

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by bouke on 07/01/17.
 */

case class LocationResource(id: String, link: String, latitude: Double, longitude: Double, locationType: String)

/**
 * Mapping to convert location resource to json
 */
object LocationResource {
  implicit val jsonWrites = new Writes[LocationResource] {
    def writes(location: LocationResource) =
      Json.obj("id" -> location.id, "latitude" -> location.latitude, "longitude" -> location.longitude, "locationType" -> location.locationType)
  }
}

class LocationResourceHandler @Inject()(routerProvider: Provider[LocationRouter], locationRepository: LocationRepository, windRepository: WindRepository, windService: WindService, locationCalculator: LocationCalculator)
                                       (implicit ec: ExecutionContext) {

  def create(locationInput: LocationFormInput): Future[LocationResource] = {
    val data: Location = locationInput.locationType match {
      case "startschip" => StartSchip(LocationId(locationInput.id), locationInput.latitude, locationInput.longitude)
      case "bovenboei" => {
        val bovenboei = BovenBoei(LocationId(locationInput.id), locationInput.latitude, locationInput.longitude)
        windService.getWindForLocation(bovenboei).map({ wind => windRepository.save(wind) })
        bovenboei
      }
    }

    locationRepository.create(data).map({ id => createLocationResource(data) })
  }

  def getOnderboei(onderboeiInput: OnderBoeiFormInput): Future[Option[LocationResource]] = {
    for {
      startSchip <- locationRepository.get(LocationId(onderboeiInput.startschipId))

      bovenboeiWind = windRepository.findByLocation(LocationId(onderboeiInput.bovenboeiId))

      onderBoei <- if (bovenboeiWind.isDefined && startSchip.isDefined) {
        //@TODO: Make repository return case class instead of generic Location object
        Future(Some(locationCalculator.calculateOnderBoei(startSchip.get, 200, locationCalculator.getRightAngleWind(bovenboeiWind.get).degree)))
      } else Future(None)

    } yield onderBoei.map(location => createLocationResource(location))

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
    LocationResource(l.id.toString, routerProvider.get.link(l.id), l.latitude, l.longitude, l.locationType)
  }

}
