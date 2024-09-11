/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/ListEditorLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:04:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Jan 2002 14:40:28   KAC
 * Now shuttles to the new parametervaluelisteditor service.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * 
 *    Rev 1.0   Sep 21 2001 11:11:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.parametervaluelisteditor.ListEditorCargo;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;

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
    static final long serialVersionUID = 6943229215372238361L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.parametermanager.ListEditorLaunchShuttle.class);

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
    protected ParameterCargo pCargo = null;

    //--------------------------------------------------------------------------
    /**

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        pCargo = (ParameterCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo) bus.getCargo();
        ReasonCodeGroupBeanModel model = pCargo.getReasonCodeGroupBeanModel();
        model.setParameterGroup(pCargo.getParameterGroup());
        cargo.setReasonCodeGroup(model);
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

