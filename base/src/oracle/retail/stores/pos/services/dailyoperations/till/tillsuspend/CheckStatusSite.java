/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillsuspend/CheckStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    dwfung    12/15/09 - Avoid a NPE when there is no till.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/25/2006 4:10:52 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:40 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *:
 *    5    .v710     1.2.2.0     10/25/2005 15:32:47    Murali Vasaudevan As a
 *         part 7.0.2 code merge this defect is fixed now Till suspended
 *         message is now displayed for both Accountablity=Register & Cashier
 *    4    .v700     1.2.1.0     9/9/2005 14:46:40      Rohit Sachdeva  For
 *         Register Accountability check added if till has been already
 *         suspended
 *    3    360Commerce1.2         3/31/2005 15:27:26     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:13     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:59     Robert Pearse
 *
 *   Revision 1.9  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.8  2004/09/20 20:50:39  kll
 *   @scr 7071: fix comment
 *
 *   Revision 1.7  2004/09/20 20:42:33  kll
 *   @scr 7071: if accountability is to the register, any User should be able to suspend a till
 *
 *   Revision 1.6  2004/07/20 15:47:24  dcobb
 *   @scr 6352 NonGap - Can suspend a till twice - Crashes if attempt to suspend a second time
 *
 *   Revision 1.5  2004/07/14 18:52:52  dcobb
 *   @scr 6014 Sale: POS not prompting to insert till
 *   Modified tillsuspend to not prompt to remove till under register accountability.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:03  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 17 2003 15:43:30   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   Feb 12 2003 18:49:06   DCobb
 * Replaced getTillByCashierID w/ getOpenTillByCashierID() and  getSuspendedTillByCashierID().
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Apr 29 2002 15:25:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:10   msg
 * Initial revision.
 *
 *    Rev 1.1   05 Nov 2001 17:00:58   epd
 * removed operator ID site from suspend service
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:20:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillsuspend;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityConstantsIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


//------------------------------------------------------------------------------
/**
    Checks pre-conditions for till suspend procedures.  If one of the
    pre-conditions is not met, the appropriate letter is issued, and
    the service exits to the TillOperations screen.<P>
    Here is how the status checks proceed: <P>
     <UL>
     <LI>If the register is not open, a RegisterClosedError letter is issued.
     <LI>If there are no tills for this register, a NoTillsOpenError is issued.
     <LI>If there is no open or suspended till in the drawer that belongs to
         this operator, a CashierError letter is issued.
     <LI>If there is a suspended till in the drawer that belongs to this
         operator, a TillAlreadySuspendedError is issued.
     </UL>
    If all the checks are passed, a Continue letter is issued and operation
    proceeds to the PromptRemoveTill site.<P>

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckStatusSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6600300928961477269L;


    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    //--------------------------------------------------------------------------
    /**
       Ensures the register is open and there is a till in the drawer that
       is open and belongs to the current operator. If no error is
       encountered, proceeds by issuing the Continue letter.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        // Status flag
        boolean bOk = true;

        // Set default letter
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);

        // Local reference to cargo
        TillSuspendCargo cargo = (TillSuspendCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = cargo.getRegister();

        // Local reference to login cashier
        EmployeeIfc cashier = cargo.getOperator();

        // Local reference to till
        TillIfc till = null;

        // Don't allow till suspend if the register is not open
        if (register.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            letter = new Letter (TillLetterIfc.REGISTER_CLOSED_ERROR);
            cargo.setTillFatalError();
            bOk = false;
        }

        // Check if there is ANY till open in this register
        if (bOk == true &&
            register.getTills().length == 0)
        {
            letter = new Letter (TillLetterIfc.NO_TILLS_OPEN_ERROR);
            cargo.setTillFatalError();
            bOk = false;
        }

        // If there are any open tills, check if this cashier
        // has one of them
        if (bOk == true && register.getAccountability() != AbstractStatusEntityConstantsIfc.ACCOUNTABILITY_REGISTER)
        {
            till = register.getOpenTillByCashierID(cashier.getEmployeeID());
            if (till == null)
            {
                // Check if this cashier has a suspended till
                till = register.getSuspendedTillByCashierID(cashier.getEmployeeID());
                if (till == null)
                {
                    letter = new Letter (CommonLetterIfc.CASHIER_ERROR);
                }
                else
                {
                    // Only open tills can be suspended
                    letter = new Letter (TillLetterIfc.TILL_SUSPEND_ERROR);
                }
                cargo.setTillFatalError();
                bOk = false;
            }
        }

        //for register accountability
        // logic for case register.getAccountability() == AbstractStatusEntityConstantsIfc.ACCOUNTABILITY_REGISTER
        // any user can suspend the till when accountability is to the Register
        if (bOk == true && till == null
                && register.getAccountability() == AbstractStatusEntityConstantsIfc.ACCOUNTABILITY_REGISTER)
        {
            till = register.getCurrentTill();
            if (till == null) 
            {
            	letter = new Letter (CommonLetterIfc.CASHIER_ERROR);
                cargo.setTillFatalError();
                bOk = false;

            } 
            else if (till.getStatus() == AbstractStatusEntityConstantsIfc.STATUS_SUSPENDED)
            {
                letter = new Letter (TillLetterIfc.TILL_SUSPEND_ERROR);
                cargo.setTillFatalError();
                bOk = false;
            }
        }

        // If everything ok
        // Add the till to be suspended to the cargo
        if (bOk == true)
        {
            cargo.setTill(till);
        }

        if (bOk && (till.getRegisterAccountability() == AbstractStatusEntityConstantsIfc.ACCOUNTABILITY_CASHIER))
        {
            letter = new Letter(CommonLetterIfc.PROMPT);
        }

        bus.mail(letter, BusIfc.CURRENT);

    }

}
