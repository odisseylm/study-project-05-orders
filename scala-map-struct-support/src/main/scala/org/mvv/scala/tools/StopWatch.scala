package org.mvv.scala.tools

import org.mvv.scala.tools.StopWatch.started

class StopWatch private (name: String) :
  private var startedNanos: Long = 0

  private def start(): Unit = startedNanos = System.nanoTime()
  def elapsedNanos: Long = if startedNanos == 0 then -1 else System.nanoTime - startedNanos
  def elapsedMs: Long =
    val asNanos = elapsedMs
    if asNanos == -1 then -1 else asNanos / 1000

  def elapsedMsg: String = elapsedMsg(null)

  def elapsedMsg(label: Any|Null): String =
    val nanos = elapsedNanos

    val descr = StringBuilder()
    if !name.isBlank then descr.append(name)
    if label.isNotNull then
      if descr.nonEmpty then descr.append(" ")
      descr.append(label)
    if descr.nonEmpty then descr.append(" ")

    val elapsedTime = formatTimePeriod(nanos)
    s"$descr$elapsedTime"

  def elapsed: String =
    val nanos = elapsedNanos
    val elapsedTimeStr = formatTimePeriod(nanos)
    elapsedTimeStr


object StopWatch :
  def started(): StopWatch = started("")
  def started(name: String): StopWatch =
    val stopWatch = new StopWatch(name)
    stopWatch.start()
    stopWatch
  // add others if needed (for example nonStarted)



/** This format can be changed! Do not rely on it! It is designed for easy reading by human! */
def formatTimePeriod(nanos: Long): String =
  val minutesPart = nanos / (60 * 1000_000_000L)
  val secondsPart = (nanos / 1000_000_000L) % 60
  val millisPart = (nanos / 1000_000) % 1000
  val microsPart = (nanos / 1000) % 1000
  val nanosPart = nanos % 1000

  def secondsPadTo2(seconds: Long): String =
    seconds.toString.leftPadTo(2, '0')
  def numberPadTo3(v: Long): String =
    if v == 0 then "0" else v.toString.leftPadTo(3, '0')

  if minutesPart != 0 then return s"$minutesPart:${secondsPadTo2(secondsPart)}"
  if secondsPart != 0 then return s"$secondsPart.${numberPadTo3(millisPart)}s"
  if millisPart  != 0 then return s"$millisPart.${numberPadTo3(microsPart)}ms"
  if microsPart  != 0 then return s"$microsPart.${numberPadTo3(nanosPart)}mcs"
  s"${nanos}ns"

  /*
  val str = StringBuilder()
  if minutesPart != 0 then str.addTimePart(minutesPart, 5).append(':')
  if secondsPart != 0 then str.addTimePart(secondsPart, 2).append(':')
  if millisPart  != 0 then str.addTimePart(millisPart, 3).append(':')
  if nanosPart   != 0 then str.addTimePart(nanosPart, 3)

  str.append("ns")
  str.toString
  */



extension (str: StringBuilder)
  private def addTimePart(part: Long, maxLength: Int): StringBuilder =
    str.append( if str.isEmpty then part else part.toString.padTo(maxLength, '0') )
