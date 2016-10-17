package reactore.usage

/**
  * Created by yadu on 5/6/16.
  */

import reactore.core.DriverHelper
import reactore.repository.Tables._
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

object SimpleQueries {

  val db = DriverHelper.db

  def getEmployees: Unit = {
    val query = for {
      employees <- employeeTable
    } yield employees

    val future = db.run(query.result)
    val res = Await.result(future, 5.seconds)
    println("employees : " + res)
  }

  def simpleQueries = {
    val res = for {
      filter <- db.run(employeeTable.filter(_.firstName like "S%").result)
      _ = println("Employee names starting with 'S' = " + filter)
      count <- db.run(employeeTable.size.result)
      _ = println("No of employees = " + count)
      groups <- db.run(employeeTable.groupBy(_.gender).map({
        case (k, v) => (k, v.size)
      }).result)
      _ = println("Groups => " + groups)
    } yield groups

    res.onFailure {
      case ex => ex.printStackTrace()
    }

    Thread.sleep(5000)
  }

  def joinQueries = {
    val joinQuery = for {
      e <- employeeTable
      u <- userTable if e.id === u.employeeId
    } yield (e.firstName, u.userName)

    db.run(joinQuery.result).map {
      x => println("Joined Res => " + x)
    }.recover {
      case ex => ex.printStackTrace()
    }
    Thread.sleep(5000)
  }

}


object TestApp extends App {
  //  SimpleQueries.getEmployees
  //  SimpleQueries.simpleQueries
  SimpleQueries.joinQueries
}