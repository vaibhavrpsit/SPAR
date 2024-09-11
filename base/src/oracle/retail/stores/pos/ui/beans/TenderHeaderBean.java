/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TenderHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:19:02   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:26   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:34:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//---------------------------------------------------------------------
/**
 * Renders the column headers for the TenderBean
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class TenderHeaderBean extends JPanel 
{
    protected JLabel amountLabel = null;
    protected JLabel numberLabel = null;
    protected JLabel typeLabel = null;
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public TenderHeaderBean() 
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * TenderHeaderBean constructor comment.
     * @param layout LayoutManager
     */
    //---------------------------------------------------------------------
    public TenderHeaderBean(LayoutManager layout) 
    {
        super(layout);
    }

    //---------------------------------------------------------------------
    /**
     * TenderHeaderBean constructor comment.
     * @param layout LayoutManager
     * @param isDoubleBuffered boolean
     */
    //---------------------------------------------------------------------
    public TenderHeaderBean(LayoutManager layout, boolean isDoubleBuffered) 
    {
        super(layout, isDoubleBuffered);
    }

    //---------------------------------------------------------------------
    /**
     * TenderHeaderBean constructor comment.
     * @param isDoubleBuffered boolean
     */
    //---------------------------------------------------------------------
    public TenderHeaderBean(boolean isDoubleBuffered) 
    {
        super(isDoubleBuffered);
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize() 
    {
        GridBagConstraints constraintsTypeLabel = new GridBagConstraints();
        GridBagConstraints constraintsNumberLabel = new GridBagConstraints();
        GridBagConstraints constraintsAmountLabel = new GridBagConstraints();
        setName("TenderHeaderBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(Color.white);
        setSize(522, 21);

        typeLabel = new JLabel();
        typeLabel.setName("TypeLabel");
        typeLabel.setOpaque(true);
        typeLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        typeLabel.setText("Type");
        typeLabel.setForeground(Color.black);
        typeLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setPreferredSize(new Dimension(4,19));

        constraintsTypeLabel.gridx = 0; constraintsTypeLabel.gridy = 0;
        constraintsTypeLabel.gridwidth = 1; constraintsTypeLabel.gridheight = 1;
        constraintsTypeLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsTypeLabel.anchor = GridBagConstraints.CENTER;
        constraintsTypeLabel.weightx = 1.0;
        constraintsTypeLabel.weighty = 0.0;

        add(typeLabel, constraintsTypeLabel);

        numberLabel = new JLabel();
        numberLabel.setName("NumberLabel");
        numberLabel.setOpaque(true);
        numberLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        numberLabel.setText("Number");
        numberLabel.setForeground(Color.black);
        numberLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setPreferredSize(new Dimension(4,19));

        constraintsNumberLabel.gridx = 1; constraintsNumberLabel.gridy = 0;
        constraintsNumberLabel.gridwidth = 1; constraintsNumberLabel.gridheight = 1;
        constraintsNumberLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsNumberLabel.anchor = GridBagConstraints.CENTER;
        constraintsNumberLabel.weightx = 1.0;
        constraintsNumberLabel.weighty = 0.0;

        add(numberLabel, constraintsNumberLabel);

        amountLabel = new JLabel();
        amountLabel.setName("AmountLabel");
        amountLabel.setOpaque(true);
        amountLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        amountLabel.setText("Amount");
        amountLabel.setForeground(Color.black);
        amountLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        amountLabel.setPreferredSize(new Dimension(4,19));

        constraintsAmountLabel.gridx = 2; constraintsAmountLabel.gridy = 0;
        constraintsAmountLabel.gridwidth = 1; constraintsAmountLabel.gridheight = 1;
        constraintsAmountLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsAmountLabel.anchor = GridBagConstraints.CENTER;
        constraintsAmountLabel.weightx = 1.0;
        constraintsAmountLabel.weighty = 0.0;

        add(amountLabel, constraintsAmountLabel);
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: TenderHeaderBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return strResult;
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(String[] args) 
    {
        java.awt.Frame frame = new java.awt.Frame();
        TenderHeaderBean aTenderHeaderBean;
        aTenderHeaderBean = new TenderHeaderBean();
        frame.add("Center", aTenderHeaderBean);
        frame.setSize(aTenderHeaderBean.getSize());
        frame.setVisible(true);
   }
}
