/* ===========================================================================
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/EnterReceiptIDSite.java /main/13 2012/12/18 13:01:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    09/11/09 - Use one field for scanning/entering transaction id
 *                         for return with receipt
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:03 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *   Revision 1.7  2004/03/11 20:34:36  baa
 *   @scr 3561 add changes to handle transaction variable length id
 *
 *   Revision 1.6  2004/02/27 22:43:50  baa
 *   @scr 3561 returns add trans not found flow
 *
 *   Revision 1.5  2004/02/26 16:47:09  rzurga
 *   @scr 0 Add optional and customizable date to the transaction id and its receipt barcode
 *
 *   Revision 1.4  2004/02/13 13:57:20  baa
 *   @scr 3561  Returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Jan 23 2004 16:07:50   baa
 * continue returns development
 *
 *    Rev 1.2   Jan 13 2004 14:41:52   baa
 * continue return developemnt
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Dec 29 2003 15:36:08   baa
 * return enhancements
 *
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:38   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Dec 2001 16:54:40   jbp
 * changes to remove IllegalReturnNumber Dialog screen.
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the RETURN_RECEIPT screen.
 * <p>
 * Note: As of 14.0, this screen is still used during Price Adjustment.
 */
@SuppressWarnings("serial")
public class EnterReceiptIDSite extends PosSiteActionAdapter
{
    /**
     * Displays the RETURN_RECEIPT screen.
     * 
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        // get the uiManager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String currentLetter = bus.getCurrentLetter().getName();
        String screenName = POSUIManagerIfc.RETURN_RECEIPT;

        // If re-entering this service use previous data
        POSBaseBeanModel model = new POSBaseBeanModel();

        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (POSBaseBeanModel) ui.getModel(screenName);
        }

        ui.showScreen(screenName, model);
    }
}
