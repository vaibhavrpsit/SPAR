/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ValidateReceiptIDAisle.java /main/1 2012/12/12 14:33:58 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  08/10/10 - fixed blank error message
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - ValidateReceiptIDAisle.java - Returns flow refactor:
 *                         deprected; moved to returns common and renamed to
 *                         ValidateTransactionIDAisle.java
 *    abhayg    03/02/10 - To Fix the issue when user entered receipt no. which
 *                         is having alphanumeric character in last 4 charcters
 *   
 *    acadar    09/11/09 - Use one field for scanning/entering transaction id
 *                         for return with receipt
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 * $
 * Revision 1.10  2004/04/30 21:49:01  cdb
 * @scr 4308 Scanning wrong bar code format throws a
 * nullpointerexception. And incorrect model was being used
 * to display dialog once the null pointer exception was avoided.
 *
 * Revision 1.9  2004/03/11 20:34:36  baa
 * @scr 3561 add changes to handle transaction variable length id
 *
 * Revision 1.8  2004/03/03 23:15:09  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.7  2004/02/26 16:47:09  rzurga
 * @scr 0 Add optional and customizable date to the transaction id and its receipt barcode
 *
 * Revision 1.6  2004/02/23 14:58:52  baa
 * @scr 0 cleanup javadocs
 *
 * Revision 1.5  2004/02/13 22:46:22  baa
 * @scr 3561 Returns - capture tender options on original trans.
 *
 * Revision 1.4  2004/02/13 13:57:20  baa
 * @scr 3561  Returns enhancements
 * Revision 1.3 2004/02/12 16:51:48 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:52:28 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:20 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.1 Dec 29 2003 15:36:16 baa return enhancements
 *
 * Rev 1.0 Aug 29 2003 16:06:02 CSchellenger Initial revision.
 *
 * Rev 1.0 Apr 29 2002 15:06:02 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:45:48 msg Initial revision.
 *
 * Rev 1.0 Sep 21 2001 11:24:56 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle is traveled when the transaction ID has been entered. E.g. when
 * performing a Price Adjustment.
 * 
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class ValidateReceiptIDAisle extends PosLaneActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
     * Maximum number of characters in a receiptid field
     */
    public static final int MAX_RECIEPT_LENGTH = 14;

    /**
     * Constant for error screen
     */
    public static final String INVALID_RETURN_NUMBER = "InvalidNumberError";

    /**
     * purchase date field
     */
    public static final String PURCHASE_DATE_FIELD = "purchaseDateField";

    /**
     * store number
     */
    public static final String STORE_NUMBER_FIELD = "storeNumberField";

    /**
     * register number
     */
    public static final String REGISTER_NUMBER_FIELD = "registerNumberField";

    /**
     * transaction number
     */
    public static final String TRANS_NUMBER_FIELD = "transactionNumberField";

    /**
     * Validates and stores the transaction ID in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String receiptId = ui.getInput();

        if (receiptId.length() != TransactionID.getTransactionIDLength())
        {
            // "Receipt" or "Other" number.
            String args[] = new String[2];
            args[0] = cargo.getNumberTypeText();
            args[1] = cargo.getNumberTypeText();

            // Using "generic dialog bean".
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INVALID_RETURN_NUMBER);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(args);

            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            TransactionIDIfc receiptNo = DomainGateway.getFactory().getTransactionIDInstance();

            receiptNo.setTransactionID(receiptId);
            if (receiptNo.getSequenceNumber() == 0 && Util.isEmpty(receiptNo.getStoreID())
                    && Util.isEmpty(receiptNo.getWorkstationID()))
            {
                String args[] = new String[2];
                args[0] = cargo.getNumberTypeText();
                args[1] = cargo.getNumberTypeText();

                // Using "generic dialog bean".
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID(INVALID_RETURN_NUMBER);
                dialogModel.setType(DialogScreensIfc.ERROR);
                dialogModel.setArgs(args);

                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                cargo.setOriginalTransactionId(receiptNo);
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }
    }
}
