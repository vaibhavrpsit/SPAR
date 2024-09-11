/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/DisplayRegisterStatusSite.java /main/13 2011/12/06 14:56:01 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  12/06/11 - Fix store close status
 *    mchellap  12/06/11 - XbranchMerge mchellap_fix_pos_store_close from
 *                         rgbustores_13.4x_generic_branch
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    mchellap  12/01/11 - Fixed store close status update
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         5/11/2007 4:18:08 PM   Mathews Kochummen use
 *      locale's date format
 * 3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:58  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:37  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   25 Jun 2003 23:34:46   baa
 * add missing tags
 *
 *    Rev 1.3   Mar 05 2003 10:21:08   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 23 2002 16:43:48   baa
 * retrieve descriptor text from bundles
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 10 2002 14:08:36   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:18:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:10   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 08 2002 09:38:58   mpm
 * Modified to read statuses from database, handle multiple cashiers.
 *
 *    Rev 1.2   Mar 06 2002 09:53:38   mpm
 * Added TillStatus screen.
 * Resolution for POS SCR-1513: Add Till Status screen
 *
 *    Rev 1.1   Feb 04 2002 19:11:52   mpm
 * Removed drawer ID from display.
 * Resolution for POS SCR-799: Add register status panel.
 *
 *    Rev 1.0   Jan 19 2002 18:39:14   mpm
 * Initial revision.
 * Resolution for POS SCR-799: Add register status panel.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

import java.text.DateFormat;


import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;


//------------------------------------------------------------------------------
/**
   This site displays the status of the register. <P>
   @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class DisplayRegisterStatusSite extends PosSiteActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       site name
    **/
    public static final String SITENAME = "DisplayRegisterStatusSite";
    /**
        Comma tag
    **/
    public static final String COMMA_TAG = "Comma";
    /**
        Comma default text
    **/
    public static final String COMMA_TEXT = ",";
    //--------------------------------------------------------------------------
    /**
       Display the status of configured devices and database
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ManagerCargo cargo = (ManagerCargo) bus.getCargo();

        // get the POSUIManager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // Setup bean model information for the UI to display
        DataInputBeanModel beanModel = new DataInputBeanModel();
        beanModel.setValue("storeID",
                           cargo.getStoreStatus().
                             getStore().getStoreID());

        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String dateString = dateTimeService.formatDate(cargo.getStoreStatus().getBusinessDate().dateValue(),
        		LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
        beanModel.setValue("businessDate",dateString);

        beanModel.setValue("storeStatus",
                           utility.retrieveCommonText(AbstractFinancialEntityIfc.STORE_STATUS_DESCRIPTORS
                             [cargo.getStoreStatus().getStatus()]));
        RegisterIfc registerStatus = cargo.getRegisterStatus();
        beanModel.setValue("registerID",
                           registerStatus.
                             getWorkstation().getWorkstationID());
        int status = registerStatus.getStatus();
        beanModel.setValue("registerStatus",
                           utility.retrieveCommonText(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
                             [status]));
        beanModel.setValue("accountability",
                           utility.retrieveCommonText(AbstractFinancialEntityIfc.ACCOUNTABILITY_DESCRIPTORS
                             [registerStatus.getAccountability()]));
        // get current till stats
        TillIfc currentTill = registerStatus.getCurrentTill();
        if (currentTill != null)
        {
            beanModel.setValue("tillID",
                               currentTill.getTillID());
            beanModel.setValue("tillStatus",
                               utility.retrieveCommonText(AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
                                 [currentTill.getStatus()]));
        }

        // get current till, this time from cargo register object
        currentTill = cargo.getRegister().getCurrentTill();
        // if no till exists, use till read from database
        if (currentTill == null)
        {
            currentTill = registerStatus.getCurrentTill();
        }
        if (currentTill != null)
        {
            EmployeeIfc[] emp = currentTill.getCashiers();
            StringBuffer cashierIDs = new StringBuffer();
            // if too many to display on one line, display only count
            if (emp.length > 10)
            {
                cashierIDs.append(Integer.toString(emp.length));
            }
            else
            {
                for (int i = 0; i < emp.length; i++)
                {
                    if (i > 0)
                    {
                        cashierIDs.append(utility.retrieveText("RegisterStatusPanelSpec",
                                                               BundleConstantsIfc.MANAGER_BUNDLE_NAME,
                                                               COMMA_TAG,
                                                               COMMA_TEXT)).
                        append(" ");
                    }
                    cashierIDs.append(emp[i].getLoginID());
                }
            }
            beanModel.setValue("cashiers",
                               cashierIDs.toString());
        }

        ui.showScreen(POSUIManagerIfc.REGISTER_STATUS, beanModel);
    }

}
