/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GetPickupDateBean.java /main/15 2012/12/31 10:23:05 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/28/12 - Setting a default pickup date
 *    cgreene   11/08/10 - update layout to not stretch field widget
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    mahising  03/05/09 - Fixed local date format issue for pickup delivery
 *    mahising  03/04/09 - Fixed issue for pickup date lable format
 *    mahising  02/25/09 - Code clean up for pickup date lable date format
 *    mahising  02/23/09 - Fixed issue for pickup date lable format
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - get pickup date bean
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

public class GetPickupDateBean extends ValidatingBean implements DocumentListener
{
    private static final long serialVersionUID = -2465489245766925342L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    protected JLabel pickupDateLabel = null;

    protected EYSDateField pickupDateField = null;

    protected GetPickupDateBeanModel beanModel = new GetPickupDateBeanModel();

    // pickup date text
    protected static String labelText = "Pickup Date ({0}):";

    // pickup date label
    public String labelTags = "PickupDateLabel";

    /**
     * Constructs bean.
     */
    public GetPickupDateBean()
    {
        super();
        initialize();
    }

    /**
     * Returns ValidatingComboBox value. Instantiates field if necessary.
     *
     * @return ValidatingComboBox panel
     */
    protected ValidatingTextField getPickupDateField()
    {
        return pickupDateField;
    }

    /**
     * Returns service item list label. Instantiates label component, if
     * necessary.
     *
     * @return label
     */
    protected JLabel getPickupDateLabel()
    {
        return pickupDateLabel;
    }

    /**
     * Returns the bean model
     *
     * @return model object
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Sets the model data into the bean fields.
     *
     * @param model the bean model
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the ServiceItemListBean model to null.");
        }

        if (model instanceof GetPickupDateBeanModel)
        {
            beanModel = (GetPickupDateBeanModel)model;
            updateBean();

        }

    }

    /**
     * Updates the model for the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setSelectedPickupDate(pickupDateField.getDate());
    }

    /**
     * Updates the bean for the current settings of this bean.
     */
    @Override
    public void updateBean()
    {
        if (beanModel.getSelectedPickupDate() != null)
        {
            EYSDate list = beanModel.getSelectedPickupDate();
            pickupDateField.setDate(list);
        }
    }

    /**
     * Initializes the class.
     */
    protected void initialize()
    {
        setName("GetPickupDateBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);
        pickupDateLabel = uiFactory.createLabel("pickupDateLabel", "PickUpDate", null, UI_LABEL);
        pickupDateField = uiFactory.createEYSDateField("pickupDateField");
        initLayout();
    }

    /**
     * Initializes the layout.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                new JLabel[] { pickupDateLabel },
                new JComponent[] { pickupDateField }, false);
    }

    /**
     * Overrides JPanel setVisible() method to request focus.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(pickupDateField);
        }
    }

    /**
     * Activates this bean.
     */
    @Override
    public void activate()
    {
        updateBean();
        super.activate();
        pickupDateField.addFocusListener(this);
        pickupDateField.getDocument().addDocumentListener(this);
    }

    /**
     * Deactivates this bean.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        pickupDateField.getDocument().removeDocumentListener(this);
        pickupDateField.removeFocusListener(this);
        pickupDateField.setText(null);
    }

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {

        String translatedLabel = getTranslatedDatePattern();
        String dateLabel = "";

        dateLabel = retrieveText(labelTags, labelText);
        pickupDateLabel.setText(LocaleUtilities.formatComplexMessage(dateLabel, translatedLabel));
        pickupDateField.setLabel(pickupDateLabel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ServiceItemListBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new GetPickupDateBean());
    }

    /**
     * Marks the start date field and end date field to be required if data is
     * entered in either field (pickupDate), otherwise the fields are marked as
     * optional.
     */
    protected void setRequiredFields()
    {
        if (!pickupDateField.getText().equals(""))

        {
            setFieldRequired(pickupDateField, true);

        }
        else
        {
            setFieldRequired(pickupDateField, false);

        }
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void changedUpdate(DocumentEvent e)
    {
        setRequiredFields();

    }

    /**
     * Implementation of DocumentListener interface.
     *
     * @param e a document event
     */
    public void insertUpdate(DocumentEvent e)
    {
        setRequiredFields();

    }

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
