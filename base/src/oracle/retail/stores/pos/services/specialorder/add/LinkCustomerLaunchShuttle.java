/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/LinkCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:52:01  mcs
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
 *    Rev 1.0   Aug 29 2003 16:07:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 19 2003 13:41:02   RSachdeva
 * setOfflineIndicator  OFFLINE_EXIT
 * Resolution for POS SCR-2722: DB offline, try to creating new sp order, POS hangs up.
 * 
 *    Rev 1.1   May 27 2003 12:28:50   baa
 * rework offline flow for customer
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 * 
 *    Rev 1.0   Apr 29 2002 15:02:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Dec 06 2001 17:26:26   dfh
 * updates to prepare for security override, cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 14:55:12   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

// foundation imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the special order cargo.
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
    static final long serialVersionUID = 8726461900194986465L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        SpecialOrderCargo cargo
    **/
    SpecialOrderCargo SCargo = null;

    //----------------------------------------------------------------------
    /**
        Copies information needed from parent service to child service.
        <P>
        @param  bus    parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent service - special order add
        SCargo = (SpecialOrderCargo)bus.getCargo();
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
        CustomerCargo custCargo = (CustomerCargo)bus.getCargo();

        // set access
         cargo.setOperator(SCargo.getOperator());
        // set sales associate to be used later...
        cargo.setSalesAssociate(SCargo.getSalesAssociate());
                // reset the database error code to UNKNOWN
        cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
        // if customer db is offline, get out
        custCargo.setOfflineIndicator(CustomerCargo.OFFLINE_EXIT);
        // pass along the register
        custCargo.setRegister(SCargo.getRegister());
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
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
