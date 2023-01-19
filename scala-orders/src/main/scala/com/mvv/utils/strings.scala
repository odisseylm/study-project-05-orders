package com.mvv.utils

import org.apache.commons.lang3.StringUtils


extension (string: CharSequence)
  def isBlank: Boolean = StringUtils.isBlank(string)
