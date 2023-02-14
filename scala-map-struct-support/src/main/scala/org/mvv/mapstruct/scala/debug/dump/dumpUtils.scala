package org.mvv.mapstruct.scala.debug.dump



val indentPerLevel: Int = 2
//val indentPerLevelStr: String = " ".repeat(indentPerLevel).nn

extension (str: StringBuilder)
  def addTagName(tagName: String, currentPadLength: Int): StringBuilder =
    val padStr = " ".repeat(currentPadLength)
    str.append(padStr).append(tagName).append('\n')
  def addTagName(tagName: String, v: Any, currentPadLength: Int): StringBuilder =
    val padStr = " ".repeat(currentPadLength)
    str.append(padStr).append(s"<$tagName>").append(v).append(s"</$tagName>").append('\n')
  def addChildTagName(childTagName: String, currentPadLength: Int): StringBuilder =
    val padChildStr = " ".repeat(currentPadLength + indentPerLevel)
    str.append(padChildStr).append(childTagName).append('\n')
  def addChildTagName(childTagName: String, value: Any, currentPadLength: Int): StringBuilder =
    val padChildStr = " ".repeat(currentPadLength + indentPerLevel)
    str.append(padChildStr)
      .append(s"<$childTagName>")
      .append(value)
      .append(s"</$childTagName>")
      .append('\n')

