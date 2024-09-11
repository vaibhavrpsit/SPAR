/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/VoidConfirmBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *    sgu       03/24/09 - remove invocations to deprecated reason code api
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         10/9/2007 10:33:34 AM  Anda D. Cadar
 *        Externalized static text
 *   5    360Commerce 1.4         3/29/2007 7:35:35 PM   Michael Boyd    CR
 *        26172 - v8x merge to trunk
 *
 *        5    .v8x      1.3.1.0     3/8/2007 10:01:35 PM   Brett J. Larsen CR
 *         4530
 *        - if a default value is selected for the code list... use it!
 *   4    360Commerce 1.3         1/25/2006 4:11:55 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse
 *:
 *   4    .v700     1.2.1.0     11/17/2005 16:20:50    Deepanshu       CR 6128:
 *        Migration from Gap. Updated initComponents() to localize
 *        reasonCodeLabel
 *   3    360Commerce1.2         3/31/2005 15:30:46     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:26:46     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:15:33     Robert Pearse
 *
 *  Revision 1.7  2004/07/22 19:03:38  aachinfiev
 *  @scr 6404 - Fixed selection of reason codes using letters.
 *
 *  Revision 1.6  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.5  2004/05/13 13:08:47  aachinfiev
 *  Fixed defect 4655. ReasonCodeField had no focus.
 *
 *  Revision 1.4  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/02 17:34:47  awilliam
 *  @scr 3823 reason code label is missing from psot void confirm screen
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 15 2004 17:51:16   epd
 * updated field to use combo box and default to blank top force user selection
 *
 *    Rev 1.1   Jan 15 2004 17:22:50   epd
 * Removed Date field
 *
 *    Rev 1.0   Aug 29 2003 16:13:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 10 2003 13:17:32   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Feb 19 2003 16:44:54   HDyer
 * Modified bean to set the void reason code text using localized strings, and to use the reason code list index to identify the selected reason code.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.3   Aug 14 2002 18:19:10   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 07 2002 19:34:28   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   27 Jun 2002 10:57:12   jbp
 * removed dirty model
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 14:54:18   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:38   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.0   Mar 18 2002 11:53:26   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 09 2002 10:46:14   mpm
 * More text externalization.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.utility.Util;
//import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
 * This bean uses the VoidConfirmBeanModel.
 * @see oracle.retail.stores.pos.ui.beans.VoidConfirmBeanModel
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class VoidConfirmBean extends ValidatingBean
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String REASON_LABEL = "ReasonCodeLabel";
    public static final String REASON_TEXT = "Reason Code:";

    // label and field placeholder constants
    public static final int NUMBER     = 0;
    public static final int TYPE       = 1;
    public static final int AMOUNT     = 2;
    public static final int MAX_FIELDS = 3;

    public static final String[] labelText =
    {
        "Transaction Number:", "Transaction Type:", "Amount:"
    };
    public static final String[] labelTags =
    {
        "TransactionNumberLabel", "TransactionTypeLabel", "AmountLabel"
    };
    /** array of labels */
    protected JLabel[] labels;

    /** array of display fields */
    protected JLabel[] fields;

    /** Label for reason code field */
    protected JLabel reasonCodeLabel;

    /** field for containing void reason codes */
    protected ValidatingComboBox reasonCodeField;

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public VoidConfirmBean()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    public void configure()
    {
        super.configure();

        uiFactory.configureUIComponent(this, UI_PREFIX);

        setName("VoidConfirmBean");
        beanModel = new VoidConfirmBeanModel();

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the bean components.
     */
    protected void initComponents()
    {
        labels = new JLabel[MAX_FIELDS];
        fields = new JLabel[MAX_FIELDS];

        for(int i=0; i<MAX_FIELDS; i++)
        {
            labels[i] = uiFactory.createLabel(retrieveText(labelTags[i],
                                                           labelText[i]), null, UI_LABEL);
            fields[i] = uiFactory.createLabel("", null, UI_LABEL);
        }

        reasonCodeLabel = uiFactory.createLabel(retrieveText(REASON_LABEL, REASON_TEXT), null, UI_LABEL);
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField", "false", "20");
        reasonCodeField.setVisible(true);
        reasonCodeField.setSelectedIndex(-1);
     }

    //--------------------------------------------------------------------------
    /**
     *  Creates the layout and lays out the components.
     */
    //--------------------------------------------------------------------------
    protected void initLayout()
    {
        // create arrays 1 element larger to account for
        // choice list and label
        JLabel[] allLabels = new JLabel[MAX_FIELDS + 1];
        JComponent[] allFields = new JComponent[MAX_FIELDS + 1];

        System.arraycopy(labels, 0, allLabels, 0, MAX_FIELDS);
        System.arraycopy(fields, 0, allFields, 0, MAX_FIELDS);

        // add the choice list and label
        allLabels[MAX_FIELDS] = reasonCodeLabel;
        allFields[MAX_FIELDS] = reasonCodeField;

        UIUtilities.layoutDataPanel(this, allLabels, allFields);
    }

    //--------------------------------------------------------------------------
    /**
     * Override JPanel set Visible to request focus.
     * @param aFlag indicates if the component should be visible or not.
     **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag && !errorFound())
        {
            setCurrentFocus(reasonCodeField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        reasonCodeField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        reasonCodeField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     * Updates the model property.
     */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        if(beanModel instanceof VoidConfirmBeanModel)
        {
            VoidConfirmBeanModel myModel =
                (VoidConfirmBeanModel)beanModel;

            myModel.setSelected(false);
            int reasonIndex = reasonCodeField.getSelectedIndex();

            if (reasonIndex >= 0)
            {
                myModel.setSelectedReasonCode(reasonIndex);
                myModel.setSelected(true);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel instanceof VoidConfirmBeanModel)
        {
            VoidConfirmBeanModel myModel =
                (VoidConfirmBeanModel)beanModel;

            fields[NUMBER].setText(myModel.getTransactionNumber());

            fields[TYPE].setText(retrieveText(myModel.getTransactionType()));

            if(!myModel.getAmountString().equals("0.00"))
            {
                fields[AMOUNT].setText(myModel.getAmountString());
            }
            else
            {
                fields[AMOUNT].setText("");
            }

            // Populate the list model with the I18N strings
            ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(myModel.getReasonCodes());
            reasonCodeField.setModel(listModel);
            if(myModel.isSelected())
            {
                reasonCodeField.setSelectedIndex(myModel.getSelectedIndex());
            }
            else
            {
                reasonCodeField.setSelectedItem(UIUtilities.retrieveCommonText(myModel.getDefaultValue()));
            }

        }
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        super.updatePropertyFields();
        if (labels != null)
        {
            for(int i = 0; i < MAX_FIELDS; i++)
            {
                labels[i].setText(retrieveText(labelTags[i],
                                               labelText[i]));
            }
        }

        if (reasonCodeLabel != null)
        {
            reasonCodeLabel.setText(retrieveText(REASON_LABEL, REASON_TEXT));
        }
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: VoidConfirmBean (Revision " +
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
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        VoidConfirmBean bean = new VoidConfirmBean();
        bean.configure();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
