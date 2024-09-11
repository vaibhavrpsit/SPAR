/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SuspendListHeaderBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:36 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:12:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:19:00   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:22   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:35:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.pos.ui.EYSPOSColorIfc;

//---------------------------------------------------------------------
/**
    This bean creates the header for the transaction history list.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//---------------------------------------------------------------------
public class SuspendListHeaderBean extends JPanel 
{
    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        label weights
    **/
    protected double[] labelWeights = 
    {
//      0.25, 0.30, 0.35, 0.75
        1, 1, 1, 1
    };
    /**
        number of fields
    **/
    protected int numberFields = labelWeights.length;
    /**
        label text to be used if label text not found in properties
    **/
    protected String[] defaultLabelText =
    {
        "Register #/Till #",
        "Transaction #",
        "Item Desc.",
        "Sub-Total"     
    };
    /**
        labels array
    **/
    protected JLabel labels[] = new JLabel[numberFields];
    /**
        label names
    **/
    protected String[] labelNames =
    {
        "RegisterIDLabel",
        "TransactionIDLabel",
        "ItemDescriptionLabel",
        "SubtotalLabel"
    };
    /**
        name of properties containing label text
    **/
    protected String[] labelPropertyNames =
    {
        "SuspendListHeaderBean.RegisterIDLabel",
        "SuspendListHeaderBean.TransactionIDLabel",
        "SuspendListHeaderBean.ItemDescriptionLabel",
        "SuspendListHeaderBean.SubtotalLabel"
    };
    /** Properties **/
    protected Properties props = null;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public SuspendListHeaderBean() 
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
        label.setMaximumSize(new Dimension(65535, 19));
        label.setForeground(Color.black);
        label.setBackground(EYSPOSColorIfc.HeaderLabelBackground);
        label.setPreferredSize(new Dimension(4, 19));
        label.setMinimumSize(new Dimension(4, 19));
        label.setHorizontalAlignment(0);
        
        return label;
    }

    //---------------------------------------------------------------------
    /**
        Adds a label to the panel at the specified position in the
        GridBag layout.
        @param label JLabel component
        @param column column attribute for GridBag constraints
        @param row row attribute for GridBag constraints
        @param weightx weightx attribute for GridBagConstraints
    **/
    //---------------------------------------------------------------------
    protected void addLabel(JLabel label, int column, int row, double weightx) 
    {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        
        labelConstraints.gridx      = column;
        labelConstraints.gridy      = row;
        labelConstraints.gridwidth  = 1; 
        labelConstraints.gridheight = 1;
        labelConstraints.fill       = GridBagConstraints.HORIZONTAL;
        labelConstraints.anchor     = GridBagConstraints.CENTER;
        labelConstraints.weightx    = weightx;
        labelConstraints.weighty    = 0.0;

        add(label, labelConstraints);
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    private void initialize() 
    {
        setName("SuspendListHeaderBean");
        setLayout(new GridBagLayout());
        setBackground(new Color(255,255,255));
        setMaximumSize(new Dimension(2147483647, 2147483647));
        setSize(new Dimension(520, 19));
        setPreferredSize(new Dimension(520, 19));
        setBounds(new Rectangle(0, 0, 520, 19));
        setSize(520, 19);
        setMinimumSize(new Dimension(520, 19));

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
            for (int i = 0; i < numberFields; i++)
            {                               // loop through labels
                labels[i] = makeLabel();
                labels[i].setName(labelNames[i]);
                labels[i].setText(props.getProperty(labelPropertyNames[i], defaultLabelText[i]));
                addLabel(labels[i], i, 0, labelWeights[i]);
            }                               // end loop through labels
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
        String strResult = new String("Class:  SuspendListHeaderBean (Revision " +
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
        return(revisionNumber);
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
        SuspendListHeaderBean aSuspendListHeaderBean;
        aSuspendListHeaderBean = new SuspendListHeaderBean();
        frame.add("Center", aSuspendListHeaderBean);
        frame.setSize(aSuspendListHeaderBean.getSize());
        frame.setVisible(true);
    }
}
