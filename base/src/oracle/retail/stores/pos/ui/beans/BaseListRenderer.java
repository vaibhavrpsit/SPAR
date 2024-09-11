/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BaseListRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/21/2006 9:56:28 PM   Kulbhushan Sharma
 *         Some code refactoring
 *    3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:34 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 14 2002 18:16:48   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:20   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:47:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:29:16   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Dec 10 2001 19:24:42   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import oracle.retail.stores.pos.ui.EYSPOSUIDefaultsIfc;

//------------------------------------------------------------------------------
/**
 *  Abstract class that implements a POS list renderer. Subclasses
 *  must implement abstract methods for displaying specific data.
 */
//------------------------------------------------------------------------------
public class BaseListRenderer extends JPanel
                              implements ListCellRenderer
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

//------------------------------------------------------------------------------
/**
 *  Default constructor.
 */
    public BaseListRenderer()
    {
        super();
    }

//------------------------------------------------------------------------------
/**
 *  Initializes the renderer.
 */
    protected void initialize()
    {
    }

//------------------------------------------------------------------------------
/**
 * This method returns a instance of java.awt.Component, which is configured to display 
 *
 * the required value. The component.paint() method is called to render the cell. 
 *
 * @param JList 
 *
 * @param Object 
 *
 * @param int 
 *
 * @param boolean
 *
 * @param boolean
 *
 * @return Component
 *
 */
    public Component getListCellRendererComponent(JList jList,
                                                  Object obj,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean isCellHasFocus)
    {
        setData(obj);

        // sets the background and foreground colors
          if (isSelected)
          {
                setBackground(jList.getSelectionBackground());
                setForeground(jList.getSelectionForeground());
          }
          else
          {
                setBackground(jList.getBackground());
                setForeground(jList.getForeground());
          }
        // draws the border if the cell has focus
        if(isCellHasFocus)
        {
              setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
        }
        else
        {
            setBorder(EYSPOSUIDefaultsIfc.NoFocusBorder);
        }
          return this;
    }

//---------------------------------------------------------------------
/**
 *  Sets the content of the visual components based on the data
 *  in the specific object to be displayed. This function is called
 *  by <code>getListCellRendererComponent</code>
 *  @param data the data object to be rendered in the list cell
 */
    public void setData(Object data)
    {
    }

//---------------------------------------------------------------------
/**
 *  Makes rendering more efficient by generating a display
 *  object with its data set to the maximum values.
 *  @return a renderable object
 */
//---------------------------------------------------------------------
    public Object createPrototype()
    {
        return new Object();
    }


}
