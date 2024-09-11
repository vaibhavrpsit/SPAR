/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerSelectHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.0   Aug 29 2003 16:10:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:10   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:57:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   26 Jan 2002 18:52:36   baa
 * ui fixes
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.1   Jan 19 2002 10:29:40   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:37:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;


//---------------------------------------------------------------------
/**
    This class represents the Header for the CustomerSelectBean
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class CustomerSelectHeaderBean extends JPanel
{

        /**
        Revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Address Column name
    **/
        protected JLabel addressLabel = null;
    /**
        Name Column name
    **/
        protected JLabel nameLabel = null;
    /**
        Telephone Column name
    **/
        protected JLabel telephoneLabel = null;


    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public CustomerSelectHeaderBean()
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
        addressLabel = new JLabel();
        addressLabel.setName("addressLabel");
        addressLabel.setOpaque(true);
        addressLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        addressLabel.setText("Address");
        addressLabel.setMaximumSize(new Dimension(65535, 19));
        addressLabel.setForeground(Color.black);
        addressLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        addressLabel.setPreferredSize(new Dimension(4, 19));
        addressLabel.setMinimumSize(new Dimension(4, 19));
        addressLabel.setHorizontalAlignment(0);

        nameLabel = new JLabel();
        nameLabel.setName("nameLabel");
        nameLabel.setOpaque(true);
        nameLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        nameLabel.setText("Customer Name/ID");
        nameLabel.setMaximumSize(new Dimension(65535, 19));
        nameLabel.setForeground(Color.black);
        nameLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        nameLabel.setPreferredSize(new Dimension(4, 19));
        nameLabel.setMinimumSize(new Dimension(4, 19));
        nameLabel.setHorizontalAlignment(0);

        telephoneLabel = new JLabel();
        telephoneLabel.setName("TelephoneLabel");
        telephoneLabel.setOpaque(true);
        telephoneLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        telephoneLabel.setText("Telephone");
        telephoneLabel.setMaximumSize(new Dimension(65535, 19));
        telephoneLabel.setForeground(Color.black);
        telephoneLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        telephoneLabel.setPreferredSize(new Dimension(4, 19));
        telephoneLabel.setMinimumSize(new Dimension(4, 19));
        telephoneLabel.setHorizontalAlignment(0);

        GridBagConstraints constraintsNameLabel = new GridBagConstraints();
        GridBagConstraints constraintsAddressLabel = new GridBagConstraints();
        GridBagConstraints constraintsTelephoneLabel = new GridBagConstraints();
        GridBagConstraints constraintsTypeLabel = new GridBagConstraints();
        setName("CustomerSelectHeaderBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255,255,255));
        setMaximumSize(new Dimension(2147483647, 2147483647));
        setSize(new Dimension(520, 19));
        setPreferredSize(new Dimension(520, 19));
        setBounds(new java.awt.Rectangle(0, 0, 520, 19));
        setSize(520, 19);
        setMinimumSize(new Dimension(520, 19));

        constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
        constraintsNameLabel.gridwidth = 1; constraintsNameLabel.gridheight = 1;
        constraintsNameLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsNameLabel.anchor = GridBagConstraints.CENTER;
        constraintsNameLabel.weightx = 1.5;
        constraintsNameLabel.weighty = 0.0;
        add(nameLabel, constraintsNameLabel);

        constraintsAddressLabel.gridx = 1; constraintsAddressLabel.gridy = 0;
        constraintsAddressLabel.gridwidth = 1; constraintsAddressLabel.gridheight = 1;
        constraintsAddressLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsAddressLabel.anchor = GridBagConstraints.CENTER;
        constraintsAddressLabel.weightx = 1.2;
        constraintsAddressLabel.weighty = 0.0;
        add(addressLabel, constraintsAddressLabel);

        constraintsTelephoneLabel.gridx = 2; constraintsTelephoneLabel.gridy = 0;
        constraintsTelephoneLabel.gridwidth = 1; constraintsTelephoneLabel.gridheight = 1;
        constraintsTelephoneLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsTelephoneLabel.anchor = GridBagConstraints.CENTER;
        constraintsTelephoneLabel.weightx = 1.0;
        constraintsTelephoneLabel.weighty = 0.0;
        add(telephoneLabel, constraintsTelephoneLabel);

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
                Frame frame = new Frame();
                CustomerSelectHeaderBean aCustomerSelectHeaderBean;
                aCustomerSelectHeaderBean = new CustomerSelectHeaderBean();
                frame.add("Center", aCustomerSelectHeaderBean);
                frame.setSize(aCustomerSelectHeaderBean.getSize());
                frame.setVisible(true);
    }
}
