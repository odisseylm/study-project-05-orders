package com.mvv.test

import org.assertj.core.api.SoftAssertions


//noinspection ScalaFileName
object SoftAssertions :
  extension (assertions: SoftAssertions)
    inline def runTests(action: SoftAssertions=>Unit): SoftAssertions = { action(assertions); assertions }
object Tests :
  // just to reuse kotlin test with minimal changes
  //def run(action: ()=>Unit): Unit = action()
  def run(action: =>Unit): Unit = action