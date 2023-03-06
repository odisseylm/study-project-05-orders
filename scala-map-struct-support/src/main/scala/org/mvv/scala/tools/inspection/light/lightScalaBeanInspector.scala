package org.mvv.scala.tools.inspection.light

import scala.collection.mutable
import scala.collection.concurrent
import scala.quoted.Quotes
import scala.tasty.inspector.{ Inspector, Tasty, TastyInspector }
//
import org.mvv.scala.tools.{ isOneOf, afterLastOrOrigin, replaceSuffix }
import org.mvv.scala.tools.{ getClassLocationUrl, jarUrlToJarPath, fileUrlToPath }
import org.mvv.scala.tools.quotes.{ classSymbolDetails, classExists, getFullClassName, classFullPackageName }
import org.mvv.scala.tools.quotes.{ asClassDef, asDefDef, asValDef }
import org.mvv.scala.tools.inspection.{ _Class, _Type, ScalaBeanInspector, typeNameToRuntimeClassName }
import org.mvv.scala.tools.inspection._Quotes.{ toMethod, toField, classKind, extractTreeType }
import org.mvv.scala.tools.quotes.dummy.DummyClass789456123



//noinspection ScalaFileName
class LightScalaBeanInspector extends ScalaBeanInspector :
  private val classesByFullName: concurrent.Map[String, _Class] = concurrent.TrieMap()

  private class InspectorImpl (val classFullName: String) extends Inspector :
    override def inspect(using q: Quotes)(beanTypes: List[Tasty[q.type]]): Unit =
      import q.reflect.*

      if !classExists(classFullName) then
        throw ClassNotFoundException(s"Class [$classFullName] is not found.")

      val classSymbol = Symbol.classSymbol(classFullName)
      if !classSymbol.isClassDef then
        throw ClassNotFoundException(s"Class [$classFullName] is not found.")

      val _class = processClassDef(classSymbol.tree.asClassDef)

      val possibleClassFullNames = List(
        classFullName, _class.fullName,
        typeNameToRuntimeClassName(classFullName), typeNameToRuntimeClassName(_class.fullName),
      )
      possibleClassFullNames.distinct.foreach( classesByFullName.put(_, _class) )


  override def classesDescr: Map[String, _Class] = Map.from(classesByFullName) // according to doc it is thread-safe
  override def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)


  private var toInspect: String = ""

  override def inspectClass(cls: Class[?]): _Class =
    inspectClass(cls.getName.nn)

  override def inspectClass(fullClassName: String): _Class =
    toInspect = fullClassName

    val dummyClassUrl = getClassLocationUrl(classOf[DummyClass789456123])
    val dummyClassUrlStr = dummyClassUrl.toExternalForm.nn

    val invoker = InspectorImpl(fullClassName)
    if dummyClassUrlStr.contains(".jar!") then
      val jarPath = jarUrlToJarPath(dummyClassUrl)
      TastyInspector.inspectTastyFilesInJar(jarPath.toString)(invoker)
    else
      val dummyClassFilePath = fileUrlToPath(dummyClassUrl)
      val dummyTastyFilePath = dummyClassFilePath.toString.replaceSuffix(".class", ".tasty")
      TastyInspector.inspectTastyFiles(List(dummyTastyFilePath))(invoker)

    classesByFullName.getOrElse(fullClassName,
      throw ClassNotFoundException(s"Class [$fullClassName] is not found."))



  private def processClassDef(using q: Quotes)(classDef: q.reflect.ClassDef): _Class =
    import q.reflect.*
    import org.mvv.scala.tools.inspection._Quotes.toMethod

    val classDefParents: List[Tree /* Term | TypeTree */] = classDef.parents
    val parentTypes: List[_Type] = classDefParents.map(p => extractTreeType(p))

    val clsSymbol: Symbol = classDef.symbol

    val declaredMethodsSymbols: List[Symbol] = clsSymbol.declaredMethods
    val methodMembersSymbols: List[Symbol] = clsSymbol.methodMembers
    val declaredFieldsSymbols: List[Symbol] = clsSymbol.declaredFields
    val fieldMembersSymbols: List[Symbol] = clsSymbol.fieldMembers

    val declaredMethods = declaredMethodsSymbols.map { m => m.tree.asDefDef.toMethod }
    val allMethods = methodMembersSymbols.map { m => m.tree.asDefDef.toMethod }

    val declaredFields = declaredFieldsSymbols.map { m => m.tree.asValDef.toField }
    val allFields = fieldMembersSymbols.map { m => m.tree.asValDef.toField }

    val fullClassName = getFullClassName(classDef)
    val _package = classFullPackageName(classDef)
    val simpleName = fullClassName.afterLastOrOrigin(".")
    val classKind = classDef.classKind

    val _class = _Class(
      fullClassName, _package, simpleName,
      classKind, parentTypes,
      allFields.map(f => (f.toKey, f)).toMap,
      allMethods.map(f => (f.toKey, f)).toMap,
      declaredFields.map(f => (f.toKey, f)).toMap,
      declaredMethods.map(f => (f.toKey, f)).toMap,
      None
    )

    _class
