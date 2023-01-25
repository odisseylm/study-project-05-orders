//noinspection ScalaUnusedSymbol ScalaWeakerAccess
package com.mvv.scala.temp.tests.macros2

import java.time.ZonedDateTime
//
import com.mvv.scala.macros.dumpTerm
import com.mvv.scala.macros.asPropValue as _pv
import com.mvv.scala.macros.asReadonlyProp as _rp
import com.mvv.scala.macros.asWritableProp as _wp
import com.mvv.scala.macros.PropValue


//noinspection ScalaWeakerAccess,TypeAnnotation
class BaseClass :
  val baseString: String = "s_baseString"
  val baseInt: Int = 852
  val baseSomeClass: Rfvtgb = Rfvtgb("s_baseSomeClass")

  val baseOptionString: Option[String] = Option("s_baseOptionString")
  val baseOptionInt: Option[String] = Option("s_baseOptionInt")
  val baseOptionSomeClass: Option[Rfvtgb] = Option(Rfvtgb("s_baseOptionSomeClass"))

  val baseJavaString: java.lang.String = "s_baseJavaString"
  val baseJavaInt: java.lang.Integer = 456
  val baseJavaSomeStdClass: ZonedDateTime = ZonedDateTime.now().nn //.nnn

  val baseOptionJavaString: Option[java.lang.String] = Option("s_baseOptionJavaString")
  val baseOptionJavaInt: Option[java.lang.Integer] = Option(951)
  val baseOptionJavaSomeStdClass: Option[ZonedDateTime] = Option(ZonedDateTime.now().nn)

  val propBaseString = _pv(baseString)
  val propBaseInt = _pv(baseInt)
  val propBaseSomeClass = _pv(baseSomeClass)

  val propBaseOptionString = _pv(baseOptionString)
  val propBaseOptionInt = _pv(baseOptionInt)
  val propBaseOptionSomeClass = _pv(baseOptionSomeClass)

  val propBaseJavaString = _pv(baseJavaString)
  val propBaseJavaInt = _pv(baseJavaInt)
  val propBaseJavaSomeStdClass = _pv(baseJavaSomeStdClass)

  val propBaseOptionJavaString = _pv(baseOptionJavaString)
  val propBaseOptionJavaInt = _pv(baseOptionJavaInt)
  val propBaseOptionJavaSomeStdClass = _pv(baseOptionJavaSomeStdClass)

  val propWithOwnerTypeBaseString = _pv(baseString)
  val propWithOwnerTypeBaseInt = _pv(baseInt)
  val propWithOwnerTypeBaseSomeClass = _pv(baseSomeClass)

  val propWithOwnerTypeBaseOptionString = _pv(this, baseOptionString)
  val propWithOwnerTypeBaseOptionInt = _pv(this, baseOptionInt)
  val propWithOwnerTypeBaseOptionSomeClass = _pv(this, baseOptionSomeClass)

  val propWithOwnerTypeBaseJavaString = _pv(this, baseJavaString)
  val propWithOwnerTypeBaseJavaInt = _pv(this, baseJavaInt)
  val propWithOwnerTypeBaseJavaSomeStdClass = _pv(this, baseJavaSomeStdClass)

  val propWithOwnerTypeBaseOptionJavaString = _pv(this, baseOptionJavaString)
  val propWithOwnerTypeBaseOptionJavaInt = _pv(this, baseOptionJavaInt)
  val propWithOwnerTypeBaseOptionJavaSomeStdClass = _pv(this, baseOptionJavaSomeStdClass)




//noinspection TypeAnnotation ScalaWeakerAccess // Explicit type is not used to verify generated types
class TesPropsClass extends BaseClass:

  val newString = "newString"
  val newSomeClass: Rfvtgb = Rfvtgb("s_baseSomeClass")
  val newOptionSomeClass: Option[Rfvtgb] = Option(Rfvtgb("s_baseOptionSomeClass"))
  val newJavaSomeStdClass: ZonedDateTime = ZonedDateTime.now().nn

  val propNewString = _pv(newString)
  val propNewSomeClass = _pv(newSomeClass)
  val propNewOptionSomeClass = _pv(newOptionSomeClass)
  val propNewJavaSomeStdClass = _pv(newJavaSomeStdClass)

  val propWithOwnerNewString = _pv(this, newString)
  val propWithOwnerNewSomeClass = _pv(this, newSomeClass)
  val propWithOwnerNewOptionSomeClass = _pv(this, newOptionSomeClass)
  val propWithOwnerNewJavaSomeStdClass = _pv(this, newJavaSomeStdClass)

  // from base class
  val propInDerivedBaseString = _pv(baseString)
  val propInDerivedBaseInt = _pv(baseInt)
  val propInDerivedBaseSomeClass = _pv(baseSomeClass)

  val propInDerivedBaseOptionString = _pv(baseOptionString)
  val propInDerivedBaseOptionInt = _pv(baseOptionInt)
  val propInDerivedBaseOptionSomeClass = _pv(baseOptionSomeClass)

  val propInDerivedBaseJavaString = _pv(baseJavaString)
  val propInDerivedBaseJavaInt = _pv(baseJavaInt)
  val propInDerivedBaseJavaSomeStdClass = _pv(baseJavaSomeStdClass)

  val propInDerivedBaseOptionJavaString = _pv(baseOptionJavaString)
  val propInDerivedBaseOptionJavaInt = _pv(baseOptionJavaInt)
  val propInDerivedBaseOptionJavaSomeStdClass = _pv(baseOptionJavaSomeStdClass)

  val propWithOwnerTypeInDerivedBaseString = _pv(this, baseString)
  val propWithOwnerTypeInDerivedBaseInt = _pv(this, baseInt)
  val propWithOwnerTypeInDerivedBaseSomeClass = _pv(this, baseSomeClass)

  val propWithOwnerTypeInDerivedBaseOptionString = _pv(this, baseOptionString)
  val propWithOwnerTypeInDerivedBaseOptionInt = _pv(this, baseOptionInt)
  val propWithOwnerTypeInDerivedBaseOptionSomeClass = _pv(this, baseOptionSomeClass)

  val propWithOwnerTypeInDerivedBaseJavaString = _pv(this, baseJavaString)
  val propWithOwnerTypeInDerivedBaseJavaInt = _pv(this, baseJavaInt)
  val propWithOwnerTypeInDerivedBaseJavaSomeStdClass = _pv(this, baseJavaSomeStdClass)

  val propWithOwnerTypeInDerivedBaseOptionJavaString = _pv(this, baseOptionJavaString)
  val propWithOwnerTypeInDerivedBaseOptionJavaInt = _pv(this, baseOptionJavaInt)
  val propWithOwnerTypeInDerivedBaseOptionJavaSomeStdClass = _pv(this, baseOptionJavaSomeStdClass)

  //val propWithOwnerAsSuperTypeInDerivedBaseOptionJavaSomeStdClass = _pv( baseOptionJavaSomeStdClass)

  //@scala.unchecked
  val rPropNewString = _rp(this, newString)
  //@scala.unchecked
  val wPropNewString = _wp(this, newString)
  //@scala.unchecked
  val rPropNewOptionSomeClass = _rp(this, newOptionSomeClass)
  //@scala.unchecked val wPropNewOptionSomeClass = _wp(this, newOptionSomeClass)

  def method333(s: Any|Null) = {}
  def method334(s: String|Null) = {}

  var tempStrPropVar1 = "gfgf"

  def aa():Unit = {
    dumpTerm((v: String) => tempStrPropVar1 = v )
    // Inlined(
    //   EmptyTree,
    //   List(),
    //   Block(
    //     List(
    //       DefDef(
    //         $anonfun,
    //         List(
    //           List(
    //             ValDef(
    //               v,
    //               Ident(String),
    //               EmptyTree)
    //             )
    //           ),
    //           TypeTree[
    //             TypeRef(
    //               ThisType(
    //                 TypeRef(
    //                   NoPrefix,
    //                   module class scala
    //                 )
    //               ),
    //               class Unit
    //             )
    //           ],
    //           Assign(
    //             Select(
    //               This(
    //                 Ident(
    //                   TesPropsClass
    //                 )
    //               ),
    //               tempStrPropVar1
    //             ),
    //             Ident(v)
    //           )
    //         )
    //       ),
    //       Closure(
    //         List(),
    //         Ident($anonfun),
    //         EmptyTree)
    //       )
    //     )


    dumpTerm((v: String) => tempStrPropVar1_=(v) )
    // Inlined(
    //   EmptyTree,
    //   List(),
    //   Block(
    //     List(
    //       DefDef(
    //         $anonfun,
    //         List(
    //           List(
    //             ValDef(
    //               v,
    //               Ident(String),
    //               EmptyTree
    //             )
    //           )
    //         ),
    //         TypeTree[
    //           TypeRef(
    //             ThisType(
    //               TypeRef(
    //                 NoPrefix,
    //                 module class scala
    //               )
    //             ),
    //             class Unit
    //           )
    //         ],
    //         Apply(
    //           Select(
    //             This(
    //               Ident(
    //                 TesPropsClass
    //               )
    //             ),
    //             tempStrPropVar1_=
    //           ),
    //           List(
    //             Ident(v)
    //           )
    //         )
    //       )
    //     ),
    //     Closure(
    //       List(),
    //       Ident($anonfun),
    //       EmptyTree
    //     )
    //   )
    // )
  }

// TODO: write unit test with reflection



@main
def test589475847(): Unit = {
  val obj = TesPropsClass()
  println(s"rPropNewString.value: ${obj.rPropNewString.value}")
  println(s"rPropNewString.asOption: ${obj.rPropNewString.asOption}")
}
