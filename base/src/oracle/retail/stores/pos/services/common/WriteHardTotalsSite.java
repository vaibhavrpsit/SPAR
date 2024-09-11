/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/WriteHardTotalsSite.java /main/13 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   04/12/12 - Changing site to use
 *                         TransactionUtilityManager.writeHardTotals().
 *                         MobilePOS requires special handling (workstationID
 *                         filename prefix) that is performed via the mobile
 *                         version of the utility.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *   Revision 1.4  2004/03/14 21:24:26  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:54:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   28 May 2002 12:21:50   vxs
 * Removed unncessary concatenations from logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:35:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:10:52   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:23:36   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Mar 2002 16:52:40   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.0   Sep 21 2001 11:14:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Read the hard totals device.
 *
 * @version $Revision: /main/13 $
 */
public class WriteHardTotalsSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -6964724575665366646L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    public static final String HARDTOTALS_ERROR_LETTER = "HardTotalsError";

    /**
     * Writes hard totals.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // everything-is-hunky-dory indicator
        boolean bOk = true;
        // letter to mail to next destination
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        // local copies of store status, register
        StoreStatusIfc storeStatus = null;
        RegisterIfc register = null;

        // pull data from abstract financial cargo
        try
        {
            // get cargo, device actions
            WriteHardTotalsCargoIfc cargo = (WriteHardTotalsCargoIfc) bus.getCargo();
            storeStatus = cargo.getStoreStatus();
            register = cargo.getRegister();
        }
        catch (Exception e)
        {
            logger.error("An error occurred getting the data from cargo to set the hard totals.", e);
            letter = new Letter(HARDTOTALS_ERROR_LETTER);
            bOk = false;
        }

        if (bOk)
        {
            // Do not write hard totals if the register is in training mode
            if (!register.getWorkstation().isTrainingMode())
            {
                // write out hard totals
                try
                {
                    TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
                    utility.writeHardTotals();
                }
                catch (DeviceException e)
                { // begin catch device exception
                    logger.warn("Hard totals device could not be written.", e);

                    if (e.getCause() != null)
                    {
                        logger.warn("DeviceException.NestedException:", e.getCause());
                    }
                    // issue HardTotalsError letter
                    letter = new Letter(HARDTOTALS_ERROR_LETTER);
                } // end catch device exception
            }
        } // end write hard totals

        // mail letter
        bus.mail(letter, BusIfc.CURRENT);
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
