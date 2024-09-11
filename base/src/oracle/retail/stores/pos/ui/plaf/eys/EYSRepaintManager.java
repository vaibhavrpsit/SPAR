/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSRepaintManager.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.RepaintManager;
import javax.swing.UIManager;

//------------------------------------------------------------------------------
/**
 *  deprecated as of release 5.1.0
 */
//------------------------------------------------------------------------------
public class EYSRepaintManager extends RepaintManager
{

    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

   //---------------------------------------------------------------------------
    /**
     *  Overridden to fill the buffered image's background. This is used to
     *  prevent odd repaint behavior when a component calls paintImmediately.
     *  The offscreen buffer will display invalid background patterns if
     *  the object being drawn does not paint the entire buffer area. This
     *  mainly occurs during mouseover events on rounded buttons.
     */
    public Image getOffscreenBuffer(Component c,
                                    int proposedWidth,
                                    int proposedHeight)
    {
        Image offscreenBuffer =
            super.getOffscreenBuffer(c, proposedWidth, proposedHeight);

        // Erase the image in case we are reusing a previously
        // allocated double buffer
        Graphics g = offscreenBuffer.getGraphics();

        // first try to use the parent's background
        if(c.getParent() != null && c.getParent().isOpaque())
        {
            g.setColor(c.getParent().getBackground());
        }
        // otherwise, use the color set in the plaf properties
        else
        {
            g.setColor(UIManager.getColor("appBackground"));
        }
        g.fillRect(0, 0, proposedWidth, proposedHeight);

        return offscreenBuffer;
    }

}
