/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StatusHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:24 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:12:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:50   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:10   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:35:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//---------------------------------------------------------------------
/**
   StatusHeaderBean
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class StatusHeaderBean extends JPanel 
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Device label
    **/    
    protected JLabel deviceLabel = null;
    /**
        Status label
    **/    
    protected JLabel statusLabel = null;
    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public StatusHeaderBean() 
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
        GridBagConstraints constraintsDeviceLabel = new GridBagConstraints();
        GridBagConstraints constraintsStatusLabel = new GridBagConstraints();
        setName("StatusHeaderBean");
        setLayout(new java.awt.GridBagLayout());
        setBackground(new Color(255,255,255));
        setMaximumSize(new Dimension(2147483647, 2147483647));
        setSize(new Dimension(520, 19));
        setPreferredSize(new Dimension(520, 19));
        setBounds(new Rectangle(0, 0, 520, 19));
        setSize(520, 19);
        setMinimumSize(new Dimension(520, 19));

        constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 0;
        constraintsDeviceLabel.gridwidth = 1; constraintsDeviceLabel.gridheight = 1;
        constraintsDeviceLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsDeviceLabel.anchor = GridBagConstraints.CENTER;
        constraintsDeviceLabel.weightx = 1.9;
        constraintsDeviceLabel.weighty = 0.0;
        deviceLabel = new JLabel();
        deviceLabel.setName("DeviceLabel");
        deviceLabel.setOpaque(true);
        deviceLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        deviceLabel.setText("Device/Database");
        deviceLabel.setMaximumSize(new Dimension(65535, 19));
        deviceLabel.setForeground(Color.black);
        deviceLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        deviceLabel.setPreferredSize(new Dimension(4, 19));
        deviceLabel.setMinimumSize(new Dimension(4, 19));
        deviceLabel.setHorizontalAlignment(0);
        add(deviceLabel, constraintsDeviceLabel);

        constraintsStatusLabel.gridx = 1; constraintsStatusLabel.gridy = 0;
        constraintsStatusLabel.gridwidth = 1; constraintsStatusLabel.gridheight = 1;
        constraintsStatusLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsStatusLabel.anchor = GridBagConstraints.CENTER;
        constraintsStatusLabel.weightx = 2.1;
        constraintsStatusLabel.weighty = 0.0;
        statusLabel = new JLabel();
        statusLabel.setName("StatusLabel");
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setText("Status");
        statusLabel.setMaximumSize(new Dimension(65535, 19));
        statusLabel.setForeground(Color.black);
        statusLabel.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        statusLabel.setPreferredSize(new Dimension(4, 19));
        statusLabel.setMinimumSize(new Dimension(4, 19));
        statusLabel.setHorizontalAlignment(0);
        add(statusLabel, constraintsStatusLabel);

        updatePropFields();
    }
  
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        this.props = props;
        updatePropFields();
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
            deviceLabel.setText(props.getProperty("StatusHeaderBean.DeviceLabel","Device/Database"));
            statusLabel.setText(props.getProperty("StatusHeaderBean.StatusLabel","Status"));
        }
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()

        // result string
        String strResult = new String("Class: StatusHeaderBean (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                  // end toString()

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
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args) 
    {
        java.awt.Frame frame = new java.awt.Frame();
        StatusHeaderBean aStatusHeaderBean;
        aStatusHeaderBean = new StatusHeaderBean();
        frame.add("Center", aStatusHeaderBean);
        frame.setSize(aStatusHeaderBean.getSize());
        frame.setVisible(true);
    }
}
