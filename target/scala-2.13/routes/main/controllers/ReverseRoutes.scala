// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jarrod_young/Desktop/jarrod-young-stocks-micro/conf/routes
// @DATE:Thu Mar 05 11:30:14 EST 2020

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:5
package controllers {

  // @LINE:5
  class ReverseStockController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def ws(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "ws")
    }
  
  }


}
