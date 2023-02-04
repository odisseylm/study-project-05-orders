package com.mvv.scala.temp.tests.tasty

import java.net.URL
import java.nio.file.Path

sealed class ClassSource

case class FileClassSource (classFile: Path) extends ClassSource
object FileClassSource :
  def apply(url: URL): FileClassSource = fromUrl(url.toExternalForm.nn)
  def fromUrl(url: String): FileClassSource = new FileClassSource(fileUrlToPath(url).get)
  def fromClass(_class: Class[?]): FileClassSource = apply(findClassPathUrl(_class).get)

class JarClassSource (jarFile: Path, filePath: String) extends ClassSource
object JarClassSource :
  def apply(url: URL): JarClassSource =
    val urlStr = url.toExternalForm.nn
    require(urlStr.startsWith("jar:file:"), s"Strange jar file url [$url].")
    var onlyJarFileUrl: String = urlStr.stripPrefix("jar:")
    var classFilePath = ""
    if !onlyJarFileUrl.endsWith(".jar") then
      val i = onlyJarFileUrl.indexOf(".jar!")
      require(i > 0, s"Strange jar file url [$url].")
      onlyJarFileUrl = onlyJarFileUrl.substring(0, i + 4).nn
      classFilePath = onlyJarFileUrl.substring(i + 4).nn
    new JarClassSource(fileUrlToPath(onlyJarFileUrl).get, classFilePath)

// TODO: remove
class TempStubClassSource extends ClassSource

private def urlToPath(url: String): Option[Path] = url match
  case fileUrl if fileUrl.startsWith("file:") => fileUrlToPath(url)
  // TODO: temp
  case jarUrl if jarUrl.startsWith("jar:file:") =>
    val aaa = url.stripPrefix("jar:file:")
    Option(Path.of(aaa.substring(0, aaa.indexOf(".jar!") + 4).nn).nn)
  //case jarUrl if jarUrl.startsWith("jar:file:") => fileUrlToPath(url.stripPrefix("jar:"))
  case _ => None

private def fileUrlToPath(url: String): Option[Path] =
  require(url.startsWith("file:"), s"Now only [file:] protocol is supported ($url).")
  val asFile = url.stripPrefix("file:")
  // TODO: improve code by using loop
  { if fileExists(asFile) then return Option(Path.of(asFile).nn) }
  { val asFileN = asFile.stripPrefix("/");  if fileExists(asFileN) then return Option(Path.of(asFileN).nn) }
  { val asFileN = asFile.stripPrefix("//"); if fileExists(asFileN) then return Option(Path.of(asFileN).nn) }
  //throw TastyFileNotFoundException(s"$asFile is not found.")
  None


private def findClassPathUrl(fullClassName: String, classLoaders: ClassLoader*): Option[URL] =
  val cls = loadClass(fullClassName, classLoaders *)
  findClassPathUrl(cls)

private def findClassPathUrl(cls: Class[?]): Option[URL] =
  val asResource = s"${cls.nn.getSimpleName}.class"
  val thisClassUrl = cls.nn.getResource(asResource)
  if thisClassUrl == null then None else Option(thisClassUrl.nn)


// TODO: move to some util file
private def tryDo[T](expr: => T): Option[T] =
  try Option[T](expr).nn catch case _: Exception => None


private def loadClass(fullClassName: String, classLoaders: ClassLoader*): Class[?] =
  val cls: Class[?] = classLoaders.view
    .flatMap(cl => tryDo(Class.forName(fullClassName, false, cl).nn))
    .headOption
    .getOrElse(Class.forName(fullClassName).nn)
  cls
