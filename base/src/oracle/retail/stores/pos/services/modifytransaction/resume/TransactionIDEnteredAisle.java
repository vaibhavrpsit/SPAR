/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/TransactionIDEnteredAisle.java /main/11 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:28  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Retrieves entered transaction ID from user interface.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class TransactionIDEnteredAisle extends PosLaneActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
     * lane name constant
     */
    public static final String LANENAME = "TransactionIDEnteredAisle";

    /**
     * Retrieves selected reason code from user interface.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // selected row from ui
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set entered transaction ID in cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        // get data from input area
        String transactionID = ui.getInput();
        // create summary from ID
        TransactionSummaryIfc summary = DomainGateway.getFactory().getTransactionSummaryInstance();
        TransactionIDIfc transID = DomainGateway.getFactory().getTransactionIDInstance();
        transID.setTransactionID(transactionID);
        summary.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
        summary.setTransactionID(transID);
        cargo.setSelectedSummary(summary);
        if (logger.isInfoEnabled())
            logger.info("" + "Selected transaction ID: " + "" + transactionID + "");

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
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