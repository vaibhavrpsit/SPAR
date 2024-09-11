/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/ValidateInquiryAisle.java /main/12 2013/11/07 11:22:41 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/07/13 - fix dialog reference to InvalidGovernmentID
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   08/30/11 - set zip and phone before checking ssn
 *    cgreene   08/29/11 - formatting
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:50:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 04 2003 15:22:24   nrao
 * Code Review Changes.
 *
 *    Rev 1.1   Nov 24 2003 19:32:12   nrao
 * Added UIUtilities.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.InstantCreditInquiryCriteriaBeanModel;
import oracle.retail.stores.pos.utility.ValidationUtility;

/**
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class ValidateInquiryAisle extends LaneActionAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();

        InstantCreditInquiryCriteriaBeanModel model = (InstantCreditInquiryCriteriaBeanModel)ui.getModel(POSUIManagerIfc.INSTANT_CREDIT_INQUIRY_CRITERIA);
        EncipheredDataIfc governmentId = model.getGovernmentId();
        cargo.setZipCode(model.getPostalCode());
        cargo.setHomePhone(model.getHomePhone());

        // Validate SSN
        boolean valid = ValidationUtility.checkGovernmentId(governmentId);
        if (valid)
        {
            cargo.setGovernmentId(governmentId);
            Letter letter;
            if (cargo.getProcess() == InstantCreditCargo.PROCESS_TEMP_PASS)
            {
                letter = new Letter("TempPass");
            }
            else if (cargo.getProcess() == InstantCreditCargo.PROCESS_REFERENCE)
            {
                letter = new Letter("Reference");
            }
            else
            {
                letter = new Letter("Inquiry");
            }
            bus.mail(letter, BusIfc.CURRENT);
        }
        else
        {
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "InvalidGovernmentID", null, "Retry");
        }
    }
}
