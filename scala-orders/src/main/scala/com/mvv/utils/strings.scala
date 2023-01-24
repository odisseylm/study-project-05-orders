package com.mvv.utils

import org.apache.commons.lang3.StringUtils


extension (string: CharSequence)
  def isBlank: Boolean = StringUtils.isBlank(string)


extension (string: String)
  // TODO: remove ones which are present in scala StringOps
  def removePrefix(prefix: String): String = StringUtils.removeStart(string, prefix).nn
  def removeSuffix(prefix: String): String = StringUtils.removeEnd(string, prefix).nn
  def uncapitalize: String = StringUtils.uncapitalize(string).nn
