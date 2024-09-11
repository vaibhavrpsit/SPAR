/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/IsOnlyTransactionSignal.java /main/12 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     01/22/10 - Code review modifications.
 *    jswan     01/21/10 - Fix an issue in which a returned gift card can be
 *                         modified during the period in which the transaction
 *                         has been suspended.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:02:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 07 2003 13:04:04   sfl
 * Added checking on nulls.
 * Resolution for POS SCR-1889: Retrieval of Suspended Transactions while DB offline
 *
 *    Rev 1.0   Apr 29 2002 15:15:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:00   msg
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
 * Clears if there is only one transaction in the list of resumed
 * transactions.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class IsOnlyTransactionSignal implements TrafficLightIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(IsOnlyTransactionSignal.class);

    public static final String SIGNALNAME = "IsOnlyTransactionSignal";

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

        // if only one transaction resumed
        if (cargo.getSuspendList() != null)
        {
            if (cargo.getSuspendList().length == 1)
            { // Begin only one transaction in the list
                r_c = true;
            } // End only one transaction in the list
        }

        // For this signal true, there must be either just one transaction in
        // the
        // suspend list, or the application must be canceling the transaction
        // due to the items in it have become invalid while it was suspended.
        // For it to be false, both conditions must be false.
        if (r_c || cargo.isCancellingRecreatedTransaction())
        {
            r_c = true;
        }
        else
        {
            r_c = false;
        }

        return r_c;
    }
}
