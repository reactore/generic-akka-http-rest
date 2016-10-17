package reactore.rest

import reactore.core.BaseRest
import reactore.repository.Tables.UserTable
import reactore.repository.{User, UserRepository}

/**
  * Created by yadu on 14/10/16.
  */
object UserRestApi extends BaseRest[UserTable, User]("users", new UserRepository)