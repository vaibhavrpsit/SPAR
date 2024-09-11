/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/notifications/RetrieveNotificationsSite.java /main/5 2014/06/03 16:31:08 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/07/14 - Installer for notifications retrieval refactor
 *    abondala  06/03/14 - mpos notifications retrieval
 *    abondala  05/30/14 - notifications UI related changes
 *    abondala  05/29/14 - introduced notifications service
 *    abondala  05/14/14 - notifications requirement
 *    abondala  05/14/14 - notifications
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.notifications;

//foundation imports
import java.util.List;

import com.oracle.retail.integration.base.bo.notificationcrivo.v1.NotificationCriVo;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.ifc.NotificationsRetrievalManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.storeservices.entities.notifications.Notification;

//--------------------------------------------------------------------------
/**
 This site displays all the notifications.
 <p>
 @version $Revision: /main/5 $
 @since 14.1
**/
//--------------------------------------------------------------------------
public class RetrieveNotificationsSite extends PosSiteActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = -3204618591735708975L;

    /**
         revision number supplied by Team Connection
     **/
     public static final String revisionNumber = "$Revision: /main/5 $";
    
     //--------------------------------------------------------------------------
     /**
         RetrieveNotificationsSite
     **/
     //--------------------------------------------------------------------------
     public static final String SITENAME = "RetrieveNotificationsSite";
     
     
     /**
         constant for parameter names
     **/
     public static final String  DAYS_TO_RETIVE_NOTIFICATIONS   = "DaysToRetrieveNotifications";
     public static final String  NOTIFICATIONS_MAXIMUM_RESULTS  = "NotificationsMaximumResults";
    
     //----------------------------------------------------------------------
     /**
         <P>
         @param  bus     Service Bus
     **/
     //----------------------------------------------------------------------
     @SuppressWarnings("unchecked")
     public void arrive(BusIfc bus)
     {
         NotificationsCargo cargo = (NotificationsCargo)bus.getCargo();
         POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
         try
         {
             boolean trainingModeOn = false;
             StatusBeanModel statusModel = new StatusBeanModel();
             if(cargo.getOperator() != null)
             {
                 statusModel.setCashierName(cargo.getOperator().getPersonName().getFirstLastName());    
             }

             if (cargo.getRegister() != null)
             {
               statusModel.setRegister(cargo.getRegister());
               trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
             }
             statusModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);

             NotificationCriVo criVo = new NotificationCriVo();
             try
             {
                 ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
                 String storeID = Gateway.getProperty("application", "StoreID", "");
                 Integer days = pm.getIntegerValue(DAYS_TO_RETIVE_NOTIFICATIONS);
                 Integer maxResults = pm.getIntegerValue(NOTIFICATIONS_MAXIMUM_RESULTS);
                 criVo.setDays(days);
                 criVo.setSearchLimit(maxResults);
                 criVo.setLocationId(storeID);
             }
             catch (ParameterException e)
             {
                 logger.error(
                              "" + Util.throwableToString(e) + "");
             }
             
             NotificationsRetrievalManagerIfc notificationsMgr = (NotificationsRetrievalManagerIfc)bus.getManager(NotificationsRetrievalManagerIfc.TYPE);
             List<Notification> notificationsList = notificationsMgr.getNotifications(criVo);
             
             cargo.setNotificationsList(notificationsList);
             
             Notification[] notificationsArray = null;
             notificationsArray = new Notification[notificationsList.size()];
             notificationsList.toArray(notificationsArray);
        
             @SuppressWarnings("rawtypes")
             ListBeanModel model = new ListBeanModel();
             model.setListModel(notificationsArray);
             model.setStatusBeanModel(statusModel);
        
             //Displays Screen
             ui.showScreen(POSUIManagerIfc.NOTIFICATIONS_LIST, model);

         }
         catch (DataException de)
         {
             logger.error("Notifications retrieve error: ", de);
             
             if(de.getErrorCode() == DataException.CONNECTION_ERROR)
             {
                 displayOfflineErrorDialog(ui);
             }
             else
             {
                 displayUnknownErrorDialog(ui);    
             }
         }
     }
     
     /**
      * Displays Offline server error dialog message
      * @param ui
      */
     public void displayOfflineErrorDialog(POSUIManagerIfc ui)
     {
         DialogBeanModel dialogBean = new DialogBeanModel();
         dialogBean.setResourceID("SERVER_CONN_ERROR");
         dialogBean.setType(DialogScreensIfc.ERROR);
         dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
         ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    
     } 
     
     /**
      * Displays unknown Error dialog message
      * @param ui
      */
     public void displayUnknownErrorDialog(POSUIManagerIfc ui)
     {
         DialogBeanModel dialogBean = new DialogBeanModel();
         dialogBean.setResourceID("UnknownError");
         dialogBean.setType(DialogScreensIfc.ERROR);
         dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);
         ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    
     } 

}