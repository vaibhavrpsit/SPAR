/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/TillCloseLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   23 Jan 2002 08:58:40   baa
 * Initial revision.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:19:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;

//------------------------------------------------------------------------------
/**
    Transfers data from TillOptions service to TillClose service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillCloseLaunchShuttle extends FinancialCargoShuttle
{                                       // begin class TillCloseLaunchShuttle
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCloseLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       shuttle name constant
    **/
    public static final String SHUTTLENAME = "TillCloseLaunchShuttle";

    //--------------------------------------------------------------------------
    /**
       Loads data from the TillClose service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
       super.load(bus);
    }                                   // end load()

    //--------------------------------------------------------------------------
    /**
       Loads data to the TillOptions service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        super.unload(bus);

        // set function in cargo
        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.CLOSE_TILL);
    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class TillCloseLaunchShuttle
