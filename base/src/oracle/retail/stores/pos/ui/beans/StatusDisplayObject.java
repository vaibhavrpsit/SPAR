/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StatusDisplayObject.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;

//------------------------------------------------------------------------------
/**
 *  A display object that contains a device or data source name along 
 *  with it's status.
 */
//------------------------------------------------------------------------------
public class StatusDisplayObject implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -806806607649327193L;

    // status constants
    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_ONLINE  = 0;
    public static final int STATUS_OFFLINE = 1;
    
    /** the name of the device/datasource */
    protected String objectName;
    
    /** and integer representing the status */
    protected Integer objectStatus;
    
    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public StatusDisplayObject()
    {
        this("", STATUS_UNKNOWN);
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Constructor that sets the device name and its status.
     */
    public StatusDisplayObject(String name, int status)
    {
        this(name, new Integer(status));
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Constructor that sets the device name and its status.
     */
    public StatusDisplayObject(String name, Integer status)
    {
        objectName = name;
        objectStatus = status;
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Returns the name of the device.
     *  @return the device name
     */
    public String getObjectName()
    {
        return objectName;
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Returns the status of the device as an integer constant.
     *  @return the device status
     */
    public int getObjectStatus()
    {
        return objectStatus.intValue();
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Sets the name of the device.
     *  @param name the name of the device
     */
    public void setObjectName(String name)
    {
        objectName = name;
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Sets the status using an integer constant.
     *  @param status an integer representing ONLINE or OFFLINE
     */
    public void setObjectStatus(int status)
    {
        objectStatus = new Integer(status);
    }
    
}
