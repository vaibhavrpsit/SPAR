/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/ListNewAlertEntriesSite.java /main/19 2013/01/15 18:46:32 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/09/13 - update order to printed status in picklist and
 *                         servicealert
 *    sgu       01/09/13 - retrieve alerts using order manager api
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    mkutiana  10/05/11 - Set the retrievedList flag appropriately - this will
 *                         toggle the next button below accordingly
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    nkgautam  08/25/10 - fixed online/offline status colour issue
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    nganesh   02/02/10 - Service Alert screen name is modified to Fulfillment
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     11   360Commerce 1.10        6/7/2008 6:09:16 AM    Manikandan Chellapan
 *           CR#31924 Enabled audit logging for training and reentry login
 *          logout
 *     10   360Commerce 1.9         5/22/2008 7:09:37 AM   subramanyaprasad gv
 *          For CR 31731: Code reviewed by Manikandan Chellapan.
 *     9    360Commerce 1.8         3/6/2008 5:47:15 AM    Chengegowda
 *          Venkatesh For CR 30275
 *     8    360Commerce 1.7         1/10/2008 8:00:00 AM   Manas Sahu
 *          Event Originator Changes
 *     7    360Commerce 1.6         1/9/2008 9:26:03 PM    Anil Bondalapati
 *          removed the commented code related to removing the parameter
 *          Browser.
 *     6    360Commerce 1.5         1/7/2008 7:54:24 AM    Chengegowda
 *          Venkatesh Audit log changes
 *     5    360Commerce 1.4         12/18/2007 1:45:23 PM  Anil Bondalapati
 *          updated comment.
 *     4    360Commerce 1.3         12/18/2007 1:23:56 PM  Anil Bondalapati
 *          Removed the Browser group as it is not supported in the base
 *          product.
 *     3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse
 *    $
 *    Revision 1.7.2.4  2004/11/05 21:54:44  bwf
 *    @scr 7529 Save screen used to use in next site to avoid reoccuring crash when site is changed.
 *
 *    Revision 1.7.2.3  2004/10/26 21:16:48  jdeleau
 *    @scr 7291 Further webstore IB updates.
 *
 *    Revision 1.9  2004/10/26 20:56:28  jdeleau
 *    @scr 7291 Adjustment for webstore for ice browser
 *
 *    Revision 1.8  2004/10/18 19:34:11  jdeleau
 *    @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 *    Revision 1.7  2004/06/03 14:47:44  epd
 *    @scr 5368 Update to use of DataTransactionFactory
 *
 *    Revision 1.6  2004/04/20 13:17:06  tmorris
 *    @scr 4332 -Sorted imports
 *
 *    Revision 1.5  2004/04/08 17:48:19  pkillick
 *    @scr Changed scr number below to 4332 from 4232
 *
 *    Revision 1.4  2004/04/08 16:20:21  pkillick
 *    @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *    Revision 1.3  2004/02/12 16:51:58  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:52:29  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Oct 21 2003 17:46:02   sfl
 * Use correct parameter value
 * Resolution for POS SCR-3414: Parameterizing Web Access
 *
 *    Rev 1.1   Oct 21 2003 17:32:08   sfl
 * Read the new parameter value to determine which screen to show.
 * Resolution for POS SCR-3414: Parameterizing Web Access
 *
 *    Rev 1.0   Aug 29 2003 16:06:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 07 2003 10:40:22   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Apr 29 2002 15:02:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:28   msg
 * Initial revision.
 *
 *    Rev 1.4   13 Mar 2002 17:07:44   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.3   Mar 10 2002 11:06:52   mpm
 * Externalized text.
 *
 *    Rev 1.2   16 Feb 2002 18:14:36   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Jan 09 2002 12:51:10   dfh
 * updates to check whether we should show the service alert
 * screen and disable the Next button to prevent crashes
 * Resolution for POS SCR-179: CR/Order, after Svc Alert queue empty, db error, then app hung
 *
 *    Rev 1.0   Sep 24 2001 13:05:34   MPM
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:13:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import java.util.List;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.SortedVector;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.BrowserBeanModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This Site retrieves all Alert Items having a status of 'new'. Once a list of
 * Alert Items has been retrieved, they are displayed in the Service Alert List
 * screen.
 *
 * @version $Revision: /main/19 $
 */
public class ListNewAlertEntriesSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2729222506243043572L;

    /**
     * class name constant
     */
    public static final String SITENAME = "ListNewAlertEntriesSite";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * screen name tag
     */
    protected static String SCREEN_NAME_TAG = "ServiceAlertScreenName";

    /**
     * screen name
     */
    protected static String SCREEN_NAME = "Fulfillment";

    /**
     * Gets a list of new Alert Entries and displays it or the database error
     * screen
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        AlertListIfc alertList = DomainGateway.getFactory().getAlertListInstance();
        Letter result = new Letter(CommonLetterIfc.FAILURE); // default db error
        boolean dbErrorFlag = false; // show alerts

        try
        {
            // If we got a database error when trying to retrieve the alert list,
            // we're re-entering and should not try to retrieve again (too loopy)
            if (!cargo.retrieveListFailed())
            {
                alertList = retrieveOrderAlerts(bus);
                if (alertList.getEntries().length == 0)
                {
                    cargo.setRetrieveListFailed(true);
                }
                else
                {
                    cargo.setRetrieveListFailed(false);
                }
                dbErrorFlag = false;
            }
        }
        catch (DataException de)
        {
            dbErrorFlag = true;
            cargo.setRetrieveListFailed(true);
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn( " error:  No Service alert MATCHES !!!");
                result = new Letter("NotFound");
            }

            logger.error( "DB error: " + de.getMessage() + "");
            cargo.setDataExceptionErrorCode(de.getErrorCode());
        }

        if (!dbErrorFlag)  // found alerts
        {
            // If we don't have an operator, get it from the till.
            EmployeeIfc operator = cargo.getOperator();
            if (operator == null)
            {
                // There should only be one cashier for the till because we would
                // have an operator if we were not in Cashier accountability.
                operator = cargo.getRegister().getCurrentTill().getCashiers()[0];
                cargo.setOperator(operator);
            }
            cargo.setEmployee(operator);

            String registerID =
                cargo.getRegister().getWorkstation().getWorkstationID();

            //show the service alert screen with updated status fields
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ListBeanModel beanModel = new ListBeanModel();
            beanModel.setListModel(alertList.getEntries());

            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            String screenName =
              utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                                   BundleConstantsIfc.SERVICE_ALERT_BUNDLE_NAME,
                                   SCREEN_NAME_TAG,
                                   SCREEN_NAME);
            StatusBeanModel statusModel = new StatusBeanModel(registerID,
                                                              operator.getPersonName().getFirstLastName(),
                                                              operator.getPersonName().getFirstLastName(),
                                                              null, screenName);

            // clear the customer's name in the status area
            statusModel.setCustomerName("");

            // Disable undo and cancel, ask the UI Manager to display the menu screen
            NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();
            if (cargo.retrieveListFailed())
            {
                globalNavigationModel.setButtonEnabled(CommonActionsIfc.NEXT, false);
            }
            else
            {
                globalNavigationModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
            }
            beanModel.setGlobalButtonBeanModel(globalNavigationModel);
            boolean trainingModeOn = false;
            if (cargo.getRegister() != null)
            {
              trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
            }
            statusModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
            beanModel.setStatusBeanModel(statusModel);

            boolean webStoreEnabled = false;

            BrowserBeanModel browserBeanModel = new BrowserBeanModel();

            String serviceAlertScreenName = POSUIManagerIfc.SERVICE_ALERT_LIST;
            if(webStoreEnabled)
            {
                if(!browserBeanModel.isInstalled())
                {
                    NavigationButtonBeanModel localButtonBeanModel = new NavigationButtonBeanModel();
                    localButtonBeanModel.setButtonEnabled(CommonActionsIfc.WEB_STORE, false);
                    beanModel.setLocalButtonBeanModel(localButtonBeanModel);
                }
                serviceAlertScreenName = POSUIManagerIfc.SERVICE_ALERT_LIST;
            }
            else
            {
                serviceAlertScreenName = POSUIManagerIfc.SERVICE_ALERT_LIST_NO_WEB_STORE;
            }
            // save the screen name to use in the following road
            cargo.setScreenNameUsed(serviceAlertScreenName);
            ui.showScreen(serviceAlertScreenName, beanModel);
        }
        else
        {
            bus.mail(result,BusIfc.CURRENT); // db error handle it
        }
    }   // arrive

    /**
     * Retrieve new store orders
     * @param bus the bus
     * @return an alert list of new orders
     * @throws DataException
     */
    protected AlertListIfc retrieveOrderAlerts(BusIfc bus) throws DataException
    {
        OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        String storeId = cargo.getStoreStatus().getStore().getStoreID();
        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();
        criteria.configure(new int[]{OrderConstantsIfc.ORDER_STATUS_NEW}, 
        		null, null, storeId, trainingMode);
        List<OrderSummaryEntryIfc> orderSummaries = orderManager.getOrderSummaries(criteria);

        SortedVector<AlertEntryIfc> alertsVector = new SortedVector<AlertEntryIfc>();
        int alertCnt = 5555;
        for (OrderSummaryEntryIfc orderSummary : orderSummaries)
        {
            AlertEntryIfc alert = DomainGateway.getFactory().getAlertEntryInstance();
            alert.setAlertType(AlertEntryIfc.ALERT_TYPE_ORDER_PICKUP);
            alert.setItemID(orderSummary.getOrderID());
            alert.setTimeIssued(orderSummary.getTimestampCreated());
            alert.setSummary("#" + alert.getItemID() + ": " + orderSummary.getOrderDescription());
            alert.setAlertID(Integer.toString(alertCnt++));
            alertsVector.add(alert);
        }

        AlertListIfc alertList = DomainGateway.getFactory().getAlertListInstance();
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeId);
        alertList.setStore(store);
        alertList.setSearchCriteria("new orders and emails");
        alertList.setTimeRetrieved();
        alertList.setEntries(alertsVector.toArray(new AlertEntryIfc[alertsVector.size()]));

        return alertList;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        if (letter instanceof ButtonPressedLetter)
        {
            String letterName = letter.getName();
            ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
            {
                // Audit Logging UserEvent for user logout
                AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
                UserEvent ev = (UserEvent) AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOG_OUT);
                RegisterIfc ri = cargo.getRegister();
                if (ri != null)
                {
                    WorkstationIfc wi = ri.getWorkstation();
                    if (wi != null)
                    {
                        ev.setRegisterNumber(wi.getWorkstationID());
                    }
                }
                ev.setStoreId(cargo.getOperator().getStoreID());
                ev.setUserId(cargo.getOperator().getLoginID());
                ev.setStatus(AuditLoggerConstants.SUCCESS);
                ev.setEventOriginator("ListNewAlertEntriesSite.depart");
                auditService.logStatusSuccess(ev);
            }
        }
    }
}
