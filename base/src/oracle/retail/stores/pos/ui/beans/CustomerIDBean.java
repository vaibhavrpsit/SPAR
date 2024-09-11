/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerIDBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    acadar    04/23/09 - translate credit card expiration date format
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.3.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    4    360Commerce 1.3         8/9/2007 4:05:55 PM    Mathews Kochummen
 *         handle drivers license dates for all locales
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
 *
 *   Revision 1.8  2004/07/22 15:27:19  aschenk
 *   @scr 5912 - Exp. Date field is now refreshed with the correct value.
 *
 *   Revision 1.7  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.6  2004/04/29 13:24:56  tmorris
 *   @scr 4265 -State drop box not being filled has been fixed.
 *
 *   Revision 1.5  2004/03/24 21:29:12  khassen
 *   @scr 4125 - added setVisible() method to set the focus of the bean.
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
 *    Rev 1.1   Nov 03 2003 11:47:22   epd
 * Updated for internationalization
 *
 *    Rev 1.0   Oct 31 2003 16:51:58   epd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Work panel bean for credit card info entry
 */
public class CustomerIDBean extends ValidatingBean
{
    private static final long serialVersionUID = -4231941813511586247L;

    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * fields and labels for customer ID data
     */
    protected JLabel idTypeLabel = null;
    protected JLabel idStateLabel = null;
    protected JLabel idCountryLabel = null;
    protected JLabel expirationDateLabel = null;

    protected ValidatingComboBox idTypeField = null;
    protected ValidatingComboBox idStateField = null;
    protected ValidatingComboBox idCountryField = null;
    protected EYSDateField       expirationDateField = null;

    // The bean model
    protected CustomerIDBeanModel beanModel = new CustomerIDBeanModel();

    /**
     * Constructor
     */
    public CustomerIDBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("CustomerIDBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
        idTypeLabel         = uiFactory.createLabel("IdTypeLabel", "IdTypeLabel", null, UI_LABEL);
        idCountryLabel      = uiFactory.createLabel("IdCountryLabel", "IdCountryLabel", null, UI_LABEL);
        idStateLabel        = uiFactory.createLabel("IdStateLabel", "IdStateLabel", null, UI_LABEL);
        expirationDateLabel = uiFactory.createLabel("ExpirationDateLabel", "ExpirationDateLabel", null, UI_LABEL);

        idTypeField = uiFactory.createValidatingComboBox("idTypeField", "false", "15");
        idCountryField = uiFactory.createValidatingComboBox("idCountryField", "false", "15");
        idStateField = uiFactory.createValidatingComboBox("idStateField", "false", "20");

        //expirationDateField = new EYSDateField(DateField.MONTH, DateField.YEAR2, "");
        expirationDateField = new EYSDateField();
        //use the same format as the credit card format (MM/yyyy)
        expirationDateField.setFormat(DateDocument.CREDITCARD_MONTH_YEAR);
        expirationDateField.setName("expirationDateField");
        expirationDateField.setColumns(16);
    }

    /**
     * Lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{idTypeLabel, idCountryLabel, idStateLabel, expirationDateLabel},
                                    new JComponent[]{idTypeField, idCountryField, idStateField, expirationDateField});
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        idTypeField.addFocusListener(this);
        idCountryField.addActionListener(this);
        idStateField.addFocusListener(this);
        expirationDateField.addFocusListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        idTypeField.removeFocusListener(this);
        idCountryField.removeFocusListener(this);
        idCountryField.removeActionListener(this);
        idStateField.removeFocusListener(this);
        expirationDateField.removeFocusListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == idCountryField)
        {
            updateStates();
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Update states as country selection changes
     * 
     * @param e a document event
     */
    public void updateStates()
    {
        int countryIndx = idCountryField.getSelectedIndex();
        if ( countryIndx < 0)
        {
             countryIndx = 0;
        }
        String[] stateList = ((CountryModel)beanModel).getStateNames(countryIndx);


        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        idStateField.setModel(stateModel);
        //select 1st element of the list for the current country
        idStateField.setSelectedIndex(0);
    }

    /**
     * Updates the model property
     */
    @Override
    public void updateModel()
    {
        beanModel.setSelectedIDType(idTypeField.getSelectedIndex());
        beanModel.setCountryIndex(idCountryField.getSelectedIndex());
        beanModel.setStateIndex(idStateField.getSelectedIndex());

        // Date in Model is stored as string
        // This simplifies the validation in the business logic
        String dateStr = expirationDateField.getText();
        if (dateStr != null && !"".equals(dateStr))
        {
            int month = expirationDateField.getDate().getMonth();
            if (month > 10)
            {
                dateStr = month + "/" + expirationDateField.getDate().getYear();
            }
            else
            {
                dateStr = "0" + month + "/" + expirationDateField.getDate().getYear();
            }
            beanModel.setExpirationDate(dateStr);
        }
    }

    /**
     * Sets the model property
     * 
     * @param model UIModelIfc
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ChargeBean model to null");
        }
        
        if (model instanceof CustomerIDBeanModel)
        {
            beanModel = (CustomerIDBeanModel) model;
            updateBean();
        }
    }

    /**
     * Do actual updating of bean from the model
     */
    @Override
    protected void updateBean()
    {
        if (beanModel.getIDTypes() != null)
        {
            Vector<String> idTypeList = beanModel.getIDTypes();
            ValidatingComboBoxModel listModel =
                new ValidatingComboBoxModel(idTypeList);

            idTypeField.setModel(listModel);
        }
        if (beanModel.getSelectedIDType() > -1)
        {
            idTypeField.setSelectedIndex(beanModel.getSelectedIDType());
        }
        setComboBoxModel(beanModel.getCountryNames(), idCountryField, beanModel.getCountryIndex());

        // update the state combo box with the new list of states
        setComboBoxModel(beanModel.getStateNames(), idStateField, beanModel.getStateIndex());

        // Date in Model is stored as string!
        // We need to reformat for 2 digit year
        String expDate = beanModel.getExpirationDate();
        String reformattedDate = "";
        if (expDate != null && expDate.length() > 0)
        {
            String year = expDate.substring(expDate.length() - 4);
            reformattedDate = expDate.substring(0, expDate.length() - 4);
            reformattedDate += year;
        }
        expirationDateField.setText(reformattedDate);

        setRequiredFields();
    }

    /**
     * Determine what fields should be required. This is not always the same for
     * Capture customer info.
     */
    protected void setRequiredFields()
    {
        idTypeField.setRequired(true);
        idCountryField.setRequestFocusEnabled(true);
        idStateField.setRequired(true);
        expirationDateField.setRequired(true);
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     * 
     * @return the POSBaseBeanModel associated with this bean.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {

        idTypeLabel.setText(retrieveText("IdTypeLabel",
                                         idTypeLabel));
        idCountryLabel.setText(retrieveText("IdCountryLabel",
                                        idCountryLabel));
        idStateLabel.setText(retrieveText("IdStateLabel",
                                          idStateLabel));
        SimpleDateFormat dateFormat = DomainGateway.getSimpleDateFormat(getDefaultLocale(), "MM/yyyy");
        String translatedLabel = getTranslatedDatePattern(dateFormat.toPattern());

        String monthYearLabel = retrieveText("ExpirationDateLabel",
                                                 expirationDateLabel);
        expirationDateLabel.setText(LocaleUtilities.formatComplexMessage(monthYearLabel,translatedLabel));


        //associate labels with fields
        idTypeField.setLabel(idTypeLabel);
        idCountryField.setLabel(idCountryLabel);
        idStateField.setLabel(idStateLabel);
        expirationDateField.setLabel(expirationDateLabel);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    public String toString()
    {
        String strResult = new String("Class: CreditCardBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
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
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CustomerIDBean bean = new CustomerIDBean();

        UIUtilities.doBeanTest(bean);
    }
}
