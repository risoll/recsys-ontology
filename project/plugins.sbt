logLevel := Level.Warn

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.5.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "3.0.0")

addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.4.2")

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.2.6")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "2.1.0")

resolvers += "Flyway" at "http://flywaydb.org/repo"