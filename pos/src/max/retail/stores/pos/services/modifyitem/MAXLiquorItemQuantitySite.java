/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.2   Kamlesh Pant  Sep 26, 2022		CapLimit Enforcement for Liquor
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXLiquorItemQuantitySite extends PosSiteActionAdapter {

	public static float liquorLimit(float quant, float value) {
		//System.out.println("MAXLiquorItemQuantitySite ::");
        float InLiqtot = 0;
        
        	InLiqtot = value*quant;
        
       //System.out.println("InLiqtot 75::"+InLiqtot);
    
// TODO Auto-generated method stub
       return InLiqtot;
		
		
	}
	
}

