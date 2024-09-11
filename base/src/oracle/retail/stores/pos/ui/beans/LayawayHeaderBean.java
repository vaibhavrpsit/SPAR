/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LayawayHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:58   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:50:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:30:52   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:36:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//------------------------------------------------------------------------------
/**
 *      Displays a row of column headeings for the
 *      Layaway bean.
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class LayawayHeaderBean extends JPanel 
{
    /*  Revision number supplied by version control */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

        /** layaway number label */
    private JLabel layawayNumberLabel = null;
    
    /** first item label */
    private JLabel firstItemLabel = null;

    /** status label */
    private JLabel statusLabel = null;

    /** date due label */
    private JLabel dateDueLabel = null;
    
    /** balance label */
    private JLabel balanceLabel = null;
    
//------------------------------------------------------------------------------
/**
 *      Default constructor.
 */
    public LayawayHeaderBean() 
    {
        super();
        initialize();
    }

//------------------------------------------------------------------------------
/**
 *      Initialize the class.
 */
    private void initialize() 
    {       
        setName("LayawayHeaderBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(Color.white);
        
        GridBagConstraints gbc = new GridBagConstraints();       
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(1, 1, 0, 0);
        
        layawayNumberLabel = makeLabel("Layaway No.", 135);      
        add(layawayNumberLabel, gbc);

        gbc.insets = new Insets(1, 0, 0, 0);
        firstItemLabel = makeLabel("First Item", 130);       
        add(firstItemLabel, gbc);

        statusLabel = makeLabel("Status", 60);
        add(statusLabel, gbc);

        dateDueLabel = makeLabel("Date Due", 70);
        add(dateDueLabel, gbc);

        balanceLabel = makeLabel("Balance", 65);
        add(balanceLabel, gbc);

/* Fix properties
        layawayNumberLabel.setText(props.getProperty("layawayHeaderBean.LayawayNumberLabel","Description/Item #"));
        firstItemLabel.setText(props.getProperty("layawayHeaderBean.FirstItemLabel","Qty"));
        statusLabel.setText(props.getProperty("layawayHeaderBean.StatusLabel","Price"));
        dateDueLabel.setText(props.getProperty("layawayHeaderBean.DateDueLabel","Discount"));
        getbalanceLabel().setText(props.getProperty("layawayHeaderBean.BalanceLabel","Ext Price"));
        statusLabel.setText(props.getProperty("layawayHeaderBean.StatusLabel","Status"));
*/
        }

//------------------------------------------------------------------------------
/**
 *      Creates and configures a JLabel for the header.
 *      @param text the text for the label
 *      @param width the preferred width of the label
 */    
    private JLabel makeLabel(String text, int width)
    {
        JLabel theLabel = new JLabel(text);
        theLabel.setOpaque(true);
        theLabel.setBorder(BorderFactory.createEtchedBorder());
        theLabel.setForeground(Color.black);
        theLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        theLabel.setPreferredSize(new Dimension(width, 19));
        theLabel.setMinimumSize(new Dimension(4, 19));
        theLabel.setHorizontalAlignment(SwingUtilities.CENTER);
        return theLabel;
    }
    
//------------------------------------------------------------------------------
/**
 *      Returns default display string. <P>
 *      @return String representation of object
 */
    public String toString()
    {
        String strResult = new String("Class: LayawayHeaderBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

//------------------------------------------------------------------------------
/**
 *      Retrieves the Team Connection revision number.
 *      @return String representation of revision number
 */
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

//------------------------------------------------------------------------------
/**
 *      Entry point for testing.
 *      @param args java.lang.String[]
 */
    public static void main(java.lang.String[] args) 
    {
        java.awt.Frame frame = new java.awt.Frame();
        LayawayHeaderBean aLayawayHeaderBean;
        aLayawayHeaderBean = new LayawayHeaderBean();
        frame.add("Center", aLayawayHeaderBean);
        frame.setSize(520, 50);
        frame.setVisible(true);
    }
}
