name := "sailstart"

version := "1.0"

scalaVersion := "2.11.8"

//libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.14"

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % Test

libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.2" % Test

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala)
  .settings(
    name := """sailstart"""
  )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  