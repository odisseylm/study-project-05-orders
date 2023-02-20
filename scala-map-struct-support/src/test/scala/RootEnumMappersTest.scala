
import scala.language.unsafeNulls
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.quotes.ClassSelectMode
import org.mvv.scala.mapstruct.mappers.enumMappingFunc


//noinspection ScalaUnnecessaryParentheses
class RootEnumMappersTest {

  @Test
  def testRootPackageEnumsIsAccessibleAtAll(): Unit = {

    val a = SoftAssertions()

    //noinspection ScalaUnusedSymbol // to make sure that it can be compiled at all
    val manualMapFunc: (RootPackageTestEnum1 => RootPackageTestEnum2) = (v: RootPackageTestEnum1) => v match
      case RootPackageTestEnum1.TestEnumValue1 => RootPackageTestEnum2.TestEnumValue1
      case RootPackageTestEnum1.TestEnumValue2 => RootPackageTestEnum2.TestEnumValue2

    a.assertThat(classOf[RootPackageTestEnum1].getPackageName).isEqualTo("")
    a.assertThat(manualMapFunc(RootPackageTestEnum1.TestEnumValue1)).isEqualTo(RootPackageTestEnum2.TestEnumValue1)
    a.assertThat(manualMapFunc(RootPackageTestEnum1.TestEnumValue2)).isEqualTo(RootPackageTestEnum2.TestEnumValue2)

    /*
    // commented out to avoid a lot of console output
    org.mvv.scala.mapstruct.debug.dump.dumpExpr((v: RootPackageTestEnum1) => (v: @unchecked) match
      case RootPackageTestEnum1.TestEnumValue1 => RootPackageTestEnum2.TestEnumValue1
    )
    */

    a.assertAll()
  }

  @Test
  def testRootPackageEnums_ByEnumFullClassName(): Unit = {

    val a = SoftAssertions()

    val mapFunc: (RootPackageTestEnum1 => RootPackageTestEnum2) =
      enumMappingFunc[RootPackageTestEnum1, RootPackageTestEnum2](ClassSelectMode.ByFullClassName)

    a.assertThat(mapFunc(RootPackageTestEnum1.TestEnumValue1)).isEqualTo(RootPackageTestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(RootPackageTestEnum1.TestEnumValue2)).isEqualTo(RootPackageTestEnum2.TestEnumValue2)

    a.assertAll()
  }

  @Test
  def testRootPackageEnums_ByEnumClassThisType(): Unit = {

    val a = SoftAssertions()

    val mapFunc: (RootPackageTestEnum1 => RootPackageTestEnum2) =
      enumMappingFunc[RootPackageTestEnum1, RootPackageTestEnum2](ClassSelectMode.ByClassThisType)

    a.assertThat(mapFunc(RootPackageTestEnum1.TestEnumValue1)).isEqualTo(RootPackageTestEnum2.TestEnumValue1)
    a.assertThat(mapFunc(RootPackageTestEnum1.TestEnumValue2)).isEqualTo(RootPackageTestEnum2.TestEnumValue2)

    a.assertAll()
  }

}
