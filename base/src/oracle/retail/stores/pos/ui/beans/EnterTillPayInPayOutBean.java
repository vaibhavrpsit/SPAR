/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayInPayOutBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.2.3.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
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
 *    Rev 1.1   Sep 16 2003 17:52:34   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 10 2003 15:08:12   bwf
 * Remove instanceof UtilityManagerIfc and replace with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Feb 13 2003 16:32:16   HDyer
 * Fixed deprecated method problem.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.3   Feb 13 2003 10:43:42   HDyer
 * Modified due to bean model changes. Use common UtilityManager method to get internationalized strings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Feb 11 2003 12:53:44   HDyer
 * Display localized strings in pay in/out reason pulldown list.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Aug 14 2002 18:17:42   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:50:20   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:36   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:10   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 07 2002 14:53:00   mpm
 * Text externalization for till UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
    Contains the visual presentation for Till Pay In and Pay Out Information
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class EnterTillPayInPayOutBean extends ValidatingBean
{
    /**
        Revision number.
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** Amount field and label index */
    protected static final int AMOUNT      = 0;
    /** Reason Code field and label index */
    protected static final int REASON_CODE = AMOUNT + 1;
    /** Highest field and label index */
    //add one because of 0 index!
    protected static final int MAX_FIELDS  = REASON_CODE + 1;

    /** Array of label text */
    protected static String labelText[] =
    {
        "Amount",
        "Reason Code",
    };

    /** Array of label text */
    protected static String labelTags[] =
    {
        "AmountLabel",
        "ReasonCodeLabel",
    };

    /** fieldLabels is an array of JLabels */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];

    /** the till payin or payout amount */
    protected CurrencyTextField amountField = null;

    /** the till payin or payout reason */
    protected ValidatingComboBox reasonCodeField = null;

    /** beanModel is the EnterTillPayInPayOut bean model */
    protected EnterTillPayInPayOutBeanModel beanModel
                                       = new EnterTillPayInPayOutBeanModel();

    //------------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //------------------------------------------------------------------------
    public EnterTillPayInPayOutBean()
    {
        super();
        initialize();
    }

    //------------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //------------------------------------------------------------------------
    protected void initialize()
    {
        setName("EnterTillPayInPayOutBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    //------------------------------------------------------------------------
    /**
     * Initialize the components.
     */
    //------------------------------------------------------------------------
    protected void initComponents()
    {
        fieldLabels[AMOUNT] =
            uiFactory.createLabel(labelText[AMOUNT], null, UI_LABEL);
        fieldLabels[REASON_CODE] =
            uiFactory.createLabel(labelText[REASON_CODE], null, UI_LABEL);

        amountField = uiFactory.createCurrencyField("amountField","false","false","true");
        amountField.setRequired(true);
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField", "false", "10");
    }

    //------------------------------------------------------------------------
    /**
     * Initialize the setting for the field labels and place the on the panel
     */
    //------------------------------------------------------------------------
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel
        (
            this,
            fieldLabels,
            new JComponent[] {amountField, reasonCodeField}
        );
    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setAmount(amountField.getText());
        beanModel.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
    }
    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    public void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for(int i = 0; i < fieldLabels.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                labelText[i]));
        }
        amountField.setLabel(fieldLabels[AMOUNT]);
        reasonCodeField.setLabel(fieldLabels[REASON_CODE]);

    }                                   // end updatePropertyFields()



    //------------------------------------------------------------------------
    /**
     * Sets the model for the current settings of this bean.
     * @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set " +
                          "EnterTillPayInPayOutBeanModel to null");
        }
        Object oldValue = beanModel;
        if (model instanceof EnterTillPayInPayOutBeanModel)
        {
            beanModel = (EnterTillPayInPayOutBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the bean if the model's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        amountField.setText(beanModel.getAmount());

        Vector reasonCodes = beanModel.getReasonCodes();
        if (reasonCodes != null)
        {
            reasonCodeField.setModel(new DefaultComboBoxModel(reasonCodes));

            if(beanModel.getSelectedIndex() > -1)
            {
                reasonCodeField.setSelectedIndex
                                      (beanModel.getSelectedIndex());
            }
        }
    }

    //------------------------------------------------------------------------
    /**
     * Overrides the inherited setVisible() to set the focus on the reply area.
       @param value boolean
    */
    //------------------------------------------------------------------------
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(amountField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Activates this bean.
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        amountField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Deactivates this bean.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        amountField.removeFocusListener(this);
    }

    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @param none
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
      public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //--------------------------------------------------------------------------
    /**
     *    Main entry point for testing.
     *    @param args string arguments
     */
     public static void main(String[] args)
     {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EnterTillPayInPayOutBean());
     }
}
