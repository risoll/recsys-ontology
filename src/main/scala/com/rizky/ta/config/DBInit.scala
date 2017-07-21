package com.rizky.ta.config

import java.sql.{Connection, DriverManager}

import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

/**
  * Created by risol_000 on 1/30/2017.
  */
object DBInit {
  val url = "jdbc:postgresql://localhost:5432/recsys-ta?charSet=UTF-8"
  val username = "postgres"
  val password = "PostgreS"

//  val url = "postgres://cxeawacfozvjyy:f929c2a61c3db0f929948343db32f504313228dfbd315dffa3c1f640254f0bba@ec2-54-243-185-123.compute-1.amazonaws.com:5432/ddaebbm2kr2hul"
//  val username = "cxeawacfozvjyy"
//  val password = "f929c2a61c3db0f929948343db32f504313228dfbd315dffa3c1f640254f0bba"

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
