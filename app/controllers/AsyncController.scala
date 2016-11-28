package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Async tasks controller
  */
@Singleton
class AsyncController @Inject() (ws: WSClient)(implicit ec: ExecutionContext) extends Controller {

  val urls = List("http://ya.ru", "http://yandex.ru", "http://google.com", "http://cmlt.ru")

  //async load pages and show time of loading.
  def pagesLoadingTime: Action[AnyContent] = Action.async {
    val startTime = System.currentTimeMillis()
    val futures = urls.map(url => {
      ws.url(url).get().map(r => (url, System.currentTimeMillis() - startTime))
    })

    for {
      list <- Future.sequence(futures)
    } yield {
      val allTime = System.currentTimeMillis() - startTime
      Ok(list.sortBy(x => x._2).map(res => res._1 + ": " + res._2 + " mls").mkString("\n") + "\nall time: " + allTime + " mls")
    }
  }
}
