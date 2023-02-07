package org.mvv.mapstruct.scala.mapstruct

import org.mapstruct.ap.spi.MapStructProcessingEnvironment
import javax.lang.model.element.ExecutableElement


class ScalaAccessorNamingStrategy extends org.mapstruct.ap.spi.DefaultAccessorNamingStrategy {

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean = super.isGetterMethod(method)

  override def isSetterMethod(method: ExecutableElement): Boolean = super.isSetterMethod(method)

  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String = super.getPropertyName(getterOrSetterMethod).nn


  override def isAdderMethod(method: ExecutableElement): Boolean = super.isAdderMethod(method)
  // for adder method
  override def getElementName(adderMethod: ExecutableElement): String = super.getElementName(adderMethod).nn
}
