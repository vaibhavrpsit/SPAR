/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ParameterHeader.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:38 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:58 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:55:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:46   msg
 * Initial revision.
 * 
 *    Rev 1.3   13 Feb 2002 17:34:30   baa
 * fix ui boxes
 * Resolution for POS SCR-1240: VisaRefPhoneNumber parameter value field box too small to see data
 *
 *    Rev 1.2   Jan 19 2002 10:31:20   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   10 Dec 2001 13:11:38   KAC
 * Commented out processing of nonmodifiable/final fields and
 * removed them from display.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 *
 *    Rev 1.0   Sep 21 2001 11:36:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//  java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

//---------------------------------------------------------------------
/**
 * Contains the visual presentation of the Prescription header
 * @deprecated as of release 5.0.0
 */
//---------------------------------------------------------------------
public class ParameterHeader extends JPanel
{
    /** Parameter name text index */
    protected static int PARAM_NAME = 0;

    /** Parameter value text index */
    protected static int VALUE      = PARAM_NAME + 1;

    /** Parameter mod text index */
    //protected static int MODIFIABLE = VALUE + 1;

    /** Parameter max text index */
    protected static int MAX_FIELDS = VALUE + 1;
    //protected static int MAX_FIELDS = MODIFIABLE + 1;

    /** Text array */
    protected static String labelText[] =
    {
        "Parameter",
        "Value"
        //"Modifiable"
    };
    /** Label fields */
    protected JLabel labels[] = new JLabel[MAX_FIELDS];

    //---------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //---------------------------------------------------------------------
    public ParameterHeader()
    {
        super();
        initialize();
    }


    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        GridBagConstraints gbc = new GridBagConstraints();

        setName("ParameterHeader");
        setLayout(new GridBagLayout());
        setForeground(Color.black);
        Font font = new Font("sansserif", 0, 12);
        setFont(font);

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 0;

        Color color = new Color(0xff, 0xff, 0xb4);
        for (int cnt = 0; cnt < MAX_FIELDS; cnt ++)
        {
            labels[cnt] = new JLabel(labelText[cnt], JLabel.CENTER);
            labels[cnt].setName(labelText[cnt]);
            labels[cnt].setFont(font);
            labels[cnt].setOpaque(true);
            labels[cnt].setBorder(BorderFactory.createLoweredBevelBorder());
            labels[cnt].setForeground(Color.black);
            labels[cnt].setBackground(color);

            // Adjust weightx according to which label is displayed
            if (cnt == PARAM_NAME)
            {
                gbc.weightx = 2.0;
            }
            else if (cnt == VALUE)
            {
                gbc.weightx = 3.0;
            }
            else
            {
                gbc.weightx = 0.1;
            }
            //gbc.weightx = (cnt != PARAM_NAME) ? 1.0 : 3.0;
            gbc.gridx = cnt;
            add(labels[cnt], gbc);
        }
    }


    //--------------------------------------------------------------------------
    /**
       Change the heading above the parameter names to reflect the group
       of those parameters.
       @param group the parameter group from which the parameters came
    **/
    //--------------------------------------------------------------------------

    public void setParameterGroup(String group)
    {
        labels[0].setText(group + " Parameter");
    }


    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        frame.addWindowListener(new WindowAdapter()
                                {
                                    public void windowClosing(WindowEvent e)
                                        {
                                            System.exit(0);
                                        }
                                }
                                );
        ParameterHeader parameterHeader = new ParameterHeader();
        frame.add("Center", parameterHeader);
        frame.setSize(parameterHeader.getSize());
        frame.setVisible(true);
    }
}
