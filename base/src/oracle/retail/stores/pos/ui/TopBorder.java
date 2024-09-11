/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/TopBorder.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:09:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:50   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

//----------------------------------------------------------------------------
/**
   Provides a border to only the top edge of a JComponent
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class TopBorder extends AbstractBorder
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -927662241565580484L;

    int thickness = 5;
    Color color = Color.lightGray;

    public TopBorder()
    {
    }

    public TopBorder(Color c, int thickness)
    {
        this.thickness = thickness;
        color = c;
    }

    /** This default implementation returns false. */
    public boolean isBorderOpaque() 
    {
        return true;
    }

    /** This default implementation does no painting. */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
    {
        Insets insets = getBorderInsets(c);
        g.setColor(color);

        g.fill3DRect(x,y,width,insets.top,true);

    }

    /** This default implementation returns the value of getBorderMargins. */
    public Insets getBorderInsets(Component c) 
    {
        return new Insets(thickness,0,0,0);
    }
}
