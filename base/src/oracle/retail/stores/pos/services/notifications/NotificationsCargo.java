/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/notifications/NotificationsCargo.java /main/1 2014/05/29 18:06:22 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  05/29/14 - introduced notifications service
 *    abondala  05/29/14 - notifications cargo
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.notifications;

import java.io.Serializable;
import java.util.List;

import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.storeservices.entities.notifications.Notification;

/**
 * Contains notifications data retrieved from the enterprise application
 * 
 * @since 14.1
 */
public class NotificationsCargo extends AbstractFinancialCargo implements  TourCamIfc, CargoIfc, Serializable
{
    /** serialVersionUID **/
    private static final long serialVersionUID = 2530742543585115789L;
    
    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /main/1 $";

    /** List of notifications returned from the enterprise **/
    protected List<Notification> notificationsList = null;
    
    /**
     * Constructs NotificationsCargo object.
    */
    public NotificationsCargo()
    {
    }
    
    /**
     * Gets the notifications list
     * @return notificationsList
     */
    public List<Notification> getNotificationsList()
    {
        return notificationsList;
    }

    /**
     * Sets the notifications list
     * @param notificationsList
     */
    public void setNotificationsList(List<Notification> notificationsList)
    {
        this.notificationsList = notificationsList;
    }
    
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = new StringBuilder(getClass().getName());
        strResult.append(" (Revision ").append(getRevisionNumber()).append(")")
                .append(hashCode());

        // pass back result
        return strResult.toString();
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }   
}
