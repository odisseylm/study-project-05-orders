package com.mvv.scala.temp.tests.givens

import com.mvv.scala.temp.tests.givens.db.{AccountDatabaseContext, AuthDatabaseContext, DbConnection}
import com.mvv.scala.temp.tests.givens.impl.User
import com.mvv.scala.temp.tests.givens.impl.{User, doOperationImpl}


def doOperationWithContextParams(): Unit = {

  val user: User = User("user1")
  val amount: BigDecimal = 5000

  val authDbContext: AuthDatabaseContext = new AuthDatabaseContext:
    def connection: DbConnection = DbConnection("CUSTOM auth postgres connection")
  val accountDbContext: AccountDatabaseContext = new AccountDatabaseContext:
    def connection: DbConnection = DbConnection("CUSTOM oracle account connection")

  doOperationImpl(user, amount)
    (using authDbContext, accountDbContext)
}
