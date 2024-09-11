/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EvaluateOperatorSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/9/2006 2:44:42 PM    Robert Zurga    Merge
 *          from EvaluateOperatorSite.java, Revision 1.2.1.0 
 *    3    360Commerce 1.2         3/31/2005 4:28:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:56 PM  Robert Pearse   
 *
 *   Revision 1.11  2004/08/24 14:24:54  dcobb
 *   @scr 6888 Not allowed to open till for new user if another till is suspended
 *   Set status to TILL_NOT_OPENunder CASHIER_ACCOUNTABLITY  when the drawer is empty and the operator has no open or suspended tills.
 *
 *   Revision 1.10  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.9  2004/07/19 15:40:25  cdb
 *   @scr 6273 Removed Training Mode Null Pointer.
 *
 *   Revision 1.8  2004/07/19 14:40:46  cdb
 *   @scr 6273 Updated to ensure register accountability behaves as before.
 *
 *   Revision 1.7  2004/07/18 22:35:20  cdb
 *   @scr 6273 Updated so EvaluateOperator takes more responsibility
 *   during till resume.
 *
 *   Revision 1.6  2004/07/18 21:26:33  cdb
 *   @scr 6273    Updated so EvaluateOperator takes more responsibility
 *   during till resume.
 *
 *   Revision 1.5  2004/07/06 23:04:05  awilliam
 *   @scr fix for 5510 insert till error msg should not appear in training mode
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   08 Nov 2003 01:04:54   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.1   Nov 05 2003 23:37:32   cdb
 * Modified to use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:00:06   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.common.TillUtility;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Makes sure the Operator is setup correctly for Till accountability.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EvaluateOperatorSite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Drawer status unknown
        Used for evaluating drawer status
    **/
    public static final int UNKNOWN = -1;
    /**
        Drawer status empty with an open till
        Used for evaluating drawer status
    **/
    public static final int DRAWER_EMPTY_OPEN = 0;
    /**
        Drawer status empty with a suspended till
        Used for evaluating drawer status
    **/
    public static final int DRAWER_EMPTY_SUSPENDED = 1;
    /**
        Drawer status occupied with register set to cashier accountability
        Used for evaluating drawer status
    **/
    public static final int DRAWER_OCCUPIED_CASHIER_ACCOUNTABILITY = 2;
    /**
        Drawer status occupied with register set to register accountability
        Used for evaluating drawer status
    **/
    public static final int DRAWER_OCCUPIED_REGISTER_ACCOUNTABILITY = 3;
    /**
     Drawer status occupied with register set to register accountability
     Used for evaluating drawer status
     **/
    public static final int TILL_NOT_OPEN = 4;
    /**
        cashier no till message tag
     */
    protected static String CASHIER_NO_TILL_TAG =
      "TillCashierHasNoTillAssignedError.CashierNoTill";
    /**
        cashier no till message
     */
    protected static String CASHIER_NO_TILL = "The cashier does not have a till";

    //----------------------------------------------------------------------
    /**
       Makes sure the Operator is setup correctly for Till for till
       accountability.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SaleCargoIfc cargo          = (SaleCargoIfc)bus.getCargo();
        RegisterIfc register    = cargo.getRegister();
        EmployeeIfc operator    = cargo.getOperator();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Drawer status
        int status = UNKNOWN;

        if (!aTillIsOpen(register))
        {
            status = TILL_NOT_OPEN;
        }
        // turn off password required for initial entry into the service
        cargo.setPasswordRequired(false);

        String openTillID = "";
        String suspendedTillID = "";
        int drawerStatus = register.getDrawer(DrawerIfc.DRAWER_PRIMARY).getDrawerStatus();
        if (drawerStatus == AbstractStatusEntityIfc.DRAWER_STATUS_UNOCCUPIED)
        {
            if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER)
            {
                // is till open?
                TillIfc[] tills = register.getTills();
                for (int i=0; i<tills.length; i++)
                {
                    if (tills[i].getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
                    {
                        openTillID = tills[i].getTillID();
                        break;
                    }
                }

                if (openTillID.length() > 0)
                {
                    status = DRAWER_EMPTY_OPEN;
                }

                // is till suspended?
                if (status == UNKNOWN || status == TILL_NOT_OPEN)
                {
                    for (int i=0; i<tills.length; i++)
                    {
                        if (tills[i].getStatus() == AbstractStatusEntityIfc.STATUS_SUSPENDED)
                        {
                            suspendedTillID = tills[i].getTillID();
                            break;
                        }
                    }
                    if (suspendedTillID.length() > 0)
                    {
                        status = DRAWER_EMPTY_SUSPENDED;
                    }
                }
            }
            else // cashier accountability
            {
                // any open tills that belong to this user?
                TillIfc till = register.getOpenTillByCashierID(cargo.getOperator().getEmployeeID());
                if (till != null)
                {
                    status = DRAWER_EMPTY_OPEN;
                    openTillID = till.getTillID();
                }

                // any suspended tills that belong to this user?
                if (status == UNKNOWN || status == TILL_NOT_OPEN)
                {
                    till = register.getSuspendedTillByCashierID(cargo.getOperator().getEmployeeID());
                    if (till != null)
                    {
                        status = DRAWER_EMPTY_SUSPENDED;
                        suspendedTillID = till.getTillID();
                    }
                    else
                    {
                        //If a drawer is available and the Accountability system setting is set to Cashier, 
                        //the system checks to see if the operator has any open or suspended tills assigned to this register.  
                        // if there are none, then a new till can be opened - see till open use case (TillOptions package)
                        
                        status = TILL_NOT_OPEN;
                        
                    }
                }

                // any floating tills that belong to this user?
                if (status == UNKNOWN || status == TILL_NOT_OPEN)
                {
                    StoreIfc store = cargo.getStoreStatus().getStore();
                    EYSDate businessDate = cargo.getStoreStatus().getBusinessDate();
                    if (TillUtility.hasFloatingTill(operator, store, businessDate, bus.getServiceName()))
                    {
                        status = DRAWER_EMPTY_SUSPENDED;
                    }
                }
            }
        } // end if (drawer unoccupied)
        
        if (status == UNKNOWN)
        {
            // drawer occupied
            if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER)
            {
                status = DRAWER_OCCUPIED_REGISTER_ACCOUNTABILITY;
            }

            else if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
            {
                status = DRAWER_OCCUPIED_CASHIER_ACCOUNTABILITY;
            }
        }

        DialogBeanModel model = new DialogBeanModel();
        TillIfc till = null;
        switch (status)
        {
            case DRAWER_EMPTY_OPEN:
                register.setCurrentTillID(openTillID);

                String[] msg = new String[1];
                msg[0] = openTillID;
                model.setArgs(msg);
                model.setResourceID(AbstractFinancialCargo.TILL_OPEN_DRAWER_EMPTY);
                model.setType(DialogScreensIfc.CONFIRMATION);
                model.setButtonLetter(DialogScreensIfc.BUTTON_YES, "OpenDrawer");
                model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.FAILURE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                break;
            case DRAWER_EMPTY_SUSPENDED:
                model.setResourceID(AbstractFinancialCargo.TILL_SUSPENDED);
                model.setType(DialogScreensIfc.CONFIRMATION);
                model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.FAILURE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                break;
            case DRAWER_OCCUPIED_REGISTER_ACCOUNTABILITY:
                operator.setLoginStatus(EmployeeIfc.LOGIN_STATUS_ACTIVE);
                ui.cashierNameChanged(operator.getPersonName().getFirstLastName());
                // Put operator in the list of cashiers; the till object makes sure
                // that it does not get added more than once.
                till = register.getCurrentTill();
                if (evaluateTill(till, ui, utility, cargo))
                {
                    if (till != null)
                    {    
                        till.addCashier(operator);
                    }
                    bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                }
                break;
            case DRAWER_OCCUPIED_CASHIER_ACCOUNTABILITY:
                // Make sure the cashier is valid to login.
                till = register.getOpenOrSuspendedTillByCashierID(operator.getEmployeeID());
                if (evaluateTill(till, ui, utility, cargo))
                {
                    // Set cashier active
                    operator.setLoginStatus(EmployeeIfc.LOGIN_STATUS_ACTIVE);
                    ui.cashierNameChanged(operator.getPersonName().getFirstLastName());
                    if (till != null)
                    {    
                        // Make this cashier's till the current till.
                        register.setCurrentTillID(till.getTillID());
                    }

                    bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                }
                break;
            case TILL_NOT_OPEN:
                bus.mail(new Letter("OpenTill"), BusIfc.CURRENT);
                break;
            default:
                // Should not default
                logger.error( "" + getClass().getName() + " Could not determine letter");
                bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
       Evaluates the state of the till to verfiy that this cashier can log
       on.
       <P>
       @param  till till being evaluated
       @param ui  UI manager subsystem
       @param utility utility manager
       @param cargo POS cargo
    **/
    //----------------------------------------------------------------------
    protected boolean evaluateTill(TillIfc till,
                                   POSUIManagerIfc ui,
                                   UtilityManagerIfc utility,
                                   SaleCargoIfc cargo)
    {
        int status      = AbstractFinancialEntityIfc.STATUS_CLOSED;
        boolean success = cargo.getRegister().getWorkstation().isTrainingMode();

        if (till != null)
        {
            status = till.getStatus();
        }

        // Save till status for signals
        cargo.setTillStatus(status);
        switch (status)
        {
            case AbstractFinancialEntityIfc.STATUS_OPEN:
            {
                success = true;
                break;
            }

            case AbstractFinancialEntityIfc.STATUS_SUSPENDED:
            {
                if (!success)
                {    
                    //Display Till Suspended error dialog
    
                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID(AbstractFinancialCargo.TILL_SUSPENDED);
                    model.setType(DialogScreensIfc.CONFIRMATION);
                    model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.FAILURE);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                }
                break;
            }

            default:
            {
                // Display No Till For Operator error dialog
                String args[] = new String[1];
                args[0] = utility.retrieveDialogText(CASHIER_NO_TILL_TAG,
                                                     CASHIER_NO_TILL);
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(AbstractFinancialCargo.CASHIER_NO_TILL);
                model.setType(DialogScreensIfc.ERROR);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
                model.setArgs(args);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                success = false;
                break;
            }
        }
        
        return(success);
    }

    //----------------------------------------------------------------------
    /**
     Returns true if there is an open or suspended till for this register.
     <P>
     @param the register
     @return true is there is an open or suspended till.
     **/
    //----------------------------------------------------------------------
    protected boolean aTillIsOpen(RegisterIfc register)
    {
        // Check register tills
        TillIfc[] tills = register.getTills();
        boolean open    = false;

        if (tills != null)
        {
            for (int i = 0; i < tills.length; i++)
            {
                TillIfc t = tills[i];
                int status = t.getStatus();
                if (status == AbstractStatusEntityIfc.STATUS_OPEN ||
                    status == AbstractStatusEntityIfc.STATUS_SUSPENDED )
                {
                    open = true;
                    break;
                }
            }
        }

        return(open);
    }
    
}
