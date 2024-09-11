/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/IsCurrentRoleNotCustomerAddFindSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:33:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   18 Feb 2002 11:41:50   baa
 * fix signals for preferred customer check
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   23 Jan 2002 12:46:40   baa
 * Initial revision.
 * Resolution for POS SCR-816: Selecting 'No' on Security Error for PCD returns to wrong screen during Customer Add
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal determines if the current role is Customer add find
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsCurrentRoleNotCustomerAddFindSignal implements TrafficLightIfc

{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3670745277739011088L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Signal name for toString
    **/
    public static final String SIGNALNAME = "IsCurrentRoleNotCustomerAddFindSignal";

    //----------------------------------------------------------------------
    /**
        Determines whether it is safe for the bus to proceed.
        <p>
        @param bus the bus trying to proceed
        @return true if not Customer add find; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        boolean flag = true;

        if (cargo.getAccessFunctionID() == RoleFunctionIfc.CUSTOMER_ADD_FIND)
        {
          flag = false;
        }

        return flag;
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + SIGNALNAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()
}
