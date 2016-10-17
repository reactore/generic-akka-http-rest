import sbt.Keys._
import sbt._

object Versions {

  val akka = "2.3.12"
  val akkaStreamV = "2.4.4"
  val scalaTestVer = "2.2.2"
  val mockito = "1.10.8"
  val postgresqlVersion = "9.4-1201-jdbc41"
  val slickVersion = "3.1.1"
  val scalaVersion = "2.11.6"
  val json4sVersion = "3.4.0"
}

object Library {

  import Versions._

  // Core dependencies
  val akkaKit = "com.typesafe.akka" %% "akka-actor" % akka
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val scalaLang = "org.scala-lang" % "scala-reflect" % scalaVersion
  val postgresql = "org.postgresql" % "postgresql" % postgresqlVersion
  val hikariCP = "com.zaxxer" % "HikariCP" % "2.4.3"

  val apacheCommons = "org.apache.commons" % "commons-email" % "1.3.3"

  val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  val codegen = "com.typesafe.slick" %% "slick-codegen" % slickVersion

  val akka_stream = "com.typesafe.akka" %% "akka-stream" % akkaStreamV
  val akka_http_core = "com.typesafe.akka" %% "akka-http-core" % akkaStreamV
  val akka_http = "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV

  val json4s = "org.json4s" %% "json4s-jackson" % json4sVersion

}

object Dependencies {

  import Library._

  val coreDeps = Seq(akkaKit)

  val slickDeps = Seq(slick, hikariCP, slickHikari, postgresql, codegen)

  val akkaHttpDeps = Seq(akka_stream, akka_http_core, akka_http)

  val utils = Seq(json4s)
}


object CustomBuild extends Build {

  import Dependencies._

  val common_settings = Defaults.coreDefaultSettings ++
    Seq(
      organization := "com.reactore",
      scalaVersion in ThisBuild := "2.11.7",
      scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation"),
      ivyScala := ivyScala.value map {
        _.copy(overrideScalaVersion = true)
      },

      libraryDependencies := coreDeps ++ slickDeps ++ akkaHttpDeps ++ utils
    )

  lazy val generic_akka_http_rest: Project = Project(id = "generic-akka-http-rest",
    base = file("."), settings = common_settings)

}