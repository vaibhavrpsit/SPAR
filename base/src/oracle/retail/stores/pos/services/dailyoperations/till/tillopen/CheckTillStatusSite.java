/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/CheckTillStatusSite.java /main/11 2012/08/07 16:19:47 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/25/12 - wptg-merged TILL_OPEN_ERROR_TAG_LINE1 and 2.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/08/12 15:16:52  dcobb
 *   @scr 6792 TillStatusSite - cargo contains outdated register/till data
 *   Corrected default error messages.
 *
 *   Revision 1.9  2004/07/23 16:19:51  dcobb
 *   @scr 6428 suspend/resume till - can't resume till after different user closes their till
 *   Update the currentTillID for the register before the save operations.
 *
 *   Revision 1.8  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.7  2004/07/02 20:00:37  dcobb
 *   @scr 5503 Training Mode button should be disabled when store / register / till are not open.
 *
 *   Revision 1.6  2004/06/23 14:24:07  khassen
 *   @scr 5263 - Updated register to reflect current status of till.
 *
 *   Revision 1.5  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/16 14:48:03  blj
 *   @scr -3838 cleanup code
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:22   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 13:39:58   baa
 * remove typo
 * Resolution for POS SCR-1563: On longer dialog messages, the Enter button is not fully displayed.  Enter works.
 *
 *    Rev 1.0   Mar 18 2002 11:29:22   msg
 * Initial revision.
 *
 *    Rev 1.5   05 Dec 2001 15:00:28   epd
 * change to business logic and drawer occupied handling code
 * Resolution for POS SCR-64: Wrong EM when Cash Acct, active till, new user opens 2nd till
 *
 *    Rev 1.4   03 Dec 2001 16:18:08   epd
 * Update drawer ID to use constant value as defined in DrawerIfc
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.3   30 Nov 2001 10:24:04   epd
 * added logic to test for closed AND reconciled tills
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   12 Nov 2001 09:57:24   epd
 * Makes use of new Drawer object
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   08 Nov 2001 09:53:40   epd
 * Uses new drawer status values.  Minor logic change/fix
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TillCargoIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks for open tills.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class CheckTillStatusSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
        Checks the status of the tills.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TillOpenCargo cargo = (TillOpenCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        EmployeeIfc cashier = cargo.getOperator();
        String letterName = CommonLetterIfc.SUCCESS;
        boolean bOK = true;

        if (register.getAccountability() == AbstractStatusEntityIfc.ACCOUNTABILITY_REGISTER)
        {
            // Under register accountability, there should be no other tills that
            // are either open or suspended
            TillIfc[] tills = register.getTills();
            for (int i = 0; i < tills.length; ++i)
            {
                if (tills[i].getStatus() != AbstractStatusEntityIfc.STATUS_RECONCILED &&
                    tills[i].getStatus() != AbstractStatusEntityIfc.STATUS_CLOSED)
                {
                    letterName = "Failure";
                    cargo.setErrorScreenName(TillOpenCargoIfc.TILL_REGISTER_ERROR);
                    bOK = false;
                    break;
                }
            }
        }
        // drawer occupied?
        if (bOK 
            && (register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus() == AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED))
        {
            letterName = "Failure";

            String occupyingTillID = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getOccupyingTillID();
            int tillStatus = register.getTillByID(occupyingTillID).getStatus();
            String[] args = new String[1];
            // occupying till ID will either be open, suspended, or closed
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            if (tillStatus == AbstractStatusEntityIfc.STATUS_OPEN)
            {
                args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_OPEN_ERROR_TAG_LINE,
                        TillCargoIfc.TILL_OPEN_ERROR_LINE);
            }
            else if (tillStatus == AbstractStatusEntityIfc.STATUS_SUSPENDED)// suspended
            {
                args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_SUSPENDED_ERROR_TAG_LINE,
                        TillCargoIfc.TILL_SUSPENDED_ERROR_LINE);
            }
            else
            // closed
            {
                args[0] = utility.retrieveDialogText(TillCargoIfc.TILL_OPEN_ERROR_TAG_LINE,
                        TillCargoIfc.TILL_OPEN_ERROR_LINE);
            }
            cargo.setErrorScreenArgs(args);
            cargo.setErrorScreenName(TillOpenCargoIfc.TILL_DRAWER_ERROR);
            bOK = false;
        }
        
        if (bOK 
            && (register.getAccountability() == AbstractStatusEntityIfc.ACCOUNTABILITY_CASHIER))
        {
            if (register.getCashierByID(cashier.getEmployeeID()) != null)
            {
                // Under cashier accountability, the cashier should have no
                // other tills that are either open or suspended
                TillIfc[] tills = register.getTills();
    
                // for each till need to check if not closed
                for (int i = 0; i < tills.length; ++i)
                {
                    // is till open or suspended
                    if (tills[i].isOpen() || tills[i].isSuspended())
                    {
                        EmployeeIfc listCashier[] = tills[i].getCashiers();
                        // loop through till's cashiers
                        for (int j = 0; j < listCashier.length; j++)
                        {
                            if (listCashier[j].getEmployeeID().equals(cashier.getEmployeeID()))
                            {
                                letterName = "Failure";
                                cargo.setErrorScreenName(TillOpenCargoIfc.TILL_CASHIER_ERROR);
                                bOK = false;
                                break;
                            }
                        }
                    }
                }
            }
            else
            {
                // Under cashier accountability, there should be no tills open
                // by other cashiers
                TillIfc[] tills = register.getTills();
                for (int i = 0; i < tills.length; ++i)
                {
                    if (tills[i].isOpen())
                    {
                        letterName = "Failure";
                        cargo.setErrorScreenName(TillOpenCargoIfc.TILL_OTHER_CASHIER_ERROR);
                        break;
                    }
                }
            }
        }



        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
