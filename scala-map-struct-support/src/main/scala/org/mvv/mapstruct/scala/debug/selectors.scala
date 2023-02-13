package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// base Selector = Selector <: AnyRef
def dumpSelector(using quotes: Quotes)(selector: quotes.reflect.Selector, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  selector match
    // Selector <: AnyRef
    // RenameSelector <: Selector
    case el if el.isRenameSelector => dumpRenameSelector(el.asInstanceOf[RenameSelector], str, padLength)
    // OmitSelector <: Selector
    case el if el.isOmitSelector => dumpOmitSelector(el.asInstanceOf[OmitSelector], str, padLength)
    // GivenSelector <: Selector
    case el if el.isGivenSelector => dumpGivenSelector(el.asInstanceOf[GivenSelector], str, padLength)
    // base Selector = Selector <: AnyRef
    case el if el.isSelector => dumpBaseSelector(el, str, padLength)



// Selector <: AnyRef
// RenameSelector <: Selector
def dumpRenameSelector(using quotes: Quotes)(selector: quotes.reflect.RenameSelector, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val fromName: String = selector.fromName
  val fromPos: Position = selector.fromPos
  val toName: String = selector.toName
  val toPos: Position = selector.toPos

  str.addTagName("<TermParamClause>", padLength)
    str.addChildTagName("fromName", fromName, padLength)
    str.addChildTagName("fromPos", positionToString(fromPos), padLength)
    str.addChildTagName("toName", toName, padLength)
    str.addChildTagName("toPos", positionToString(toPos), padLength)
  str.addTagName("</TermParamClause>", padLength)



// OmitSelector <: Selector
def dumpOmitSelector(using quotes: Quotes)(selector: quotes.reflect.OmitSelector, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val name: String = selector.name
  val namePos: Position = selector.namePos

  str.addTagName("<OmitSelector>", padLength)
    str.addChildTagName("name", name, padLength)
    str.addChildTagName("namePos", positionToString(namePos), padLength)
  str.addTagName("</OmitSelector>", padLength)



// GivenSelector <: Selector
def dumpGivenSelector(using quotes: Quotes)(selector: quotes.reflect.GivenSelector, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  val bound: Option[TypeTree] = selector.bound

  str.addTagName("<GivenSelector>", padLength)
  bound.foreach(b => dumpTree(b, str, padLength + 2 * indentPerLevel))
  str.addTagName("</GivenSelector>", padLength)



//noinspection ScalaUnusedSymbol
// base Selector <: AnyRefSelector
def dumpBaseSelector(using quotes: Quotes)(selector: quotes.reflect.Selector, str: StringBuilder, padLength: Int): Unit =
  str.addTagName("<Selector/>", padLength)


//noinspection ScalaUnusedSymbol
private def positionToString(using quotes: Quotes)(pos: quotes.reflect.Position): String =
  import quotes.reflect.*
  val start: Int = pos.start
  val end: Int = pos.end
  val sourceFile: SourceFile = pos.sourceFile
  val sourceFileStr: String = sourceFileToString(sourceFile)
  val startLine: Int = pos.startLine
  val endLine: Int = pos.endLine
  val startColumn: Int = pos.startColumn
  val endColumn: Int = pos.endColumn
  val sourceCode: Option[String] = pos.sourceCode

  s"Position { $sourceFileStr $startLine:$startColumn - $endLine:$endColumn }"


private def sourceFileToString(using quotes: Quotes)(sourceFile: quotes.reflect.SourceFile): String =
  import quotes.reflect.*
  //noinspection ScalaUnusedSymbol
  val getJPath: Option[java.nio.file.Path] = sourceFile.getJPath
  val name: String = sourceFile.name
  val path: String = sourceFile.path
  val content: Option[String] = sourceFile.content

  s"SourceFile { name: $name, path: $path, content length: ${ content.map(_.length).getOrElse(-1) } }"
