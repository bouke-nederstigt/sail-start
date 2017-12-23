package v1.location

import v1.wind.Wind

/**
 * Created by bouke on 03/09/17.
 */

class LocationCalculator {
  /**
   * Returns the destination point from ‘this’ point having travelled the given distance on the
   * given initial bearing (bearing normally varies around path followed).
   */
  def calculateOnderBoei(startSchip: Location, startLineLength: Int, startLineBearing: Int): OnderBoei = {
    val φ1 = math.toRadians(startSchip.latitude)
    val λ1 = math.toRadians(startSchip.longitude)
    val θ = math.toRadians(startLineBearing.toDouble)

    val sinφ1 = math.sin(φ1)
    val cosφ1 = math.cos(φ1)

    val cosAd = math.cos(getAngularDistance(startLineLength))
    val sinAd = math.sin(getAngularDistance(startLineLength))

    val cosλ1 = math.cos(λ1)
    val sinλ1 = math.sin(λ1)

    val cosθ = math.cos(θ)
    val sinθ = math.sin(θ)

    val sinφ2 = sinφ1 * cosAd + cosφ1 * sinAd * cosθ
    val φ2 = math.asin(sinφ2)

    val y = sinθ * sinAd * cosφ1
    val x = cosAd - sinφ1 * sinφ2

    val λ2 = λ1 + math.atan2(y, x)

    val lat2 = math.toDegrees(φ2)
    val long2 = (math.toDegrees(λ2) + 540) % 360 - 180 //normalise to -180 .. 180

    OnderBoei(startSchip.id, lat2, long2)
  }

  def getRightAngleWind(wind: Wind): Wind = {
    val newDegree: Int = wind.degree + 90
    if (newDegree > 360) {
      Wind(wind.location, wind.speed, newDegree - 360)
    } else {
      Wind(wind.location, wind.speed, newDegree)
    }
  }

  protected def getAngularDistance(distance: Int): Double = {
    //distance should be in same unit as radius (metres)
    val radius = 6371e3 // earth radius in metres

    distance / radius // angular distance in radians
  }

  //midpoint between startschip & onderboei
  //http://www.movable-type.co.uk/scripts/latlong-vectors.html#intersection (vectors)
  //http://www.movable-type.co.uk/scripts/latlong.html - Intersection of two paths given start points and bearings
  //Bearing bovenboei -> wind
  //Bearing startschip -> wind + 90
  //Destination point given distance and bearing from start point (tweede link)
  //lengte startlijn  variabele
}
