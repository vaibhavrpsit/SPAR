/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerHistoryListHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:09:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:04   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:56:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:29:34   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   19 Oct 2001 15:34:08   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//---------------------------------------------------------------------
/**
    This bean creates the header for the transaction history list.
    @version $KW=@(#); $Ver=pos_4.5.0:16; $EKW;
*/
//---------------------------------------------------------------------
public class CustomerHistoryListHeaderBean extends JPanel
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:16; $EKW;";
    /**
        Header "Date" label
    **/
    protected JLabel dateLabel = null;
    /**
        Header "Location" label
    **/
    protected JLabel locationLabel = null;
    /**
        Header "Transaction Type" label
    **/
    protected JLabel typeLabel = null;
    /**
        Header "Total Amount" label
    **/
    protected JLabel totalLabel = null;
    /**
        Header "Transaction Number" label
    **/
    protected JLabel transactionLabel = null;

    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public CustomerHistoryListHeaderBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * Make a new label object.
     * @return JLabel
     */
    //---------------------------------------------------------------------
    protected JLabel makeLabel()
    {
        JLabel label = new JLabel();

        label.setOpaque(true);
        label.setBorder(BorderFactory.createLoweredBevelBorder());
        label.setForeground(Color.black);
        label.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        return label;
    }

    //---------------------------------------------------------------------
    /**
     * Make a new constraints object.
     * @return JLabel
     */
    //---------------------------------------------------------------------
    protected GridBagConstraints makeConstraints()
    {
        GridBagConstraints labelConstraints = new GridBagConstraints();

        labelConstraints.gridy      = 0;
        labelConstraints.gridwidth  = 1;
        labelConstraints.gridheight = 1;
        labelConstraints.fill       = GridBagConstraints.HORIZONTAL;
        labelConstraints.anchor     = GridBagConstraints.CENTER;
        labelConstraints.weighty    = 1.0;
        labelConstraints.weightx    = 2.0;

        return labelConstraints;
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("CustomerHistoryListHeaderBean");
        setLayout(new GridBagLayout());
        setBackground(Color.white);
        GridBagConstraints labelConstraints = makeConstraints();

        // Build Date Label and constraints
        labelConstraints.gridx = 0;
        transactionLabel = makeLabel();
        transactionLabel.setName("TransactionLabel");
        transactionLabel.setText("Transaction #");
        add(transactionLabel, labelConstraints);


        // Build Location Label and constaints
        labelConstraints.gridx = 1;
        labelConstraints.weightx = 3.0;

        locationLabel = makeLabel();
        locationLabel.setName("LocationLabel");
        locationLabel.setText("Location");
        add(locationLabel, labelConstraints);

        // Build Date Label and constraints
        labelConstraints.gridx = 2;
        labelConstraints.weightx = 2.0;
        typeLabel = makeLabel();
        typeLabel.setName("TypeLabel");
        typeLabel.setText("Type");
        add(typeLabel, labelConstraints);

        // Build item desc Label and constaints
        labelConstraints.gridx = 3;
        dateLabel = makeLabel();
        dateLabel.setName("DateLabel");
        dateLabel.setText("Date");
        add(dateLabel, labelConstraints);

        // Build transaction total label and constraints.
        labelConstraints.gridx = 4;
        labelConstraints.weightx = 1.0;
        totalLabel = makeLabel();
        totalLabel.setName("TotalLabel");
        totalLabel.setText("Total");
        add(totalLabel, labelConstraints);

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
            transactionLabel.setText(props.getProperty("CustomerHistoryListHeaderBean.TransactionLabel","Transaction"));
            locationLabel.setText(props.getProperty("CustomerHistoryListHeaderBean.LocationLabel","Location"));
            typeLabel.setText(props.getProperty("CustomerHistoryListHeaderBean.TypeLabel","Type"));
            dateLabel.setText(props.getProperty("CustomerHistoryListHeaderBean.DateLabel","Date"));
            totalLabel.setText(props.getProperty("CustomerHistoryListHeaderBean.TotalLabel","Total"));
        }
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CustomerHistoryListHeaderBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        CustomerHistoryListHeaderBean aCustomerHistoryListHeaderBean;
        aCustomerHistoryListHeaderBean = new CustomerHistoryListHeaderBean();
        frame.add("Center", aCustomerHistoryListHeaderBean);
        frame.setSize(aCustomerHistoryListHeaderBean.getSize());
        frame.setVisible(true);
    }
}
