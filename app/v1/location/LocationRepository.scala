package v1.location


import scala.collection.mutable
import scala.concurrent.Future

import javax.inject.{Inject, Singleton}

/**
 * Created by bouke on 07/01/17.
 */

final case class Location(id: LocationId, latitude: Double, longitude: Double)

class LocationId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object LocationId {
  def apply(raw: String): LocationId = {
    require(raw != null)
    new LocationId(Integer.parseInt(raw))
  }
}

trait LocationRepository {
  def create(data: Location): Future[LocationId]

  def list(): Future[Iterable[Location]]

  def get(id: LocationId): Future[Option[Location]]
}

@Singleton
class InMemoryLocationRepository @Inject() extends LocationRepository {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val locations = mutable.HashMap.empty[LocationId, Location]

  override def list(): Future[Iterable[Location]] = {
    Future.successful {
      logger.trace(s"list: ")
      locations.valuesIterator.toList
    }
  }

  override def get(id: LocationId): Future[Option[Location]] = {
    Future.successful {
      logger.trace(s"get: id = $id")
      locations.get(id)
    }
  }

  override def create(data: Location): Future[LocationId] = {
    Future.successful {
      locations += (data.id -> data)
      data.id
    }
  }
}


