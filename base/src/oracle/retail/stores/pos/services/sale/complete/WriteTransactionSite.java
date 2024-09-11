/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/WriteTransactionSite.java /main/12 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:21:10   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 05 2003 13:47:24   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This site writes the transaction to the database.
 * 
 * @version $Revision: /main/12 $
 */
public class WriteTransactionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3261555400454081162L;
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Send the Save letter (the work is done in the aisles)
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

        bus.mail(new Letter("Save"), BusIfc.CURRENT);
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