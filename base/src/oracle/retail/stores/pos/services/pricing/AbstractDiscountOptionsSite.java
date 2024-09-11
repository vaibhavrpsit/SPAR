/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractDiscountOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse   
 *
 Revision 1.3  2004/03/16 18:30:45  cdb
 @scr 0 Removed tabs from all java source code.
 *
 Revision 1.2  2004/02/19 19:01:09  cdb
 @scr 3588     Cleaned up some discount options buttons
 *
 Revision 1.1  2004/02/19 18:13:51  dcobb
 @scr 3588 Abstract common code  to abstract class.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 This site displays damage discount options.
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
abstract public class AbstractDiscountOptionsSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     Displays the discount options
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    abstract public void arrive(BusIfc bus);

    //----------------------------------------------------------------------
    /**
     *   Builds the ModifyItemBeanModel; this bean contains the the line item
     *   and the model that sets the local navigation buttons to their correct
     *   enabled states.
     *   @param  lineItemList     The itemlist to modify.
     *   @return ListBeanModel for Modify Item
     */
    //----------------------------------------------------------------------
    protected ListBeanModel getModifyItemBeanModel(SaleReturnLineItemIfc[] lineItemList)
    {
        ListBeanModel model = new ListBeanModel();

        if (lineItemList != null)
        {
            model.setListModel(lineItemList);
            model.setUpdateStatusBean(false);
        }
        return model;
    }

    //----------------------------------------------------------------------
    /**
     *   Configures the local buttons
     *   @param  model ListBeanModel
     *   @param  pricingCargo PricingCargo
     */
    //----------------------------------------------------------------------
    public void configureLocalButtons(ListBeanModel model, PricingCargo pricingCargo)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        if (pricingCargo.getItems() == null || pricingCargo.getItems().length == 0)
            // nothing selected
        {
            navModel.setButtonEnabled(CommonActionsIfc.ITEM_DISC_AMT,false);
            navModel.setButtonEnabled(CommonActionsIfc.ITEM_DISC_PER,false);
        }
        else
        {
            navModel.setButtonEnabled(CommonActionsIfc.ITEM_DISC_AMT,true);
            navModel.setButtonEnabled(CommonActionsIfc.ITEM_DISC_PER,true);
        }

        model.setLocalButtonBeanModel(navModel);
    }
}
