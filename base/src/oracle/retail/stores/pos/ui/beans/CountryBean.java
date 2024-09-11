/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CountryBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/08/10 - update layout to not stretch field widget
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 5    I18N_P2    1.3.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR 29826
 *       - Setting the size of the combo boxes. This change was necessary
 *      because the width of the combo boxes used to grow according to the
 *      length of the longest content. By setting the size, we allow the width
 *       of the combo box to be set independently from the width of the
 *      dropdown menu.
 * 4    360Commerce 1.3         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *Revision 1.4  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 09 2003 16:38:26   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to capture the country.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CountryBean extends ValidatingBean
{
    private static final long serialVersionUID = 3052046185015388722L;

    /**
     * Country Bean model
     */
    protected CountryBeanModel beanModel = new CountryBeanModel();

    protected String countrylabelTag = "IDCountryLabel";

    /**
     * Fields and labels
     */
    protected JLabel countryLabel;

    /** Country list */
    protected ValidatingComboBox countryField = null;

    /**
     * Default Constructor.
     */
    public CountryBean()
    {
        super();
        initialize();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {       
        countryField  = uiFactory.createValidatingComboBox("countryField", "false", "15");
    }
    
    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        countryLabel = uiFactory.createLabel(countrylabelTag, countrylabelTag, null, UI_LABEL);
    }

    /**
     * Returns the base bean model.
     * 
     * @return POSBaseBeanModel
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

       
    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel() 
    {
        if (countryField.getSelectedIndex() >= 0)
        {
            beanModel.setCountryIndex(countryField.getSelectedIndex());
        }
        else
        {
            beanModel.setCountryIndex(0);
        }
    }

    /**
     * Sets the model property value.
     * 
     * @param model UIModelIfc the new value for the property.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set CountryBeanModel" +
                "model to null");
        }

        if (model instanceof  CountryBeanModel)
        {
            beanModel = (CountryBeanModel) model;
            updateBean();
        }
    }
    
    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {             
        //Retrieve countries and update combo box
        String[] countryList =beanModel.getCountryNames();
        ValidatingComboBoxModel validatingModel = new ValidatingComboBoxModel(countryList);

        countryField.setModel(validatingModel);
        
        CurrencyTypeIfc[] countries = beanModel.getCountries();
        
        // set the default country to Canada
        for (int i = 0; i < countries.length; i++)
        {
            if (beanModel.getDefaultCountryCode().equals(countries[i].getCountryCode()))
            {
                beanModel.setCountryIndex(i);
                break;
            }
        }

        countryField.setSelectedIndex(beanModel.getCountryIndex());

        countryField.setRequired(true);
    }

    /**
     * Initialize the class.
     */
    protected void initialize() 
    {     
        setName("CountryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initializeFields();
        initializeLabels();
        initLayout();
    }

    protected void initLayout()
    {
        JComponent[] components = new JComponent[]
        {
            countryField
        };
        JLabel[] labels = new JLabel[]
        {
            countryLabel
        };
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }
    
    /**
     *  The framework calls this method just before display
     */
    @Override
    public void activate()
    {
        updateBean();
    }

        
    /**
     *  Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {     
        countryLabel.setText(retrieveText(countrylabelTag));
    }
    
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) 
    {
        UIUtilities.setUpTest();

        CountryBeanModel aModel = new CountryBeanModel();

        CountryBean aBean = new CountryBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}
