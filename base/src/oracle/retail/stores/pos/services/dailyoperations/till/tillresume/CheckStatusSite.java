/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/CheckStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/13 19:02:59  kll
 *   @scr 7027: fix furnished by the PepBoys initiative
 *
 *   Revision 1.5  2004/07/14 18:52:52  dcobb
 *   @scr 6014 Sale: POS not prompting to insert till
 *   Modified tillsuspend to not prompt to remove till under register accountability.
 *
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:07  mcs
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
 *    Rev 1.0   Aug 29 2003 15:58:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 02 2003 15:21:16   RSachdeva
 * Missing Till ID prompt when suspended till in drawer
 * Resolution for POS SCR-2253: Till Resume missing Till ID prompt when suspended till in drawer
 * 
 *    Rev 1.2   Feb 12 2003 18:44:52   DCobb
 * Rework  for floating till not the first till opened on a 
 * register and to match flow in functional spec.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Dec 20 2002 11:32:06   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Apr 29 2002 15:25:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:54   msg
 * Initial revision.
 *
 *    Rev 1.3   30 Jan 2002 16:17:34   epd
 * fixed minor accountability issue
 * Resolution for POS SCR-949: Till Options - user attempts to resume a till that is not suspended
 *
 *    Rev 1.2   03 Dec 2001 16:18:08   epd
 * Update drawer ID to use constant value as defined in DrawerIfc
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   12 Nov 2001 09:59:16   epd
 * Updated so that you cannot resume a till if there is a till in the drawer that is not yours.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:20:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;
// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


//------------------------------------------------------------------------------
/**
     Checks pre-conditions for till resume procedures.  If one of the
     pre-conditions is not met, the appropriate letter is issued, and
     the service exits to the TillOperations screen.<P>
     Here is how the status checks proceed: <P>
     <UL>
     <LI>If the register is not open, a RegisterClosedError letter is issued.
     <LI>If there is a current till in the register and it is not suspended, a
         TillNotSuspendedError letter is issued.
     <LI>Under cashier accountability, If there is a suspended till in the drawer 
         that does not belong to this operator, a CashierError letter is issued.
     <LI>If there is a suspended till in the drawer that belongs to this
         operator, a Success letter is issued and operation proceeds to the
         UpdateStatus site.
     <LI>If there is no till in the drawer, a TillId letter is issued and operation
         proceeds to the EnterTill site.
     </UL>
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckStatusSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       CheckStatusSite
    **/
    //--------------------------------------------------------------------------
    public static final String SITENAME = "CheckStatusSite";

    //--------------------------------------------------------------------------
    /**
       Ensures the register is open. If there is a till in the drawer, ensures
       that it is suspended and a TillId letter is issued to prompt for Till Id. 
       Otherwise if no error has been encountered,proceeds by issuing the Continue letter.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // Status flags
        boolean bOk = true;
        boolean continueChecking = true;
        boolean stationaryTill = true;

        // Set default letter
        String letterName = "TillId";

        // Local reference to cargo
        TillResumeCargo cargo = (TillResumeCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = cargo.getRegister();

        // Local reference to login cashier
        EmployeeIfc cashier = cargo.getOperator();

        // Local reference to till
        TillIfc till = null;

        // Accountability flag
        int accountFlag = register.getAccountability();

        // CHECK 1: Check if the register is open.
        if (register.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            letterName = "RegisterClosedError";
            cargo.setTillFatalError();
            bOk = false;
            continueChecking = false;
        }
        
        if (continueChecking)
        {
            till = register.getCurrentTill();
            if (till != null)
            {
                // CHECK 2: There is a current till; 
                // it must be suspended.
                EmployeeIfc[] cashiers = till.getCashiers();

                boolean tillBelongsToThisCashier = false;
                for (int i = 0; i < cashiers.length; i++)
                {
                    if (cashiers[i].getEmployeeID().equals(cashier.getEmployeeID()))
                    {
                        tillBelongsToThisCashier = true;
                    }
                }

                if (till.getStatus() != AbstractFinancialEntityIfc.STATUS_SUSPENDED)
                {
                    if (tillBelongsToThisCashier)
                    {
                    letterName = "TillNotSuspendedError";
                    }
                    else
                    {
                        letterName = "NoCashDrawersAvailable";
                    }

                    cargo.setTillFatalError();
                    bOk = false;
                    continueChecking = false;
                }
            }
        }

        if (continueChecking)
        {
            if (till != null)
            {
                if (till.getTillType() == AbstractStatusEntityIfc.TILL_TYPE_STATIONARY)
                {
                    if (till.getRegisterAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
                    {
                        // CHECK 3: There is a current till; it must belong to this operator 
                        continueChecking = false;
                        EmployeeIfc listCashier[] = till.getCashiers();
                        boolean matchFound = false;
                        for (int j = 0; j < listCashier.length; j++)
                        {
                            if (listCashier[j].getEmployeeID().equals(cashier.getEmployeeID()))
                            {
                                matchFound = true;
                                // Add the till to be resumed to the cargo
                                letterName = CommonLetterIfc.SUCCESS;
                                cargo.setTill(till);
                                cargo.setTillID(till.getTillID());
                            }
                        }
    
                        if (!matchFound)
                        {
                            letterName = "CashierError";
                            cargo.setTillFatalError();
                            bOk = false;
                        }
                    }
                }
                else
                {
                    // current till is floating ...
                    letterName = "TillId";
                    stationaryTill = false;
                    continueChecking = false;
                }
            }   
            else
            {
                letterName = "TillId";
                stationaryTill = false;
                continueChecking = false;
            }
        }

        if (bOk == true && stationaryTill == true)
        {
            if ((register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus() ==
                 AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED))
            {
                letterName = "TillId";
            }

            // Add the till to be resumed, if found, to the cargo.
            cargo.setTill(till);
            cargo.setTillID(till.getTillID());
        }
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
