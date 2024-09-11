/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSScrollBar.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JScrollBar;

public class EYSScrollBar extends JScrollBar
{
    public final static String uiClassID = "EYSScrollBarUI";
    
    /**
     * Default constuctor.
     */
    public EYSScrollBar() 
    {
        super();
    }
    
    /**
     * Creates a scrollbar with the specified orientation.
     * @param orientation HORIZONTAL or VERTICAL
     */
    public EYSScrollBar(int orientation) 
    {
        super(orientation);
    }

    public EYSScrollBar(int orientation, int value, int extent, int min, int max)   
    {
        super(orientation, value, extent, min, max);
    }


    public String getUIClassID() {
        return uiClassID;
    }
}
