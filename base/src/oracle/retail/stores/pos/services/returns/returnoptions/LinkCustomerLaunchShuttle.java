/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/LinkCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
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
 *    4    360Commerce 1.3         3/24/2008 12:39:34 PM  Deepti Sharma   merge
 *          from v12.x to trunk
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/08/09 17:37:21  mweis
 *   @scr 6181 When returning w/out a receipt, we link to existing customers.  We do not attempt to create new ones.
 *
 *   Revision 1.6  2004/06/25 15:32:16  cdb
 *   @scr 4286 Updated flow of Returns with customer required when offline.
 *
 *   Revision 1.5  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 20 2003 17:37:08   RSachdeva
 * setOfflineIndicator  OFFLINE_EXIT
 * Resolution for POS SCR-2743: DB offline, Flow error for return by Purchase Date & Store Number
 * 
 *    Rev 1.1   May 27 2003 08:48:42   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 16:13:04   baa
 * add Entering/Exiting Custmer msg to journal
 * Resolution for POS SCR-648: Customer Find not journaling Entering and Exiting Customer during MBC
 *
 *    Rev 1.0   Mar 18 2002 11:46:20   msg
 * Initial revision.
 *
 *    Rev 1.5   05 Mar 2002 17:41:46   cir
 * Enable Find only
 * Resolution for POS SCR-1380: Add & Delete are enabled in Cust Search for a return by store, cust or date
 *
 *    Rev 1.4   08 Feb 2002 14:08:50   baa
 * test
 * Resolution for POS SCR-1202: Return by item requiring Customer hangs on Customer Contact
 *
 *    Rev 1.3   08 Feb 2002 14:08:02   baa
 * testing
 * Resolution for POS SCR-1202: Return by item requiring Customer hangs on Customer Contact
 *
 *    Rev 1.2   08 Feb 2002 14:01:54   baa
 * get register from cargo
 * Resolution for POS SCR-1202: Return by item requiring Customer hangs on Customer Contact
 *
 *    Rev 1.1   29 Jan 2002 08:42:38   baa
 * fix operator
 * Resolution for POS SCR-941: Getting Sercurty Access notice from Date Range Return at Cust Add, Find, Del
 *
 *    Rev 1.0   Sep 21 2001 11:25:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the parent cargo with information from the child cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2063322407826701461L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.LinkCustomerLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Return options cargo
    **/
    ReturnOptionsCargo roCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from parent service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // retrieve cargo from the parent service
        roCargo = (ReturnOptionsCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by child service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // retrieve cargo from the child
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();

        // set access
        cargo.setOperator(roCargo.getOperator());

        // set register
        cargo.setRegister(roCargo.getRegister());

        // set the return transaction id in the options cargo
        cargo.setLinkDoneSwitch(CustomerMainCargo.LINK);

        // If the customer DB is offline, cancel the service.
        if (roCargo.isCustomerMustLink())
        {    
            cargo.setOfflineIndicator(CustomerMainCargo.OFFLINE_ADD);
        }
        else
        {
            cargo.setOfflineIndicator(CustomerMainCargo.OFFLINE_EXIT);
            // We only need to find existing customers. 
            // We do not need to create/add customers and/or businesses
            cargo.setFindOnlyMode(true);
        }
        
        // We are a "Returns" transaction
        cargo.setReturn(true);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  LinkCustomerLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
