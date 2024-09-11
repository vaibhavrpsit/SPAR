/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/CashDrawerCloseAisle.java /main/11 2011/02/16 09:13:29 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:24   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:34   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This aisle opens the cash drawer.
 * 
 * @version $Revision: /main/11 $
 */
public class CashDrawerCloseAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 8222942416506476496L;
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Attempts to close cash drawer.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSDeviceActions drawer = new POSDeviceActions((SessionBusIfc) bus);
        StatusBeanModel statusModel = new StatusBeanModel();

        try
        {
            // if cash drawer open then prompt to close
            if (drawer.isOpen().equals(Boolean.TRUE))
            {
                try
                {
                    drawer.waitForDrawerClose(); // blocks

                    // Update cash drawer status to ONLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
                }
                catch (DeviceException e)
                {
                    logger.warn("Unable to wait for cash drawer.", e);

                    // Update cash drawer status to OFFLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
                }
            }
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to close cash drawer.", e);

            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
        }

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.CLOSE_DRAWER, baseModel);

        //clear line display device of leftover tender information
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display.", e);
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }

}
