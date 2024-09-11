/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/OtherNumberRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Returns flow refactor: deprected obsolete class.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:55 PM  Robert Pearse   
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
 *    Rev 1.1   Aug 15 2002 15:09:56   jriggins
 * Replaced hardcoded text by pulling the text from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:05:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// java imports

// domain imports

// pos imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;


//--------------------------------------------------------------------------
/**
    This road sets the number type text to "Other Number".
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated in 13.3 no longer used
**/
//--------------------------------------------------------------------------
public class OtherNumberRoad extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       Other number bundle tag
    **/
    public static final String OTHER_NUMBER_TAG = "OtherNumber";
    /**
       Other number default text
    **/
    public static final String OTHER_NUMBER_TEXT = "other";

    //----------------------------------------------------------------------
    /**
       This road sets the number type text to "Other Number".
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();

        UtilityManagerIfc utility = 
          (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String otherNumberStr = 
          utility.retrieveText("ReturnItemInfoSpec",
                               BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                               OTHER_NUMBER_TAG,
                               OTHER_NUMBER_TEXT);
                               
        cargo.setNumberTypeText(otherNumberStr);

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
        String strResult = new String("Class:  OtherNumberRoad (Revision " +
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
        OtherNumberRoad obj = new OtherNumberRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
