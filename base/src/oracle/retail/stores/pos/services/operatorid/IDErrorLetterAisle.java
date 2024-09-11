/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/IDErrorLetterAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 15:13:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

// java imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This aisle mails the iderror letter.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IDErrorLetterAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sends a id error letter.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        bus.mail(new Letter("IDError"), BusIfc.CURRENT);

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
        // result string
        String strResult = new String("Class:  IDErrorLetterAisle (Revision " +
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
        IDErrorLetterAisle obj = new IDErrorLetterAisle();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
