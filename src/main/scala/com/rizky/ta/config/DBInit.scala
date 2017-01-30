package com.rizky.ta.config

import java.sql.{Connection, DriverManager}

import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

/**
  * Created by risol_000 on 1/30/2017.
  */
object DBInit {
  val url = "jdbc:postgresql://localhost:5432/recsys-ta?charSet=UTF-8"
  val username = "postgres"
  val password = "postgres"
  val driver = "org.postgresql.Driver"
  var connection: Option[Connection] = None

  def config(): Unit = {
    ConnectionPool.singleton(url, username, password)
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(enabled = false)
    getConnection()
  }


  def getConnection(): Unit = {
    try {
      // make the connection
      Class.forName(driver)
      connection = Some(DriverManager.getConnection(url, username, password))
    } catch {
      case e: Throwable => e.printStackTrace
    }
  }
}
