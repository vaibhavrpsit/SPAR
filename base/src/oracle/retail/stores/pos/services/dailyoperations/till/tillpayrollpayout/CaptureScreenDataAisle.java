/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/CaptureScreenDataAisle.java /main/14 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sgu    01/14/09 - use decimal format to set string value of a currency
 *                      object
 *    ohorne 10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         4/25/2007 8:52:29 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         3/29/2007 6:25:10 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/11/2007 7:56:35 PM   Brett J. Larsen
           CR 4530
           - default code values not being preselected in comboboxes

           integrated method rename to reduce confusion/increase consistency
      3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse
     $
     Revision 1.5  2004/09/23 00:07:17  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.4  2004/07/30 21:19:30  dcobb
     @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
     Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).

     Revision 1.3  2004/07/20 21:36:10  jdeleau
     @scr 5525 Persist data if an error screen pops up, so the
     user doesn't have to retype everything.

     Revision 1.2  2004/07/14 18:47:09  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.1  2004/03/12 18:19:23  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.3  2004/02/12 16:50:04  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:47:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:58:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 13 2003 10:56:40   HDyer
 * Modified for bean model changes.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:26:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:18   msg
 * Initial revision.
 *
 *    Rev 1.1   02 Mar 2002 12:47:56   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.0   Sep 21 2001 11:19:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:58   msg
 * header update
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterTillPayrollPayOutBeanModel;

/**
 * CaptureScreenDataAisle for the till payroll pay out use case. Captures the
 * information entered by the user into the bean model over to the cargo.
 * 
 * @author khassen
 */
public class CaptureScreenDataAisle extends PosLaneActionAdapter
        implements ParameterConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -6880331515841180303L;

    public static final String LANENAME = "CaptureScreenDataAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TillPayrollPayOutCargo cargo = (TillPayrollPayOutCargo) bus.getCargo();
        POSUIManagerIfc        ui    = (POSUIManagerIfc)        bus.getManager(UIManagerIfc.TYPE);
        // Default to doing validation.
        Letter  letter = new Letter(CommonLetterIfc.VALIDATE);
        UtilityIfc util = null;
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

        // Check to see if we really need to do validation of the employee
        // ID.  If not, then we set the letter to "Continue".
        String validateEmployeeIDString = util.getParameterValue(DAILYOPERATIONS_ValidatePayrollPayOutEmployeeID, NO);
        if (validateEmployeeIDString.equalsIgnoreCase(NO))
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        // Get the model for the bean
        EnterTillPayrollPayOutBeanModel model =
            (EnterTillPayrollPayOutBeanModel) ui.getModel(POSUIManagerIfc.PAYROLL_PAY_OUT);

        // Set PayOut Amount and ReasonCode into the Cargo
        CurrencyIfc      amount     = DomainGateway.getBaseCurrencyInstance();
        // Create TenderDescriptor instance for local cash
        TenderDescriptorIfc td      = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setCountryCode(amount.getCountryCode());
        td.setCurrencyID(amount.getType().getCurrencyId());

        amount.setStringValue(LocaleUtilities.parseCurrency(model.getAmount().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString());
        cargo.setAmount(amount.negate());
        cargo.setPaidTo(model.getPaidTo());
        cargo.setEmployeeID(model.getEmployeeID());

        for (int i = 0; i < model.getNumAddressLines(); i++)
        {
            cargo.setAddressLine(i, model.getAddressLine(i));
        }

        CodeListIfc list = cargo.getReasonCodes();
        if (list != null)
        {
            String reason = model.getSelectedReasonKey();
            CodeEntryIfc entry = list.findListEntryByCode(reason);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
            cargo.setSelectedLocalizedReasonCode(localizedCode);
        }

        list = cargo.getApprovalCodes();
        if (list != null)
        {
            String approval = (String)list.getKeyEntries().get(model.getSelectedApprovalCodeIndex());
            CodeEntryIfc entry = list.findListEntryByCode(approval);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(approval);
            localizedCode.setText(entry.getLocalizedText());
            cargo.setSelectedLocalizedApprovalCode(localizedCode);
        }

        cargo.setComments(model.getComment());

        if (amount.compareTo(cargo.getRegister().getCurrentTill().getAmountTotal(td)) >= 1)
        {
            letter = new Letter("PayrollPayOutError");
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
