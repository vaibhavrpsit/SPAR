/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GetDeliveryDateBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    mahising  03/05/09 - Fixed local date format issue for pickup delivery
 *    mahising  02/25/09 - Code clean up for delivery lable format
 *    mahising  02/23/09 - Fixed issue for delivery date lable format
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - get delivery date bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

public class GetDeliveryDateBean extends ValidatingBean implements DocumentListener
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected JLabel deliveryDateLabel = null;

    protected EYSDateField deliveryDateField = null;

    protected GetDeliveryDateBeanModel beanModel = new GetDeliveryDateBeanModel();

    // Delivery date text
    protected static String labelText = "Delivery Date ({0}):";

    // Delivery date label
    public String labelTags = "DeliveryDateLabel";

    // ----------------------------------------------------------------------
    /**
     * Constructs bean.
     */
    // ----------------------------------------------------------------------
    public GetDeliveryDateBean()
    {
        super();
        initialize();
    }

    // ---------------------------------------------------------------------
    /**
     * Returns ValidatingComboBox value. Instantiates field if necessary.
     * <P>
     *
     * @return ValidatingComboBox panel
     */
    // ---------------------------------------------------------------------
    protected ValidatingTextField getDeliveryDateField()
    { // begin getServiceItemListField()
        return deliveryDateField;
    } // end getServiceItemListField()

    // ---------------------------------------------------------------------
    /**
     * Returns service item list label. Instantiates label component, if
     * necessary.
     * <P>
     *
     * @return label
     */
    // ---------------------------------------------------------------------
    protected JLabel getDeliveryDateLabel()
    {
        return deliveryDateLabel;
    }

    /**
     * Returns the bean model
     *
     * @return model object
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    // ---------------------------------------------------------------------
    /**
     * Sets the model data into the bean fields.
     * <P>
     *
     * @param model the bean model
     */
    // ---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    { // begin setModel()
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the ServiceItemListBean model to null.");
        }

        if (model instanceof GetDeliveryDateBeanModel)
        {
            beanModel = (GetDeliveryDateBeanModel)model;
            updateBean();

        }

    } // end setModel()

    // ------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    // ------------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setSelectedDeliveryDate(deliveryDateField.getDate());

    }

    // ------------------------------------------------------------------------
    /**
     * Updates the bean for the current settings of this bean.
     */
    // ------------------------------------------------------------------------
    public void updateBean()
    {

        if (beanModel.getSelectedDeliveryDate() != null)
        {
            EYSDate list = beanModel.getSelectedDeliveryDate();
            deliveryDateField.setDate(list);
            deliveryDateField.setText("");
        }
    }

    // ---------------------------------------------------------------------
    /**
     * Initializes the class.
     */
    // ---------------------------------------------------------------------
    protected void initialize()
    {
        setName("GetDeliveryDateBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        deliveryDateLabel = uiFactory.createLabel("deliveryDateLabel", "DeliveryDate", null, UI_LABEL);
        deliveryDateField = uiFactory.createEYSDateField("deliveryDateField");
        UIUtilities.layoutDataPanel(this, new JLabel[] { deliveryDateLabel }, new JComponent[] { deliveryDateField }, false);
    }

    // ---------------------------------------------------------------------
    /**
     * Overrides JPanel setVisible() method to request focus.
     * <P>
     */
    // ---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(deliveryDateField);
        }
    }

    // ---------------------------------------------------------------------
    /**
     * Activates this bean.
     */
    // ---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        deliveryDateField.addFocusListener(this);
        deliveryDateField.getDocument().addDocumentListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Deactivates this bean.
     */
    // ---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        deliveryDateField.getDocument().removeDocumentListener(this);
        deliveryDateField.removeFocusListener(this);
        deliveryDateField.setText(null);
    }

    // ---------------------------------------------------------------------
    /**
     * Updates property-based fields.
     */
    // ---------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        // begin updatePropertyFields()

        String translatedLabel = getTranslatedDatePattern();
        String dateLabel = "";
        dateLabel = retrieveText(labelTags, labelText);
        deliveryDateLabel.setText(LocaleUtilities.formatComplexMessage(dateLabel, translatedLabel));
        deliveryDateField.setLabel(deliveryDateLabel);

    } // end updatePropertyFields()

    // ---------------------------------------------------------------------
    /**
     * Returns default display string.
     * <P>
     *
     * @return String representation of object
     */
    // ---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: ServiceItemListBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    // ---------------------------------------------------------------------
    /**
     * Main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    // ---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new GetDeliveryDateBean());
    }

    // ---------------------------------------------------------------------
    /**
     * Marks the start date field and end date field to be required if data is
     * entered in either field (pickupDate), otherwise the fields are marked as
     * optional.
     **/
    // ---------------------------------------------------------------------
    protected void setRequiredFields()
    {
        if (!deliveryDateField.getText().equals(""))

        {
            setFieldRequired(deliveryDateField, true);

        }
        else
        {
            setFieldRequired(deliveryDateField, false);

        }
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    // ----
    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */

    public void changedUpdate(DocumentEvent e)
    {
        setRequiredFields();

    }

    //--------------------------------------------------------------------------
    // ----
    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void insertUpdate(DocumentEvent e)
    {
        setRequiredFields();

    }

    //--------------------------------------------------------------------------
    // ----
    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */

    public void removeUpdate(DocumentEvent e)
    {
        setRequiredFields();

    }
}
