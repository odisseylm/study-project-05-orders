package org.mvv.scala.tools.inspection.tasty

import org.mvv.scala.tools.inspection.*

import java.net.URL
import java.nio.file.{ Files, Path }
import scala.annotation.tailrec
//
import org.mvv.scala.tools.{ afterFirstOr, checkNotNull, isNotNull, tryDo }
import org.mvv.scala.tools.{ loadClass, getClassLocationUrl, fileUrlToPath, jarUrlToJarPath }



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



def isScala3Class(cls: Class[?]): Boolean = tastyFileExists(cls)

def isScala2Class(cls: Class[?]): Boolean =
  try
    val scala2MetaDataAnnotation = loadClass("scala.reflect.ScalaSignature")
      .asInstanceOf[Class[? <: java.lang.annotation.Annotation]]
    cls.getAnnotation(scala2MetaDataAnnotation) .isNotNull
  catch case _: Exception => false
