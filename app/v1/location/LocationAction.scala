package v1.location

import javax.inject.Inject

import org.slf4j.LoggerFactory
import play.api.http.HttpVerbs
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.i18n.Messages


import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by bouke on 08/01/17.
 */

class LocationRequest[A](request: LocationRequest, val messages: Messages) extends WrappedRequest(request)

class LocationAction @Inject()(messagesApi: MessagesApi)(
  implicit ec: ExecutionContext)
  extends ActionBuilder[LocationRequest]
  with HttpVerbs {

  type LocationRequestBlock[A] = LocationRequest[A] => Future[Result]

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def invokeBlock[A](request: Request[A], block: LocationRequest[A]): Future[Result] = {
    if (logger.isTraceEnabled()){
      logger.trace(s"invokeblock: request: request = $request")
    }

    val messages = messagesApi.preferred(request)
    val future =  block(new LocationRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }

  }
}
