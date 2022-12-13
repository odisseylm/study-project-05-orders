
ThisBuild / organization := "com.mvv.temp"
ThisBuild / scalaVersion := "3.2.1"
ThisBuild / version      := "1.0-SNAPSHOT"

name := "scala-01"
scalaVersion := "3.2.1"
version := "1.0-SNAPSHOT"


//lazy val root = (project in file("."))
lazy val scala01 = (project in file("."))
  .settings(
    name := "scala-01"
  )

libraryDependencies ++= Seq(
  "org.junit.jupiter" % "junit-jupiter" % "5.9.1" % Test,

  "org.scalatest" % "scalatest_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-core_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-funsuite_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-flatspec_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-funspec_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-featurespec_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-matchers-core_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-shouldmatchers_3" % "3.2.14" % Test,
  "org.scalatest" % "scalatest-mustmatchers_3" % "3.2.14" % Test,

  "org.specs2" % "specs2-core_3" % "5.2.0" % Test,
  "org.specs2" % "specs2-junit_3" % "5.2.0" % Test,
  "org.specs2" % "specs2-scalacheck_3" % "5.2.0" % Test,
  "org.specs2" % "specs2-matcher_3" % "5.2.0" % Test,
  "org.specs2" % "specs2-matcher-extra_3" % "5.2.0" % Test,
  "org.specs2" % "specs2-html_3" % "5.2.0" % Test,

  //"org.mockito" % "mockito-scala-scalatest_2.13" % "1.17.12" % Test,
  "org.mockito" % "mockito-core" % "4.8.0" % Test,

  //"" % "" % "" % Test,
  //groupID % artifactID % revision,
  //groupID % otherID % otherRevision
)

/*
lazy val hello = (project in file("."))
  .settings(
    name := "scala-01",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  )
*/
