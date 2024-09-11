/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/FindCustomerLaunchShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 * $
 * Revision 1.9  2004/09/23 00:07:12  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.8  2004/04/13 21:43:09  bwf
 * @scr 4263 Fix problem with decomposition.
 *
 * Revision 1.1  2004/04/05 15:44:13  epd
 * @scr 4263 Moved Mail Bank Check to new sub tour
 *
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/02/27 20:59:05  bjosserand
 * @scr 0 Mail Bank Check
 * Revision 1.4 2004/02/17 19:26:17 epd @scr 0 Code cleanup. Returned unused
 * local variables.
 * 
 * Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.2 Feb 06 2004 16:53:36 bjosserand Mail Bank Check.
 * 
 * Rev 1.1 Feb 05 2004 14:27:00 bjosserand Mail Bank Check.
 * 
 * Rev 1.0 Nov 04 2003 11:17:42 epd Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;

//--------------------------------------------------------------------------
/**
 * This shuttle is used to go to the customer service. $Revision: /main/13 $
 */
//--------------------------------------------------------------------------
public class FindCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4827342651770277724L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "FindCustomerLaunchShuttle";
    /**
     * tender cargo reference
     */
    protected TenderCargo tenderCargo;

    //----------------------------------------------------------------------
    /**
     * Load a copy of TenderCargo into the shuttle for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        tenderCargo = (TenderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Make a CustomerFindCargo and populate it.
     * <p>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();
        TenderableTransactionIfc trans =
            (TenderableTransactionIfc) ((ADO) tenderCargo.getCurrentTransactionADO()).toLegacy();

        // set the role function ID
        cargo.setAccessFunctionID(RoleFunctionIfc.CUSTOMER_ADD_FIND);

        // set the transaction ID
        cargo.setTransactionID(trans.getTransactionID());
        cargo.setOperator(tenderCargo.getOperator());
        cargo.setRegister(tenderCargo.getRegister());

        // set the linkDoneSwitch
        cargo.setLinkDoneSwitch(CustomerCargo.LINK);
        // Set customer service to enable find option only
        // If the customer DB is offline, cancel the service.
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        if (trans.getTransactionID() != null)
        {
            CustomerUtilities.journalCustomerEnter(bus, trans.getCashier().getEmployeeID(), trans.getTransactionID());
        }
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class:  FindCustomerLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
