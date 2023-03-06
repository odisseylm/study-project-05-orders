package org.mvv.scala.tools.beans

import org.mvv.scala.tools.inspection._Class
import org.mvv.scala.tools.inspection.{ JavaBeanProperty, InspectMode }

import java.awt.Image
import java.beans.{ BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor }
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.tools.OptionOps.allAreDefined
import org.mvv.scala.tools.inspection.ScalaBeanInspector



class ScalaBeanProps private (_class: _Class) extends java.beans.BeanInfo :
  private val beanProps: BeanProperties = _class.toBeanProperties(InspectMode.AllSources)
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
  // global thread-safe version
  private val beansInspector = ScalaBeanInspector.createLight()

  def apply(cls: Class[?]): ScalaBeanProps =
    val _class = beansInspector.inspectClass(cls)
    new ScalaBeanProps(_class)



extension (beanProperties: BeanProperties)
  def toPropertyDescriptors: List[PropertyDescriptor] =

    val allBeanProps: Iterable[JavaBeanProperty] = beanProperties.beanProps.values
      .map { bp => bp match
        case jbp: JavaBeanProperty => jbp
        case _ => throw IllegalArgumentException(s"PropertyDescriptor requires JavaBeanProperty (source: ${bp.ownerClass.fullName}#${bp.name}).")
      }
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



private def validateBeanProperties(props: Iterable[JavaBeanProperty]): Unit =
  props.foreach { prop =>
    val runtimeGetMethodExists = prop.runtimeGetMethods.exists(_.nonEmpty)
    if !runtimeGetMethodExists then
      log.warn(s"Field [${prop.ownerClass.fullName}#${prop.field}] does not have getter method" +
        s" and cannot be represented as standard java bean property.")

    require(allAreDefined(prop.runtimeOwnerClass, prop.runtimeGetMethods, prop.runtimeSetMethods),
      "toPropertyDescriptors requires BeanProperties with pre-loaded runtime types.")
  }
