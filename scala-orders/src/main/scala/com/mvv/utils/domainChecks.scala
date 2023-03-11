package com.mvv.utils

import scala.annotation.targetName
import com.mvv.props.{ safeValue, isPropInitialized }



@targetName("checkIdOption")
def checkId(id: => Long|Option[Long]|Null, msg: =>String): Long = checkIdImpl(id, msg)
@targetName("checkIdOption")
def checkId(id: => Long|Option[Long]|Null): Long = checkId(id, s"Id is not set or incorrect [${id.safeValue}].")


private def checkIdImpl(idExpr: => Long|Option[Long]|Null, msg: =>String): Long =
  import com.mvv.nullables.AnyCanEqualGivens.given

  val _id = if idExpr.isPropInitialized then idExpr else null
  val id: Long|Null = _id match
    case null => null
    case vv: Long => vv
    case Some(vv) => vv
    case None => null

  check(id != null && id != 0 && id != -1 && id != None, msg)
  id.nn

private def isValidIdValue(id: Long): Boolean =
  id != 0 && id != -1
