package com.mvv.scala.temp.tests.givens

import com.mvv.scala.temp.tests.givens.db.{AccountDatabaseContext, AuthDatabaseContext}
import com.mvv.scala.temp.tests.givens.givenss.DatabaseBlaBlaContexts.authDbContext
import com.mvv.scala.temp.tests.givens.givenss.DatabaseBlaBlaContexts.accountDbContext
import com.mvv.scala.temp.tests.givens.impl.{User, doOperationImpl}

def doOperation(): Unit = {

  val user: User = User("user1")
  val amount: BigDecimal = 5000

  doOperationImpl(user, amount)
    (using authDbContext, accountDbContext)
}
