//package com.mvv.scala.temp.tests.macros2
package com.mvv.scala.macros


enum LogLevel :
  // In spite of scala code convention it is better to use log-level in upper case
  // since it is already usual/expected format
  case TRACE, DEBUG, INFO, WARN, ERROR

// T O D O: find better name
trait LogAppender :
  protected def appendMessage(logLevel: LogLevel, msg: =>String): Unit

//noinspection ScalaWeakerAccess,ScalaUnusedSymbol
trait Logger :
  self: LogAppender =>

  def currentLogeLevel: LogLevel
  def toLog(logLevel: LogLevel): Boolean = logLevel.ordinal >= currentLogeLevel.ordinal
  def log(logLevel: LogLevel, msg: =>String): Unit = if toLog(logLevel) then appendMessage(logLevel, msg)
  def trace(msg: =>String): Unit = log(LogLevel.TRACE, msg)
  def debug(msg: =>String): Unit = log(LogLevel.DEBUG, msg)
  def info(msg: =>String): Unit = log(LogLevel.INFO, msg)
  def warn(msg: =>String): Unit = log(LogLevel.WARN, msg)
  def error(msg: =>String): Unit = log(LogLevel.ERROR, msg)


object Logger extends ConsoleLogger(macrosLogLevel)


private class ConsoleLogger
  (override val currentLogeLevel: LogLevel = LogLevel.TRACE)
  extends Logger, LogAppender :
  //override val currentLogeLevel: LogLevel = LogLevel.Trace
  protected override def appendMessage(logLevel: LogLevel, msg: => String): Unit =
    val out = if logLevel.ordinal < LogLevel.WARN.ordinal then Console.out else Console.err
    out.println(s"Scala macros $logLevel $msg")


private def macrosLogLevel: LogLevel = LogLevel.TRACE
