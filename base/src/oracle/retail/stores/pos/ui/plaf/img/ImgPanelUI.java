/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgPanelUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/02/12 16:52:16  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 15 2003 13:51:22   baa
 * modify img plaf classes to extend the eys look and feel
 * Resolution for 3169: Image plaf borders not correctly displayed as borders are not rounded
 * 
 *    Rev 1.1   Apr 03 2003 14:32:52   jgs
 * Changing the conduit script to allow the resource manager to read the images changed the timing of drawing the screen.  This change allows the app a little more time to get it done.
 * Resolution for 2101: Remove uses of  foundation constant  EMPTY_STRING
 * 
 *    Rev 1.0   Apr 29 2002 14:45:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:08   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.beans.ImageBeanIfc;
import oracle.retail.stores.pos.ui.plaf.eys.EYSPanelUI;

//------------------------------------------------------------------------------
/**
 * @deprecated as of release 5.0.0
 */
//------------------------------------------------------------------------------
public class ImgPanelUI extends EYSPanelUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

   //--------------------------------------------------------------------------
    /**
     *  Creates panel UI object
     *  @param c the panel as a JComponent
     *  @returns the panel UI
     */
    //--------------------------------------------------------------------------
    public static ComponentUI createUI(JComponent c)
    {
        return new ImgPanelUI();
    }

    //--------------------------------------------------------------------------
    /**
     *    Overrides the paint method to display a background image if available.
     *    @param g the panels's graphics object
     *    @param c the panel as a JComponent
     */
    //-------------------------------------------------------------------------
    public void paint(Graphics g, JComponent c)
    {
        Graphics2D g2d = (Graphics2D)g;

        if(c instanceof ImageBeanIfc &&
           ((ImageBeanIfc)c).getBackgroundImage() != null)
        {
            g2d.drawImage(((ImageBeanIfc)c).getBackgroundImage(), null, c);
        }

        super.paint(g, c);
    }
    //--------------------------------------------------------------------------
    /**
     *    Retrieves the PVCS revision number.
     *    @return String representation of revision number
     */
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
