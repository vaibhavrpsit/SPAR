/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CashDrawerOnlineAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the cash drawer responds as online.

     <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CashDrawerOnlineAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Set statuses and mails a letter.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Update cash drawer status
        ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS,
                         POSUIManagerIfc.ONLINE);

        CashDrawerCargoIfc cargo = (CashDrawerCargoIfc)bus.getCargo();
        cargo.setCashDrawerOnline(true);
        bus.mail(new Letter("CashDrawerOnlineComplete"), BusIfc.CURRENT);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  CashDrawerOnlineAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

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

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        CashDrawerOnlineAisle obj = new CashDrawerOnlineAisle();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
