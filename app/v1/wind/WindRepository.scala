package v1.wind

import v1.location.{LocationId, Location}
import scala.collection.mutable

final case class Wind(location: Location, speed: Float, degree: Int)

trait WindRepository {
  def save(wind: Wind): Boolean

  def findAll(): Iterable[Wind]

  def findByLocation(locationId: LocationId): Option[Wind]
}

class InMemoryWindRepository extends WindRepository {
  private val winds = mutable.HashMap.empty[LocationId, Wind]

  override def save(wind: Wind): LocationId = {
    winds += (wind.location.id -> wind)
    wind.location.id
  }

  override def findAll(): Iterable[Wind] = {
    winds.valuesIterator.toList
  }

  override def findByLocation(locationId: LocationId): Option[Wind] = {
    winds.get(locationId)
  }
}
