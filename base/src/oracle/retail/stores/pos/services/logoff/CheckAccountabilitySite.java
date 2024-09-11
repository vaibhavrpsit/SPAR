/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/logoff/CheckAccountabilitySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:54  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:42   msg
 * Initial revision.
 * 
 *    Rev 1.4   28 Feb 2002 09:25:10   vxs
 * till.getCashiers() instead of register.getCurrentTill().getCashiers
 * Resolution for POS SCR-1375: Log out of last cashier allowed by system during Reg. Accountability
 *
 *    Rev 1.3   27 Feb 2002 10:48:40   vxs
 * replaced register.getCashiers() (returns all past/present) with register.getCurrentTill().getCashiers();
 * Resolution for POS SCR-1375: Log out of last cashier allowed by system during Reg. Accountability
 *
 *    Rev 1.2   23 Jan 2002 11:08:10   vxs
 * Making use of LastCashierLogoffError dialog screen.
 * Resolution for POS SCR-202: Last cashier is able to logoff at the Sell Item screen SEE QA NOTES
 *
 *    Rev 1.1   26 Oct 2001 10:19:18   epd
 * Removed commented out code referring to accountability
 * parameter (parameter removed, setting now stored in database)
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:21:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.logoff;

// java imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This site checks for the pre-condition of register accountability and
        for a minimum number of cashiers logged in (2) to permit cashier logoff.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckAccountabilitySite extends PosSiteActionAdapter
{

    public static final String SITENAME = "CheckAccountabilitySite";

    //--------------------------------------------------------------------------
    /**
       This site checks for the pre-condition of register accountability
       and      for a minimum number of cashiers logged in (2) to permit
       cashier logoff.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui;
        DialogBeanModel model;

        // Set default letter
        Letter letter = new Letter(CommonLetterIfc.FAILURE);

        // Local reference to cargo
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = (RegisterIfc) cargo.getRegister();

        // Local reference to login cashier
        EmployeeIfc cashier = cargo.getOperator();

        if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            model = new DialogBeanModel();
            model.setResourceID("LogoffNotAvailableError");
            model.setType(DialogScreensIfc.ERROR);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
        else if (register.getAccountability() == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER)
        {
            TillIfc till = register.getCurrentTill();
            if (till != null)
            {
                if (till.isOpen())
                {
                    EmployeeIfc cashiers[] = till.getCashiers();
                    // Test for more than one active cashier
                    if (cashiers.length < 2)
                        // If only one cashier in till, must use register close
                    {
                        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                        model = new DialogBeanModel();
                        model.setResourceID("LastCashierLogoffError");
                        model.setType(DialogScreensIfc.ERROR);
                        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
                    }
                    else
                    {
                        int activeCount = 0;
                        for (int c=0; c < cashiers.length; c++)
                        {
                            if (cashiers[c].getLoginStatus() == EmployeeIfc.LOGIN_STATUS_ACTIVE)
                            {
                                activeCount++;
                            }
                        }
                        if (activeCount > 1)
                        {
                            letter = new Letter(CommonLetterIfc.SUCCESS);
                            bus.mail(letter, BusIfc.CURRENT);
                        }
                        else
                        {
                            ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                            model = new DialogBeanModel();
                            model.setResourceID("LogoffRegisterCloseError");
                            model.setType(DialogScreensIfc.ERROR);
                            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
                        }
                    }
                }       // till is open
                else
                {
                    // till is not open
                    ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                    model = new DialogBeanModel();
                    model.setResourceID("LogoffTillNotOpenError");
                    model.setType(DialogScreensIfc.ERROR);
                    ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
                }
            }   // till not null
            else
            {
                                // till is null
                ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                model = new DialogBeanModel();
                model.setResourceID("LogoffTillNotOpenError");
                model.setType(DialogScreensIfc.ERROR);
                ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
            }
        }       // register accountability
        else
        {
            letter = new Letter(CommonLetterIfc.PARAMETER_ERROR);
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
