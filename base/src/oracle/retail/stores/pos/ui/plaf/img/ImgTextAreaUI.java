/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgTextAreaUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *    Rev 1.1   Jul 15 2003 13:51:22   baa
 * modify img plaf classes to extend the eys look and feel
 * Resolution for 3169: Image plaf borders not correctly displayed as borders are not rounded
 * 
 *    Rev 1.0   Apr 29 2002 14:45:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:12   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import oracle.retail.stores.pos.ui.plaf.eys.EYSTextAreaUI;

//------------------------------------------------------------------------------
/**
 *  Implements a Text Area UI to add anti aliasing
 */
//------------------------------------------------------------------------------
public class ImgTextAreaUI extends EYSTextAreaUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //--------------------------------------------------------------------------
   /**
    *  Creates text area UI object
    *  @param c the text area  as a JComponent
    *  @returns the text area  box UI
    */
   //--------------------------------------------------------------------------
    public static ComponentUI createUI(JComponent c)
    {
        return new ImgTextAreaUI();
    }


    //--------------------------------------------------------------------------
    /**
     *    Overrides the paintSafely method to set up antialiasing.
     *    @param g the text area's graphics object
     */
    //--------------------------------------------------------------------------
    protected void paintSafely(Graphics g)
    {
        Graphics2D g2D = (Graphics2D)g;

        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        super.paintSafely(g);
    }

 }
