/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/CheckOnlyOneDiscountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:57 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/02 00:34:01  cdb
 *   @scr 5337 Cleanup and Optimization
 *
 *   Revision 1.4  2004/06/15 16:46:56  awilliam
 *   @scr 5337 able to apply second employee discount no error msg appears
 *
 *   Revision 1.3  2004/03/26 21:18:19  cdb
 *   @scr 4204 Removing Tabs.
 *
 *   Revision 1.2  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.1  2004/03/08 18:09:16  cdb
 *   @scr 3588 Changed point in flow where items are checked
 *   for discountability.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site will show the Discount Already Applied dialog if maximum number
    of discounts parameter is set to only one discount, and another manual 
    discount already exists.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckOnlyOneDiscountSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.pricing.PricingCargo.class);

    //----------------------------------------------------------------------
    /**
        Show the Discount Already Applied dialog if maximum number
        of discounts parameter is set to only one discount, and another manual 
        discount already exists.
        @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get required managers                                             
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        if (discountAlreadyAppliedDialogRequired(bus))
        {
            showDiscountAlreadyAppliedDialog(ui);
        }
        else
        {
            // Send the continue or success letter
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the discount already applied confirmation screen. <P>
     *   @param  ui       The POSUIManager
     */
    //----------------------------------------------------------------------
    protected void showDiscountAlreadyAppliedDialog(POSUIManagerIfc ui)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.DISCOUNT_ALREADY_APPLIED);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.CONTINUE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,CommonLetterIfc.CANCEL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if the Discount Already Applied Dialog should be displayed.
     *   @param bus The service bus
     *   @return true if dialog should be displayed, false otherwise
     */
    //----------------------------------------------------------------------
    private boolean discountAlreadyAppliedDialogRequired(BusIfc bus)
    {
        boolean showDialogRequired = false;
        boolean isOnlyOneDiscount = false;
        boolean failure = false;
        String parameterValue = "";

        // retrieve Maximum Number of Discounts allowed from parameter file
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            parameterValue = pm.getStringValue(PricingCargo.MAX_DISCOUNTS_ALLOWED);
            parameterValue.trim();
            if (PricingCargo.ONE_TOTAL.equals(parameterValue))
            {
                isOnlyOneDiscount = true; 
            }
            else if (!PricingCargo.ONE_OF_EACH_TYPE.equals(parameterValue))
            {
                failure = true;
            }
        }
        catch (ParameterException e)
        {
            failure = true;
            logger.error( "" + Util.throwableToString(e) + "");
        }
        if (!failure)
        {
            if (logger.isInfoEnabled())
            {
                logger.info("Parameter read: "
                            + PricingCargo.MAX_DISCOUNTS_ALLOWED
                            + "=["  + parameterValue + "]");
            }
        }
        else
        {
            logger.error("Parameter read: "
                         + PricingCargo.MAX_DISCOUNTS_ALLOWED
                         + "=[" + parameterValue + "]");
        }
        
        if (!failure && isOnlyOneDiscount)
        {
            PricingCargo cargo = (PricingCargo) bus.getCargo();
            showDialogRequired = cargo.hasExistingManualDiscounts();
        }
        return showDialogRequired;
    }
    

}
