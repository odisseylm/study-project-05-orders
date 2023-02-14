package org.mvv.mapstruct.scala

import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
import java.lang.reflect.Method as JavaMethod


class ScalaBeanProps private (_class: _Class) extends java.beans.BeanInfo :
  private val beanProps: BeanProperties = _class.beanProperties
  private val propertyDescriptors: List[PropertyDescriptor] = beanProps.toPropertyDescriptors

  override def getBeanDescriptor: BeanDescriptor = BeanDescriptor(_class.runtimeClass)

  override def getEventSetDescriptors: Array[EventSetDescriptor] = Array[EventSetDescriptor]()

  override def getDefaultEventIndex: Int = -1

  override def getPropertyDescriptors: Array[PropertyDescriptor] = propertyDescriptors.toArray

  override def getDefaultPropertyIndex: Int = -1

  override def getMethodDescriptors: Array[MethodDescriptor] = Array()

  override def getAdditionalBeanInfo: Array[BeanInfo] = Array[BeanInfo]()

  override def getIcon(iconKind: Int): Image|Null = null
end ScalaBeanProps


object ScalaBeanProps :
  private val beansInspector = ScalaBeansInspector()

  def apply(cls: Class[?]): ScalaBeanProps =
    val _class = beansInspector.inspectClass(cls)
    new ScalaBeanProps(_class)


extension (beanProperties: BeanProperties)
  def toPropertyDescriptors: List[PropertyDescriptor] =
    val asPropDescriptors = beanProperties.beanProps.values.map(prop =>
      val setterMethodOrNull = if prop.javaSetMethods.nonEmpty then prop.javaSetMethods.head else null
      PropertyDescriptor(prop.name, prop.javaGetMethods.head, setterMethodOrNull)
    ).toList
    asPropDescriptors
