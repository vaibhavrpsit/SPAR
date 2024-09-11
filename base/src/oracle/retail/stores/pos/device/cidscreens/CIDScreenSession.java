/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/CIDScreenSession.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:39 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

import java.beans.PropertyChangeSupport;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.InputDeviceSession;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;



public class CIDScreenSession
    extends InputDeviceSession  
    implements DeviceSessionIfc
{

    DeviceModelIfc deviceModel = null;
    //---------------------------------------------------------------------
    /**
       The property name for retreiving the Scanner Data
    **/
    //---------------------------------------------------------------------
    public static final String CIDSCREEN_DATA = "CIDScreen data";

    /**
     * Constructor for CID screen session
     *
     */
    public CIDScreenSession()
    {
        super();
        propertyChange = new PropertyChangeSupport(this);
    }
    //---------------------------------------------------------------------
    /**
        This method returns the device control, regardless of the 
        current device mode.
       <P>
       <B>Pre-conditions</B>
       <UL>
       <LI>The DeviceSession is not in use.
       </UL>
       <B>Post-conditions</B>
       <UL>
       <LI>The DeviceSession is in use.
       </UL>
       @return Object The device control managed by this DeviceSession.
       @exception DeviceException is thrown if the device control is null;
    */
    //---------------------------------------------------------------------
    public Object getDevice() throws DeviceException
    {
        return null;
    }


    //--------------------------------------------------------------------- 
    /**
        Activate the device controlled by this DeviceSession.  Activate
        will enable the device and take exclusive access to the device.
        If exclusive access cannot be obtained, activate will throw
        a DeviceException.
        <P>
        Subclasses must provide an implementation of this method. 
        <P>
        @param mode Access mode
        @exception DeviceException thrown if device cannot be activated
    **/
    //--------------------------------------------------------------------- 
    public void activate(String mode) throws DeviceException
    {
    }

    //--------------------------------------------------------------------- 
    /**
        Deactivate the device controlled by this DeviceSession.
        This method will release exclusive access to the device.
        <P>
        Subclasses must implement this method
        <P>
        @exception DeviceException thrown if device cannot be deactivated
    **/
    //--------------------------------------------------------------------- 
    public void deactivate() throws DeviceException
    {
    }

    //---------------------------------------------------------------------
    /**
    Forces the LineDisplay control to close.
    @exception DeviceException is thrown if the shutDown cannot be completed.
    **/
    //---------------------------------------------------------------------
    public void shutDown() throws DeviceException
    {
    }

    //---------------------------------------------------------------------
    /**
       Get the Data model for the input device.
       @return DeviceModelIfc data Model for the CIDScreen
       @throws DeviceException if the mode is invalid
       or the mode cannot be set.
    */
    //---------------------------------------------------------------------
    public DeviceModelIfc getDeviceModel() throws DeviceException
    {
        return deviceModel;
    }

    //---------------------------------------------------------------------
    /**
       Set the Data model for the input device.
       @param DeviceModelIfc data Model from the CIDScreen
       @throws DeviceException if the mode is invalid
       or the mode cannot be set.
    */
    //---------------------------------------------------------------------
    public void setDeviceModel(DeviceModelIfc deviceModel) throws DeviceException
    {
        this.deviceModel = deviceModel;
        propertyChange.firePropertyChange(
                            CIDSCREEN_DATA,
                            null,
                            deviceModel);

    }

    //---------------------------------------------------------------------
    /**
       Enables/disables the device and data event.
       @param enable true to enable, false to disable.
       @throws DeviceException if the device cannot be
       enabled/disabled.
    */
    //---------------------------------------------------------------------
    public void setEnabled(boolean enable) throws DeviceException
    {
    }


}

