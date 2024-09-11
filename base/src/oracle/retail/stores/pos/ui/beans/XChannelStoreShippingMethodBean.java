/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/XChannelStoreShippingMethodBean.java /main/2 2014/06/12 18:37:32 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/12/14 - refactor and code cleanup
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* abhinavs    06/06/14 - Initial Version
* abhinavs    06/06/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Shipping Option Information
 * @since 14.1
 * @author abhinavs
 */
public class XChannelStoreShippingMethodBean  extends ValidatingBean
{
    private static final long serialVersionUID = 3543654756L;

    /** Item list label tag */
    protected static String ITEM_LIST_LABEL = "ItemListLabel";
    /** ship to label tag */
    protected static final String SHIP_TO_LABEL = "ShipToLabel";
    /** estimated ship date tag */
    protected static final String ESTIMATED_SHIP_DATA = "EstimatedShipDateLabel";
    
    /** item lists Label */
    protected JLabel itemListLabel = null;

    /** Container of method item lists */
    protected ValidatingList itemList = null;

    /** Scroll long list of items */
    protected JScrollPane itemListScrollPane = null;
    
    /** The bean model */
    public XChannelShippingMethodBeanModel beanModel = null;

    /** empty label */
    protected JLabel emptyLabel = null;

    /** Address line 1 field */
    protected JLabel addressLine1Field = null;

    /** Address line 2 field */
    protected JLabel addressLine2Field = null;

    /** Address line 3 field */
    protected JLabel addressLine3Field = null;

    /** city, state, zip, country field */
    protected JLabel cityStateZipField = null;

    /** labels for fields */
    protected JLabel shipToLabel = null;

    /** Label for Estimated available to ship date */
    protected JLabel estimatedShipDateLabel = null;

    /** Estimated Available to ship date field */
    protected JLabel estimatedShipDateField = null;

    /** Store ID field */
    protected JLabel customerNameOrStoreIdLabel = null;

    /**
     * Default class Constructor and initializes its components.
     */
    public XChannelStoreShippingMethodBean()
    {
        initialize();
    }

    /**
     * Initialize the class and its screen members.
     */
    protected void initialize()
    {
        setName("XChannelStoreShippingMethodBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initializeLabels();
        initializeFields();
        layoutComponents();
    }

    /**
     * Initialize the labels on this screen
     */
    protected void initializeLabels()
    {
        itemListLabel = uiFactory.createLabel("itemListLabel", "itemListLabel", null, UI_LABEL);
        shipToLabel = uiFactory.createLabel("shipToLabel", "shipToLabel", null, UI_LABEL);
        emptyLabel = uiFactory.createLabel("emptyLabel", "", null, UI_LABEL);
        estimatedShipDateLabel = uiFactory.createLabel("estimatedShipDateLabel", "Estimated Arrival Date:", null, UI_LABEL);
        
        customerNameOrStoreIdLabel = uiFactory.createTextLabel("customerNameOrStoreIdLabel");      
        addressLine1Field = uiFactory.createTextLabel("addressLine1Field");
        addressLine2Field = uiFactory.createTextLabel("addressLine2Field");
        addressLine3Field = uiFactory.createTextLabel("addressLine3Field");
        cityStateZipField = uiFactory.createTextLabel("cityStateZipField");        
        estimatedShipDateField = uiFactory.createTextLabel("estimatedShipDateField");
    }

    /**
     * Initialize the fields on this screens.
     */
    protected void initializeFields()
    {
        // set item list width and high
        itemListScrollPane = uiFactory.createSelectionList("itemList", 322, 60, "false");
        itemList = (ValidatingList)itemListScrollPane.getViewport().getView();
        // make the list not selectable
        itemList.setEnabled(false);

    }

    /**
     * Layout the components on this screen
     */
    protected void layoutComponents()
    {
        JLabel[] labels = { itemListLabel, shipToLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel,
                estimatedShipDateLabel};                

        JComponent[] components = { itemListScrollPane, customerNameOrStoreIdLabel, addressLine1Field, addressLine2Field,
                addressLine3Field, cityStateZipField, estimatedShipDateField };
        setLayout(new GridBagLayout());
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }

    /**
     * Overrides the inherited setVisible().
     * 
     * @param visible boolean
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

    }

    /**
     * Gets the POSBaseBeanModel for validation of the current settings of the
     * bean.
     * 
     * @return the POSBaseBeanModel for the current values.
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }
    
    /**
     * Sets the model for the current settings of this bean.
     * 
     * @param model the model for the current values of this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set XChannelShippingMethodBean model to null");
        }

        if (model instanceof XChannelShippingMethodBeanModel)
        {
            beanModel = (XChannelShippingMethodBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the bean and changes fields based upon whether a layaway is in
     * progress
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateBean()
    {
        customerNameOrStoreIdLabel.setText(getCustomerNameOrStoreID());
        addressLine1Field.setText(beanModel.getAddressLine1());
        addressLine2Field.setText(beanModel.getAddressLine2());
        addressLine3Field.setText(beanModel.getAddressLine3());
        String cityState = beanModel.getCity();

        if (beanModel.getCountryInfo() != null)
        {
            if (beanModel.getStateInfo() != null)
            {
                cityState = cityState + beanModel.getCountryInfo().getAddressDelimiter() + " " + beanModel.getState();
            }

            cityState = cityState + " " + beanModel.getPostalCode();
            cityState = cityState + " " + beanModel.getCountryInfo().getPostalCodeDelimiter() + " "
                    + beanModel.getCountryInfo().getCountryName();
        }

        cityStateZipField.setText(cityState);

        ArrayList<String> itemNumbers = ((XChannelShippingMethodBeanModel)beanModel).getItemNumbers();
        ArrayList<String> itemDescriptions = ((XChannelShippingMethodBeanModel)beanModel).getItemDescriptions();

        if (itemNumbers.size() > 0 && itemDescriptions.size() > 0)
        {
            String items[] = new String[itemNumbers.size()];
            for (int i = 0; i < itemNumbers.size(); i++) // String
                                                         // itemNo:itemNumbers)
            {
                items[i] = new StringBuffer(itemNumbers.get(i)).append("    ").append(itemDescriptions.get(i))
                        .toString();
            }
            itemList.setListData(items);
        }

        
        String estimatedShipDate = DateTimeServiceLocator.getDateTimeService().formatDate(
                                                beanModel.getEstimatedAvailableToShipDate().getTime(), 
                                                LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), 
                                                DateFormat.SHORT);
        estimatedShipDateField.setText(estimatedShipDate);
    }

    /**
     * get store id
     * @return
     */
    protected String getCustomerNameOrStoreID()
    {
        return retrieveText("Store") + " " + ((XChannelShippingMethodBeanModel)beanModel).getStoreID();
    }
    
    
    /**
     * The framework calls this method just before display
     */
    @Override
    public void activate()
    {
        super.activate();
        updateModel();
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
    }

    /**
     * Updates property-based fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        if (shipToLabel != null)
        {
            shipToLabel.setText(retrieveText(SHIP_TO_LABEL, shipToLabel.getText()));
        }
        if (itemListLabel != null)
        {
            itemListLabel.setText(retrieveText(ITEM_LIST_LABEL, itemListLabel.getText()));
        }
        if (estimatedShipDateLabel != null)
        {
            estimatedShipDateLabel.setText(retrieveText(ESTIMATED_SHIP_DATA, estimatedShipDateLabel.getText()));
        }
        itemList.setLabel(itemListLabel);

    }
}
