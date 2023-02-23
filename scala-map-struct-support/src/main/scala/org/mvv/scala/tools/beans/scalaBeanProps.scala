package org.mvv.scala.tools.beans

import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.tools.OptionOps.allAreDefined



class ScalaBeanProps private (_class: _Class) extends java.beans.BeanInfo :
  private val beanProps: BeanProperties = _class.toBeanProperties(TypesLoadMode.All)
  private val propertyDescriptors: List[PropertyDescriptor] = beanProps.toPropertyDescriptors

  override def getBeanDescriptor: BeanDescriptor = BeanDescriptor(_class.runtimeClass.get)

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

    val allBeanProps: Iterable[BeanProperty] = beanProperties.beanProps.values
    validateBeanProperties(allBeanProps)

    val asPropDescriptors = allBeanProps
      .filter { prop =>
        val runtimeGetMethodExists = prop.runtimeGetMethods.exists(_.nonEmpty)
        runtimeGetMethodExists
      }
      .map { prop =>
        val runtimeSetMethodOrNull = if prop.runtimeSetMethods.get.nonEmpty then prop.runtimeSetMethods.get.head else null
        PropertyDescriptor(prop.name, prop.runtimeGetMethods.get.head, runtimeSetMethodOrNull)
      }
      .toList
    asPropDescriptors



private def validateBeanProperties(props: Iterable[BeanProperty]): Unit =
  props.foreach { prop =>
    val runtimeGetMethodExists = prop.runtimeGetMethods.exists(_.nonEmpty)
    if !runtimeGetMethodExists then
      log.warn(s"Field [${prop.ownerClass.fullName}#${prop.runtimeField.get}] does not have getter method" +
        s" and cannot be represented as standard java bean property.")

    require(allAreDefined(prop.runtimeOwnerClass, prop.runtimeGetMethods, prop.runtimeSetMethods),
      "toPropertyDescriptors requires BeanProperties with pre-loaded runtime types.")
  }
