/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/TaxExemptEnteredAisle.java /main/11 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 26 2003 15:42:46   RSachdeva
 * Removed use of CodeEntry getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   13 Mar 2002 17:07:40   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.0   Sep 21 2001 11:31:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

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
import oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;

/**
 * This aisle is traversed when a TaxExempt number is entered at the UI and the
 * Next button is pressed. This aisle will set the TaxExempt number in the
 * cargo.
 * 
 * @version $Revision: /main/11 $
 */
public class TaxExemptEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 9150273249336237178L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Sets the TaxExempt number in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // get cargo handle
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo) bus.getCargo();

        // get bean model
        StringWithReasonBeanModel beanModel = 
            (StringWithReasonBeanModel) ui.getModel(POSUIManagerIfc.TRANSACTION_TAX_EXEMPT);
        // retrieve certificate ID, reason
        String id = beanModel.getValue();
        String reason = beanModel.getSelectedReasonKey();
        // log results
        String message =
            new String("***** TaxExemptEnteredAisle received input [" +
                       id +
                       "] reason [" +
                       reason +
                       "].");
        if (logger.isInfoEnabled()) logger.info( "" + message + "");
        // set values in transaction tax object
        TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
        tax.setTaxExemptCertificateID(id);
        CodeEntryIfc reasonEntry = null;
        // retrieve reason code list
        CodeListIfc rcl = cargo.getLocalizedExemptReasonCodes();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (rcl != null)
        {
            reasonEntry = rcl.findListEntryByCode (reason);
            localizedCode.setCode(reason);
            localizedCode.setText(reasonEntry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        tax.setReason(localizedCode);

        tax.setTaxMode(TaxIfc.TAX_MODE_EXEMPT);
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
        // return string
        return (revisionNumber);
    }
}