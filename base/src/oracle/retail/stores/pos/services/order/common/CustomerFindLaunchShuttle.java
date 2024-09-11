/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/CustomerFindLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:21 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
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
 *    Rev 1.0   Aug 29 2003 16:03:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:38   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:52   msg
 * Initial revision.
 * 
 *    Rev 1.3   16 Nov 2001 10:34:52   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   30 Oct 2001 16:11:06   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   24 Oct 2001 15:07:26   baa
 * Disable  Add/Delete buttons when calling Customer find
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.0   Sep 24 2001 13:01:08   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
   From the order service, an option exists to search for orders by Customer.
   This shuttle goes from Order to Customer Find.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerFindLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -712291069394861019L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.order.common.CustomerFindLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       UserAccessCargoIfc cargo
    **/
    protected OrderCargo oCargo = null;

    //----------------------------------------------------------------------
    /**
       Load UserAccessCargoIfc Cargo data into the shuttle for transfer to Customer Find.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        oCargo = (OrderCargo) bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Unloads shuttle data into the Customer Find cargo. Set the LinkDoneSwitch so
       that the Link button is enabled and the Done button is disabled in the
       CustomerAddress screen.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerMainCargo customerCargo = (CustomerMainCargo) bus.getCargo();

        customerCargo.setOperator(oCargo.getOperator());

        // takes a constant which is defined in CustomerCargo LINKANDDONE, LINK, DONE
        customerCargo.setLinkDoneSwitch(CustomerCargo.LINK);
        // set offline behavior to exit customer service if database is down.
        customerCargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        // Call customer for FindOnly
        customerCargo.setFindOnlyMode(true);
        // Pass register information
        customerCargo.setRegister(oCargo.getRegister());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
