package v1.location

import org.scalatest.{FlatSpec}
import v1.wind.Wind

/**
 * Created by bouke on 04/09/17.
 */
class LocationCalculatorTest extends FlatSpec {

  "calculateOnderBoei" should "return OnderBoei " in {
    val calculator = new LocationCalculator
    val bovenBoei = BovenBoei(LocationId("123"), 52.3558184, 4.8868948) // unused for now
    val startSchip = StartSchip(LocationId("123"), 52.3558184, 4.8868948)
    val wind = Wind(bovenBoei, 4, 0)
    val onderBoei = calculator.calculateOnderBoei(startSchip,  200, 90)

    assert(onderBoei.id == startSchip.id)
    assert(onderBoei.longitude == 4.8898397459714715) // 4.8898
    assert(onderBoei.latitude == 52.35581836339878) // 52.3558
  }

}
