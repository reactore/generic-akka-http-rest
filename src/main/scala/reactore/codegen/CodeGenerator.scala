package reactore.codegen

import java.io.File
import java.sql.Date
import java.util

import reactore.repository.{Employee, Tables, User}
import slick.codegen.SourceCodeGenerator
import slick.dbio.DBIOAction
import slick.dbio.Effect.{Schema, Write}
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.profile.SqlProfile.ColumnOption.SqlType
import reactore.core.DriverHelper._

import scala.concurrent.ExecutionContext.Implicits.global

object CodeGenerator extends App {

  val outputFolder = new File(".").getCanonicalPath + File.separator + "generated" + File.separator

  val ddlQuery = Tables.schema.create


  db.run(ddlQuery).map {
    x => println("successfully created tables")
      SourceCodeGenerator.main(
        Array("slick.driver.PostgresDriver", jdbcDriver, url, outputFolder, "com.reactore.repository", user, password)
      )

  }.recover {
    case ex => ex.printStackTrace()
  }

  db.run(advancedSetup).map {
    x => println("Setup successfully completed")
  }.recover {
    case ex => ex.printStackTrace()
  }

  def advancedSetup: DBIOAction[Unit, NoStream, Write with Schema] = {
    DBIO.seq(
      Tables.schema.create,
      Tables.employeeTable forceInsertAll SampleData.employeesSampleData,
      Tables.userTable ++= SampleData.userSampleData
    )
  }

  Thread.sleep(5000)
}

object CustomCodeGenerator extends App {
  val outputFolder = new File(".").getCanonicalPath + File.separator + "generated" + File.separator

  val schemaName = "employee"
  val modelAction = PostgresDriver.createModel(Some(PostgresDriver.defaultTables.map(_.filter(_.name.schema.contains(schemaName)))))
  val modelFuture = db.run(modelAction)
  val futureRes = modelFuture.map(model => new SourceCodeGenerator(model) {
    override def entityName = dbTableName => dbTableName.toCamelCase

    override def tableName = dbTableName => dbTableName.toLowerCase.toCamelCase + "Table"

    override def Table = new Table(_) {
      table =>
      override def TableValue = new TableValue {
        override def doc = ""

        override def rawName = super.rawName.uncapitalize
      }

      override def EntityType = new EntityType {
        override def classEnabled = true

        override def doc = ""
      }

      override def ForeignKey = new ForeignKeyDef(_) {
        override def doc = ""
      }

      override def PlainSqlMapper = new PlainSqlMapperDef {
        override def doc = ""
      }

      override def Column = new Column(_) {
        // e.g. to a custom enum or anything else
        override def doc = ""

        override def rawType = model.tpe match {
          case "String" => model.options.find(_.isInstanceOf[SqlType])
            .map(_.asInstanceOf[SqlType].typeName).map({
            case "hstore"   => "Map[String, String]"
            case _          => "String"
          }).getOrElse("String")

          case _ => super.rawType
        }

        override def rawName = (table.model.name.table, this.model.name) match {
          case tbl: (_, _) if (tbl._2.toLowerCase == (tbl._1 + "id").toLowerCase) => "id"
          case col: (_, _)                                                        => col._2.uncapitalize // super.rawName
          case _                                                                  => super.rawName
        }
      }
    }

  })


  futureRes.map {
    x => x.writeToFile("slick.driver.PostgresDriver", outputFolder, "com.reactore")
  }
  Thread.sleep(5000)
}

object SampleData {
  val dob = new Date(new util.Date(1990, 1, 1).getTime)
  val employeesSampleData = Seq(
    Employee(100, "Sachin", "Tendulkar", dob, false, "Male"),
    Employee(101, "Rahul", "Dravid", dob, false, "Male"),
    Employee(102, "Sourav", "Ganguly", dob, false,"Male"),
    Employee(103, "Saina", "Nehwal", dob, false,"Female"),
    Employee(104, "Deepika", "Pallikkal", dob, false,"Female")
  )

  val userSampleData = Seq(
    User(0, "master", "sachin@gmail.com", "india", false, 100),
    User(0, "wall", "dravid@gmail.com", "india", false, 101),
    User(0, "dada", "ganguly@gmail.com", "india", false, 102),
    User(0, "saina", "saina@gmail.com", "india", false, 103),
    User(0, "deepika", "deepika@gmail.com", "india", false, 104)
  )

}
