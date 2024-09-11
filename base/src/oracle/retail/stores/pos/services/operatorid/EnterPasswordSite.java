/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EnterPasswordSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:02 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 24 2003 12:10:32   bwf
 * Depart and set employee to null if undo.
 * Resolution for 2892: Unable to log in to POS if ESC is used during the Operator ID flow
 * 
 *    Rev 1.0   Apr 29 2002 15:13:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the PASSWORD Screen. <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterPasswordSite extends PosSiteActionAdapter
{
    
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the PASSWORD Screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.OPERATOR_PASSWORD, new POSBaseBeanModel());

    }

    //----------------------------------------------------------------------
    /**
       Gets rid of employee if undo
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        String tempLetter = "Undo";
        if(bus.getCurrentLetter().getName().equals(tempLetter))
        {
            OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
            cargo.setSelectedEmployee(null);
        }   
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
        String strResult = new String("Class:  EnterPasswordSite (Revision " +
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
        EnterPasswordSite obj = new EnterPasswordSite();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
