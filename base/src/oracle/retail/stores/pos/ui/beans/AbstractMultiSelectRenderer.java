/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AbstractMultiSelectRenderer.java /main/13 2012/09/12 11:57:12 blarsen Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:16:42   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:47:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:06   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:28:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   27 Oct 2001 10:24:52   mpm
 * Merged Pier 1, Virginia ABC changes.
 *
 *    Rev 1.0   Sep 21 2001 11:37:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.util.Properties;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * This is the Abstract renderer that will be used for Multiple Item Selection
 * lists. You will need to extend from this class and implement the two abstract
 * functions.
 * 
 * @version $Revision: /main/13 $
 */
public abstract class AbstractMultiSelectRenderer extends JPanel implements ListCellRenderer
{
    private static final long serialVersionUID = 2425867511968714915L;
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /** Creates the LineBorder for the renderer */
    protected static final LineBorder focusBorder = (LineBorder)LineBorder.createBlackLineBorder();
    /** make EmptyBorder same size as LineBorder */
    protected static final EmptyBorder noFocusBorder = new EmptyBorder(1,1,1,1);

    /** JPanel does track this, so add it in. */
    protected boolean enabled = true;
    /** The properties object which contains local specific text. */
    protected Properties props;

    /**
     * Constructor
     */
    public AbstractMultiSelectRenderer()
    {
        super();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        setData(value);

        if (isSelected && isEnabled())
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        // draw the border if the cell has focus
        if (cellHasFocus)
        {
            setBorder(focusBorder);
        }
        else
        {
            setBorder(noFocusBorder);
        }
        return this;
    }

    public void setEnabled(boolean value)
    {
        enabled = value;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Set the properties to be used by this bean
     * 
     * @param props the propeties object
     */
    public void setProps(Properties props)
    {
        this.props = props;
    }

    /**
     * Update the fields based on the properties
     */
    protected abstract void setPropertyFields();

    /**
     * This function is to be used for Setting the visual components for your
     * Cell. This function is called by getListCellRendererComponent
     */
    public abstract void setData(Object data);

    /**
     * This function is to be used for Setting the prototype in the list. This
     * is needed for making rendering more efficient. Create the object you want
     * to render setting all the data to the maximum values
     */
    public abstract Object createPrototype();
}
