/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FinancialTotalsSummaryHeader.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:10:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:53:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:30:22   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:37:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
// quarry imports

//------------------------------------------------------------------------------ 
/**
    This class displays the header for the FinancialTotalsSummaryBean. <P>
    @see FinancialTotalsSummaryBean
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------ 
public class FinancialTotalsSummaryHeader extends JPanel 
{                                       // begin class FinancialTotalsSummaryHeader
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        constant for type label
    **/
    protected static int LABEL_INDEX_TYPE = 0;
    /**
        constant for type entered label
    **/
    protected static int LABEL_INDEX_ENTERED = 1;
    /**
        constant for type expected label
    **/
    protected static int LABEL_INDEX_EXPECTED = 2;
    /**
        number of label fields
    **/
    protected static int LABEL_MAX_FIELDS = 3;
    /**
        label text
    **/
    protected static String labelText[] =
    {
      "Type", 
      "Entered", 
      "Expected"
    };
    /**
        field labels
    **/
        protected JLabel labels[] = new JLabel[LABEL_MAX_FIELDS];

    //---------------------------------------------------------------------
    /**
        Constructs FinancialTotalsSummaryBeanHeader object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsSummaryHeader() 
    {                                   // begin FinancialTotalsSummaryHeader()
        super();
        initialize();
    }                                   // end FinancialTotalsSummaryHeader()

    //---------------------------------------------------------------------
    /**
        Initialize the bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //--------------------------------------------------------------------- 
    protected void initialize() 
    {                                   // begin initialize()
        GridBagConstraints gbc = new GridBagConstraints();

        setName("FinancialTotalsSummaryBeanHeader");
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
        for (int cnt = 0; cnt < LABEL_MAX_FIELDS; cnt ++)
        {
            labels[cnt] = new JLabel(labelText[cnt], JLabel.CENTER);
            labels[cnt].setName(labelText[cnt]);
            labels[cnt].setFont(font);
                        labels[cnt].setOpaque(true);
                        labels[cnt].setBorder(BorderFactory.createLoweredBevelBorder());
            labels[cnt].setForeground(Color.black);
            labels[cnt].setBackground(color);
            if (cnt == LABEL_INDEX_TYPE)
            {
                gbc.weightx = 2.0;
            }
            else
            {
                gbc.weightx = 1.0;
            }
            gbc.gridx = cnt;
            add(labels[cnt], gbc); 
        }

    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        Main test method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param args String[]
    **/
    //--------------------------------------------------------------------- 
    public static void main(java.lang.String[] args) 
    {                                   // begin main()
        java.awt.Frame frame = new java.awt.Frame();
        FinancialTotalsSummaryHeader header = new FinancialTotalsSummaryHeader();
        frame.add("Center", header);
        frame.setSize(header.getSize());
        frame.setVisible(true);
    }                                   // end main()
}                                       // end class FinancialTotalsSummaryHeader
