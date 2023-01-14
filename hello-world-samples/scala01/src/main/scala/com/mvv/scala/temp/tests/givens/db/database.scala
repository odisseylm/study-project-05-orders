package com.mvv.scala.temp.tests.givens.db


case class DbConnection (name: String)

trait DatabaseContext :
  def connection: DbConnection

// different types are used for easy passing of given
trait AuthDatabaseContext extends DatabaseContext
trait AccountDatabaseContext extends DatabaseContext
