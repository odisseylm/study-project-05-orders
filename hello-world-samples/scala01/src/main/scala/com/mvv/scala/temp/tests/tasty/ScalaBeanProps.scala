package com.mvv.scala.temp.tests.tasty

import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}


class ScalaBeanProps (val asJavaClass: Class[Any]) extends java.beans.BeanInfo :
  override def getBeanDescriptor: BeanDescriptor = BeanDescriptor(asJavaClass)

  override def getEventSetDescriptors: Array[EventSetDescriptor] = Array[EventSetDescriptor]()

  override def getDefaultEventIndex: Int = -1

  override def getPropertyDescriptors: Array[PropertyDescriptor] = ???

  override def getDefaultPropertyIndex: Int = -1

  override def getMethodDescriptors: Array[MethodDescriptor] = Array()

  override def getAdditionalBeanInfo: Array[BeanInfo] = Array[BeanInfo]()

  override def getIcon(iconKind: Int): Image|Null = null
end ScalaBeanProps


