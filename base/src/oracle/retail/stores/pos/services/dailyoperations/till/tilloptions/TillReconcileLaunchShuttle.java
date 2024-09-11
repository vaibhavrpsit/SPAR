/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/TillReconcileLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;

//------------------------------------------------------------------------------
/**
    Transfers data from TillOptions service to TillReconcile service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillReconcileLaunchShuttle extends FinancialCargoShuttle
{                                       // begin class TillReconcileLaunchShuttle
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
    public static final String SHUTTLENAME = "TillReconcileLaunchShuttle";

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
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        cargo.setRequestedService(RoleFunctionIfc.RECONCILE_TILL);
        cargo.setAccessFunctionID(RoleFunctionIfc.RECONCILE_TILL);
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

}                                       // end class TillReconcileLaunchShuttle
