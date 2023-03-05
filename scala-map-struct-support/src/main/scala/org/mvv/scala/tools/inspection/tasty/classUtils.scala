package org.mvv.scala.tools.inspection.tasty

import java.net.{ URI, URL }
//
import org.mvv.scala.tools.{ tryDo, checkNotNull }



def getClassLocationUrl(fullClassName: String, classLoaders: ClassLoader*): URL =
  val cls = loadClass(fullClassName, classLoaders)
  getClassLocationUrl(cls)


def getClassLocationUrl(cls: Class[?]): URL =
  var asResource = s"${cls.nn.getSimpleName}.class"
  var thisClassUrl = cls.getResource(asResource)
  if thisClassUrl == null then // for local/member classes
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
