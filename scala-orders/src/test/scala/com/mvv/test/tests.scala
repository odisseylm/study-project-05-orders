package com.mvv.test

import java.lang.reflect.Field as JField
import org.assertj.core.api.SoftAssertions
import com.mvv.utils.isNotOneOf
import com.mvv.nullables.{ isNotNull, nnArray }


//noinspection ScalaFileName
object SoftAssertions :
  extension (assertions: SoftAssertions)
    inline def runTests(action: SoftAssertions=>Unit): SoftAssertions = { action(assertions); assertions }
object Tests :
  // just to reuse kotlin test with minimal changes
  def run(action: =>Unit): Unit = action


def initField(obj: AnyRef, fieldName: String, value: Any): Unit =
  findClassField(obj.getClass.nn, fieldName)
    .orElse(findClassField(obj.getClass.nn, "_" + fieldName))
    .orElse(throw IllegalArgumentException(s"Class [${obj.getClass.nn.getName}] does not have field [$fieldName]."))
    .foreach(f =>
      f.trySetAccessible()
      f.set(obj, value)
    )


def findClassField(klass: Class[?], name: String): Option[JField] = findClassFieldImpl(klass, name)

def findClassFieldImpl(klass: Class[?]|Null, name: String): Option[JField] =
  List(klass)
    .filter(cls => cls.isNotNull && cls.isNotOneOf(classOf[Object], classOf[AnyRef]))
    .map(_.nn)
    .flatMap(_.getDeclaredFields.nnArray)
    .find(_.getName.nn == name)
    .orElse { Option(klass).flatMap(cls => findClassFieldImpl(cls.nn.getSuperclass, name)) }
