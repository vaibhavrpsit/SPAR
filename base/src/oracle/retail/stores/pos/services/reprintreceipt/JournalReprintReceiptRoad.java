/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/JournalReprintReceiptRoad.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:11 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/02 21:19:16  kll
 *   @scr 5224: associate the transactionID withe the Reprint journal entry
 *
 *   Revision 1.5  2004/04/26 19:51:14  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Add Reprint Select flow.
 *
 *   Revision 1.4  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:05:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:07:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:44   msg
 * Initial revision.
 *
 *    Rev 1.1   06 Mar 2002 16:29:24   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.0   Sep 21 2001 11:23:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;
// foundation imports
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    Journal the fact that the receipt was reprinted.
    @version $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class JournalReprintReceiptRoad extends PosLaneActionAdapter
{                                       // begin class JournalReprintReceiptRoad
    //--------------------------------------------------------------------------
    /**
        Issue a journal entry to the effect that the receipt was reprinted.
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        // journal only necessary if receipt printed
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();

        // Determine the number of receipts printed
        int count = cargo.getReprintReceiptCount();
        cargo.setReceiptPrinted(false);

        // get the Journal manager
        JournalManagerIfc jmi =
          (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        // journal the save
        if (jmi != null)
        {
            TenderableTransactionIfc trans = cargo.getTenderableTransaction();
            StringBuffer journalString = new StringBuffer();




            Object dataObject[]={count};


            String receiptCount = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REPRINT_RECEIPT_COUNT, dataObject);


            Object transactionDataObject[]={trans.getTransactionID().toString()};


            String transactionId = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_LABEL, transactionDataObject);


            journalString.append(Util.EOL)
			            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REPRINT_RECEIPT, null))
			            .append(Util.EOL)
			            .append(receiptCount)
			            .append(Util.EOL)
			            .append(transactionId)
			            .append(Util.EOL);



//            journalString.append(Util.EOL).append("Reprint Receipt")
//                         .append(Util.EOL)
//                         .append(count)
//                         .append(" Receipts Printed")
//                         .append(Util.EOL)
//                         .append("Transaction:  ")
//                         .append(trans.getTransactionID().toString())
//                         .append(Util.EOL);

            jmi.journal(cargo.getOperator().getEmployeeID(),
                            trans.getTransactionID(),
                            journalString.toString());
        }
        else
        {
            logger.warn(
                        "No journal manager found.");
        }

    }                                   // end traverse()
}                                       // end class JournalReprintReceiptRoad
