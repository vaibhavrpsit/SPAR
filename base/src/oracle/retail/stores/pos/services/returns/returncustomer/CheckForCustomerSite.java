/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/CheckForCustomerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
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
 *    Rev 1.0   Apr 29 2002 15:06:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:24:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    Checks to see if there is already a customer in the cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckForCustomerSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * ReadTrans bundle tag
     */
    public static final String READ_TRANS = "ReadTrans";
    
    /**
     * FindCustomer bundle tag
     */
    public static final String FIND_CUST  = "FindCustomer";
    //----------------------------------------------------------------------
    /**
       Checks to see if there is already a customer in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        Letter letter = new Letter(READ_TRANS);
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();

        if (cargo.getCustomer() == null)
        {
            letter = new Letter(FIND_CUST);
            cargo.setStartedWithCustomer(false);
        }

        bus.mail(letter, BusIfc.CURRENT);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CheckForCustomerSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

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
}
