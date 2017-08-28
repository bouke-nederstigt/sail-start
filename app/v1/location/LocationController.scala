package v1.location

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.format.Formats._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import v1.wind.Wind
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext, Future}


import scala.concurrent.ExecutionContext

/**
 * Created by bouke on 07/01/17.
 */

case class LocationFormInput(id: String, latitude: Double, longitude: Double, locationType: String)

case class OnderBoeiFormInput(startschipId: String, bovenboeiId: String)

class LocationController @Inject()(
                                    action: LocationAction,
                                    handler: LocationResourceHandler,
                                    val messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends Controller with I18nSupport {

  def validateLocationForm(id: String, latitude: Double, longitude: Double, locationType: String) = {
    locationType match {
      case "bovenboei" => true
      case "startschip" => true
      case _ => false
    }
  }

  private val form: Form[LocationFormInput] = {
    Form(
      mapping(
        "id" -> nonEmptyText(),
        "latitude" -> of[Double],
        "longitude" -> of[Double],
        "locationType" -> nonEmptyText()
      )(LocationFormInput.apply)(LocationFormInput.unapply) verifying("Form validation failed", fields => fields match {
        case locationFormInput => validateLocationForm(locationFormInput.id, locationFormInput.latitude, locationFormInput.longitude, locationFormInput.locationType)
      })
    )
  }

  private val onderboeiForm: Form[OnderBoeiFormInput] = {
    Form(
      mapping(
        "startschipId" -> nonEmptyText(),
        "bovenboeiId" -> nonEmptyText()
      )(OnderBoeiFormInput.apply)(OnderBoeiFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { locations => Ok(Json.toJson(locations)) }
    }
  }

  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(id).map { location =>
        Ok(Json.toJson(location))
      }
    }
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonLocation()
    }
  }

  def startschip: Action[AnyContent] = {
    action.async { implicit request =>
      processOnderBoeiLocation()
    }
  }

  protected def processJsonLocation[A]()(implicit request: LocationRequest[A]): Future[Result] = {
    def failure(badForm: Form[LocationFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: LocationFormInput) = {
      handler.create(input).map { location =>
        Created(Json.toJson(location)).withHeaders(LOCATION -> location.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  protected def processOnderBoeiLocation[A]()(implicit request: LocationRequest[A]): Future[Result] = {
    def failure(badForm: Form[OnderBoeiFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: OnderBoeiFormInput) = {
      handler.getOnderboei(input).map { location => Created(Json.toJson(location)).withHeaders(LOCATION -> location.link) }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
