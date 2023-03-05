package org.mvv.scala.tools.inspection.tasty

import scala.quoted.Quotes
import scala.collection.mutable
//
import org.mvv.scala.tools.{ tryDo, loadClass }
import org.mvv.scala.tools.quotes.{ refName, isExprStatement, classFullPackageName }
import org.mvv.scala.tools.inspection._Quotes.extractType
import org.mvv.scala.tools.inspection.{ ClassKind, InspectMode, _Field, _Method, _Type }
import org.mvv.scala.tools.inspection.tasty.{ FilePackageContainer, _ClassEx }



def logUnexpectedTreeEl(using q: Quotes)(processor: q.reflect.TreeTraverser|String, treeEl: q.reflect.Tree): Unit =
  import q.reflect.TreeTraverser
  val logPrefix: String = processor match
    case s: String => s
    case p: TreeTraverser => p.simpleClassName
  log.warn(s"$logPrefix: Unexpected tree element!" +
    s" Please explicitly process or ignore element: $treeEl")



//noinspection ScalaUnusedSymbol
def processTypeDef(using q: Quotes)(typeDef: q.reflect.TypeDef, _package: FilePackageContainer): Unit =
  // seems now nothing useful there
  //logUnexpectedTreeEl("processTypeDef", typeDef)
  log.debug(s"processTypeDef => typedefs are ignored now: $typeDef")



//noinspection NoTailRecursionAnnotation , // there is no recursion at all
def processClassDef2(using q: Quotes)(
  classDef: q.reflect.ClassDef,
  inspectMode: InspectMode,
  ): List[_ClassEx] =
  val _package = FilePackageContainer(classFullPackageName(classDef))
  processClassDef(classDef, _package, inspectMode, ClassSource.MacroQuotes)



def processClassDef(using q: Quotes)(
  classDef: q.reflect.ClassDef,
  _package: FilePackageContainer,
  inspectMode: InspectMode,
  classSource: ClassSource,
  ): List[_ClassEx] =
  import q.reflect.*

  val _simpleClassName: String = classDef.name
  val _fullClassName: String = s"${_package.fullName}.$_simpleClassName"
  val parents: List[_Type] = classDef.parents
    .map(parentTree => extractType(parentTree))
    .filter(_type => !classesToIgnore.contains(_type))

  var internalClasses: List[_ClassEx] = Nil

  val declaredFields = mutable.ArrayBuffer[_Field]()
  val declaredMethods = mutable.ArrayBuffer[_Method]()

  val body: List[Statement] = classDef.body

  body.foreach { (tree: Tree) =>
    import org.mvv.scala.tools.inspection._Quotes.{extractType, toField, toMethod}

    val logPrefix = s"${this.simpleClassName}: "

    try
      tree match
        case PackageClause(pid: Ref, _) => // hm... is it possible??
          log.warn(s"$logPrefix Unexpected package definition [${refName(pid)}] inside class [$_fullClassName].")

        case cd: ClassDef =>
          internalClasses ++= processClassDef(cd, _package, inspectMode, classSource)

        case td: TypeDef => processTypeDef(td, _package)

        case vd: ValDef =>
          val f: _Field = vd.toField
          declaredFields.addOne(f)

        // functions/methods
        case dd: DefDef =>
          val m: _Method = dd.toMethod
          log.debug(s"$logPrefix method: $m")
          declaredMethods.addOne(m)

        case _: Import => // ignore
        // statements
        // some instructions inside class definition

        case _ =>
          if !tree.isExprStatement then
            logUnexpectedTreeEl("processClassDef", tree)
    catch
      // There is AssertionError because it can be thrown by scala compiler?!
      //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
      case ex: (Exception | AssertionError) => log.error(s"$logPrefix Unexpected error $ex", ex)
  }

  val runtimeClass: Option[Class[?]] = if inspectMode == InspectMode.AllSources
                                       then tryDo(loadClass(_fullClassName)) else None

  val classFullName = classDef.symbol.fullName

  val _class = _ClassEx(
    classFullName,
    _package.fullName,
    _simpleClassName,
    ClassKind.Scala3, runtimeClass.map(cls => ClassSource.of(cls)).orElse(Option(classSource)),
    parents,
    declaredFields.map(f => (f.toKey, f)).toMap,
    declaredMethods.map(m => (m.toKey, m)).toMap,
    runtimeClass,
  )(None)

  val allClasses = _class :: internalClasses
  allClasses

end processClassDef
