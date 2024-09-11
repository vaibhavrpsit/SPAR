/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/ShowItemTaxOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/07/09 22:36:17  jdeleau
 *   @scr 5155
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ShowItemTaxOptionsSite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     * Constant for the Override button action.
     * <P>
     */
    //--------------------------------------------------------------------------
    private static final String ACTION_OVERRIDE = "Override";
    //--------------------------------------------------------------------------
    /**
     * Constant for the ON/OFF button action.
     * <P>
     */
    //--------------------------------------------------------------------------
    private static final String ACTION_ONOFF = "OnOff";
    
    /**
     * This arrive shows the screen for all the options for ModifyItemTax.
     * 
     * @param bus
     *            Service Bus
     */
    public void arrive(BusIfc bus)
    {

        // retrieve cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ListBeanModel beanModel = getModifyItemBeanModel(cargo.getItems());

        if (cargo.isSendOutOfArea())
        {
            //Skip menu options an show override tax screen
            bus.mail(new Letter("OverrideTaxRate"), BusIfc.CURRENT);
        }
        else
        {
            // show the UI to select the tax option
            uiManager.showScreen(POSUIManagerIfc.ITEM_TAX_OPTIONS, beanModel);
        }

    }
    
    protected ListBeanModel getModifyItemBeanModel(TaxableLineItemIfc[] lineItems)
    {
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
        ListBeanModel mibModel;

        boolean taxable = true;
        // If there is no item
        if (lineItems == null)
        {
            mibModel = null;
        }
        else
        {
            for (int i = 0; i < lineItems.length; i++)
            {
                if (lineItems[i].getTaxMode() == TaxIfc.TAX_MODE_NON_TAXABLE)
                {
                    taxable = false;
                    break;
                }
            }

            if (taxable)
            {
                nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE, true);
                nbbModel.setButtonEnabled(CommonActionsIfc.ON_OFF, true);
            }
            else
            {
                nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE, false);
                nbbModel.setButtonEnabled(CommonActionsIfc.ON_OFF, true);
            }

            mibModel = new ListBeanModel();
            mibModel.setListModel(lineItems);
            mibModel.setLocalButtonBeanModel(nbbModel);
        }

        return mibModel;
    }
}
