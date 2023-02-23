package org.mvv.scala.tools.beans


import scala.quoted.Quotes
import scala.collection.mutable
//
import org.mvv.scala.tools.beans._Quotes.extractType
import org.mvv.scala.tools.tryDo
import org.mvv.scala.tools.quotes.refName



def logUnexpectedTreeEl(using q: Quotes)(processor: q.reflect.TreeTraverser|String, treeEl: q.reflect.Tree): Unit =
  import q.reflect.TreeTraverser
  val logPrefix: String = processor match
    case s: String => s
    case p: TreeTraverser => p.simpleClassName
  log.warn(s"$logPrefix: Unexpected tree element!" +
    s" Please explicitly process or ignore element: $treeEl")



def processTypeDef(using q: Quotes)(typeDef: q.reflect.TypeDef, _package: FilePackageContainer): Unit =
  // TODO: impl
  logUnexpectedTreeEl("processTypeDef", typeDef)



def processClassDef(using q: Quotes)(
  classDef: q.reflect.ClassDef,
  _package: FilePackageContainer,
  inspectMode: InspectMode,
  ): _Class =
  import q.reflect.*

  val _simpleClassName: String = classDef.name
  val _fullClassName: String = s"${_package.fullName}.$_simpleClassName"
  val parents: List[_Type] = classDef.parents
    .map(parentTree => extractType(parentTree))
    .filter(_type => !classesToIgnore.contains(_type))

  //val declaredFields = scala.collection.mutable.ArrayBuffer[_Fields]
  val declaredFields = mutable.ArrayBuffer[_Field]()
  val declaredMethods = mutable.ArrayBuffer[_Method]()

  val body: List[Statement] = classDef.body

  body.foreach { (tree: Tree) =>
    import org.mvv.scala.tools.beans._Quotes.{extractType, toField, toMethod}

    val logPrefix = s"${this.simpleClassName}: "

    try
      tree match
        case PackageClause(pid: Ref, _) => // hm... is it possible??
          log.warn(s"$logPrefix Unexpected package definition [${refName(pid)}] inside class [$_fullClassName].")

        case ClassDef(internalClassName: String, _, _, _, _) =>
          log.info(s"$logPrefix Internal class [$_fullClassName#$internalClassName] is ignored" +
            s" because there is no sense to process such this-dependent classes.")

        case td@TypeDef(typeName: String, rhs: Tree) =>
          processTypeDef(td, _package)

        case vd@ValDef(name: String, tpt: TypeTree, rhs: Option[Term]) =>
          val f: _Field = vd.toField
          log.info(s"$logPrefix field: $f")
          declaredFields.addOne(f)

        // functions/methods
        case dd@DefDef(name: String, paramss: List[ParamClause], tpt: TypeTree, rhs: Option[Term]) =>
          val m: _Method = dd.toMethod
          log.info(s"$logPrefix method: $m")
          declaredMethods.addOne(m)

        case _ =>
          logUnexpectedTreeEl("processClassDef", tree)
    catch
      // There is AssertionError because it can be thrown by scala compiler?!
      //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
      case ex: (Exception | AssertionError) => log.error(s"$logPrefix Unexpected error $ex", ex)
  }

  val runtimeClass: Option[Class[?]] = if inspectMode == InspectMode.AllSources
                                       then tryDo(loadClass(_fullClassName)) else None

  val _class = _Class(
    _package.fullName, _simpleClassName,
    ClassKind.Scala3, runtimeClass.map(cls => ClassSource.of(cls)),
    parents,
    declaredFields.map(f => (f.toKey, f)).toMap,
    declaredMethods.map(m => (m.toKey, m)).toMap,
    runtimeClass,
  )(None)
  _class

end processClassDef
