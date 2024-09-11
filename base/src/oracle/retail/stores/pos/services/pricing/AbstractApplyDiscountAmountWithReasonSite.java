/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractApplyDiscountAmountWithReasonSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    acadar 11/02/08 - cleanup
 *    acadar 10/31/08 - fix the reason code invalid message
 *    acadar 10/31/08 - removed commented out/deprecated code
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 *                      discounts
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:25 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
     $
     Revision 1.7.2.1  2004/11/12 17:27:14  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.7  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.6  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.5  2004/03/22 04:02:02  cdb
     @scr 3588 Code Review cleanup

     Revision 1.4  2004/03/19 23:27:50  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review cleanup.

     Revision 1.3  2004/03/16 18:30:45  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.2  2004/03/04 19:54:15  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Abstract method to get reason code list.

     Revision 1.1  2004/02/25 21:27:57  cdb
     @scr 3588 Re-added lost Invalid Reason Code dialog.


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
 *   Extends the AbstractApplyDiscountAmountSite for discounts that require
 *   a reason code. <p>
 *   @see oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountAmountSite
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public abstract class AbstractApplyDiscountAmountWithReasonSite extends AbstractApplyDiscountAmountSite
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     *   Apply the previously validated discounts by amount. <P>
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
        CodeListIfc rcl = getDiscountAmountCodeList(cargo);
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
     *   Returns identifiction for the screen containing Reason Code data. <P>
     *   @return  screen model name
     */
    //----------------------------------------------------------------------
    public abstract String getUIModel();


    /**
     *   Returns list of Reason Code data. <P>
     *   @param   cargo PricingCargo
     *   @return  reason code list
    */
    public abstract CodeListIfc getDiscountAmountCodeList(PricingCargo cargo);


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
            if (logger.isInfoEnabled()) logger.info(
            "Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
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
