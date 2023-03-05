package org.mvv.scala.tools.inspection

import org.mvv.scala.tools.inspection._Class
import org.mvv.scala.tools.inspection.{ _Field, _FieldKey, _Method, _MethodKey }


extension (_class: _Class)
  def getMethod(name: String): _Method =
    _class.methods.getOrElse(_MethodKey(name, Nil, false), throw IllegalStateException(s"No method [$name] in ${_class.methods.keys}."))

  def getValOrField(name: String): _Field =
    _class.fields.getOrElse(_FieldKey(name), throw IllegalStateException(s"No val/field [$name] in ${_class.fields.keys}."))
