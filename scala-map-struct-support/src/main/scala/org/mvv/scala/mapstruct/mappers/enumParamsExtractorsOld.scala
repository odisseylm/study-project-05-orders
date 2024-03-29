/*
package org.mvv.scala.mapstruct.mappers


private def parseCustomEnumMappingTuples[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using q: Quotes)(using Type[EnumFrom], Type[EnumTo])
  (inlined: q.reflect.Inlined): List[(String, String)] =
  import q.reflect.*

  /*
  val traverser = new TreeTraverser {
    //override def traverseTree(tree: Tree)(owner: Symbol): Unit = ???
    override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
      try {
        println(s"%%% traverseTree ${tree.getClass.nn.getSimpleName}")
        super.traverseTree(tree)(owner)
      } catch {
        case ex: Exception =>
          println(s"%%% traverseTree ERROR ${tree.getClass.nn.getSimpleName} ${ex}")
          //report.error(s"unexpected error ${e}", tree.pos)
          //throw e
      }
    }
    protected override def traverseTreeChildren(tree: Tree)(owner: Symbol): Unit =
      println(s"%%% traverseTreeChildren ${tree.getClass.nn.getSimpleName}")
      super.traverseTreeChildren(tree)(owner)
  }
  */

  // T O D O: impl
  // val inlinedCall: Option[Tree] = inlined.call
  //require(inlinedCall.isEmpty || inlinedCall.get == EmptyTree, "Expected only simple tuple expression.")

  val bindings: List[Definition] = inlined.bindings
  require(bindings.isEmpty, "Expected only simple tuple expression.")

  val body: Term = inlined.body
  val bodyTypeClassName = typeReprFullClassName(body.tpe)

  //traverser.traverseTree(body)(body.symbol)

  def getElementsFromTyped(el: Tree): List[Tree] =
    // Typed ( SeqLiteral ( List(
    require(el.isTyped, s"Typed is expected but was $el.")
    getElements(el.asInstanceOf[Typed].expr)


  val elements: List[Tree] = bodyTypeClassName match
    case "Nil" | "scala.Nil" | "scala.collection.immutable.Nil" => Nil
    case "Tuple2" | "scala.Tuple2" => List(body)
    case "_*" | "<repeated>" | "scala.<repeated>" => getElementsFromTyped(body)
    case "List" | "scala.collection.immutable.List" =>
      require(body.isApply, s"Unexpected List format (creating list using :: is not supported, only simple format List(...) is supported)")
      val listApplyArgs: List[Term] = body.asInstanceOf[Apply].args
      require(listApplyArgs.sizeIs == 1) // List.apply has ONE repeated param
      getElementsFromTyped(listApplyArgs.head)
    case other => throw IllegalStateException(s"Unexpected type of body $other ( $body ).")

  val customEnumMappingTuples = elements.map(el =>
    require(el.isApply)
    parseApplyWithTypeApplyCustomEnumMappingTuple[EnumFrom, EnumTo](el.asInstanceOf[Apply]))
  customEnumMappingTuples


// probably not ideal solution
def typeReprFullClassName(using q: Quotes)(typeRepr: q.reflect.TypeRepr): String =
  val rawClassFullName = typeRepr.classSymbol.map(_.fullName.stripSuffix("$"))
    .getOrElse(typeRepr.show)
  rawClassFullName

private def getElements(using q: Quotes)(tree: q.reflect.Tree): List[q.reflect.Tree] =
  import q.reflect.Tree
  tree match
    case el if el.isSeqLiteral => getByReflection(el, "elems", "elements", "items").unwrapOption.asInstanceOf[List[Tree]]
    case other => throw IllegalStateException(s"Getting elements from ${other.getClass.nn.getName} is not supported yet.")


private def parseApplyWithTypeApplyCustomEnumMappingTuple[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (using q: Quotes)(using Type[EnumFrom], Type[EnumTo])
  (applyWithTypeApply: q.reflect.Apply): (String, String) =

  import q.reflect.*

  val logPrefix = s"parseApplyWithTypeApplyCustomEnumMappingTuple [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "

  val bodyAsApply: Apply = applyWithTypeApply
  val bodyApplyFun: Term = bodyAsApply.fun
  val bodyApplyArgs: List[Term] = bodyAsApply.args

  require(bodyApplyFun.isTypeApply)
  val typeApply: TypeApply = bodyApplyFun.asInstanceOf[TypeApply]
  val typeApplySelect: Select = typeApply.fun.asInstanceOf[Select]
  val typeApplyClassName = getTypeApplyClassName(typeApplySelect)

  val errorMsgTuple2TypeIsExpected = "Tuple2 is expected."
  require(typeApplyClassName.isOneOf("Tuple2", "scala.Tuple2"), errorMsgTuple2TypeIsExpected)

  val typeApplyArgs: List[TypeTree] = typeApply.args
  require(typeApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)
  val typeRepr1 = typeApplyArgs.head.tpe
  val typeRepr2 = typeApplyArgs.tail.head.tpe

  require(typeRepr1 == TypeRepr.of[EnumFrom])
  require(typeRepr2 == TypeRepr.of[EnumTo])

  require(bodyApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)
  val enumValueNames = (extractSimpleName(bodyApplyArgs.head), extractSimpleName(bodyApplyArgs.tail.head))
  log.trace(s"$logPrefix enumValueNames: $enumValueNames")
  enumValueNames
*/




/*
// Select(Ident(TestEnum1),TestEnumValue4)
// Select(Select(Select(Select(Select(Select(Select(Ident(com),mvv),scala),temp),tests),macros2),TestEnum1)
//
private def extractName(using q: Quotes)(term: q.reflect.Tree): String =
  import q.reflect.*
  term match
    case el if el.isBind  => el.asInstanceOf[Bind].name
    case el if el.isIdent  => el.asInstanceOf[Ident].name
    case el if el.isTypeIdent => el.asInstanceOf[TypeIdent].name
    case el if el.isSelect => el.asInstanceOf[Select].name
    case el if el.isTypeSelect => el.asInstanceOf[TypeSelect].name
    case el if el.isNamedArg => el.asInstanceOf[NamedArg].name
    case el if el.isSelectOuter => el.asInstanceOf[SelectOuter].name
    case el if el.isSimpleSelector => el.asInstanceOf[SimpleSelector].name
    case el if el.isOmitSelector => el.asInstanceOf[OmitSelector].name
    case el if el.isTypeProjection => el.asInstanceOf[TypeProjection].name
    case el if el.isTypeBind => el.asInstanceOf[TypeBind].name
    case el if el.isNamedType => el.asInstanceOf[NamedType].name
    case el if el.isTypeProjection => el.asInstanceOf[TypeProjection].name
    case el if el.isSymbol => el.asInstanceOf[Symbol].name
    case el if el.isRefinement => el.asInstanceOf[Refinement].name
    case el if el.isDefinition => el.asInstanceOf[Definition].name
*/
