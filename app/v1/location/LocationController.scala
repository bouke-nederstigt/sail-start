package v1.location

import javax.inject.Inject

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


import scala.concurrent.ExecutionContext

/**
 * Created by bouke on 07/01/17.
 */

case class LocationFormInput(id: String, latitude: Double, longitude: Double)

class LocationController @Inject()(
                                    action: LocationAction,
                                    handler: LocationResourceHandler)(implicit ec: ExecutionContext)
  extends Controller {

  private val form: Form[LocationFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "id" -> nonEmptyText(),
        "latitude" -> of[Double],
        "longitude" -> of[Double]
      )(LocationFormInput.apply)(LocationFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { locations => Ok(Json.toJson(locations)) }
    }
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonLocation()
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
}
