/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CurrencyDetailBean.java /rgbustores_13.4x_generic_branch/2 2011/11/10 12:54:39 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/10/11 - rollback change to denominationDAO that switch name
 *                         and displayName
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       03/02/09 - use denomination descriptions from its I18n table
 *    mdecama   01/26/09 - Increased the size of the Total Field.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         10/8/2007 1:56:14 PM   Peter J. Fierro Add a
 *          method that lays out dual columns for CurrencyDetailBean
 *    7    360Commerce 1.6         5/18/2007 9:18:14 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    6    360Commerce 1.5         5/8/2007 11:32:26 AM   Anda D. Cadar
 *         currency changes for I18N
 *    5    360Commerce 1.4         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:45:22 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:16 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/19 17:12:25  dcobb
 *   @scr 5855 Pickup and Loan: Data Field for Register ID is incorrect
 *   Updates to version 7 Pickup & Loan REQ.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Feb 09 2004 10:38:38   DCobb
 * Refresh the register field from the bean model.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.2   Feb 06 2004 17:05:48   DCobb
 * Added Currency Detail screens for Pickup & Loan.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.1   Sep 10 2003 15:27:40   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 14 2003 16:18:12   RSachdeva
 * Max changed
 * Resolution for POS SCR-3047: Till Pickup Screen - Detail Currency Screen  US/Candian- Max should be 9 but is only 4
 *
 *    Rev 1.4   Apr 18 2003 09:42:32   baa
 * fixes to bundles
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.3   Jan 17 2003 17:22:00   crain
 * Retrieved the labels from the bundle
 * Resolution for 1928: System crashes when getting to Currency Detail screen
 *
 *    Rev 1.2   Jan 17 2003 16:03:36   crain
 * Removed the components in setModel()
 * Resolution for 1911: Returning from Help to Currency Detail changes the UI
 *
 *    Rev 1.1   Aug 14 2002 18:17:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:58   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:34   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:54:40   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 08 2002 11:00:10   mpm
 * Externalized text for poscount UI screens.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Allows the user to enter currency count detail.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class CurrencyDetailBean extends ValidatingBean
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 1111196108637301213L;

    /** version from revision system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /** holds the max number of rows on the panel */
    private static final int MAX_PER_COLUMN = 9;

    /** total label key **/
    protected static String TOTAL_LABEL = "TotalLabel";

    /** register label keys **/
    protected static String TO_REGISTER_LABEL = "ToRegisterLabel";
    protected static String FROM_REGISTER_LABEL = "FromRegisterLabel";

    /** the bean model */
    private CurrencyDetailBeanModel beanModel = null;

    /** listener for updating the total field */
    private CountListener countListener = new CountListener();

    /** listener for highlighting fields */
    private SelectListener selectListener = new SelectListener();

    /** listener for setting caret to visible */
    private CaretListener caretListener = new CaretListener();

    /** the total field */
    private ConstrainedTextField totalField = null;

    /** the total label */
    private JLabel totalLabel = null;

    /** all labels */
    private JLabel[] fieldLabels = null;

    /** the count fields */
    private NumericTextField[] countFields = null;

    /** The number of fields on the screen. */
    private int numberOfFields = 0;

    /**
     * the to/from register field for Pickup/Loan when operating without a safe.
     */
    private NumericTextField registerField = null;

    /** the label for the toRegisterField. */
    private JLabel registerLabel = null;

    /**
     * Initializes and places the totals field in the panel.
     */
    private void initTotalField()
    {
        // set the label
        String labelText = retrieveText(TOTAL_LABEL, "Total:");
        totalLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

        // set the field.
        totalField = uiFactory.createConstrainedField("TotalField", "1", "14", "14");
        totalField.setEditable(false);
        totalField.setEnabled(true);
    }

    /**
     * Initializes the register field for a pickup or loan when operating
     * without a safe.
     */
    private void initRegisterField()
    {
        registerField = null;
        if (!beanModel.getOperateWithSafeFlag())
        {
            // Set the label
            String labelText = null;
            if (beanModel.getPickupFlag())
            {
                labelText = retrieveText(TO_REGISTER_LABEL, "To Register:");
            }
            else if (beanModel.getLoanFlag())
            {
                labelText = retrieveText(FROM_REGISTER_LABEL, "From Register:");
            }

            if (labelText != null)
            {
                registerLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

                // set the field
                registerField = uiFactory.createNumericField("registerField", "1", "3");
                registerField.setEditable(true);
                registerField.setEnabled(true);

                // make this a required field
                registerField.setLabel(registerLabel);
                setFieldRequired(registerField, true);

                // set the register field from the bean model
                if (beanModel.getRegister() != null)
                {
                    registerField.setText(beanModel.getRegister());
                }
            }
        }
    }

    /**
     * Called right after screen is displayed.
     */
    public void setVisible()
    {
        if (countFields != null && countFields[0] != null)
        {
            setCurrentFocus(countFields[0]);
        }
    }

    /**
     * Called when the panel is removed from the screen.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        removeAll();

        for (int cnt = 0; cnt < numberOfFields; cnt++)
        {
            countFields[cnt].getDocument().removeDocumentListener(countListener);
            countFields[cnt].removeFocusListener(selectListener);
            countFields[cnt].removeKeyListener(caretListener);
        }
        numberOfFields = 0;
    }

    /**
     * Controls setup of all the labels and fields.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        // Set the field values and listeners
        Long[] counts = beanModel.getDenominationCounts();

        for (int cnt = 0; cnt < numberOfFields; cnt++)
        {
            if (counts == null)
            {
                countFields[cnt].setLongValue(0L);
            }
            else
            {
                countFields[cnt].setLongValue(counts[cnt].longValue());
            }
            countFields[cnt].getDocument().addDocumentListener(countListener);
            countFields[cnt].addFocusListener(selectListener);
            countFields[cnt].addKeyListener(caretListener);
        }
        initTotalField();
        initRegisterField();

        if (numberOfFields <= MAX_PER_COLUMN)
        {
            // this call assumes 1 column of labels + their associated
            // components
            UIUtilities.layoutDataPanel(this, fieldLabels, countFields);
            // add the total field
            UIUtilities.layoutComponent(this, totalLabel, totalField, 0, numberOfFields + 2, true);

            // add the register field if available
            if (registerField != null)
            {
                UIUtilities.layoutComponent(this, registerLabel, registerField, 0, numberOfFields + 4, true);
            }
        }
        else
        {
            // this call lays out 2 column of labels + their associated
            // components
            UIUtilities.layoutDualPanel(this, fieldLabels, countFields, totalLabel, totalField, registerLabel,
                    registerField);
        }

        // Set the total field value.
        fireCountEvent();
    }

    /**
     * Sets up the labels.
     */
    private void initLabels()
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String[] lblText = beanModel.getTotal().getDenominationDisplayNames(locale);

        for (int cnt = 0; cnt < numberOfFields; cnt++)
        {
            fieldLabels[cnt] = uiFactory.createLabel(lblText[cnt], lblText[cnt], null, UI_LABEL);
        }
    }

    /**
     * Sets up the fields.
     */
    private void initFields()
    {
        for (int cnt = 0; cnt < numberOfFields; cnt++)
        {
            countFields[cnt] = uiFactory.createNumericField("CountField" + cnt, "1", "9");
            countFields[cnt].setColumns(8);
            countFields[cnt].setLabel(fieldLabels[cnt]);
        }
    }

    /**
     * Calculates the value of the total amount field.
     */
    private void fireCountEvent()
    {
        CurrencyIfc total = (CurrencyIfc)beanModel.getTotal().clone();
        total.setStringValue("0");

        for (int i = 0; i < numberOfFields; i++)
        {
            long count = getCountFromField(i);
            total = total.add(calculateCurrencyAmount(count, i));
        }

        // get base currency type format
        // String pattern =
        // DomainGateway.getBaseCurrencyType().getCurrencyDisplayFormat();
        totalField.setText(total.toFormattedString());
    }

    /**
     * Get calculate the dollar amount of an individual denomination.
     * 
     * @param int the number of pennies, pesoes, francs the user has counted.
     * @param int the index of the denomination.
     */
    protected CurrencyIfc calculateCurrencyAmount(long count, int index)
    {
        CurrencyIfc amount = (CurrencyIfc)beanModel.getTotal().clone();

        String[] value = amount.getDenominationValues();
        amount.setStringValue(value[index]);
        amount = amount.multiply(new BigDecimal(count));

        return amount;
    }

    /**
     * Get the count from the field and Calculate the dollar amount of an
     * individual denomination.
     */
    protected long getCountFromField(int index)
    {
        long value = 0;
        try
        {
            value = countFields[index].getLongValue();
        }
        catch (java.lang.NumberFormatException e)
        {
        }

        return value;
    }

    /**
     * Gets the data from the screen, puts it in the model and returns the
     * model.
     */
    @Override
    public void updateModel()
    {
        CurrencyIfc total = (CurrencyIfc)beanModel.getTotal().clone();
        total.setStringValue("0");
        Long[] counts = new Long[numberOfFields];

        for (int i = 0; i < numberOfFields; i++)
        {
            long count = getCountFromField(i);
            counts[i] = new Long(count);
            total = total.add(calculateCurrencyAmount(count, i));
        }
        beanModel.setDenominationCounts(counts);
        beanModel.setTotal(total);

        if (registerField != null)
        {
            beanModel.setRegister(registerField.getText());
        }
    }

    /**
     * Returns the bean model.
     * 
     * @return model object
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Sets the model in the bean.
     * 
     * @param the model
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set CurrencyDetailBean" + " to null");
        }
        
        if (model instanceof CurrencyDetailBeanModel)
        {
            beanModel = (CurrencyDetailBeanModel)model;

            removeAll();

            for (int cnt = 0; cnt < numberOfFields; cnt++)
            {
                countFields[cnt].getDocument().removeDocumentListener(countListener);
                countFields[cnt].removeFocusListener(selectListener);
                countFields[cnt].removeKeyListener(caretListener);
            }
            numberOfFields = 0;

            buildScreen();
        }
    }

    /**
     * The field labels and number of data entry fields are contained in the
     * model, so the screen must be built on the fly.
     */
    private void buildScreen()
    {
        numberOfFields = beanModel.getTotal().getDenominationNames().length;
        fieldLabels = new JLabel[numberOfFields];
        countFields = new NumericTextField[numberOfFields];

        for (int cnt = 0; cnt < numberOfFields; cnt++)
        {
            fieldLabels[cnt] = new JLabel();
            countFields[cnt] = new NumericTextField();
        }

        initLabels();
        initFields();
        updatePropertyFields();
        initialize();

        // Set the tabbing order
        this.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        setCurrentFocus(countFields[0]);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return new String("Class: " +
                Util.getSimpleClassName(this.getClass()) +
                "(Revision " + getRevisionNumber() + ") @" + hashCode());

    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {
        if (totalLabel != null)
        {
            totalLabel.setText(retrieveText(TOTAL_LABEL, totalLabel));
        }

        if (totalField != null)
        {
            totalField.setLabel(totalLabel);
        }
    }

    // --------------------------------------------------------------------------
    /**
     * This inner class is a listener that causes the updates to the total
     * field.
     */
    class CountListener implements DocumentListener
    {

        public CountListener()
        {
        }

        public void changedUpdate(DocumentEvent evt)
        {
            fireCountEvent();
        }

        public void insertUpdate(DocumentEvent evt)
        {
            fireCountEvent();
        }

        public void removeUpdate(DocumentEvent evt)
        {
            fireCountEvent();
        }
    }

    // --------------------------------------------------------------------------
    /**
     * This inner class is a listener that highlights the data in the field.
     */
    private class SelectListener extends FocusAdapter
    {
        public void focusGained(FocusEvent fe)
        {
            NumericTextField field = (NumericTextField)fe.getComponent();
            field.selectAll();
            field.getCaret().setVisible(false);
        }
    }

    // --------------------------------------------------------------------------
    /**
     * This inner class is a listener that makes the caret visible when a key.
     */
    private class CaretListener extends KeyAdapter
    {
        public void keyTyped(KeyEvent ke)
        {
            NumericTextField field = (NumericTextField)ke.getComponent();
            field.getCaret().setVisible(true);
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Main entry point for testing.
     * 
     * @param args array of arguments passed in
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        CurrencyDetailBean bean = new CurrencyDetailBean();

        CurrencyDetailBeanModel model = new CurrencyDetailBeanModel();
        model.setTotal(DomainGateway.getBaseCurrencyInstance());
        model.setSummaryCurrencyDescription("Cash");

        bean.setModel(model);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
