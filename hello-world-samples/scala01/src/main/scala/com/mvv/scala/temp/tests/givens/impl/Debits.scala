package com.mvv.scala.temp.tests.givens.impl

import com.mvv.scala.temp.tests.givens.db.AuthDatabaseContext
import com.mvv.scala.temp.tests.givens.db.AccountDatabaseContext
import com.mvv.scala.temp.tests.givens.impl.User

// In separate package/file to make sure that givens are not picked up from imports

def verifyUser(user: User)(using authDbContext: AuthDatabaseContext): Unit = {
  println(s"verifyUser $user, database: ${authDbContext.connection}") }

def openDebit(user: User, amount: BigDecimal)
             (using accountDbContext: AccountDatabaseContext): Unit = {
  println(s"Opening debit $amount for user $user, database: ${accountDbContext.connection}") }

def doOperationImpl(user: User, amount: BigDecimal)
                   (using authDbContext: AuthDatabaseContext, accountDbContext: AccountDatabaseContext): Unit = {

  println(s"doOperationImpl : OPEN DEBIT $amount/$user, databases: ${authDbContext.connection}/${accountDbContext.connection}")

  verifyUser(user)
  openDebit(user, 1000)
}

/*
def doOperationImpl22(user: User, amount: BigDecimal): Unit = {

  println(s"doOperationImpl : OPEN DEBIT $amount/$user")

  verifyUser(user)
  openDebit(user, 1000)
}
*/
