package org.mvv.scala.tools.inspection.tasty

import org.mvv.scala.tools.inspection.*

import java.net.URL
import java.nio.file.{Files, Path}
import scala.annotation.tailrec
//
import org.mvv.scala.tools.{afterFirstOr, checkNotNull, isNotNull, tryDo}
//import org.mvv.scala.tools.beans.



sealed class ClassSource
object ClassSource :
  def of(cls: Class[?]): ClassSource =
    val classUrl = getClassLocationUrl(cls)
    classUrl.getProtocol match
      case "file" => DirectoryClassSource(cls)
      case "jar"  => JarClassSource(cls)
      case "jrt"  => SystemClassSource(cls) // system classloader
      case _ => throw IllegalArgumentException(s"Unsupported url [$classUrl].")

  val MacroQuotes: MacroQuotesClassSource = MacroQuotesClassSource()

  extension (cls: Class[?])
    def classKind: ClassKind = cls match
      case scala2Class if isScala2Class(scala2Class) => ClassKind.Scala2
      case scala3Class if isScala3Class(scala3Class) => ClassKind.Scala3
      case _ => ClassKind.Java


case class DirectoryClassSource (directoryPath: Path) extends ClassSource
object DirectoryClassSource :
  def apply(cls: Class[?]): DirectoryClassSource =
    val classUrl = getClassLocationUrl(cls)
    val classPath = fileUrlToPath(classUrl).toString.nn
    val packageAndClassAsPath = s"${cls.getName}".replace('.', java.io.File.separatorChar).nn + ".class"

    require(classPath.endsWith(packageAndClassAsPath))
    val dirPathStr = classPath.stripSuffix(packageAndClassAsPath).stripSuffix(java.io.File.separator.nn)
    new DirectoryClassSource(Path.of(dirPathStr).nn)



case class JarClassSource (jarPath: Path) extends ClassSource
object JarClassSource :
  def apply(cls: Class[?]): JarClassSource =
    val classUrl = getClassLocationUrl(cls)
    new JarClassSource(jarUrlToJarPath(classUrl))



case class SystemClassSource (classSource: URL) extends ClassSource
object SystemClassSource :
  def apply(cls: Class[?]): SystemClassSource = new SystemClassSource(getClassLocationUrl(cls))



class MacroQuotesClassSource extends ClassSource :
  override def toString: String = "MacroQuotesClassSource"



//noinspection NoTailRecursionAnnotation (there is no recursion)
private def jarUrlToJarPath(url: URL): Path = jarUrlToJarPath(url.toExternalForm.nn)
private def jarUrlToJarPath(url: String): Path =
  require(url.startsWith("jar:file:"), s"Only local jar file supported (but not [$url]).")
  val fileUrlPart = url.stripPrefix("jar:")
  val jarPathStr = if fileUrlPart.endsWith(".jar")
    then fileUrlPart
  else
    val i = fileUrlPart.indexOf(".jar!")
    require(i > 0, s"Incorrect jar url [$url].")
    fileUrlPart.substring(0, i + 4).nn
  fileUrlToPath(jarPathStr)


//noinspection NoTailRecursionAnnotation , // there is no recursion at all
def fileUrlToPath(url: URL): Path = fileUrlToPath(url.toExternalForm.nn)
def fileUrlToPath(url: String): Path =
  require(url.startsWith("file:"), s"Now only [file:] protocol is supported ($url).")
  val asFile = url.stripPrefix("file:")
  var existentPath: Option[Path] = None
  for
    i <- 0 to 2
    if existentPath.isEmpty
  do
    val asFileN = asFile.stripPrefix("/".repeat(i).nn)
    if fileExists(asFileN) then existentPath = Option(Path.of(asFileN).nn)

  existentPath.getOrElse(Path.of(asFile).nn)


private inline def fileExists(f: String) = Files.exists(Path.of(f))



def tastyFileUrl(cls: Class[?]): Option[URL] =
  var tastyAsResourceUrl: URL|Null = cls.getResource(s"${cls.getSimpleName}.tasty")
  if tastyAsResourceUrl == null then // for local/member classes
    val fullClassName = cls.nn.getName.nn
    val packageName = cls.getPackageName.nn
    val asResource = fullClassName.stripPrefix(packageName).stripPrefix(".") + ".tasty"
    tastyAsResourceUrl = cls.getResource(asResource)
  if tastyAsResourceUrl == null then None else Option(tastyAsResourceUrl)

def tastyFileExists(cls: Class[?]): Boolean = tastyFileUrl(cls).isDefined
//noinspection ScalaUnusedSymbol
def tastyFileExists(fullClassName: String, classLoaders: ClassLoader*): Boolean =
  tastyFileExists(loadClass(fullClassName, classLoaders))


def isScala3Class(cls: Class[?]): Boolean = tastyFileExists(cls)

def isScala2Class(cls: Class[?]): Boolean =
  try
    val scala2MetaDataAnnotation = loadClass("scala.reflect.ScalaSignature")
      .asInstanceOf[Class[? <: java.lang.annotation.Annotation]]
    cls.getAnnotation(scala2MetaDataAnnotation) .isNotNull
  catch case _: Exception => false
