/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/find/IsCustomerAddSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   May 27 2003 09:23:52   baa
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:33:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:08   msg
 * Initial revision.
 * 
 *    Rev 1.2   21 Dec 2001 12:40:58   baa
 * deprecate old classes
 * Resolution for POS SCR-478: Deprecate unused customer classes
 *
 *    Rev 1.1   23 Oct 2001 16:53:24   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.find;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
//--------------------------------------------------------------------------
/**
    This determines if there was a dataconnection failure.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated As of release 5.0.0
**/
//--------------------------------------------------------------------------
public class IsCustomerAddSignal implements TrafficLightIfc

{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8294846484641574194L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Determines whether it is safe for the bus to proceed.
        <p>
        @param bus the bus trying to proceed
        @return true if the data exception error code is CONNECTION_ERROR
                and link is true
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        return cargo.isNewCustomer();
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
