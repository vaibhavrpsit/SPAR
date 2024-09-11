/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/FindCustomerLaunchShuttle.java /main/11 2014/06/03 13:25:27 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  05/09/14 - Set appID from cargo.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:42   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 16:12:58   baa
 * add Entering/Exiting Custmer msg to journal
 * Resolution for POS SCR-648: Customer Find not journaling Entering and Exiting Customer during MBC
 *
 *    Rev 1.0   Mar 18 2002 11:45:24   msg
 * Initial revision.
 *
 *    Rev 1.4   28 Jan 2002 22:44:16   baa
 * ui fixes
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.3   16 Nov 2001 10:35:20   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   30 Oct 2001 16:20:40   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   23 Oct 2001 16:54:42   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:24:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the child cargo (CustomerFindCargo) with
    information from the parent cargo (ReturnCustomerCargo).
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class FindCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8142536097042406663L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returncustomer.FindCustomerLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       Return By CustomerIfc cargo
    **/
    ReturnCustomerCargo rcCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from parent service.
       @param  bus Parent Service Bus.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // retrieve cargo from the parent service
        rcCargo = (ReturnCustomerCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by child service.
       @param  bus Child Service Bus.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // retrieve cargo from the child
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        // set access employee
        EmployeeIfc operador = rcCargo.getOperator();
        cargo.setOperator(operador);
        // set the return transaction id in the options cargo
        cargo.setLinkDoneSwitch(CustomerMainCargo.LINK);
        // If the customer DB is offline, cancel the service.
        cargo.setFindOnlyMode(true);
        cargo.setOfflineExit(true);
        cargo.setRegister(rcCargo.getRegister());
        cargo.setOfflineIndicator(CustomerMainCargo.OFFLINE_EXIT);
        // set that we are a "Returns" transaction.
        cargo.setReturn(true);
        cargo.setAppID(rcCargo.getAppID());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  FindCustomerLaunchShuttle (Revision " +
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
