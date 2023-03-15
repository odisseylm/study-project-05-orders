package com.mvv.scala.mapstruct

import scala.reflect.ClassTag
import org.mapstruct.factory.Mappers
import com.mvv.utils.tryDo


def getScalaMapStructMapper[T]()(implicit classTag: ClassTag[T]): T =
  val jClass = classTag.runtimeClass
  val mapper: T = tryDo { Mappers.getMapper[T](jClass.asInstanceOf[Class[T]]).nn }
    .getOrElse {
      val possibleJavaMapperClassName = s"${jClass.getPackageName}.j.J${jClass.getSimpleName}"
      val possibleJavaMapperClass: Class[T] = Class.forName(possibleJavaMapperClassName).asInstanceOf[Class[T]]
      Mappers.getMapper[T](possibleJavaMapperClass).nn }
  mapper
