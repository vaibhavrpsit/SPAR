/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/OperatorIdLaunchShuttle.java /main/11 2013/11/19 09:42:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/18/13 - create unlockScreen method for cargo that needs to
 *                         control the ui not to unlock until it is done.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:11:33 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:48 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/15/2005 14:57:28    Jason L. DeLeau 4204:
 *      Remove duplicate instances of UserAccessCargoIfc
 * 3    360Commerce1.2         3/31/2005 15:29:12     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:23:48     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:12:50     Robert Pearse
 *
 *Revision 1.5  2004/09/23 00:07:15  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.4  2004/04/09 16:55:59  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:49:02  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:37:44  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 17 2003 13:20:24   HDyer
 * Set the cargo data for doing an override.
 * Resolution for POS SCR-2089: Manager Override maintains manager security level rather than reverting to lower level
 *
 *    Rev 1.0   Apr 29 2002 15:37:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:07:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:38   msg
 * Initial revision.
 *
 *    Rev 1.1   22 Oct 2001 16:59:46   pdd
 * Added SCR association.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   22 Oct 2001 15:00:10   pdd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.ifc.UICargoIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

/**
 * This shuttle carries the required contents from the calling service to the
 * OperatorId service.
 * 
 * @version $Revision: /main/11 $
 */
public class OperatorIdLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2525201938399275155L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(OperatorIdLaunchShuttle.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "OperatorIdLaunchShuttle";
    /**
     */
    protected UserAccessCargoIfc callingCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        callingCargo = (UserAccessCargoIfc)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        cargo.setMaximumAttempts(1);
        cargo.setHandleError(true);

        // Set the needed override information in the cargo
        cargo.setSecurityOverrideFlag(true);
        cargo.setAccessFunctionID(callingCargo.getAccessFunctionID());
        if (callingCargo instanceof UICargoIfc)
        {
            cargo.setUnlockScreenAfterDialog(((UICargoIfc)callingCargo).isUnlockScreenAfterDialog());
        }
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}