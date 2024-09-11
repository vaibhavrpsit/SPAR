/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/SendCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:09 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/06/21 13:13:55  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.1  2004/06/16 21:42:50  lzhao
 *   @scr 4670: refactoring send package.
 *
 *   Revision 1.3  2004/02/12 16:51:54  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:44   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:03:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   04 Jan 2002 16:23:34   baa
 * set register value
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   03 Jan 2002 14:22:20   baa
 * cleanup code
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.0   19 Nov 2001 14:45:30   sfl
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

// foundation imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the item cargo.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SendCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4077646183533921800L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       item cargo
    **/
    protected ItemCargo iCargo = null;

    //----------------------------------------------------------------------
    /**
        Copies information needed from parent service to child service.
        @param  bus    parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent service
        iCargo = (ItemCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by child service. Copies the access employee
        and sales associate, sets the exit when offline flag for customer.
        @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the child - customer main/customer
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();

        // set access
        cargo.setOperator(iCargo.getOperator());

        // set register
        cargo.setRegister(iCargo.getRegister());

        // reset the database error code to UNKNOWN
        cargo.setDataExceptionErrorCode(DataException.UNKNOWN);

        // if customer db is offline, get out
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        cargo.setLinkDoneSwitch(CustomerCargo.LINK);

        // if transaction in progress pass over id
        if (iCargo.getTransaction() != null)
        {
            cargo.setTransactionID(iCargo.getTransaction().getTransactionID());
        }
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  SendCustomerLaunchShuttle (Revision " +
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
