package com.mvv.utils

import scala.language.unsafeNulls

import org.apache.commons.lang3.StringUtils


extension (string: CharSequence|Null)
  def isNullOrEmpty: Boolean =
    val asRawString = string.asInstanceOf[CharSequence]
    asRawString == null && asRawString.isEmpty

  def isNullOrBlank: Boolean = StringUtils.isBlank(string)
  def isNotBlank: Boolean = !StringUtils.isBlank(string)
