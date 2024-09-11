/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/POSFocusRegulator.java /main/11 2012/10/16 17:37:33 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/08/12 - deprecated
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:07 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 23 2003 14:16:06   dcobb
 * Migrate to JDK 1.4.1.
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:09:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:48   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.awt.Component;

import javax.swing.SwingUtilities;

/**
 * Singleton class that allows a component to be registered as needing focus.
 * The POS Application Frame calls this object to see if a component has been
 * set for focus. This circumvents a problem that can occur on the first screen
 * if a {@link Component} has requested focus before the Application Frame is visible.
 * 
 * @version $Revision: /main/11 $
 * @deprecated as of 14.0. No replacement. See
 * <a href="http://docs.oracle.com/javase/tutorial/uiswing/misc/focus.html">
 * How to Use the Focus Subsystem</a>.
 */
public class POSFocusRegulator
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /** the component that needs focus */
    public Component component = null;

    /** singleton instance of the regulator */
    private static POSFocusRegulator instance;

    /**
     * Private constructor to insure singleton.
     */
    private POSFocusRegulator()
    {
    }

    /**
     * Returns the singleton instance of this object.
     * 
     * @return the singleton instance
     */
    public static POSFocusRegulator getInstance()
    {
        // lazy instantiation
        if (instance == null)
        {
            instance = new POSFocusRegulator();
        }
        return instance;
    }

    /**
     * Used by a bean to set a component for focus when the application frame is
     * visible.
     * 
     * @param c the component that needs focus
     */
    public void setComponent(Component c)
    {
        component = c;
    }

    /**
     * If a component has asked for focus, this method requests focus.
     */
    public void checkComponent()
    {
        // if there is a component set, request the focus
        if (component != null)
        {
            SwingUtilities.invokeLater(new FocusRunner(component));

            // set the component to null
            component = null;
        }
    }

    /**
     * Inner class to request focus on awt event queue
     */
    private class FocusRunner implements Runnable
    {
        private Component component;

        private FocusRunner(Component c)
        {
            component = c;
        }

        public void run()
        {
            component.requestFocusInWindow();
        }
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}