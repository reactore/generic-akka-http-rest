package reactore.rest

import reactore.core.BaseRest
import reactore.repository.Tables.EmployeeTable
import reactore.repository.{Employee, EmployeeRepository}

/**
  * Created by yadu on 14/10/16.
  */
object EmployeeRestApi extends BaseRest[EmployeeTable, Employee]("employees", new EmployeeRepository) {
  val moreRoutes = path("employees" / "count"){
    complete("call the repository method here to get the count")
  } ~ route
}