package com.mvv.scala.temp.tests.tasty

import java.lang.reflect.Method

enum PropertyOwnerKindType :
  case Java, Scala


/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
case class Property (
  name: String,
  propertyType: Class[?],
  ownerKind: PropertyOwnerKindType,
  ownerClass: Class[?],
  javaGetMethods: List[Method],
  javaSetMethods: List[Method],
  owner: _Class,
)


