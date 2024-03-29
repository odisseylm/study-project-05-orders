package org.mvv.scala.tools


enum LogLevel :
  case TRACE, DEBUG, INFO, WARN, ERROR;


sealed trait Logger :
  def trace(msg: => String): Unit
  def trace(msg: => String, ex: Throwable): Unit
  def debug(msg: => String): Unit
  def debug(msg: => String, ex: Throwable): Unit
  def info (msg: => String): Unit
  def info (msg: => String, ex: Throwable): Unit
  def warn (msg: => String): Unit
  def warn (msg: => String, ex: Throwable): Unit
  def error(msg: => String): Unit
  def error(msg: => String, ex: Throwable): Unit

object Logger :
  def apply(_class: Class[?]): Logger = apply(_class.getName.nn)
  def apply(name: String): Logger =
    if isUnderMaven || isUnderGradle then Slf4jLogger(name)
    if isUnderSbt then Slf4jLogger(name)
    else new ConsoleLogger(name)

private def isUnderMaven: Boolean =
  // not implemented
  false
private def isUnderGradle: Boolean =
  // not implemented
  false
private def isUnderSbt: Boolean =
  // not implemented
  false



private def getLogLevel(loggerName: String): LogLevel =
  getSysPropOrEnvVar(s"$loggerName.logLevel")
    .orElse(getSysPropOrEnvVar(s"scalaMapStruct.logLevel"))
    .map(logLevelStr => LogLevel.valueOf(logLevelStr.trim.nn.toUpperCase.nn))
    .getOrElse(LogLevel.INFO)



final class ConsoleLogger (val loggerName: String, val logLevel: LogLevel) extends Logger :
  def this(loggerName: String) = this(loggerName, getLogLevel(loggerName))

  private inline def isEnabled(logLevel: LogLevel): Boolean = // SCALAMAPSTRUCT_LOGLEVEL
    logLevel.ordinal >= this.logLevel.ordinal

  def this(klass: Class[?]) = this(klass.getName.nn)
  def this(klass: Class[?], logLevel: LogLevel) = this(klass.getName.nn, logLevel)

  // we can make them 'inline' but I do not think that it is good idea
  override def trace(msg: =>String): Unit = if isEnabled(LogLevel.TRACE) then printlnToOut(LogLevel.TRACE, msg)
  override def debug(msg: =>String): Unit = if isEnabled(LogLevel.DEBUG) then printlnToOut(LogLevel.DEBUG, msg)
  override def info(msg:  =>String): Unit = if isEnabled(LogLevel.INFO)  then printlnToOut(LogLevel.INFO, msg)
  override def warn(msg:  =>String): Unit = if isEnabled(LogLevel.WARN)  then printlnToErr(LogLevel.WARN, msg)
  override def error(msg: =>String): Unit = if isEnabled(LogLevel.ERROR) then printlnToErr(LogLevel.ERROR, msg)

  override def trace(msg: =>String, ex: Throwable): Unit =
    if isEnabled(LogLevel.TRACE) then printlnToOut(LogLevel.TRACE, msg); ex.printStackTrace()
  override def debug(msg: =>String, ex: Throwable): Unit =
    if isEnabled(LogLevel.DEBUG) then printlnToOut(LogLevel.DEBUG, msg); ex.printStackTrace()
  override def info(msg: =>String, ex: Throwable): Unit =
    if isEnabled(LogLevel.INFO)  then printlnToOut(LogLevel.INFO, msg);  ex.printStackTrace()
  override def warn(msg: =>String, ex: Throwable): Unit =
    if isEnabled(LogLevel.WARN)  then printlnToErr(LogLevel.WARN, msg);  ex.printStackTrace(System.err)
  override def error(msg: =>String, ex: Throwable): Unit =
    if isEnabled(LogLevel.ERROR) then printlnToErr(LogLevel.ERROR, msg); ex.printStackTrace(System.err)

  private def printlnToOut(logLevel: LogLevel, msg: =>String): Unit =
    Console.out.println(formatOutLine(logLevel, msg))
  private def printlnToErr(logLevel: LogLevel, msg: =>String): Unit =
    Console.err.println(formatOutLine(logLevel, msg))
  private def formatOutLine(logLevel: LogLevel, msg: => String): String =
    // can be optimized if needed
    s"${logLevel.toString.padTo(5, ' ')} $msg"

private def getSysPropOrEnvVar(sysPropName: String): Option[String] =
  Option(System.getProperty(sysPropName).nnIgnore)
    .orElse(Option(System.getenv(sysPropName.toUpperCase.nn.replace('.', '_').nn).nnIgnore))



final class Slf4jLogger (val loggerName: String) extends Logger :
  private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(loggerName).nn

  // we can make them 'inline' but I do not think that it is good idea
  override def trace(msg: =>String): Unit = if log.isTraceEnabled() then log.trace(msg)
  override def debug(msg: =>String): Unit = if log.isDebugEnabled then log.debug(msg)
  override def info(msg:  =>String): Unit = if log.isInfoEnabled then log.info(msg)
  override def warn(msg:  =>String): Unit = if log.isWarnEnabled then log.warn(msg)
  override def error(msg: =>String): Unit = if log.isErrorEnabled then log.error(msg)

  override def trace(msg: =>String, ex: Throwable): Unit = if log.isTraceEnabled() then log.trace(msg, ex)
  override def debug(msg: =>String, ex: Throwable): Unit = if log.isDebugEnabled then log.debug(msg, ex)
  override def info(msg:  =>String, ex: Throwable): Unit = if log.isInfoEnabled then log.info(msg, ex)
  override def warn(msg:  =>String, ex: Throwable): Unit = if log.isWarnEnabled then log.warn(msg, ex)
  override def error(msg: =>String, ex: Throwable): Unit = if log.isErrorEnabled then log.error(msg, ex)



final class Log4j2Logger (val loggerName: String) extends Logger :
  private val log: org.apache.logging.log4j.Logger = org.apache.logging.log4j.LogManager.getLogger(loggerName).nn

  // we can make them 'inline' but I do not think that it is good idea
  override def trace(msg: =>String): Unit = if log.isTraceEnabled then log.trace(msg)
  override def debug(msg: =>String): Unit = if log.isDebugEnabled then log.debug(msg)
  override def info(msg:  =>String): Unit = if log.isInfoEnabled  then log.info(msg)
  override def warn(msg:  =>String): Unit = if log.isWarnEnabled  then log.warn(msg)
  override def error(msg: =>String): Unit = if log.isErrorEnabled then log.error(msg)

  override def trace(msg: =>String, ex: Throwable): Unit = if log.isTraceEnabled then log.trace(msg, ex)
  override def debug(msg: =>String, ex: Throwable): Unit = if log.isDebugEnabled then log.debug(msg, ex)
  override def info(msg:  =>String, ex: Throwable): Unit = if log.isInfoEnabled then log.info(msg, ex)
  override def warn(msg:  =>String, ex: Throwable): Unit = if log.isWarnEnabled then log.warn(msg, ex)
  override def error(msg: =>String, ex: Throwable): Unit = if log.isErrorEnabled then log.error(msg, ex)
