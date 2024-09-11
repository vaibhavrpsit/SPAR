/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/TillEnteredAisle.java /main/13 2013/08/05 17:25:18 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   08/01/13 - Making the till id to uppercase to have consistency
 *    rgour     10/05/12 - WPTG issues
 *    rabhawsa  06/25/12 - wptg - merged keys TILL_OPEN_ERROR_TAG_LINE1 and 2
 *                         in property file
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/7/2008 10:04:24 PM   Robinson Joseph
 *         Modified the code to trim the till id obtained from UI.
 *    4    360Commerce 1.3         7/27/2007 1:14:16 PM   Michael P. Barnett If
 *          database is offline, read till from the hardtotals rather than
 *         display an error.
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.9  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/05/17 22:36:11  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add second argument to TillAccountabilityError.
 *
 *   Revision 1.7  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.6  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/04/12 17:37:20  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Corrected indentation.
 *
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.7   May 05 2003 14:51:06   RSachdeva
 * Till Id Comparison Check
 * Resolution for POS SCR-2271: POS is crashed at till reconcile in below case.
 * 
 *    Rev 1.6   May 02 2003 15:42:44   RSachdeva
 * Check if Drawer is unoccupied. If occupied, check if Entered Till matches till occupying the drawer.
 * Resolution for POS SCR-2253: Till Resume missing Till ID prompt when suspended till in drawer
 * 
 *    Rev 1.5   Mar 10 2003 17:10:08   DCobb
 * Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.4   Feb 18 2003 14:51:34   DCobb
 * Initialize tillAccountFlag to ACCOUNTABILITY_INVALID.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.3   Feb 17 2003 15:43:30   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.2   Feb 12 2003 18:46:02   DCobb
 * Moved register and till modifications to UpdateStatusSite.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Jan 06 2003 12:13:04   DCobb
 * Set the register's current till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Dec 20 2002 11:25:36   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
    This aisle is traversed when the register has no suspended tills for this
    cashier and the user has entered the ID of the till to be resumed.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class TillEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7892370212506273003L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /** Argument tag for Till Accountability Error dialog */
    //public static final String TILL_ACCOUNTABILITY_RESUMED_TAG = "TillAccountabilityError.resumed";
    /** Argument text for Till Accountability Error dialog */
    public static final String TILL_ACCOUNTABILITY_RESUMED_TEXT = "resumed";

    //--------------------------------------------------------------------------
    /**
       The till ID has been entered by the user.
       CHECK 1: Is this a valid till ID?
       CHECK 2: Is there a till with this till ID?
       CHECK 3: Is entered till suspended?
       CHECK 4: If entered till is not floating, it must belong to the
       current register.
       CHECK 5: Check if the current register accountability matches the
       register accountability on which the till was opened.
       CHECK 6: Under Cashier accountability, check if the regsiter cashier is
       same as the till owner.
       CHECK 7: Check if Drawer is unoccupied
       If all checks are cleared, the till is set in the cargo.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TillIfc till = null;
        boolean bOk = true;
        String letterName = CommonLetterIfc.CONTINUE;

        TillResumeCargo cargo = (TillResumeCargo) bus.getCargo();
        cargo.setErrorType(TillCargo.TILL_NO_ERROR_TYPE);

        RegisterIfc register = (RegisterIfc) cargo.getRegister();

        // Get till id from UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String tillId = ui.getInput().trim().toUpperCase();

        // CHECK 1: Is this a valid till ID?
        if (DomainGateway.getFactory().getTillInstance().isIdValid(tillId) != true)
        {
            letterName = "Error";
            cargo.setErrorType(TillCargo.TILL_ID_INVALID_ERROR_TYPE);
            cargo.setTillFatalError();
            bOk = false;
        }

        // Retrieve till from database
        if (bOk)
        {
            try
            {
                FinancialTotalsDataTransaction dt = null;
                
                dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
                
                till = dt.readTillWithTotals(register.getWorkstation().getStore(),
                                             tillId,
                                             register.getBusinessDate());
            }
            catch (DataException e)
            {
              
                //get till from register
                till = register.getTillByID(tillId);
/*              
                letterName = "Error";

                cargo.setTillFatalError();
                bOk = false;
                if (e.getErrorCode() == DataException.NO_DATA)
                {
                    cargo.setErrorType(TillCargo.TILL_NOT_FOUND_ERROR_TYPE);
                }
                else
                {
                    cargo.setErrorType(TillCargo.TILL_DATABASE_ERROR_TYPE);
                }
*/                
                
            }
        }


        // CHECK 2: Is there a till with this till ID?
        if (bOk)
        {
            if (till == null)
            {
                letterName = "Error";
                cargo.setErrorType(TillCargo.TILL_NOT_FOUND_ERROR_TYPE);
                cargo.setTillFatalError();
                bOk = false;
            }
        }

        // CHECK 3: Is entered till suspended?
        if (bOk)
        {
            if (till.getStatus() != AbstractFinancialEntityIfc.STATUS_SUSPENDED)
            {
                letterName = "Error";
                cargo.setErrorType(TillCargo.TILL_NOT_SUSPENDED_ERROR_TYPE);
                cargo.setTillFatalError();
                bOk = false;
            }
        }

        

        
        // CHECK 4: If entered till is not floating, it must belong to the
        // current register.
        if (bOk)
        {
            if (till.getTillType() != AbstractFinancialEntityIfc.TILL_TYPE_FLOATING)
            {
                TillIfc[] tills = register.getTills();
                boolean match = false;
                for (int i = 0; i < tills.length; i++)
                {
                    if (tills[i].getTillID().equalsIgnoreCase(tillId))
                    {
                        match = true;
                        break;
                    }
                }
                if (!match)
                {
                    letterName = "Error";
                    cargo.setErrorType(TillCargo.TILL_NOT_FLOATING_ERROR_TYPE);
                    bOk = false;
                }
            }
        }

        // CHECK 5: Current register accountability must match
        // the register accountability on which the till was opened.
        int tillAccountFlag = AbstractFinancialEntityIfc.ACCOUNTABILITY_INVALID;
        if (bOk)
        {
            tillAccountFlag = till.getRegisterAccountability();
            int regAccountFlag = register.getAccountability();
            if (tillAccountFlag != regAccountFlag)
            {
                letterName = "Error";
                cargo.setErrorType(TillCargo.TILL_ACCOUNTABILITY_ERROR_TYPE);
                cargo.setTillFatalError();
                bOk = false;
                // set args for error message
                String[] errorScreenArgs = new String[1];
                errorScreenArgs[0] = tillId;
                cargo.setErrorScreenArgs(errorScreenArgs);
            }
        }

        // CHECK 6: Under cashier accountability, check if the regsiter cashier
        // is same as the till owner.
        if (bOk)
            {
                if (tillAccountFlag == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
                {
                    EmployeeIfc cashier = cargo.getOperator();
                    EmployeeIfc tillOwner = till.getSignOnOperator();
                    String cashierEmployeeID = cashier.getEmployeeID();
                    String tillOwnerEmployeeID = tillOwner.getEmployeeID();

                    if (!tillOwnerEmployeeID.equals(cashierEmployeeID))
                    {
                        letterName = "Error";
                        cargo.setErrorType(TillCargo.TILL_CASHIER_ERROR_TYPE);
                        cargo.setTillFatalError();
                        bOk = false;
                    }
            }
        }
        
        //CHECK 7: Check if Drawer is unoccupied
        if (bOk)
        {
            int drawerStatus = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus();
            if (drawerStatus == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED)
            {
                String occupyingTillID = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getOccupyingTillID();
                if (!occupyingTillID.equalsIgnoreCase(tillId))
                {
                    UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                    int tillStatus = register.getTillByID(occupyingTillID).getStatus();
                    String argsForErrorScreen[] = errorScreenArgs(tillStatus,
                                                                  utility);
                    cargo.setErrorScreenArgs(argsForErrorScreen);
                    cargo.setErrorType(TillCargo.TILL_DRAWER_ERROR_TYPE);
                    letterName = "Error";
                    cargo.setTillFatalError();
                    bOk = false;
                }
            }
        }   
        
        // If everything ok so far, add till to cargo.
        if (bOk)
        {
            cargo.setTillID(tillId);
            cargo.setTill(till);
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    //---------------------------------------------------------------------
    /**
       Error Screen Args for No Cash Drawer Dialog Box <P>
       @param tillStatus Till Status
       @param utility UtilityManager Reference 
       @return Array of String arguments for error screen
    **/
    //---------------------------------------------------------------------
    public String[] errorScreenArgs(int tillStatus,
                                    UtilityManagerIfc utility) 
    { 
        String[] args = new String[1];
        // occupying till ID will either be open or suspended
        if (tillStatus == AbstractStatusEntityIfc.STATUS_OPEN)
        {
            args[0] = utility.retrieveDialogText(TillCargo.TILL_OPEN_ERROR_TAG_LINE, TillCargo.TILL_OPEN_ERROR_LINE);
        }
        else
        // suspended
        {
            args[0] = utility.retrieveDialogText(TillCargo.TILL_SUSPENDED_ERROR_TAG_LINE,
                    TillCargo.TILL_SUSPENDED_ERROR_LINE);
        }
        return args;
    }
}
