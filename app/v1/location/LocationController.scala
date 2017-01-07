package v1.location

import javax.inject.Inject

import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

/**
 * Created by bouke on 07/01/17.
 */
class LocationController @Inject()(
                                    action: LocationAction,
                                    handler: LocationResourceHandler)(implicit ec: ExecutionContext)
  extends Controller {

}
