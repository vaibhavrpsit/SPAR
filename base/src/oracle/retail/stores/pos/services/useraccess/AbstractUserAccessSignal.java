/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/useraccess/AbstractUserAccessSignal.java /main/11 2011/02/16 09:13:33 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:22 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:52:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:08:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:57:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:50:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:28:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.useraccess;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;

/**
 * This is the base class for all the user access signals.  The
 * child class passes a bus (which contains a UserAccessCargoIfc) and
 * the functionID to the hasAccess() method.  This method returns true if
 * the user has access to the function.
 * <P>
 * There are two signals for each function (i.e., POS, Price Inquiry, Open
 * Till, etc.); the "AccessNotOk" signal returns true if the user does NOT
 * have access the function.
 * <P>
 * The sites in which these signals appear does the follow:
 *     1)  if the user has access (the "AccessOk" signal returns true),
 *         it continues on.
 *     2)  if the user does not have access (the "AccessNotOk" signal 
 *         returns true), an aisle displays an error message and the 
 *         user is returned to the previous screen.
 *
 * @see oracle.retail.stores.pos.services.useraccess.AbstractUserAccess
 * @see oracle.retail.stores.pos.services.useraccess.AbstractUserAccessAisle
 * @version $Revision: /main/11 $
 */
public abstract class AbstractUserAccessSignal implements TrafficLightIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1016862254654409919L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Determines if a user has access to a function.
     * 
     * @param bus Service Bus
     * @param functionID function identifier
     * @return flag indicating if employee has access
     * @see oracle.retail.stores.domain.employee.RoleFunctionIfc
     */
    public boolean hasAccess(BusIfc bus, int functionID)
    {
        UserAccessCargoIfc cargo = (UserAccessCargoIfc)bus.getCargo();
        return (AbstractUserAccess.hasAccess(cargo.getOperator(), functionID));
    }
}
