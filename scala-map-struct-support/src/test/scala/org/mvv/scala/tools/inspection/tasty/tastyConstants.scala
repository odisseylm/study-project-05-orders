package org.mvv.scala.tools.inspection.tasty


class DummyClass456

private def getCurrentProjectDir: String =
  val classLocationUrl = getClassLocationUrl(classOf[DummyClass456])
  val classPath = fileUrlToPath(classLocationUrl)
  val projectDirPath = classPath.resolve("../../../../../../../../..").nn.normalize()
  projectDirPath.toString



//val projectDir: String = s"${System.getProperty("user.home")}/projects/study-project-05-orders/scala-map-struct-support"
val projectDir: String = getCurrentProjectDir

val classesDir = s"$projectDir/target/classes"
val testClassesDir = s"$projectDir/target/test-classes"
