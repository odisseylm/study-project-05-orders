package com.mvv.scala.temp.tests.givens.givenss

import com.mvv.scala.temp.tests.givens.db.{AccountDatabaseContext, AuthDatabaseContext, DbConnection}


// !! Some name which are surely different from used class names (to avoid given auto-pickup) !!!
object DatabaseBlaBlaContexts :
  given authDbContext: AuthDatabaseContext = new AuthDatabaseContext :
    def connection: DbConnection = DbConnection("auth postgres connection")
  given accountDbContext: AccountDatabaseContext = new AccountDatabaseContext :
    def connection: DbConnection = DbConnection("oracle account connection")


//noinspection ScalaUnusedSymbol
def somethingElse(): String = "abc"
