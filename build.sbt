import com.earldouglas.xwp.JettyPlugin

lazy val commonSettings = Seq(
  organization := "com.rizky.ta",
  version := "1.0.0",
  scalaVersion := "2.11.7",
  ivyScala := ivyScala.value map {
    _.copy(overrideScalaVersion = true)
  }
)

lazy val root = (project in file(".")).
  enablePlugins(JettyPlugin).
  settings(commonSettings: _*).
  settings(
    name := "recsys-ontology"
  )
val scalatraVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % scalatraVersion withSources(),
  "org.scalatra" %% "scalatra-json" % scalatraVersion withSources() ,
  "org.json4s" %% "json4s-jackson" % "3.2.11" withSources() ,
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided" withSources() ,
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.10.v20150310" % "container" withSources() ,
  "org.eclipse.jetty" % "jetty-servlets" % "9.2.10.v20150310" % "container" withSources() ,
  "org.eclipse.jetty" % "jetty-plus" % "9.2.10.v20150310" % "container" withSources() ,
  "c3p0" % "c3p0" % "0.9.1.2" withSources() ,
  "org.scalatest" %% "scalatest" % "2.2.4" % "provided" withSources() ,
  "org.scalatra" % "scalatra-specs2_2.11" % "2.3.1" withSources() ,
  "org.scalikejdbc" % "scalikejdbc_2.11" % "2.2.6" withSources() ,
  "org.scalikejdbc" % "scalikejdbc-test_2.11" % "2.2.6" withSources(),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" withSources() ,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41" withSources()
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "ElasticSearch Repo" at "http://maven.elasticsearch.org/public-releases/"
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"