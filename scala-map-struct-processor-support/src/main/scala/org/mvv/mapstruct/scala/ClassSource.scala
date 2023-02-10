package org.mvv.mapstruct.scala

import java.net.URL
import java.nio.file.Path
import scala.annotation.tailrec


sealed class ClassSource
object ClassSource :
  def of(cls: Class[?]): ClassSource =
    val classUrl = getClassLocationUrl(cls)
    classUrl match
      case _ if classUrl.getProtocol == "file" => DirectoryClassSource(cls)
      case _ if classUrl.getProtocol == "jar"  => JarClassSource(cls)
      case _ => throw IllegalArgumentException(s"Unsupported url [$classUrl].")


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


def getClassLocationUrl(fullClassName: String, classLoaders: ClassLoader*): URL =
  val cls = loadClass(fullClassName, classLoaders *)
  getClassLocationUrl(cls)

private def getClassLocationUrl(cls: Class[?]): URL =
  var asResource = s"${cls.nn.getSimpleName}.class"
  var thisClassUrl = cls.getResource(asResource)
  if thisClassUrl == null then // for local/member classes
    asResource = s"${cls.nn.getName.nn.stripPrefix(cls.getPackageName.nn).stripPrefix(".")}.class"
    thisClassUrl = cls.getResource(asResource)
  checkNotNull(thisClassUrl, s"Location of class [${cls.getName}] is not found.")


def loadClass(fullClassName: String, classLoaders: ClassLoader*): Class[?] =
  val cls: Class[?] = classLoaders.view
    .flatMap(cl => tryDo(loadClassImpl(fullClassName, Option(cl)).nn))
    .headOption
    .getOrElse(loadClassImpl(fullClassName, None).nn)
  cls

private def loadClassImpl(fullClassName: String, classLoader: Option[ClassLoader]): Class[?] =
  try  classLoader.map(cl => Class.forName(fullClassName, false, cl).nn).getOrElse(Class.forName(fullClassName).nn)
  catch case _: Exception =>
    val fixedName = fixInternalClassName(fullClassName)
    classLoader.map(cl => Class.forName(fixedName, false, cl).nn).getOrElse(Class.forName(fixedName).nn)

private def fixInternalClassName(fullClassName: String) =
  val classParts = fullClassName.split('.')
  classParts
    .map(p => if p.charAt(0).isUpper then p + '$' else p + '.' )
    .mkString("")
    .stripSuffix(".")
    .stripSuffix("$")


def tastyFileUrl(cls: Class[?]): URL|Null =
  var tastyAsResourceUrl = cls.getResource(s"${cls.getSimpleName}.tasty")
  if tastyAsResourceUrl == null then // for local/member classes
    val asResource = s"${cls.nn.getName.nn.stripPrefix(cls.getPackageName.nn).stripPrefix(".")}.tasty"
    tastyAsResourceUrl = cls.getResource(asResource)
  tastyAsResourceUrl

def tastyFileExists(cls: Class[?]): Boolean = tastyFileUrl(cls) != null
//noinspection ScalaUnusedSymbol
def tastyFileExists(fullClassName: String, classLoaders: ClassLoader*): Boolean =
  val cls = loadClass(fullClassName, classLoaders*)
  tastyFileExists(cls)


def isScala3Class(cls: Class[?]): Boolean = tastyFileExists(cls)

def isScala2Class(cls: Class[?]): Boolean =
  try
    val scala2MetaDataAnnotation = loadClass("scala.reflect.ScalaSignature")
      .asInstanceOf[Class[? <: java.lang.annotation.Annotation]]
    cls.getAnnotation(scala2MetaDataAnnotation) .isNotNull
  catch case _: Exception => false
