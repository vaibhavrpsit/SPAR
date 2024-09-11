/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractDiscountOptionsSite;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 This site displays damage discount options.
 @version $Revision: 3$
 **/
//--------------------------------------------------------------------------
abstract public class MAXAbstractDiscountOptionsSite extends AbstractDiscountOptionsSite
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 269185571130684578L;
	/** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
     Displays the discount options
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    abstract public void arrive(BusIfc bus);

    

    //----------------------------------------------------------------------
    /**
     *   Configures the local buttons
     *   @param  model ListBeanModel
     *   @param  pricingCargo PricingCargo
     */
    //----------------------------------------------------------------------
    public void configureLocalButtons(ListBeanModel model, MAXPricingCargo pricingCargo)  //Rev 1.0 changes
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        if (pricingCargo.getItems() == null || pricingCargo.getItems().length == 0)
            // nothing selected
        {
            navModel.setButtonEnabled("ItemDiscAmt",false);
            navModel.setButtonEnabled("ItemDiscPer",false);
        }
        else
        {
            navModel.setButtonEnabled("ItemDiscAmt",true);
            navModel.setButtonEnabled("ItemDiscPer",true);
        }

        model.setLocalButtonBeanModel(navModel);
    }
}
