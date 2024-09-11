/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/TillReconcileReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   01/29/10 - add method syncTransactionSequence
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:15 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 * Revision 1.2.2.1  2004/11/22 20:12:12  rsachdeva
 * @scr 7761 Till Open after Reconcile
 *
 * Revision 1.2  2004/06/30 02:20:36  mweis
 * @scr 5421 Remove unused imports (for eclipse)
 *
 * Revision 1.1  2004/06/30 00:21:24  dcobb
 * @scr 5165 - Allowed to reconcile till when database is offline.
 * @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 * Revision 1.1  2004/04/15 18:57:00  dcobb
 * @scr 4205 Feature Enhancement: Till Options
 * Till reconcile service is now separate from till close.
 *
 * Revision 1.1  2004/04/12 18:39:21  dcobb
 * @scr 4205 Feature Enhancement: Till Options
 * Moving remove till prompt to dailyoperations/common package.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;

/**
 * This service copies any needed information from the cargo used in one service
 * to another service.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TillReconcileReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 8140902936517251570L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TillReconcileReturnShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The calling service's cargo.
     */
    protected TillReconcileCargo returnCargo = null;

    /**
     * Copies information from the cargo used in the calling service.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        // execute FinancialCargoShuttle class load
        super.load(bus);
        returnCargo = (TillReconcileCargo)bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        // execute FinancialCargoShuttle class unload
        super.unload(bus);

        // get cargo reference and set attributes
        TillCloseCargo cargo = (TillCloseCargo)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        RegisterIfc returnRegister = returnCargo.getRegister();
        register.setSequenceNumbers(returnRegister);

        // update the till status in register's till
        String tillID = returnCargo.getTillID();
        int status = returnRegister.getTillByID(tillID).getStatus();
        TillIfc till = register.getTillByID(tillID);
        till.setStatus(status);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class:  TillReconcileLaunchShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
