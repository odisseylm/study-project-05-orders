package com.mvv.utils

import scala.annotation.targetName
import com.mvv.props.{ safeValue, isPropInitialized }



def checkId(id: => Long|Option[Long]|Null, msg: =>String): Long = checkIdImpl(id, msg)
def checkId(id: => Long|Option[Long]|Null): Long = checkId(id, s"Id is not set or incorrect [${id.safeValue}].")

def checkIdNotSet(id: => Long|Option[Long]|Null, msg: =>String): Unit = checkIdNotSetImpl(id, msg)
def checkIdNotSet(id: => Long|Option[Long]|Null): Unit = checkIdNotSet(id, s"Id is not set or incorrect [${id.safeValue}].")



private def getIdValueOrNull(idExpr: => Long|Option[Long]|Null): Long|Null =
  import com.mvv.nullables.AnyCanEqualGivens.given
  val _id = if idExpr.isPropInitialized then idExpr else null
  val id: Long | Null = _id match
    case null => null
    case vv: Long => vv
    case Some(vv) => vv
    case None => null
  id


def isValidId(idExpr: => Long|Option[Long]|Null): Boolean =
  import com.mvv.nullables.AnyCanEqualGivens.given
  val id = getIdValueOrNull(idExpr)
  val idIsSet = id != null && id != 0 && id != -1 && id != None
  idIsSet


def isUninitializedId(idExpr: => Long|Option[Long]|Null): Boolean =
  import com.mvv.nullables.AnyCanEqualGivens.given
  val id = getIdValueOrNull(idExpr)
  val _isUninitializedId = id == null || id == 0 || id == -1 || id == None
  _isUninitializedId


private def checkIdImpl(idExpr: => Long|Option[Long]|Null, msg: =>String): Long =
  val id = getIdValueOrNull(idExpr)
  check(isValidId(id), msg)
  id.nn


private def checkIdNotSetImpl(idExpr: => Long|Option[Long]|Null, msg: =>String): Unit =
  val id = getIdValueOrNull(idExpr)
  check(isUninitializedId(id), msg)
