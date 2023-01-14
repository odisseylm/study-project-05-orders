package com.mvv.scala.temp.tests.givens

import org.junit.jupiter.api.{DisplayName, Test}


class GivenUnitTest {

  @Test
  @DisplayName("doOperationWithContextParams")
  def testDoOperationWithContextParams(): Unit = {
    doOperationWithContextParams()
  }

  @Test
  @DisplayName("doOperationWithContextParamsAsManuallySetGivens")
  def testDoOperationWithContextParamsAsManuallySetGivens(): Unit = {
    doOperationWithContextParamsAsManuallySetGivens()
  }

  @Test
  @DisplayName("doOperationWithContextParamsAsAutoImportedGivens")
  def testDoOperationWithContextParamsAsAutoImportedGivens(): Unit = {
    doOperationWithContextParamsAsAutoImportedGivens()
  }
}
