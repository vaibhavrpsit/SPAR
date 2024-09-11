/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/CashDrawerService.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:30:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:51:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:44:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:59:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:14:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:09:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import oracle.retail.stores.foundation.manager.device.CashDrawerDevice;
import oracle.retail.stores.foundation.manager.device.DeviceException;

//--------------------------------------------------------------------------
/**
    Cash Drawer implementation using the Bedrock CashDrawer. 

**/
//--------------------------------------------------------------------------


public class CashDrawerService
{             
    protected CashDrawerDevice cashDrawer;
                          
    public CashDrawerService()
    {
    }

    //---------------------------------------------------------------------
    /**
        Sets the Bedrock Cash Drawer.
     **/
    //---------------------------------------------------------------------
    public void setCashDrawer(CashDrawerDevice cashDrawer)
    {
        this.cashDrawer = cashDrawer;
    }


    //---------------------------------------------------------------------
    /**
        Set the alert beeper parameters.
    **/
    //--------------------------------------------------------------------- 
    public void setBeepTimeout(int v)
    {
       cashDrawer.setBeepTimeout(v);
    }
    public void setFrequency(int v)
    {
       cashDrawer.setBeepFrequency(v);
    }
    public void setDuration(int v)
    {
       cashDrawer.setBeepDuration(v);
    }
    public void setDelay(int v)
    {
       cashDrawer.setBeepDelay(v);
    }

    //---------------------------------------------------------------------
    /**
        Pops open the cash drawer.
    **/
    //--------------------------------------------------------------------- 
    public void openDrawer() throws DeviceException
    {
       cashDrawer.openDrawer();
    }
    //---------------------------------------------------------------------
    /**
        Check to see if cash drawer is open. 
    **/
    //--------------------------------------------------------------------- 
    public boolean isOpen() throws DeviceException
    {
       return(cashDrawer.isOpen());
    }

    //---------------------------------------------------------------------
    /**
        Wait for drawer to close.
    **/
    //--------------------------------------------------------------------- 
    public void waitForDrawerClose() throws DeviceException
    {
       cashDrawer.waitForDrawerClose();
    }

    //---------------------------------------------------------------------
    /**
        Enable alert beep.
    **/
    //--------------------------------------------------------------------- 
    public void alertBeepOn()
    {
       cashDrawer.alertBeepOn();
    }
    //---------------------------------------------------------------------
    /**
        Disable alert beep.
    **/
    //--------------------------------------------------------------------- 
    public void alertBeepOff()
    {
       cashDrawer.alertBeepOff();
    }

    
}
