/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/logoff/CheckCashierSite.java /main/16 2012/09/12 11:57:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:24 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse
 *
 *   Revision 1.6  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.5  2004/07/09 15:11:22  dcobb
 *   @scr 2009 Password for user ID is not prompted for after an employee logs off POS while in Register Accountability mode.
 *   Logoff letter is now mailed upon successful logoff.
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
 *    Rev 1.1   Jul 16 2003 17:38:54   bwf
 * Make sure employee is active before allowing logoff.
 * Resolution for 1782: Cashier Logoff is not removing the cashier from the list of active cashiers
 *
 *    Rev 1.0   Apr 29 2002 15:19:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:44   msg
 * Initial revision.
 *
 *    Rev 1.3   Dec 12 2001 08:14:46   vxs
 * Formatting for e-journal cashier logoff.
 * Resolution for POS SCR-118: Cashier logoff does not match E Journal document.
 *
 *    Rev 1.2   Dec 11 2001 17:50:40   vxs
 * Reformatting the cashier logoff ejournal entry.
 * Resolution for POS SCR-118: Cashier logoff does not match E Journal document.
 *
 *    Rev 1.1   Nov 30 2001 15:04:04   vxs
 * Error screen shows when cashier not logged in tries logging off.
 * Resolution for POS SCR-146: A cashier not logged on the register can logoff with no error
 *
 *    Rev 1.0   Sep 21 2001 11:21:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.logoff;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site checks for the cashier in the list of active cashiers. If there,
 * logoff is done by marking inactive.
 *
 * @version $Revision: /main/16 $
 */
public class CheckCashierSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6270127620538157530L;

    /**
     * Checks for the cashier in the list of active cashiers. If there, logoff
     * is done by marking the cashier inactive.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        EmployeeIfc cashiers[] = register.getCashiers();

        // Look for cashier in the list
        if (cashiers != null)
        {
            // Local reference to cashier
            EmployeeIfc cashier = cargo.getOperator();
            String cashierID = cashier.getEmployeeID();

            for (int i = 0; i < cashiers.length; i++)
            {
                if (cashierID.equals(cashiers[i].getEmployeeID()) &&
                    cashiers[i].getLoginStatus() != EmployeeIfc.LOGIN_STATUS_INACTIVE)
                {
                    // set login status to inactive
                    cashiers[i].setLoginStatus(EmployeeIfc.LOGIN_STATUS_INACTIVE);

                    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                    ui.salesAssociateNameChanged(" ");
                    ui.cashierNameChanged(" ");

                    // get the Journal manager
                    JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                    // journal the operator status
                    if (jmi != null)
                    {
                        Date now = new Date();
                       // Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
                        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
                        String dateString = dateTimeService.formatDate(now, journalLocale, DateFormat.SHORT);
                        String timeString = dateTimeService.formatTime(now, journalLocale, DateFormat.SHORT);

                		Object[] cashierDataArgs = {cashierID};
                		String cashierData =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.ORDER_CASHIER_ID, cashierDataArgs);

                		Object[] registerDataArgs = {register.getWorkstation().getWorkstationID()};
                		String registerData =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.REGISTER_LABEL, registerDataArgs);


	                      jmi.journal(cashierID,
	                      (String) null,
	                      I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.ENTER_LOG_OFF_SITE, null) + Util.EOL
	                      + cashierData + Util.EOL
	                      + dateString + Util.EOL
	                      + timeString + Util.EOL
	                      + registerData + Util.EOL
	                      + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.EXIT_LOG_OFF_SITE, null)+Util.EOL);

                    }
                    else
                    {
                        logger.error( "No journal manager found.");
                    }
                    bus.mail(new Letter(CommonLetterIfc.LOGOFF), BusIfc.CURRENT);
                    break;
                }

                //if reached last cashier and still no match, display 'not logged in' error screen.
                else if(i == cashiers.length-1)
                {
                    // build model
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("LogoffNotLoggedInError");
                model.setType(DialogScreensIfc.ERROR);

                // display screen
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
                }
            }
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }
    }
}
