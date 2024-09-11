/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractApplyDiscountPercentWithReasonSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    acadar 11/02/08 - cleanup
 *    acadar 10/31/08 - removed deprecated code
 *    acadar 10/31/08 - minor fixes for manual discounts localization
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:25 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
     $
     Revision 1.4.2.1  2004/11/12 17:27:14  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.4  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.3  2004/03/22 04:02:02  cdb
     @scr 3588 Code Review cleanup

     Revision 1.2  2004/03/04 19:54:15  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Abstract method to get reason code list.

     Revision 1.1  2004/02/25 21:27:57  cdb
     @scr 3588 Re-added lost Invalid Reason Code dialog.

     Revision 1.2  2004/02/24 22:36:29  cdb
     @scr 3588 Added ability to check for previously existing
     discounts of the same type and capture the prorate user
     selection. Also migrated item discounts to validate in
     the percent and amount entered aisle to be consistent
     with employee discounts.

     Revision 1.1  2004/02/23 22:27:23  dcobb
     @scr 3588 Abstract common code  to abstract class.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.pricing;

import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

//--------------------------------------------------------------------------
/**
 *   Apply the previously validated discounts by percent.
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
abstract public class AbstractApplyDiscountPercentWithReasonSite extends AbstractApplyDiscountPercentSite
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     *   Apply the previously validated discounts by percent. <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get access to common elements
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Retrieve data from UI model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ((POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE)).getModel(getUIModel());
        String reason = beanModel.getSelectedReasonKey();

        // Validate the Reason Code ID Check Digit, Valid Reason Code exists
        CodeListIfc rcl = getDiscountPercentCodeList(cargo);
        CodeEntryIfc reasonEntry = null;
        if (rcl != null)
        {
            reasonEntry = rcl.findListEntryByCode(reason, false);
        }

        if (reasonEntry == null ||
                !isValidCheckDigit(utility, reasonEntry.getCode(), bus.getServiceName()))
        {
            // clear the invalid reason code
            beanModel.clearSelectedReason();
            // save the bean model
            cargo.setDecimalWithReasonBeanModel(beanModel);
            // display the invalid discount error screen
            showInvalidReasonCodeDialog((POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE));
        }
        else
        {
            super.arrive(bus);
        }
    }

    //----------------------------------------------------------------------
    /**
     *   Returns Model Identifiction for UI component containing Reason Code data. <P>
     *   @return  Screen model name
     */
    //----------------------------------------------------------------------
    public abstract String getUIModel();


    /**
     *   Returns list of Reason Code data. <P>
     *   @param   cargo PricingCargo
     *   @return  reason code list
     */
    public abstract CodeListIfc getDiscountPercentCodeList(PricingCargo cargo);


    //--------------------------------------------------------------------------
    /**
     Check digit validation.
     <P>
     @param utility       utility manager
     @param reasonCodeID  the reason code ID that needs to be checked
     @param serviceName   service name
     @return True if valid
     **/
    //----------------------------------------------------------------------
    protected static boolean isValidCheckDigit(UtilityManagerIfc utility,
            String reasonCodeID,
            String serviceName)
    {
        boolean isValid = false;
        if ( !utility.validateCheckDigit(
                CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE,
                reasonCodeID))
        {
            // If check digit is not configured for reason code, the check digit function will always return true
            if (logger.isInfoEnabled())
            {
                logger.info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");

            }
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the invalid discount error screen. <P>
     *   @param  ui       The POSUIManager
     */
    //----------------------------------------------------------------------
    protected void showInvalidReasonCodeDialog(POSUIManagerIfc ui)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.INVALID_REASON_CODE);
        dialogModel.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
