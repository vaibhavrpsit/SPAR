/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/KeyFilterListener.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva  28813:
 *         Initial Bulk Migration for Java 5 Source/Binary Compatibility of
 *        All Products
 *   3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:59 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:12 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.text.JTextComponent;

public class KeyFilterListener implements KeyListener
{
    Vector devices;
    DeviceFilter device;
    JTextComponent component;


    public KeyFilterListener()
    {
        devices = new Vector();
    }
  

    public void setComponent(JTextComponent c)
    {
        component = c;
    }

    public void addDeviceFilter(DeviceFilter deviceFilter)
    {
        devices.addElement(deviceFilter);
    }


    public void keyPressed(KeyEvent e)
    {
    }
    public void keyReleased(KeyEvent e)
    {
    }
    public void keyTyped(KeyEvent e)
    {
        int op;
        
        char c = e.getKeyChar();

        if(!Character.isISOControl(c))
        {
            for(Enumeration enumer=devices.elements(); enumer.hasMoreElements();)
            {
                device = (DeviceFilter)enumer.nextElement();
                op = device.nextChar(c);
                if(op != device.DEFAULT)
                {
                    e.consume();
                }
            }
        } 
    }






}
