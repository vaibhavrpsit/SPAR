/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/TillCloseReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 * Revision 1.6  2004/07/12 16:47:12  dcobb
 * @scr 6093 Till Options: Drawer opens twice.
 * @scr 6072 Till Options Reusing transaction number
 * Added getRegister() method to TillReconcile cargo to get the local register and
 * added setLocalRegister(register) method to save the local register upon return from tillclose station.
 *
 * Revision 1.5  2004/07/12 13:55:21  jriggins
 * @scr 5421 Removed unused imports which were causing local build errors under our accepted Eclipse settings
 *
 * Revision 1.4  2004/07/09 23:27:01  dcobb
 * @scr 5190 Crash on Pickup Canadian Checks
 * @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 * Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 * Revision 1.3  2004/07/08 16:43:12  dcobb
 * @scr 4205 Feature Enhancement: Till Options
 * tillreconcile needs updated sequence number upon return from tillclose.
 *
 * Revision 1.2  2004/06/30 18:17:59  dcobb
 * @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 * Revision 1.1  2004/06/30 00:21:24  dcobb
 * @scr 5165 - Allowed to reconcile till when database is offline.
 * @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;

//--------------------------------------------------------------------------
/**
    This service copies any needed information from the cargo used
    in one service to another service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TillCloseReturnShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillCloseReturnShuttle.class);
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     The calling service's cargo.
    **/
    protected TillCloseCargo returnCargo = null;

    //--------------------------------------------------------------------------
    /**
     Copies information from the cargo used in the called service. <P>
     @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // execute FinancialCargoShuttle class load
        super.load(bus);
        returnCargo = (TillCloseCargo) bus.getCargo();
    }
  
    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the service. <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // execute FinancialCargoShuttle class unload
        super.unload(bus);

        // get cargo reference and set attributes
        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        RegisterIfc returnRegister = returnCargo.getRegister();           
        cargo.setLocalRegister(returnRegister);        
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  TillCloseReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
