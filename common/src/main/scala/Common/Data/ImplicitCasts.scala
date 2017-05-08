package Common.Data

import Common.DataSet

object ImplicitCasts {

  implicit def str2ds(str: String): DataSet = DataString(str)
  implicit def bool2ds(bool: java.lang.Boolean): DataSet = DataBoolean(bool)
  implicit def bool2ds(bool: Boolean): DataSet = DataBoolean(bool)

}