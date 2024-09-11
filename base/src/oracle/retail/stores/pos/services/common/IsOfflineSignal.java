/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/IsOfflineSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 15:54:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:35:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:09:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This determines if there was a dataconnection failure.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IsOfflineSignal implements TrafficLightIfc
{
    static final long serialVersionUID = 5175263752792533491L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Determines whether it is safe for the bus to proceed.
     * 
     * @param bus the bus trying to proceed
     * @return true if the data exception error code is CONNECTION_ERROR
     */
    public boolean roadClear(BusIfc bus)
    {
        boolean isOffline = false;
        DBErrorCargoIfc cargo = (DBErrorCargoIfc) bus.getCargo();

        // Check the data exception error code
        if (cargo.getDataExceptionErrorCode() == DataException.CONNECTION_ERROR)
        {
            isOffline = true;
        }

        return (isOffline);
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
