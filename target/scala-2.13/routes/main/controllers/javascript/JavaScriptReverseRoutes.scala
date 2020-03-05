// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jarrod_young/Desktop/jarrod-young-stocks-micro/conf/routes
// @DATE:Thu Mar 05 11:30:14 EST 2020

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:5
package controllers.javascript {

  // @LINE:5
  class ReverseStockController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def ws: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.StockController.ws",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "ws"})
        }
      """
    )
  
  }


}
