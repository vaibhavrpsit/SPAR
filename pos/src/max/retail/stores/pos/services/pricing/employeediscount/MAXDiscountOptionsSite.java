/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.pos.services.pricing.MAXAbstractDiscountOptionsSite;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.event.PriceChange;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays employee discount options.
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXDiscountOptionsSite extends MAXAbstractDiscountOptionsSite
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7759272873835593450L;
	/** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
       Displays the employee discount options
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /**
         * Rev 1.0 changes start here
         */
    	String pmValue = null;
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		//PriceChangeIfc priceChange = new PriceChange();
		
		try {
			pmValue = pm.getStringValue("EmployeeDiscountMethod");
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXDiscountOptionsSite.arrive(), cannot find the EmployeeDiscountMethod parameter.");
		}
        MAXPricingCargo pricingCargo = (MAXPricingCargo)bus.getCargo();
      
        /**
         * Rev 1.0 changes end here
         */
        ListBeanModel model = getModifyItemBeanModel(pricingCargo.getItems());

        configureLocalButtons(model, pricingCargo);
        /**
         * Rev 1.0 changes start here
         */
    	if (("Manual".equalsIgnoreCase(pmValue)))
			ui.showScreen(POSUIManagerIfc.EMPLOYEE_DISCOUNT_OPTIONS, model);
		else if (pricingCargo.isEmployeeRemoveSelected()) {
			bus.mail("RemoveAutoEmpDisc");
		} else {
			pricingCargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			pricingCargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
			bus.mail("AutoEmpDisc"); 
		}
      //  ui.showScreen(POSUIManagerIfc.EMPLOYEE_DISCOUNT_OPTIONS, model);  
    	  /**
         * Rev 1.0 changes end here
         */
    }
}
