package org.mvv.scala.tools

import java.lang.Math.min


extension (s: String)
  def replaceSuffix(oldSuffix: String, newSuffix: String): String =
    if s.endsWith(oldSuffix) then s.stripSuffix(oldSuffix) + newSuffix else s

  def endsWithOneOf(suffix: String, otherSuffixes: String*): Boolean =
    s.endsWith(suffix) || otherSuffixes.exists(s.endsWith)

  def afterLast(delimiter: Char): Option[String] =
    val lastIndex = s.lastIndexOf(delimiter)
    if lastIndex == -1 then None else Option(s.substring(lastIndex + 1).nn)

  def afterLast(delimiter: String): Option[String] =
    val lastIndex = s.lastIndexOf(delimiter)
    if lastIndex == -1 then None else Option(s.substring(lastIndex + delimiter.length).nn)

  def afterFirst(delimiter: String): Option[String] =
    val firstIndex = s.indexOf(delimiter)
    if firstIndex == -1 then None else Option(s.substring(firstIndex + delimiter.length).nn)

  def beforeFirst(delimiter: Char): Option[String] =
    val firstIndex = s.indexOf(delimiter)
    if firstIndex == -1 then None else Option(s.substring(0, firstIndex).nn)

  // TODO: find better name
  def beforeFirstOrOriginal(delimiter: Char): String =
    val firstIndex = s.indexOf(delimiter)
    if firstIndex == -1 then s else s.substring(0, firstIndex).nn

  def beforeLast(delimiter: Char): Option[String] =
    val lastIndex = s.lastIndexOf(delimiter)
    if lastIndex == -1 then None else Option(s.substring(0, lastIndex).nn)

  def safeSubString(beginIndex: Int, endIndex: Int): String =
    val lastIndex = min(endIndex, s.length)
    s.substring(beginIndex, lastIndex).nn

  def ifBlank(alternativeValue: => String): String =
    if s.isBlank then alternativeValue else s

  def uncapitalize: String =
    if s.isNull || s.isEmpty || !s.charAt(0).isUpper then s
    else s"${s.charAt(0).toLower}${s.substring(1)}"

