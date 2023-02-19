package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.Enum as ScalaEnum
//
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }
// for debug only
import org.mvv.scala.mapstruct.debug.dump.{ isImplClass, activeFlags, activeFlagEntries, dumpSymbol }
import org.mvv.scala.mapstruct.debug.printFields


private val log: Logger = Logger("org.mvv.scala.mapstruct.mappers.passParamTests")


//noinspection ScalaUnusedSymbol
//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsRepeatedParams[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMappings: (EnumFrom, EnumTo)*): Any =
  ${ _internalTestCustomMappingsAsRepeatedParamsImpl[EnumFrom, EnumTo]( 'customMappings ) }

def _internalTestCustomMappingsAsRepeatedParamsImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using quotes: Quotes)(using Type[EnumFrom], Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsRepeatedParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsRepetaed: ${customMappings.asTerm}")

  //noinspection DuplicatedCode
  val customMappingAsEnumNames: List[(String, String)] = extractCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}



//noinspection ScalaUnusedSymbol
//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsListParam[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMappings: List[(EnumFrom, EnumTo)]): Any =
  ${ _internalTestCustomMappingsAsListParamImpl[EnumFrom, EnumTo]( 'customMappings ) }

def _internalTestCustomMappingsAsListParamImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMappings: Expr[Seq[(EnumFrom, EnumTo)]])
  (using quotes: Quotes)(using Type[EnumFrom], Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsListParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsList: ${customMappings.asTerm}")

  //noinspection DuplicatedCode
  val customMappingAsEnumNames: List[(String, String)] = extractCustomEnumMappingTuplesExpr[EnumFrom, EnumTo](customMappings)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}



//noinspection ScalaUnusedSymbol
//----------------------------------------------------------------------------------------------------------------------
inline def _internalTestCustomMappingsAsSingleParam[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (inline customMapping: (EnumFrom, EnumTo)): Any =
  ${ _internalTestCustomMappingsAsSingleParamImpl[EnumFrom, EnumTo]( 'customMapping ) }

def _internalTestCustomMappingsAsSingleParamImpl[EnumFrom <: ScalaEnum, EnumTo <: ScalaEnum]
  (customMapping: Expr[(EnumFrom, EnumTo)])
  (using quotes: Quotes)(using Type[EnumFrom], Type[EnumTo]): Expr[Any] =

  import quotes.reflect.*

  val logPrefix = s"_internalTestCustomMappingsAsSingleParamsImpl [ ${Type.show[EnumFrom]} => ${Type.show[EnumTo]} ], "
  log.trace(s"$logPrefix customMappingsSingleParam: ${customMapping.asTerm}")

  val customMappingAsEnumNames: (String, String) = extractCustomEnumMappingTupleExpr[EnumFrom, EnumTo](customMapping)
  log.info(s"$logPrefix customMappingAsEnumNames: $customMappingAsEnumNames")
  '{}
