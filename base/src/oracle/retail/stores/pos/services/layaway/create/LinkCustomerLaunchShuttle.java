/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/LinkCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 29 2003 16:09:54   baa
 * enable customer add on create layaway
 * 
 *    Rev 1.1   May 27 2003 08:48:14   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:34:34   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:20:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create;

// foundation imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the layaway cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkCustomerLaunchShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 366594756117267848L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
        /**
            layaway cargo
    **/
    LayawayCargo lCargo = null;

    //----------------------------------------------------------------------
    /**
        Copies information needed from parent service to child service.
        <P>
        @param  bus    parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent service
        lCargo = (LayawayCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by child service. Copies the access employee
        and sales associate, sets the exit when offline flag for customer.
        <P>
        @param  burrrs     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the child - customer main/customer
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        //CustomerCargo custCargo = (CustomerCargo)bus.getCargo();

        // set access
        cargo.setOperator(lCargo.getOperator());
        // set sales associate to be used later...
        cargo.setSalesAssociate(lCargo.getSalesAssociate());
        // reset the database error code to UNKNOWN
        cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
        // if customer db is offline, get out
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        cargo.setLinkDoneSwitch(CustomerCargo.LINK);
        cargo.setFindOnlyMode(false);
        // pass register to retrieve training mode
        cargo.setRegister(lCargo.getRegister());
        // if salereturn transaction in progress pass over id
        if (lCargo.getSaleTransaction() != null)
        {
            cargo.setTransactionID(lCargo.getSaleTransaction().getTransactionID());
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
