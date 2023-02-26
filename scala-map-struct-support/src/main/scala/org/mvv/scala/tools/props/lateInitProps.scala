package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.targetName
import scala.compiletime.uninitialized
import scala.quoted.{ Expr, Quotes, Type, Varargs }
//
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName }
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, qStringLiteral, findClassThisScopeTypeRepr }


/*
def isInitialized(v: Option[?]) = v.isDefined
def isInitialized(v: AnyRef) =
  import scala.language.unsafeNulls
  v != null // uninitialized



class Class1
class Class2 extends Class1

def isInitialized(v: Class1): Boolean = ???
def isInitialized(v: Class2): Boolean = ???

@targetName("isIntInitialized")
def isInitialized(v: NamedValue[Int]): Boolean = ???
@targetName("isAnyRefInitialized")
def isInitialized(v: NamedValue[AnyRef]): Boolean = ???
@targetName("isOptionInitialized")
def isInitialized(v: NamedValue[Option[?]]): Boolean = ???
@targetName("isStringInitialized")
def isInitialized(v: NamedValue[String]): Boolean = ???
@targetName("isClass1Initialized")
def isInitialized(v: NamedValue[Class1]): Boolean = ???
@targetName("isClass2Initialized")
def isInitialized(v: NamedValue[Class2]): Boolean = ???

val prop1: String = ""

val aa: List[(String, ()=>Boolean)] = List(
  ("prop1", () => isInitialized(prop1))


)
*/

type IsInitializedProps = List[(String, ()=>Boolean)]


inline def currentClassIsInitializedProps: IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl }

def currentClassIsInitializedPropsImpl(using q: Quotes): Expr[IsInitializedProps] =
  import q.reflect.*

  //val log = Logger(topClassOrModuleFullName) // java.lang.NoClassDefFoundError: org/mvv/scala/tools/quotes/quotesPrimitives$package$
  //        at org.mvv.scala.tools.quotes.baseMacros$package$.topClassOrModuleFullNameImpl(baseMacros.scala:19)
  val log = Logger("fsdfdsfdf") // TODO: dfdf

  //val initProps: mutable.ArrayBuffer

  val classDef: ClassDef = find1stOwnerClass().get
  val body: List[Statement] = classDef.body

  val valDefs: List[ValDef] = body.map { stat =>
      stat match
        case vd: ValDef => Option(vd).filter(toCheckInitState)
        case _ => None // ignore
    }
    .filter(_.isDefined).map(_.get)

  //classDef.

  val ownerFullClassName = classDef.symbol.fullName

  //val tuple: Term = aaaa(ownerFullClassName, valDefs.head)
  val tuples: List[Term] = valDefs.map(vd => aaaa(ownerFullClassName, vd))
  val tuplesExprs = tuples.map(_.asExprOf[(String,()=>Boolean)])
  val exprOfTupleList = Expr.ofList(tuplesExprs)

  println(s"%%% resListExpr: ${exprOfTupleList.show}")

  //val tuples = valDefs.map(vd => qStringValueTuple2(vd.name, qInlineValName(vd.name)))
  //val listOfTuples = qList(tuples)

  //???
  //'{ List( ("amountNotInitialized", () => isInitialized(amountNotInitialized)) ) }
  //'{ List( ("amountNotInitialized", () => org.mvv.scala.tools.props.isInitialized(amountNotInitialized)) ) }
  //'{ List( ("amountNotInitialized", () => isInitialized(amountNotInitialized)) ) }
  //val aa: Expr[String] = '{ "amountNotInitialized" }
  //val aa: Expr[String] = Inlined(None, Nil, qStringLiteral("amountNotInitialized")).asExpr[String]
  //'{ List( ($aa, () => isInitialized(amountNotInitialized)) ) }
  //  .asTerm.symbol.changeOwner(Symbol.spliceOwner)
  //???
  //ddd.asTerm
  exprOfTupleList



def toCheckInitState(using q: Quotes)(valDef: q.reflect.ValDef): Boolean =
  //???
  true



def qTuple2(using q: Quotes)(v1: q.reflect.Term, v2: q.reflect.Term): q.reflect.Term =
  import q.reflect.*
  //val tuple2ClassTerm: Term = qClassNameOf[Tuple2]
  val tuple2ClassTerm: Term = qClassName(classOf[Tuple2[Any,Any]])
  val tuple2ApplySelect = Select.unique(tuple2ClassTerm, "apply")
  val tuple2TypeApply = TypeApply(tuple2ApplySelect, List(TypeTree.of[String], TypeTree.of[()=>Boolean]))
  val tupleApply = Apply(tuple2TypeApply, List(v1, v2))
  tupleApply



def qStringValueTuple2(using q: Quotes)(str: String, value: q.reflect.Term): q.reflect.Term =
  qTuple2(qStringLiteral(str), value)



def qInlineValName(using q: Quotes)(valName: String): q.reflect.Term =
  ???



def qList(using q: Quotes)(values: List[q.reflect.Term]): q.reflect.Term =
  ???


// TODO: use [T <: ReadOnlyProp[?] ]
// TODO: rename
inline def lateInitProps: List[ReadOnlyProp[AnyRef]] =
  ${ lateInitPropsImpl }


inline def lateInitProps2[ClassT]: List[ReadOnlyProp[AnyRef]] =
  ${ lateInitProps2Impl[ClassT] }


def lateInitPropsImpl(using q: Quotes): Expr[List[ReadOnlyProp[AnyRef]]] =
  import q.reflect.*

  val ownerClass: ClassDef = find1stOwnerClass()
    .getOrElse { report.errorAndAbort("Owner class is not found.") }

  ownerClass.body.map { (el: Statement) =>
    el match
      case valDef: ValDef =>
        println(s"%%% lateInitProp: valDef = $valDef")
        println(s"%%% lateInitProp: valDef.annotations = ${valDef.symbol.annotations}")
        println(s"%%% lateInitProp: valDef rhs = ${valDef.rhs}")
      case other =>
        println(s"%%% lateInitProp: other = $other")
  }

  '{ Nil }


private def find1stOwnerClass(using q: Quotes)(): Option[q.reflect.ClassDef] =
  import q.reflect.* //{ Symbol, tree }

  var s: Symbol = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isClassDef do
    s = s.maybeOwner

  if s.isClassDef then Option(s.tree.asInstanceOf[ClassDef]) else None




def lateInitProps2Impl[ClassT](using q: Quotes)(using Type[ClassT]): Expr[List[ReadOnlyProp[AnyRef]]] =
  import q.reflect.*

  val ownerClass: ClassDef = findClassDef[ClassT]()
    //.getOrElse { report.errorAndAbort("Owner class is not found.") }

  ownerClass.body.map { (el: Statement) =>
    el match
      case valDef: ValDef =>
        println(s"%%% lateInitProp2: valDef name = ${valDef.name}")
        println(s"%%% lateInitProp2: valDef tpt  = ${valDef.tpt}")
        println(s"%%% lateInitProp2: valDef rhs  = ${valDef.rhs}")
        println(s"%%% lateInitProp2: valDef annotations  = ${valDef.symbol.annotations}")

      case other =>
        println(s"%%% lateInitProp2: other = $other")
  }

  '{ Nil }


private def findClassDef[ClassT](using q: Quotes)(using Type[ClassT])(): q.reflect.ClassDef =
  import q.reflect.*

  val tr = TypeRepr.of[ClassT]

  val s = Symbol.classSymbol(tr.show)

  println(s"%%% findClassDef: $s")
  println(s"%%% findClassDef: ${s.tree}")

  s.tree.asInstanceOf[ClassDef]



def aaaa(using q: Quotes)(classFullName: String, fieldOrGetter: q.reflect.ValDef): q.reflect.Term =
  import q.reflect.*

  //val classRepr: TypeRepr = TypeRepr.of[O]
  //val this_ = This(classRepr.classSymbol.get)

  //val classRepr: TypeRepr = Symbol TypeRepr.
  val classSymbol = Symbol.classSymbol(classFullName)
  val this_ = This(classSymbol)
  //val (classTypeRepr: TypeRepr, className333: String) = TypeRef.unaaply(classSymbol.typeRef)
  //val (classTypeRepr: TypeRepr, className333: String) = classSymbol.typeRef
  //val this_ = This(classRepr.classSymbol.get)

  val propType: TypeRepr = fieldOrGetter.tpt.tpe
  val valSymbol = Symbol.newVal(
    this_.symbol, fieldOrGetter.name, propType,
    Flags.EmptyFlags, Symbol.noSymbol
  )

  // Select( This( Ident(LateInitPropsTest) ), val1 )

  // qClassName(classFullName)
  val valSelect = Select.unique(This(classSymbol), fieldOrGetter.name)

  /*
  // TODO: use proper type!!!! Not String as this!!!
  val this_ = This(TypeRepr.of[O].classSymbol.get)

  val setterMethodSymbol = Symbol.newMethod(
    this_.symbol, // TODO: use util function
    setterFullMethodName,
    MethodType(
      List("v88"))( // parameter list - here a single parameter
      _ => List(Symbol.requiredClass(TypeRepr.of[T].show).typeRef),
      _ => TypeRepr.of[Unit]
    ))
  */

  //Ident()

  val scopeTypRepr: TypeRepr = findClassThisScopeTypeRepr(classSymbol).get
  val isInitializedTerRef = TermRef(scopeTypRepr, "isInitialized")
  val isInitializedIdentTerm = Ident(isInitializedTerRef)

  val isInitializedApply = Apply(isInitializedIdentTerm, List(valSelect))

  val rhsFn: (Symbol, List[Tree]) => Tree = (_: Symbol, _: List[Tree]) => { isInitializedApply }
  val isInitializedAnonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(
      Nil)(
      _ => Nil,
      _ => TypeRepr.of[Boolean]
    ),
    rhsFn
  )

  val tuple = qStringValueTuple2(fieldOrGetter.name, isInitializedAnonFunLambda)

  //Varargs.

  //List(1, 2)

  println(s"%%% isInitializedAnonFunLambda: ${isInitializedAnonFunLambda.show} $isInitializedAnonFunLambda")

  //val ee = Expr.ofList(List(tuple.asExprOf[(String,()=>Boolean)]))
  //println(s"%%% isInitializedAnonFunLambda ee: \n${ee.show} \n${ee.asTerm}")

  //???
  tuple



/*
def aaaa(using q: Quotes)(classFullName: String, fieldOrGetter: q.reflect.ValDef): q.reflect.Term =
  import q.reflect.*

  //val classRepr: TypeRepr = TypeRepr.of[O]
  //val this_ = This(classRepr.classSymbol.get)

  //val classRepr: TypeRepr = Symbol TypeRepr.
  val classSymbol = Symbol.classSymbol(classFullName)
  val this_ = This(classSymbol)
  //val (classTypeRepr: TypeRepr, className333: String) = TypeRef.unaaply(classSymbol.typeRef)
  //val (classTypeRepr: TypeRepr, className333: String) = classSymbol.typeRef
  //val this_ = This(classRepr.classSymbol.get)

  val propType: TypeRepr = fieldOrGetter.tpt.tpe

  /*
  val getterMethodSymbol = Symbol.newMethod(
    this_.symbol,
    fieldOrGetter.name,
    MethodType(
      Nil)(
      _ => Nil,
      _ => propType,
    ))
  */

  val getterValSymbol = Symbol.newVal(
    this_.symbol,
    fieldOrGetter.name,
    propType,
    Flags.EmptyFlags, Symbol.noSymbol
  )

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
    println("testFunc 06  rhsFn s: $s: $paramsAsTrees")

    //val getterMethodSymbolAsSelect = Select(thisExpr.asTerm, getterValSymbol)
    val getterMethodSymbolAsSelect = Select(this_, getterValSymbol)

    val applyParams: List[Term] = paramsAsTrees
      .map((vvv: Tree) => {
        //val typeApply = TypeApply(getterMethodSymbolAsSelect, List(TypeTree.of[T]))
        val typeApply = TypeApply(getterMethodSymbolAsSelect, List(TypeTree.ref(propType.typeSymbol)))
        typeApply
      })

    val apply: Apply = Apply(getterMethodSymbolAsSelect, applyParams)
    println(s"apply: ${apply.show}.")
    apply
  }

  val anonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(
      Nil)(
      _ => Nil,
      _ => propType
    ),
    rhsFn
  )

  println(s"%%% anonFunLambda: ${anonFunLambda.show} $anonFunLambda")
  ???
*/
