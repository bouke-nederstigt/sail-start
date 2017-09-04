package v1.location

import org.scalatest.{FlatSpec}
import v1.wind.Wind

/**
 * Created by bouke on 04/09/17.
 */
class LocationCalculatorTest extends FlatSpec {

  "getRightAngleWind" should "add 90 to wind degree" in {
    val calculator = new LocationCalculator
    val wind = Wind(BovenBoei(LocationId("123"), 52.123, 49.000), 4, 90)
    val newWind = calculator.getRightAngleWind(wind)

    assert(newWind.degree == 180)
  }

  "getRightAngleWind" should "not be more than 360" in {
    val calculator = new LocationCalculator
    val wind = Wind(BovenBoei(LocationId("123"), 52.123, 49.00), 4, 271)
    val newWind = calculator.getRightAngleWind(wind)

    assert(newWind.degree == 1)
  }
}
