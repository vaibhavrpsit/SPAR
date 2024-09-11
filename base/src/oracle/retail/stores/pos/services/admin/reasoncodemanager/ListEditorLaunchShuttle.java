/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/ListEditorLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:18  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/19 18:48:56  awilliam
 *   @scr 4374 Reason Code featrure work
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:39  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.listeditor.ListEditorCargo;

//------------------------------------------------------------------------------
/**
    The SecurityLaunchShuttle moves data from
    the Admin service to the Security service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ListEditorLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8646707506036983838L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.reasoncodemanager.ListEditorLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "ListEditorLaunchShuttle";

    /**
       Source cargo
    **/
    protected ReasonCodeCargo rcCargo = null;

    //--------------------------------------------------------------------------
    /**

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        rcCargo = (ReasonCodeCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        ListEditorCargo cargo = (ListEditorCargo) bus.getCargo();

        cargo.setReasonCodeGroup(rcCargo.getReasonCodeGroup());
        cargo.setReasonCodeScreenToDisplay(rcCargo.getReasonCodeScreenToDisplay());
    }


    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class: " + SHUTTLENAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}

