/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/TillReconcileCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
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
 *    4    360Commerce 1.3         5/2/2008 6:14:35 AM    Manas Sahu      When
 *         parameter is set to detail and the float is not enetered by the
 *         operator in that case the Audit Log has to report the summary.
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/12 16:47:12  dcobb
 *   @scr 6093 Till Options: Drawer opens twice.
 *   @scr 6072 Till Options Reusing transaction number
 *   Added getRegister() method to TillReconcile cargo to get the local register and
 *   added setLocalRegister(register) method to save the local register upon return from tillclose station.
 *
 *   Revision 1.4  2004/07/08 16:43:12  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   tillreconcile needs updated sequence number upon return from tillclose.
 *
 *   Revision 1.3  2004/06/30 02:20:36  mweis
 *   @scr 5421 Remove unused imports (for eclipse)
 *
 *   Revision 1.2  2004/06/30 00:21:24  dcobb
 *   @scr 5165 - Allowed to reconcile till when database is offline.
 *   @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;

//------------------------------------------------------------------------------
/**
    Till Reconcile Cargo

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillReconcileCargo extends TillCloseCargo
{
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** This sequence number is set upon returning from tillclose */
    int nextTransactionSequenceNumber = -1;
    
    //----------------------------------------------------------------------
    /**
        Determines if we need to clone the Register due to user cancelling the
        float/till counts. <P>
        @return The localRegister clone of parent Register
    **/
    //----------------------------------------------------------------------
    public RegisterIfc getRegister()
    {
        if (localRegister == null)
        {
           localRegister = (RegisterIfc)super.getRegister().clone();
        }

        return(localRegister);
     }
    
    //----------------------------------------------------------------------
    /**
        Sets the local Register upon return from TillClose station.
        @param value  The localRegister clone of parent Register
    **/
    //----------------------------------------------------------------------
    public void setLocalRegister(RegisterIfc value)
    {
        localRegister = value;
     }
    
    protected FinancialTotalsIfc financialTotal;
    
    public void setFinancialTotals(FinancialTotalsIfc financialTotal)
    {
    	this.financialTotal = financialTotal;
    }
    
    public FinancialTotalsIfc getFinancialTotals()
    {
    	return financialTotal;
    }
}
