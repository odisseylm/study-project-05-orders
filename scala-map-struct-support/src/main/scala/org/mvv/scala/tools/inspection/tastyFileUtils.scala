package org.mvv.scala.tools.inspection

import java.net.URL
import org.mvv.scala.tools.{ isNull, optionOf, loadClass }



def tastyFileUrl(cls: Class[?]): Option[URL] =
  var tastyAsResourceUrl: URL|Null = cls.getResource(s"${cls.getSimpleName}.tasty")
  if tastyAsResourceUrl.isNull then // for local/member classes
    val fullClassName = cls.nn.getName.nn
    val packageName = cls.getPackageName.nn
    val asResource = fullClassName.stripPrefix(packageName).stripPrefix(".") + ".tasty"
    tastyAsResourceUrl = cls.getResource(asResource)
  optionOf[URL](tastyAsResourceUrl)



def tastyFileExists(cls: Class[?]): Boolean = tastyFileUrl(cls).isDefined


//noinspection ScalaUnusedSymbol, NoTailRecursionAnnotation // there is no recursion
def tastyFileExists(fullClassName: String, classLoaders: ClassLoader*): Boolean =
  tastyFileExists(loadClass(fullClassName, classLoaders))
