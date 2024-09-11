/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSRequiredPasswordFieldUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:35 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:00 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/02/12 16:52:14  mcs
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
 *    Rev 1.0   Aug 29 2003 16:13:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Sep 10 2002 16:13:52   baa
 * Initial revision.
 * Resolution for POS SCR-1810: Adding pasword validating fields
 * 
 *    Rev 1.0   Sep 10 2002 12:05:24   baa
 * Initial revision.
 * Resolution for kbpos SCR-2270: When changing password at POS, the password is visible to all
 * 
 *    Rev 1.1   10 Apr 2002 13:59:58   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

// java imports
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 *  Implements a required box around a text field ui
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class EYSRequiredPasswordFieldUI extends BasicPasswordFieldUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     *  Creates required text field UI object
     *  @param c the text field  as a JComponent
     *  @returns the text field  box UI
     */
    //--------------------------------------------------------------------------
    public static ComponentUI createUI(JComponent c)
    {
        return new EYSRequiredPasswordFieldUI();
    }

    //--------------------------------------------------------------------------
    /**
     *  Repaints the required text field background
     *  @param g the text field  graphic object
     */
    //--------------------------------------------------------------------------
    protected void paintBackground(Graphics g)
    {
        JComponent c = getComponent();

        g.setColor(c.getBackground());

        if(c.getWidth() > 0)
        {
            g.fillRect(0, 0, c.getWidth()-EYSBorderFactory.REQUIRED_INSET, c.getHeight());
            g.setColor(c.getParent().getBackground());
            g.fillRect(c.getWidth()-EYSBorderFactory.REQUIRED_INSET, 0, EYSBorderFactory.REQUIRED_INSET, c.getHeight());
        }
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
