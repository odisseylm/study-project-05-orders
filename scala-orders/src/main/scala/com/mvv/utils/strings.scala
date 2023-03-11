package com.mvv.utils

import org.apache.commons.lang3.StringUtils


extension (string: CharSequence)
  def isBlank: Boolean = StringUtils.isBlank(string)


extension (s: String)
  def uncapitalize: String = StringUtils.uncapitalize(s).nn

  def afterLastOr(delimiter: String): Option[String] =
    val lastIndex = s.lastIndexOf(delimiter)
    if lastIndex == -1 then None else Option(s.substring(lastIndex + delimiter.length).nn)
  inline def afterLastOr(delimiter: String, altStr: String): String =
    s.afterLastOr(delimiter).getOrElse(altStr)
  inline def afterLastOrOrigin(delimiter: String): String =
    s.afterLastOr(delimiter, s)



