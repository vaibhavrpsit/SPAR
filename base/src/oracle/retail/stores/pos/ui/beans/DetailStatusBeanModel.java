/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DetailStatusBeanModel.java /main/16 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   07/02/10 - update getListModel to loop through all statuses
 *                         configured by paramter
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:56 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:34 PM  Robert Pearse   
 *
 * Revision 1.4  2004/03/16 17:15:22  build
 * Forcing head revision
 *
 * Revision 1.3  2004/03/16 17:15:17  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:27  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Oct 22 2003 14:26:54   rsachdeva
 * Device Status
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.1   Oct 21 2003 12:49:02   rsachdeva
 * Device Status
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.0   Aug 29 2003 16:10:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   25 Jun 2003 23:36:50   baa
 * uptate register/device status
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This is the bean model used by the DetailStatusBean.
 * 
 * @version $Revision: /main/16 $
 * @see oracle.retail.stores.pos.ui.beans.DetailStatusBean
 */
public class DetailStatusBeanModel extends ListBeanModel
{
    private static final long serialVersionUID = 2076397673494794534L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    protected HashMap<String,Integer> statusMap = new HashMap<String,Integer>(0);

    /**
     * field Device Line Items
     */
    protected Vector<String> fieldDeviceLineItems = new Vector<String>();

    /**
     * field Status Line Items
     */
    protected Vector<String> fieldStatusLineItems = new Vector<String>();

    /**
     * devices to show
     */
    protected List<String> devicesToShow = new ArrayList<String>();

    /**
     * DetailStatusBeanModel constructor comment.
     */
    public DetailStatusBeanModel()
    {
        super();
    }

    /**
     * Gets the data in the list model as a POSListModel.
     * 
     * @return the list model
     */
    @Override
    public POSListModel getListModel()
    {
        List<String> devices = getDevicesToShow();
        StatusDisplayObject[] statusList = new StatusDisplayObject[devices.size()];

        for (int i = 0; i < statusList.length; i++)
        {
            String device = devices.get(i);
            Integer status = statusMap.get(device);
            if (status == null)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            statusList[i] = new StatusDisplayObject(device, status);
        }

        return new POSListModel(statusList);
    }

    /**
     * Gets the statusLineItems property (Vector) value.
     * 
     * @return The statusLineItems property value.
     * @see #setStatusLineItems
     */
    public Vector<String> getStatusLineItems()
    {
        return fieldStatusLineItems;
    }

    /**
     * Gets the DeviceLineItems property (Vector) value.
     * 
     * @return The deviceLineItems property value.
     * @see #setDeviceLineItems
     */
    public Vector<String> getDeviceLineItems()
    {
        return fieldDeviceLineItems;
    }

    /**
     * Sets the device status on the given device.
     * 
     * @param device the name of the device
     * @param status and integer constant representing the status
     */
    public void setDeviceStatus(String device, int status)
    {
        if (device != null && !device.equals(""))
        {
            statusMap.put(device, status);
        }
    }

    /**
     * Sets the statusLineItems property (Vector) value.
     * 
     * @param statusLineItems The new value for the property.
     * @see #getStatusLineItems
     */
    public void setStatusLineItems(Vector<String> statusLineItems)
    {
        fieldStatusLineItems = statusLineItems;
    }

    /**
     * Sets the deviceLineItems property (Vector) value.
     * 
     * @param deviceLineItems The new value for the property.
     * @see #getDeviceLineItems
     */
    public void setDeviceLineItems(Vector<String> deviceLineItems)
    {
        fieldDeviceLineItems = deviceLineItems;
    }

    /**
     * Sets the devices to show
     * 
     * @param devicesToShow Names of Devices to Show as stated in Parameter
     */
    public void setDevicesToShow(List<String> devicesToShow)
    {
        this.devicesToShow = devicesToShow;
    }

    /**
     * Gets the devices to show
     * 
     * @return List Names of Devices to Show as stated in Parameter
     */
    public List<String> getDevicesToShow()
    {
        return devicesToShow;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("devicesToShow", devicesToShow);
        builder.append("fieldDeviceLineItems", fieldDeviceLineItems);
        builder.append("fieldStatusLineItems", fieldStatusLineItems);
        builder.append("statusMap", statusMap);
        return builder.toString();
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }
}