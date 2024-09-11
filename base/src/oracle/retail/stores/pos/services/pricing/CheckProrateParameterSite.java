/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/CheckProrateParameterSite.java /main/12 2012/08/27 11:23:05 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - removed the place holder from key
 *                         DiscountConfirmation
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.7  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.6  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.5  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.4  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/19 20:08:49  cdb
 *   @scr 3588 Corrected behavior problem when non-prorated discounts are done.
 *
 *   Revision 1.2  2004/02/18 23:50:09  cdb
 *   @scr 3588 Corrected another prorate problem.
 *   Updated Receipt Printing.
 *
 *   Revision 1.1  2004/02/16 21:24:11  cdb
 *   @scr 3588 Added checking of prorate parameter and
 *   showing appropriate dialogs accordingly.
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site shows the prorate discount dialog when the prorate parameter
    is set to true and more than one item has been selected for discount.
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CheckProrateParameterSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     constant for error dialog screen
     **/
    public static final String DISCOUNT_CONFIRMATION = "DiscountConfirmation";

    /**
     constant for parameter name
     **/
    public static final String PRORATE_DISCOUNT_AMOUNT = "ProrateDiscountAmount";

    /**
     constant for error dialog screen
     **/
    public static final String DOLLAR_OFF = "DollarOff";
    
    //----------------------------------------------------------------------
    /**
       Show the prorate discount dialog when the prorate parameter
       is set to true and more than one item has been selected for discount.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        cargo.setProrateDiscountByAmount(false);
        cargo.setContainsSellAndReturnItems(false);
        
        // Get required managers                                             
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        if (cargo.getItems()!= null &&
            cargo.getItems().length > 1 &&
            prorateSelectionDialogRequired((ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE)))
        {
            showProrateSelectionDialog(ui);
        }
        else
        {
            // Send the continue or success letter
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
    }
                
    /**
     * Displays the prorate discount selection dialog.
     * 
     * @param ui The POSUIManager
     */
    protected void showProrateSelectionDialog(POSUIManagerIfc ui)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(DISCOUNT_CONFIRMATION);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if the Discount Already Applied Dialog should be displayed.
     *   @param pm The ParameterManager
     *   @return true if dialog should be displayed, false otherwise
     */
    //----------------------------------------------------------------------
    private boolean prorateSelectionDialogRequired(ParameterManagerIfc pm)
    {
        boolean selectionDialogRequired = false;

        // retrieve Prorate Discount Amount from parameter file
        try
        {
            selectionDialogRequired = pm.getBooleanValue(PRORATE_DISCOUNT_AMOUNT).booleanValue();
            if (selectionDialogRequired)
            {
                if (logger.isInfoEnabled()) logger.info("Parameter read: "
                                                        + PRORATE_DISCOUNT_AMOUNT
                                                        + "=[" + selectionDialogRequired + "]");
            }
        }
        catch (ParameterException e)
        {
            logger.error(Util.throwableToString(e));
        }
        
        return selectionDialogRequired;
    }

}
