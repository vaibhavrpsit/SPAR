/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/IsLayawayTransactionSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/08/09 14:20:27  kll
 *   @scr 6796: logging clean-up
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:37:08   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:08   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:04:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:10:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:33:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal checks to see if the till is suspended.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IsLayawayTransactionSignal implements TrafficLightIfc
{
    static final long serialVersionUID = 4572355899018953584L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsLayawayTransactionSignal.class);

    /**
     * Checks to see if the till is suspended.
     * 
     * @return true if the till is suspended, false otherwise.
     */
    public boolean roadClear(BusIfc bus)
    {
        logger.debug("IsLayawayTransactionSignal.roadClear() - entry");

        boolean result = false;
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        if (cargo.getTransaction() instanceof LayawayTransaction)
        {
            result = true;
        }
        logger.debug("IsLayawayTransactionSignal.roadClear() - exit");
        return (result);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  IsTillSuspendedSignal (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
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
