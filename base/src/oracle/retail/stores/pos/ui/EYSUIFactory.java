/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/EYSUIFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:36 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:01 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:09:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:44:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:44   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   27 Oct 2001 10:26:30   mpm
 * Initial revision.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;
// java awt imports
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//------------------------------------------------------------------------------
/**
    This class generates frequently used user interface artifacts.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EYSUIFactory
{
    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Creates a header label of the specified width containing the specified
        text. <P>
        @param text label text content
        @param width width of label
        @return JLabel of specified textual content and width
    **/
    //---------------------------------------------------------------------
    public static JLabel createHeaderLabel(String text, int width)
    {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBorder(EYSPOSUIDefaultsIfc.POSEtchedBorder);
        label.setForeground(EYSPOSUIDefaultsIfc.LabelForeground);
        label.setBackground(EYSPOSUIDefaultsIfc.HeaderLabelBackground);
        label.setPreferredSize(new Dimension(width, 19));
        label.setHorizontalAlignment(SwingUtilities.CENTER);
        return label;
    }

    //---------------------------------------------------------------------
    /**
        Creates an input label containing the specified text.
        @param text label text content
        @return JLabel of specified textual content
    **/
    //---------------------------------------------------------------------
    public static JLabel createInputLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(EYSPOSUIDefaultsIfc.LabelForeground);
        return label;
    }

    //---------------------------------------------------------------------
    /**
        Creates a list label of the specified width.
        @param width width of label
        @return JLabel of specified width
    **/
    //---------------------------------------------------------------------
    public static JLabel createListLabel(int width)
    {
        JLabel label = new JLabel();
        label.setFont(EYSPOSUIDefaultsIfc.ListFont);
        label.setOpaque(false);
        label.setPreferredSize(new Dimension(width, 19));
        label.setForeground(EYSPOSUIDefaultsIfc.LabelForeground);
        return label;
    }

    //---------------------------------------------------------------------
    /**
        Configures a standard JTextField object, setting its name and
        editable flag.
        @param field JTextField object
        @param name input field name
        @param editable editable flag
    **/
    //---------------------------------------------------------------------
    public static void configureInputField(JTextField field,
                                           String name,
                                           boolean editable)
    {
        field.setName(name);
        field.setColumns(15);
        field.setEditable(editable);

        if (!editable)
        {
            field.setBorder(EYSPOSUIDefaultsIfc.EmptyBorder0);
        }
    }

}
