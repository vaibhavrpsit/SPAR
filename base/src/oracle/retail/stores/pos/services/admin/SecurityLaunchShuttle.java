/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/SecurityLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:15  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 19 2003 16:22:46   crain
 * Remove deprecated calls.
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:36:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Jan 2002 17:23:18   baa
 * set operator
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:10:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.security.SecurityCargo;

//------------------------------------------------------------------------------
/**
    The SecurityLaunchShuttle moves data from the Admin service to the Security service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SecurityLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1093734249969202405L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.SecurityLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       class name constant.
    **/
    public static final String SHUTTLENAME = "SecurityLaunchShuttle";

    /**
       source cargo.
    **/
    protected AdminCargo aCargo = null;

    //--------------------------------------------------------------------------
    /**

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        aCargo = (AdminCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        SecurityCargo cargo = (SecurityCargo) bus.getCargo();

        cargo.setRegister(aCargo.getRegister());
        cargo.setOperator(aCargo.getOperator());
        cargo.setStoreStatus(aCargo.getStoreStatus());

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
        String strResult = new String("Class:  SecurityLaunchShuttle (Revision " +
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

