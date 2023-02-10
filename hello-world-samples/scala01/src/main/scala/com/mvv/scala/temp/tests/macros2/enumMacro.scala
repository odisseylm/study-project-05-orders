package com.mvv.scala.temp.tests.macros2

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.ClassTag
//
import com.mvv.scala.macros.dumpTerm
import com.mvv.scala.macros.findCurrentScopeTypeRepr


//inline def enumMappingFunc[EnumFrom <: scala.reflect.Enum, EnumTo <: scala.reflect.Enum]
//  (inline expr: EnumFrom): (EnumFrom => EnumTo) = // EnumTo =
//  ${ enumMappingFuncImpl[EnumFrom, EnumTo]('expr) }


inline def enumMappingFunc[EnumFrom /*<: scala.reflect.Enum*/, EnumTo /*<: scala.reflect.Enum*/]
  //(): Any =
  (): (EnumFrom => EnumTo) = // EnumTo =
  ${ enumMappingFuncImpl[EnumFrom, EnumTo]() }


def enumMappingFuncImpl[EnumFrom /*<: scala.reflect.Enum*/, EnumTo /*<: scala.reflect.Enum*/]
  //(expr: Expr[(EnumFrom => EnumTo)])
  ()
  (using quotes: Quotes)(using etFrom: Type[EnumFrom])(using etTo: Type[EnumTo]):
    Expr[(EnumFrom => EnumTo)] = // Expr[EnumTo] =
    //Expr[Any] = // Expr[EnumTo] =
  import quotes.reflect.*

  //Ident(TermRef(TypeRepr.of[EnumFrom], "TestEnumValue1"))
  //Ident(TermRef(TypeRepr.of[EnumFrom], "TestEnumValue1_777"))

  def enumValues[EnumType /*<: scala.reflect.Enum*/](using Type[EnumType]): List[String] =
    val classSymbol: Symbol = Symbol.classSymbol(Type.show[EnumType]) // Symbol.requiredClass(typeNameStr)
    val children: List[Symbol] = classSymbol.children
    // maybe with complex enums we need to filter out non-enum items
    val enumNames: List[String] = children.map(_.name)
    enumNames

  /*
  val typeRepr: TypeRepr = TypeRepr.of[EnumFrom]
  val typeName: String = Type.show[EnumFrom]
  val classSymbol2: Symbol = Symbol.requiredClass(typeName) //.termSymbol

  printFields("classSymbol2", Symbol.requiredClass(typeName))
  printFields("newBind", Symbol.newBind(classSymbol2, "TestEnumValue1", Flags.Enum, typeRepr))

  ???
  */

  /*
  def classToTerm[T](using Type[T]): Term =
    val typeRepr: TypeRepr = TypeRepr.of[T]
    val typeName: String = Type.show[T]
    //val classSymbol1: Symbol = Symbol.classSymbol(typeName)//.termSymbol
    val classSymbol2: Symbol = Symbol.requiredClass(typeName)//.termSymbol

    ???

    //typeRepr.termSymbol

    printFields("typeRepr.typeSymbol", typeRepr.typeSymbol)
    printFields("typeRepr.classSymbol", typeRepr.classSymbol)
    printFields("typeRepr.termSymbol", typeRepr.termSymbol)
    printFields("typeRepr.widen.termSymbol", typeRepr.widen.termSymbol)
    printFields("typeRepr.widenByName.termSymbol", typeRepr.widenByName.termSymbol)
    //printFields("classSymbol1", classSymbol1)
    printFields("classSymbol2", classSymbol2)

    //require(classSymbol2.isTerm)
    val asTree = classSymbol2.tree
    //require(asTree.isTerm)
    //asTree.asInstanceOf[Term]
    //printFields("classSymbol1.tree", classSymbol1.tree)
    printFields("classSymbol2.tree", classSymbol2.tree)

    printFields("classSymbol2.typeRef", classSymbol2.typeRef)

    //classSymbol2.

    //val term: Term = classSymbol2.asExprOf[T].asTerm
    //val term: Term = classSymbol2.tree.asExprOf[T].asTerm
    val term: Term = classSymbol2.asInstanceOf[Term]
    //val termRef: TermRef = TermRef(TypeRepr.of[T], "abc")
    //val term: Term = Ident(termRef)
    println(s"term: $term")

    //???
    term
  */


  val enumFromValues = enumValues[EnumFrom]
  val enumToValues = enumValues[EnumTo]
  println(s"enumFromValues: $enumFromValues")
  println(s"enumToValues: $enumToValues")

  val allEnumValues = (enumFromValues ++ enumToValues).distinct

  // if enums are not symmetric it will cause compilation error of generated (by this macro) scala code
  // what is expected/desired behavior

  // def apply(call: Option[Tree], bindings: List[Definition], expansion: Term): Inlined

  // trait MatchModule : def apply(selector: Term, cases: List[CaseDef]): Match

  val enumFromFromTypeRepr: TypeRepr = TypeRepr.of[EnumFrom]
  val enumToFromTypeRepr: TypeRepr = TypeRepr.of[EnumTo]

  val enumFromClassName: String = Type.show[EnumFrom]
  val enumToClassName: String = Type.show[EnumTo]

  val enumFromClassSymbol: Symbol = Symbol.classSymbol(enumFromClassName)
  val enumToClassSymbol: Symbol = Symbol.classSymbol(enumToClassName)



  /*
  allEnumValues.map { enumValue =>
    val t1 = TermRef(TypeRepr.of[EnumFrom], enumValue)
    // trait CaseDefModule : def apply(pattern: Tree, guard: Option[Term], rhs: Term): CaseDef
    val caseDef = CaseDef(
      //Select(Ident(enumFromFromName), Ident(enumValue).asInstanceOf[Symbol]),
      //Select(Ident(TypeRepr.of[EnumFrom]), Symbol.),
      //Select.unique(Ident(TypeRepr.of[EnumFrom].asTermRef), enumValue),
      //Select.unique(Literal. Ident(TypeRepr.of[EnumFrom].asTermRef), enumValue),
      //Select.unique(Literal. Ident(TypeRepr.of[EnumFrom].asTermRef), enumValue),
      Select.unique(enumFromClassSymbol.asInstanceOf[Term], enumValue),
      None,
      Block(
        Nil,
        //Select(Ident(enumFromToName), enumValue)
        //Select.unique(Ident(TypeRepr.of[EnumTo].asTermRef), enumValue),
        Select.unique(enumToClassSymbol.asInstanceOf[Term], enumValue),
      )
    )
    println(s"caseDef: $caseDef")
    caseDef
  }
  */

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
    println("test44Func 06  rhsFn s: $s: $paramsAsTrees")

    /*
    val applyParams: List[Term] = paramsAsTrees
      .map((vvv: Tree) => {
        printFields("testFunc 07   vvv", vvv)

        val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
        val fun = Select(vvv.asInstanceOf[Term], asInstanceOfMethod)
        println(s"fun666777: $fun")

        val typeApply = TypeApply(fun, List(TypeTree.of[T]))
        typeApply
      })

    val apply: Apply = Apply(setterMethodSymbolAsSelect, applyParams)
    println(s"apply: ${apply.show}.")
    apply
    */

    /*
    val classSymbol1: Symbol = Symbol.requiredClass(TypeRepr.of[EnumFrom].show)
    //val childSymbol1: Symbol = classSymbol1.children.head
    //Literal(ClassOfConstant(TypeRepr.of[EnumFrom]))
    //Select(Ident(), Symbol.)

    val dd1 = Symbol.newBind(classSymbol1, "TestEnumValue1", Flags.Enum, TypeRepr.of[EnumFrom])

    val classSymbol2: Symbol = Symbol.requiredClass(TypeRepr.of[EnumTo].show)
    val dd2 = Symbol.newBind(classSymbol2, "TestEnumValue1", Flags.Enum, TypeRepr.of[EnumTo])
    println(s"dd2: $dd2")

    //val childSymbol2: Symbol = classSymbol2.children.head
    val dd3 = classSymbol2.children.head.termRef
    println(s"dd3: $dd3")

    val dd4 = Ident(classSymbol2.children.head.termRef)
    println(s"dd4: $dd4")

    val s4 = Select(dd4, classSymbol1)
    println(s"s4: $s4")

    //val dd5 = Ident(TermRef(TypeRepr.of[EnumFrom], TypeRepr.of[EnumFrom].show))
    //println(s"dd5: $dd5")

    //Select(classSymbol2.asExprOf[classOf[EnumFrom]].asTerm, Symbol. "TestEnumValue1")
    //println(s"ref: ${Ref(Symbol.requiredClass(TypeRepr.of[EnumTo].show))}")
    //println(s"ref: ${Ref(Symbol.classSymbol(TypeRepr.of[EnumTo].show))}")
    //println(s"ref: ${Ref(classSymbol2)}")

    printFields("typeSymbol", TypeRepr.of[EnumTo].typeSymbol)
    printFields("classSymbol", TypeRepr.of[EnumTo].classSymbol.get)
    printFields("termSymbol", TypeRepr.of[EnumTo].termSymbol)
    printFields("widenTermRefByName", TypeRepr.of[EnumTo].widenTermRefByName)
    printFields("widen.widenTermRefByName", TypeRepr.of[EnumTo].widen.widenTermRefByName)

    val ii = Ident(Ref(classSymbol2).asInstanceOf[TermRef])
    println(s"ii: $ii")

    val ss = Select(classSymbol2.asInstanceOf[Term], classSymbol2.children.head.asInstanceOf[Symbol])
    println(s"ss: $ss")

    //val s4 = Select(dd4, classSymbol1)
    //println(s"s4: $s4")
    */

    //val ident3: Term = Ident(Symbol.classSymbol("com.mvv.scala.temp.tests.macros2.TestEnum1").termRef)
    // T O D O: use widen

    val scopeTypRepr = findCurrentScopeTypeRepr(Symbol.requiredClass(TypeRepr.of[EnumFrom].show), 0)
    println(s"scopeTypRepr: $scopeTypRepr")

    //Ident.copy(original: Tree)(name: String)
    val termRef123From = TermRef(scopeTypRepr.get, "TestEnum1")
    val ident123From = Ident(termRef123From)
    val selectFrom123From = Select.unique(ident123From, "TestEnumValue1")
    println(s"selectFrom123From: $selectFrom123From")

    val termRef123To = TermRef(scopeTypRepr.get, "TestEnum2")
    val ident123To = Ident(termRef123To)
    val selectFrom123To = Select.unique(ident123To, "TestEnumValue1")
    println(s"selectFrom123: $selectFrom123To")

    //???

    val enumFromClassSymbol = Symbol.requiredClass(TypeRepr.of[EnumFrom].show)
    val identFrom: Term = Ident(enumFromClassSymbol.termRef)
    val enumValueChildFrom = enumFromClassSymbol
      .children
      .find(_.toString.contains("TestEnumValue1")).get
    val selectFrom = Select.apply(identFrom, enumValueChildFrom)
    println(s"selectFrom: $selectFrom")

    /*
    val enumToClassSymbol = Symbol.requiredClass(TypeRepr.of[EnumTo].show)
    val identTo: Term = Ident(enumToClassSymbol.termRef)
    val enumValueChildTo = enumToClassSymbol
      .children
      .find(_.toString.contains("TestEnumValue1")).get
    val selectTo = Select.apply(identTo, enumValueChildTo)
    println(s"selectTo: $selectTo")
    */


    val caseDef = CaseDef(
      //Select.unique(classToTerm[EnumFrom], enumValue),
      //dd1.tree,
      //childSymbol1.tree,
      //selectFrom,
      selectFrom123From,
      None,
      Block(
        Nil,
        //Select.unique(classToTerm[EnumTo], enumValue),
        //childSymbol2.tree.asInstanceOf[Term]
        //dd2.tree.asInstanceOf[Term],
        //selectTo,
        //selectFrom,
        selectFrom123To,
      )
    )
    println(s"caseDef: $caseDef")
    //caseDef

    /*
    val caseDefs: List[CaseDef] = allEnumValues.map { enumValue =>
      //val t1 = TermRef(TypeRepr.of[EnumFrom], enumValue)

      //val typeRepr: TypeRepr = TypeRepr.of[EnumFrom]
      //val typeName: String = Type.show[EnumFrom]
      //val classSymbol2: Symbol = Symbol.requiredClass(typeName) //.termSymbol
      //val b = Symbol.newBind(classSymbol2, "TestEnumValue1", Flags.Enum, typeRepr)
      //println(b)
      //printFields("bind", b)

      //val aa = classSymbol2.children.head
      //printFields("aa", aa)


      //val s = Select.unique(b.asTerm, "TestEnumValue1")
      //println(s)

      //???
      /*
      val caseDef = CaseDef(
        Select.unique(classToTerm[EnumFrom], enumValue),
        None,
        Block(
          Nil,
          Select.unique(classToTerm[EnumTo], enumValue),
        )
      )
      println(s"caseDef: $caseDef")
      caseDef
      */
    }
    */

    val matchExpr: Match = Match(paramsAsTrees.head.asInstanceOf[Term], List(caseDef))
    println(s"matchExpr: $matchExpr")

    matchExpr
  }


  val anonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(
      List("v44"))( // parameter names
      _ => List(TypeRepr.of[EnumFrom]),
      _ => TypeRepr.of[EnumTo],
      //_ => TypeRepr.of[EnumFrom],
    ),
    rhsFn
  )

  println(s"anonFunLambda expr: ${anonFunLambda.asExprOf[(EnumFrom => EnumTo)].show}")
  //println(s"anonFunLambda expr: ${anonFunLambda.asExprOf[(EnumFrom => EnumFrom)].show}")
  //println(s"anonFunLambda expr: ${anonFunLambda.asExprOf[Any].show}")

  //return anonFunLambda.asExprOf[(EnumFrom => EnumTo)]
  //anonFunLambda.asExprOf[Any]

  //'{}

  val inlined = Inlined(None, Nil, anonFunLambda)
  ////inlined.asExprOf[EnumTo]
  //println(s"inlined: $inlined")
  val inlinedExpr = inlined.asExprOf[(EnumFrom => EnumTo)]
  //val inlinedExpr = inlined.asExprOf[(EnumFrom => EnumFrom)]
  println(s"${inlinedExpr.show}")
  inlinedExpr
  //inlined.asExprOf[Any]


  /*

  val caseFields1 =  classSymbol1.caseFields
  println(s"caseFields1: $caseFields1")
  val caseFields2 =  classSymbol2.caseFields
  println(s"caseFields2: $caseFields2")

  val children1 =  classSymbol1.children
  println(s"children1: $children1")
  val children2: List[Symbol] =  classSymbol2.children
  println(s"children2: $children2")

  val classSymbol: Symbol = Symbol.classSymbol(typeNameStr) // Symbol.requiredClass(typeNameStr)
  val children: List[Symbol] =  classSymbol.children
  val enumNames1 = children2.map(_.name)
  println(s"^^^ enumNames: $enumNames1")
  */

  //val asClass = classOf[]
  //expr
