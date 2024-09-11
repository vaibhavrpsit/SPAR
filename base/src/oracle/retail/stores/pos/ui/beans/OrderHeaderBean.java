/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/12 20:00:59  cdb
 *   @scr 0 Attempting to remove deprecated class.
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:06   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:31:12   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 24 2001 11:19:22   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//---------------------------------------------------------------------
/**
 * This type was created in VisualAge.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
   @deprecated as of release 7.0
 */
//---------------------------------------------------------------------
public class OrderHeaderBean extends JPanel 
{
    
    /* Status field */
    protected JLabel statusLabel = null;
    /*  Revision number supplied by version control */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** The description label component **/
    protected JLabel descriptionLabel = null;
    /** The quantity label component **/
    protected JLabel qtyLabel         = null;
    /** The price label component **/
    protected JLabel priceLabel       = null;
    /** The discount label component **/
    protected JLabel discountLabel    = null;
    /** The extented price label component **/
    protected JLabel extPriceLabel    = null;

    /** The properties object which contains local specific text **/
    protected Properties props        = null;
    
    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public OrderHeaderBean() 
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
        setName("OrderHeaderBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255,255,255));
        setMaximumSize(new Dimension(2147483647, 2147483647));
        setSize(new Dimension(520, 19));
        setPreferredSize(new Dimension(520, 19));
        setBounds(new Rectangle(0, 0, 520, 19));
        setSize(520, 19);
        setMinimumSize(new Dimension(520, 19));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.gridy = 0;
        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 2.0;
        constraints.weighty = 0.0;
        descriptionLabel = new JLabel();
        descriptionLabel.setName("DescriptionLabel");
        descriptionLabel.setOpaque(true);
        descriptionLabel.setBorder(new Flush3DBorder());
        descriptionLabel.setText("Description/Item #");
        descriptionLabel.setMaximumSize(new Dimension(65535, 19));
        descriptionLabel.setForeground(Color.black);
        descriptionLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        descriptionLabel.setPreferredSize(new Dimension(4, 19));
        descriptionLabel.setMinimumSize(new Dimension(4, 19));
        descriptionLabel.setHorizontalAlignment(0);
        add(descriptionLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 1; constraints.gridy = 0;
        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.6;
        constraints.weighty = 0.0;
        qtyLabel = new JLabel();
        qtyLabel.setName("QtyLabel");
        qtyLabel.setOpaque(true);
        qtyLabel.setBorder(new Flush3DBorder());
        qtyLabel.setText("Qty");
        qtyLabel.setMaximumSize(new Dimension(65535, 19));
        qtyLabel.setForeground(Color.black);
        qtyLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        qtyLabel.setPreferredSize(new Dimension(4, 19));
        qtyLabel.setMinimumSize(new Dimension(4, 19));
        qtyLabel.setHorizontalAlignment(0);
        add(qtyLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2; constraints.gridy = 0;
        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        priceLabel = new JLabel();
        priceLabel.setName("PriceLabel");
        priceLabel.setOpaque(true);
        priceLabel.setBorder(new Flush3DBorder());
        priceLabel.setText("Price");
        priceLabel.setMaximumSize(new Dimension(65535, 19));
        priceLabel.setForeground(Color.black);
        priceLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        priceLabel.setPreferredSize(new Dimension(4, 19));
        priceLabel.setMinimumSize(new Dimension(4, 19));
        priceLabel.setHorizontalAlignment(0);
        add(priceLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 3; constraints.gridy = 0;
        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        discountLabel = new JLabel();
        discountLabel.setName("DiscountLabel");
        discountLabel.setOpaque(true);
        discountLabel.setBorder(new Flush3DBorder());
        discountLabel.setText("Discount");
        discountLabel.setMaximumSize(new Dimension(65535, 19));
        discountLabel.setForeground(Color.black);
        discountLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        discountLabel.setPreferredSize(new Dimension(4, 19));
        discountLabel.setMinimumSize(new Dimension(4, 19));
        discountLabel.setHorizontalAlignment(0);
        add(discountLabel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 4; constraints.gridy = 0;
        constraints.gridwidth = 1; constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        extPriceLabel = new JLabel();
        extPriceLabel.setName("ExtPriceLabel");
        extPriceLabel.setOpaque(true);
        extPriceLabel.setBorder(new Flush3DBorder());
        extPriceLabel.setText("Ext Price");
        extPriceLabel.setMaximumSize(new Dimension(65535, 19));
        extPriceLabel.setForeground(Color.black);
        extPriceLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        extPriceLabel.setPreferredSize(new Dimension(4, 19));
        extPriceLabel.setMinimumSize(new Dimension(4, 19));
        extPriceLabel.setHorizontalAlignment(0);
        add(extPriceLabel, constraints);
        
        GridBagConstraints constraintsStatusLabel = new GridBagConstraints();
        
        constraintsStatusLabel.gridx = 5; constraintsStatusLabel.gridy = 0;
        constraintsStatusLabel.gridwidth = 1; constraintsStatusLabel.gridheight = 1;
        constraintsStatusLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsStatusLabel.anchor = GridBagConstraints.CENTER;
        constraintsStatusLabel.weightx = 1.0;
        constraintsStatusLabel.weighty = 0.0;
        statusLabel = new JLabel();
        statusLabel.setName("StatusLabel");
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new Flush3DBorder());
        statusLabel.setText("Status");
        statusLabel.setMaximumSize(new Dimension(65535, 19));
        statusLabel.setForeground(Color.black);
        statusLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        statusLabel.setPreferredSize(new Dimension(4, 19));
        statusLabel.setMinimumSize(new Dimension(4, 19));
        statusLabel.setHorizontalAlignment(0);
        add(statusLabel, constraintsStatusLabel);
        statusLabel.setText("Status");


  }

    //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void updatePropFields()
    {
        if (props != null)
        {   
            descriptionLabel.setText(props.getProperty("SaleHeaderBean.DescriptionLabel","Description/Item #"));
            qtyLabel.setText(props.getProperty("SaleHeaderBean.QtyLabel","Qty"));
            priceLabel.setText(props.getProperty("SaleHeaderBean.PriceLabel","Price"));
            discountLabel.setText(props.getProperty("SaleHeaderBean.DiscountLabel","Discount"));
            extPriceLabel.setText(props.getProperty("SaleHeaderBean.ExtPriceLabel","Ext Price"));
            
            statusLabel.setText(props.getProperty("OrderHeaderBean.StatusLabel","Status"));            
        }
    }
    
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: OrderHeaderBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
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
        OrderHeaderBean aOrderHeaderBean;
        aOrderHeaderBean = new OrderHeaderBean();
        frame.add("Center", aOrderHeaderBean);
        frame.setSize(aOrderHeaderBean.getSize());
        frame.setVisible(true);
    }
}
