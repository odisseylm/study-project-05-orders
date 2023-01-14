package com.mvv.scala.temp.tests.givens

import com.mvv.scala.temp.tests.givens.db.{AccountDatabaseContext, AuthDatabaseContext}
import com.mvv.scala.temp.tests.givens.impl.{User, doOperationImpl}

// import all givens
import com.mvv.scala.temp.tests.givens.givenss.DatabaseBlaBlaContexts.given
// or you can import them separately
//import com.mvv.scala.temp.tests.givens.givenss.DatabaseBlaBlaContexts.{authDbContext, accountDbContext}


def doOperationWithContextParamsAsAutoImportedGivens(): Unit = {

  val user: User = User("user1")
  val amount: BigDecimal = 5000

  doOperationImpl(user, amount)
}
