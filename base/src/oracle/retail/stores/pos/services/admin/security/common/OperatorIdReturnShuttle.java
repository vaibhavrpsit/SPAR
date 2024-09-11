/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/OperatorIdReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 * 4    360Commerce 1.3         1/25/2006 4:11:33 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/15/2005 14:57:27    Jason L. DeLeau 4204:
 *      Remove duplicate instances of UserAccessCargoIfc
 * 3    360Commerce1.2         3/31/2005 15:29:12     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:23:49     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:12:51     Robert Pearse
 *
 *Revision 1.5  2004/09/23 00:07:15  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.4  2004/04/09 16:55:59  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:49:02  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:37:44  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:37:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:07:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:40   msg
 * Initial revision.
 *
 *    Rev 1.1   22 Oct 2001 16:59:44   pdd
 * Added SCR association.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   22 Oct 2001 15:00:10   pdd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

// Foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the OperatorId service to the calling service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class OperatorIdReturnShuttle implements ShuttleIfc
{                                       // begin class OperatorIdReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4377447266918471519L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.security.common.OperatorIdReturnShuttle.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "OperatorIdReturnShuttle";
    /**
        Child service cargo
    **/
    protected OperatorIdCargo opIDCargo = null;

    //--------------------------------------------------------------------------
    /**
       Gets the cargo from the Operator ID service into the shuttle.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        opIDCargo = (OperatorIdCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
        Unloads the data from the shuttle to the calling service's cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        UserAccessCargoIfc callingCargo = (UserAccessCargoIfc) bus.getCargo();
        callingCargo.setOperator(opIDCargo.getSelectedEmployee());
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class OperatorIdReturnShuttle
