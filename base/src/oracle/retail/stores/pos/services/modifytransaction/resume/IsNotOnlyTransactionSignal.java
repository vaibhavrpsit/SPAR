/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/IsNotOnlyTransactionSignal.java /main/11 2014/05/14 14:41:28 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
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
 *    Rev 1.0   Aug 29 2003 16:02:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 07 2003 13:04:04   sfl
 * Added checking on nulls.
 * Resolution for POS SCR-1889: Retrieval of Suspended Transactions while DB offline
 *
 *    Rev 1.0   Apr 29 2002 15:15:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Clears if there is more than one transaction in the list of resumed
 * transactions.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class IsNotOnlyTransactionSignal implements TrafficLightIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(IsNotOnlyTransactionSignal.class);

    public static final String SIGNALNAME = "IsNotOnlyTransactionSignal";

    /**
     * roadClear determines whether it is safe for the bus to proceed
     * 
     * @param bus the bus trying to proceed
     * @return true if safe; false otherwise
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {

        boolean r_c = false; // return code

        // get the cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        // if more than one transaction resumed
        if (cargo.getSuspendList() != null)
        {
            if (cargo.getSuspendList().length > 1)
            { // Begin more than one transaction in the list
                r_c = true;
            } // End more than one transaction in the list
        }

        return r_c;
    }
}
