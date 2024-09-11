/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/EnterTillPayrollPayOutAmountSite.java /main/14 2012/08/27 11:22:47 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 08/16/12 - wptg - removed placeholder from key PayrollPayOutPrompt
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    ebthor 11/04/08 - Post merge updates.
 *    ohorne 10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================

     $Log:
      5    .v8x      1.3.1.0     3/11/2007 8:00:09 PM   Brett J. Larsen CR 4530
            - default code values not being pre-selected in drop-down lists

           this change adds support for default values (while maintaining
           support for values selected earlier in the xaction flow)
      4    360Commerce1.3         7/28/2006 5:40:56 PM   Brett J. Larsen 4530:
           default reason code fix
           v7x->360Commerce merge
      3    360Commerce1.2         3/31/2005 4:28:05 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:28 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:55 PM  Robert Pearse
     $

      4    .v7x      1.2.1.0     6/23/2006 4:49:55 AM   Dinesh Gautam   CR
           4530: Fix for reason code

     Revision 1.11  2004/09/15 16:34:22  kmcbride
     @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions

     Revision 1.10  2004/08/23 16:16:00  cdb
     @scr 4204 Removed tab characters

     Revision 1.9  2004/07/22 00:06:33  jdeleau
     @scr 3665 Standardize on I18N standards across all properties files.
     Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.

     Revision 1.8  2004/07/20 21:36:10  jdeleau
     @scr 5525 Persist data if an error screen pops up, so the
     user doesn't have to retype everything.

     Revision 1.7  2004/07/14 18:47:09  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.6  2004/06/16 00:04:47  aschenk
     @scr 5521 - Changed the approval drop down to not have a default. Also had to update approval code label (it was printing incorrectly)

     Revision 1.5  2004/06/15 02:22:10  aschenk
     @scr 5520 - Changed the reason code drop down to not have a default.

     Revision 1.4  2004/05/25 13:13:43  jeffp
     @scr 4371 - Added check for the customer address parameter

     Revision 1.3  2004/04/28 20:25:38  cdb
     @scr 4572 Addressing incorrect implementation of item 5 for PayInPayOutReceiptSignatureLinePrintingParameter

     Revision 1.2  2004/03/15 14:37:10  khassen
     Pay in/out revisions - additional comments.

     Revision 1.1  2004/03/12 18:19:23  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.3  2004/02/12 16:50:04  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:47:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 13 2003 10:54:10   HDyer
 * Made change for beanModel setReasonCodes signature change.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:26:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 09 2002 17:17:30   mpm
 * Text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:19:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:58   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

//java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterTillPayrollPayOutBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 *
 * @author khassen
 *
 * Used by the payroll pay out use case.
 */
public class EnterTillPayrollPayOutAmountSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8938746996988689278L;

    public static final String revisionNumber = "$Revision: /main/14 $";
    public static final String SITENAME       = "EnterTillPayrollPayOutAmountSite";


    /**
     * arrive method.
     * @param bus the bus.
     */
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        //get till info from cargo
        TillPayrollPayOutCargo cargo = (TillPayrollPayOutCargo)bus.getCargo();

        //set model with data
        EnterTillPayrollPayOutBeanModel model = new EnterTillPayrollPayOutBeanModel();

        //set Prompt and Response arg
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        model.setPromptAndResponseModel(parModel);

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // Set the reason codes in the model from the cargo.
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                                                         CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_REASON_CODES);
        cargo.setReasonCodes(reasonCodes);
        model.inject(reasonCodes, CodeConstantsIfc.CODE_UNDEFINED, lcl);


        // Set the approval codes in the model from the cargo.
        CodeListIfc approvalCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                                                         CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_APPROVAL_CODES);
        cargo.setApprovalCodes(approvalCodes);
        model.setApprovalCodes(approvalCodes.getTextEntries(lcl));
        model.setDefaultValue(approvalCodes.getDefaultOrEmptyString(lcl));
        model.setSelectedApprovalCodeIndex(CodeConstantsIfc.CODE_INTEGER_UNDEFINED);
        
        UtilityIfc util;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
      
        // 3 line address is always true as of 13.1
        model.setNumAddressLines(3);

        // Get the validation parameter and store it in the
        // cargo.  This will be used to determine whether or not
        // validation of the employee is required.
        String validateIDString = util.getParameterValue("ValidatePayrollPayOutEmployeeID", "N");
        if (validateIDString.equalsIgnoreCase("N"))
        {
            cargo.setValidateEmployeeID(false);
        }
        else
        {
            cargo.setValidateEmployeeID(true);
        }
        // Get the number of signature lines that ought to be visible
        // on the receipt.
        String numSigLinesString = util.getParameterValue("PayInPayOutReceiptSignatureLinePrinting", "OneSignatureLine");
        boolean isOneLine  = numSigLinesString.equals("OneSignatureLine");
        boolean isTwoLines = numSigLinesString.equals("TwoSignatureLines");
        if (isOneLine)
        {
            cargo.setNumSigLines(1);
        }
        else if (isTwoLines)
        {
            cargo.setNumSigLines(2);
        }
        else
        {
            cargo.setNumSigLines(0);
        }
        
        // Populate model with data in the cargo, in case this we are at this
        // site because of an error, we want to show the old data.
        if(cargo.getAmount() != null)
        {
            model.setAmount(cargo.getAmount().abs().toString());
        }

        model.setPaidTo(cargo.getPaidTo());
        model.setEmployeeID(cargo.getEmployeeID());

        for (int i = 0; i < model.getNumAddressLines(); i++)
        {
            model.setAddressLine(i, cargo.getAddressLine(i));
        }
        
        model.setComment(cargo.getComments());
        // END data population
        
        //show screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.setModel(POSUIManagerIfc.PAYROLL_PAY_OUT, model);
        ui.showScreen(POSUIManagerIfc.PAYROLL_PAY_OUT);
    }
}
