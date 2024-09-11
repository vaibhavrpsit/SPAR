/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/TillEnteredAisle.java /main/14 2013/08/05 17:25:19 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   08/01/13 - Making the till id to uppercase to have consistency
 *    rgour     10/05/12 - WPTG issues
 *    abhayg    09/02/10 - Fix for till reconcile issue.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/25/2008 2:06:25 PM   Mathews Kochummen
 *         forward port change from v12x to trunk
 *    4    360Commerce 1.3         1/7/2008 10:11:17 PM   Robinson Joseph
 *         Modified the code to trim the till id obtained from UI
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/07/01 15:57:37  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Code cleanup.
 *
 *   Revision 1.5  2004/06/03 14:47:42  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.4  2004/05/17 22:36:11  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add second argument to TillAccountabilityError.
 *
 *   Revision 1.3  2004/05/17 16:53:18  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Check for matching accountability when the till was closed at a different register.
 *
 *   Revision 1.2  2004/05/14 22:41:01  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add the till to the register when reconciling at a different register.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
     Validates the entered till ID and the status of the till. If the entered 
     till ID is not valid, the error type is set in the cargo, the till fatal 
     error flag is set if appropriate and a TillError letter is issued. 
     <P>
     Here is how the till validation proceeds: <P>
     <UL>
     <LI>If the till ID is greater than 5 alphanumeric characters or no till is
     found matching the entered till ID, a TillIDError is issued.
     <LI>If the till is not found for this register, a database operation is issued.
     Should a database error be encountered, a DatabaseError is issued. If no till 
     is found, a TillNotFoundError is issued.
     <LI>Under Cashier Accountabililty, if the till associated with this till 
     ID is not the cashier's till, a Cashier Error is issued.
     <LI>If the the till associated with this till ID is suspended, a
     TillSuspendedError letter is issued.
     <LI>If the till status is reconciled, a TillAlreadyReconciled error is issued.
     <LI>If the till status is open, a TillOpen letter is issued. The service then
     prompts the operator to close the till.
     <LI>If the current register accountability does not match the register 
     accountability on which the till was closed, a TillAccountabilityError error 
     is issued.
     </UL>
     If all the checks are passed, the till is added to the register if it does not 
     already belong. The operator is added to the till. The till and the
     till's id are set in the cargo. A Continue letter is issued and the system
     proceeds to the OpenDrawer site.
    <P>
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class TillEnteredAisle extends PosLaneActionAdapter
{                                                                               // begin class TillEnteredAisle
    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/14 $";
    /** Argument tag for Till Accountability Error dialog */
    //public static final String TILL_ACCOUNTABILITY_RECONCILED_TAG = "TillAccountabilityError.reconciled";
    /** Argument text for Till Accountability Error dialog */
    public static final String TILL_ACCOUNTABILITY_RECONCILED_TEXT = "reconciled";

    //--------------------------------------------------------------------------
    /**
       Ensures the the entered till ID is valid, this till is open, and
       the cashier is assigned to this till, then then proceeds by issuing the
       Continue letter.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Local reference to till cargo
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        
        String letterName = CommonLetterIfc.CONTINUE;
        boolean bOk = true;

        // initialize till
        cargo.setTill(null); 
        cargo.setTillID(null);       

        // Local reference to register
        RegisterIfc register = cargo.getRegister();

        // Get Input (till id) from UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String tillID = ui.getInput().trim().toUpperCase();

        // Is this a valid till ID
        if (DomainGateway.getFactory().getTillInstance().isIdValid(tillID) != true)
        {
            letterName = TillLetterIfc.TILL_ERROR;
            cargo.setErrorType(TillCargo.TILL_ID_INVALID_ERROR_TYPE);
            bOk = false;
        }

        // Local reference to entered till (if found)
        TillIfc till = null;
        if (bOk)
        { 
            till = register.getTillByID(tillID);
        }


        // Is there a till with this till ID?
        if (bOk)
        {
            if (till == null)
            {
             // Retrieve till from database if this till does not belong to current register
                try
                {
                    FinancialTotalsDataTransaction dt = null;
                    
                    dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
                    
                    till = dt.readTillStatus(register.getWorkstation().getStore(), tillID);
                }
                catch(DataException e)
                {
                   till = null;
                }
                if(till==null)
                {
                    letterName = TillLetterIfc.TILL_ERROR;
                    cargo.setErrorType(TillCargo.TILL_ID_INVALID_ERROR_TYPE);
                    bOk = false;
                }
               
            }
        }
    
        // If cashier accountability, check if the entered till was opened 
        // by the login operator
        if (bOk 
            && till.getRegisterAccountability() == AbstractStatusEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            if (!cargo.getOperator().getEmployeeID().equals(till.getSignOnOperator().getEmployeeID()))
            {    
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_CASHIER_ERROR_TYPE);
                bOk = false;
            }
        }
        
        // Is entered till suspended or reconciled?
        if (bOk)
        {
            if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_SUSPENDED)
            {
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_SUSPENDED_ERROR_TYPE);
                cargo.setTillFatalError();
                bOk = false;
            } 
            else if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
            {
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_ALREADY_RECONCILED_TYPE);
                cargo.setTillFatalError();
                bOk = false;
            }
        }
        
        
        if (bOk)
        {
            // Is entered till open?
            if (till.getStatus() == AbstractFinancialEntityIfc.STATUS_OPEN)
            {
                letterName = TillLetterIfc.TILL_OPEN;
            }
            else 
            {
                // Was the till closed on a different register?
                if (register.getTillByID(tillID) == null)
                {
                    int tillAccountFlag = AbstractFinancialEntityIfc.ACCOUNTABILITY_INVALID;
                    tillAccountFlag = till.getRegisterAccountability();
                    int regAccountFlag = register.getAccountability();

                    // Current register accountability must match the register 
                    // accountability on which the till was closed.
                    if (tillAccountFlag == regAccountFlag)
                    {
                        // Add till to register
                        register.addTill(till);
                        till = register.getTillByID(tillID);
                        // Add cashier to till
                        till.addCashier(cargo.getOperator());
                    }
                    else
                    {    
                        letterName = TillLetterIfc.TILL_ERROR;;
                        cargo.setErrorType(TillCargo.TILL_ACCOUNTABILITY_ERROR_TYPE);
                        cargo.setTillFatalError();
                        bOk = false;
                        // set args for error message
                        String[] errorScreenArgs = new String[1];
                        errorScreenArgs[0] = tillID;
                        cargo.setErrorScreenArgs(errorScreenArgs);
                    }
                }
            }            
        }        

        // If everything ok, set till and tillID in cargo
        if (bOk)
        {           
            cargo.setTill(till);
            cargo.setTillID(tillID);
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}                                                                               // end class TillEnteredAisle
