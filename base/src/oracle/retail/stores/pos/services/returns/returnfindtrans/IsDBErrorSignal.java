/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/IsDBErrorSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
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
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This determines whether we're dealing w/ the database error screen or not
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class IsDBErrorSignal implements TrafficLightIfc
{
    static final long serialVersionUID = -5306759322254222039L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsDBErrorSignal.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * roadClear determines whether it is safe for the bus to proceed
     * 
     * @param bus the bus trying to proceed
     * @return true if not cashdrawer; false otherwise
     */
    public boolean roadClear(BusIfc bus)
    {
        DBErrorCargoIfc cargo = (DBErrorCargoIfc) bus.getCargo();
        boolean dBError = true;
        if (cargo.getDataExceptionErrorCode() == DataException.NONE)
        {
            dBError = false;
        }

        return dBError;
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

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class:  " + getClass().getName() + " (Revision " + getRevisionNumber() + ")"
                + hashCode());

        return (strResult);
    }
}
