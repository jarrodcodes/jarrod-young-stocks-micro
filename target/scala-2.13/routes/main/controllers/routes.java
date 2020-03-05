// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jarrod_young/Desktop/jarrod-young-stocks-micro/conf/routes
// @DATE:Thu Mar 05 11:30:14 EST 2020

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseStockController StockController = new controllers.ReverseStockController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseStockController StockController = new controllers.javascript.ReverseStockController(RoutesPrefix.byNamePrefix());
  }

}
