package com.softwaremill.session

import akka.http.scaladsl.model.{DateTime, HttpHeader}
import akka.http.scaladsl.model.headers.{HttpCookie, Cookie, `Set-Cookie`}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.TestData._

trait MultipleTransportTest { this: ScalatestRouteTest =>

  trait TestUsingTransport {
    def transportName: String
    def getSession: Option[String]
    def setSessionHeader(s: String): HttpHeader
    def isSessionExpired: Boolean

    def getSessionTransport: GetSessionTransport
    def setSessionTransport: SetSessionTransport
  }

  object TestUsingCookies extends TestUsingTransport {
    val sessionCookieName = sessionConfig.sessionCookieConfig.name
    val refreshTokenCookieName = sessionConfig.refreshTokenCookieConfig.name

    val transportName = "cookies"

    def cookiesMap: Map[String, HttpCookie] = headers
      .collect { case `Set-Cookie`(cookie) => cookie.name -> cookie }.toMap

    def getSession = cookiesMap.get(sessionCookieName).map(_.value)
    def setSessionHeader(s: String) = Cookie(sessionCookieName, s)
    def isSessionExpired = cookiesMap.get(sessionCookieName).flatMap(_.expires).contains(DateTime.MinValue)

    def getRefreshToken = cookiesMap.get(refreshTokenCookieName).map(_.value)
    def setRefreshTokenHeader(s: String) = Cookie(refreshTokenCookieName, s)
    def isRefreshTokenExpired = cookiesMap.get(refreshTokenCookieName).flatMap(_.expires).contains(DateTime.MinValue)

    def getSessionTransport = usingCookies
    def setSessionTransport = usingCookies
  }
}
