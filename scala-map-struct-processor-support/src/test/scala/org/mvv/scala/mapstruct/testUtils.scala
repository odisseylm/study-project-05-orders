package org.mvv.scala.mapstruct


// Using 'given'(implicits) there is just for playing with scala.
// I'm not Scala guru and I don't know whether it is bad or good approach/practice.

def getMethod(name: String)(using _class: _Class) =
  _class.methods.getOrElse(_MethodKey(name, Nil, false), throw IllegalStateException(s"No method [$name] in ${_class.methods.keys}."))

def getValOrField(name: String)(using _class: _Class) =
  _class.fields.getOrElse(_FieldKey(name), throw IllegalStateException(s"No val/field [$name] in ${_class.fields.keys}."))

