// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jarrod_young/Desktop/jarrod-young-stocks-micro/conf/routes
// @DATE:Thu Mar 05 11:30:14 EST 2020


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
