package org.mvv.scala.tools


extension (s: String)
  def replaceSuffix(oldSuffix: String, newSuffix: String): String =
    if s.endsWith(oldSuffix) then s.stripSuffix(oldSuffix) + newSuffix else s

  def endsWithOneOf(suffix: String, otherSuffixes: String*): Boolean =
    s.endsWith(suffix) || otherSuffixes.exists(s.endsWith)

  def lastAfter(delimiter: Char): Option[String] =
    val lastIndex = s.lastIndexOf(delimiter)
    if lastIndex == -1 then None else Option(s.substring(lastIndex + 1).nn)
