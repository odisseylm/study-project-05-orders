package com.mvv.log


// Feel free to add support of logLevel/marker
// type LogLevel = org.slf4j.event.Level
// type LogMarker = org.slf4j.Marker

final class Logger (val loggerName: String) :
  private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(loggerName).nn

  def this(klass: Class[?]) = this(klass.getName.nn)

  // we can make them 'inline' but I do not think that it is good idea
  def trace(msg: =>String): Unit = if log.isTraceEnabled() then log.trace(msg)
  def debug(msg: =>String): Unit = if log.isDebugEnabled then log.debug(msg)
  def info(msg: =>String): Unit = if log.isInfoEnabled then log.info(msg)
  def warn(msg: =>String): Unit = if log.isWarnEnabled then log.warn(msg)
  def error(msg: =>String): Unit = if log.isErrorEnabled then log.error(msg)


trait LoggerMixin :
  val log: Logger = Logger(this.getClass)
