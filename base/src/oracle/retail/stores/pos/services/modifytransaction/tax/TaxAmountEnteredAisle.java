/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/TaxAmountEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/09/02 13:05:38 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 09/02/11 - refactored method names around enciphered objects
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - localization of transaction tax reason codes
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:22 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:45 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:40 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:14:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:02   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

/**
 * This aisle is traversed when a tax rate is entered at the UI. This aisle will
 * set the tax rate number in the cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TaxAmountEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 2202124815665043167L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Sets the tax rate number in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get cargo handle
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo) bus.getCargo();

        // get bean model
        DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) ui
                .getModel(POSUIManagerIfc.TRANSACTION_TAX_OVERRIDE_AMOUNT);
        // retrieve amount, reason
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
        amount.setStringValue(beanModel.getValue().toString());
        String reason = beanModel.getSelectedReasonKey();

        // set values in transaction tax object
        TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
        tax.setOverrideAmount(amount);
        CodeEntryIfc reasonEntry = null;
        // retrieve reason code list
        CodeListIfc rcl = cargo.getLocalizedOverrideAmountReasonCodes();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (rcl != null)
        {
            reasonEntry = rcl.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(reasonEntry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        tax.setReason(localizedCode);
        tax.setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_AMOUNT);
        cargo.setTransactionTax(tax);
        cargo.setDirtyFlag(true);
        cargo.setNextFlag(true);
        // mail a letter
        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
