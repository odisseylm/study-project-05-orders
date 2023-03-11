package org.mvv.scala.tools

import java.net.{ URI, URL }
import java.nio.file.{ Path, Files }
//
import org.mvv.scala.tools.{ tryDo, checkNotNull }



def getClassLocationUrl(fullClassName: String, classLoaders: ClassLoader*): URL =
  val cls = loadClass(fullClassName, classLoaders)
  getClassLocationUrl(cls)



def getClassLocationUrl(cls: Class[?]): URL =
  var asResource = s"${cls.nn.getSimpleName}.class"
  var thisClassUrl = cls.getResource(asResource)
  if thisClassUrl.isNull then // for local/member classes
    asResource = s"${cls.nn.getName.nn.stripPrefix(cls.getPackageName.nn).stripPrefix(".")}.class"
    thisClassUrl = cls.getResource(asResource)
  checkNotNull(thisClassUrl, s"Location of class [${cls.getName}] is not found.")



def tryToLoadClass(fullClassName: String, classLoaders: Iterable[ClassLoader]): Option[Class[?]] =
  try Option(loadClass(fullClassName, classLoaders)) catch case _: Exception => None



def loadClass(fullClassName: String): Class[?] = loadClass(fullClassName, Nil)
def loadClass(fullClassName: String, classLoaders: Iterable[ClassLoader]): Class[?] =
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



//noinspection NoTailRecursionAnnotation (there is no recursion)
def jarUrlToJarPath(url: URL): Path = jarUrlToJarPath(url.toExternalForm.nn)

def jarUrlToJarPath(url: String): Path =
  require(url.startsWith("jar:file:"), s"Only local jar file supported (but not [$url]).")
  val fileUrlPart = url.stripPrefix("jar:")
  val jarPathStr = if fileUrlPart.endsWith(".jar")
    then fileUrlPart
  else
    val i = fileUrlPart.indexOf(".jar!")
    require(i > 0, s"Incorrect jar url [$url].")
    fileUrlPart.substring(0, i + 4).nn
  fileUrlToPath(jarPathStr)



inline def fileExists(f: String) = Files.exists(Path.of(f))
