/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/XChannelShippingMethodBean.java /main/11 2014/06/12 18:37:32 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     09/09/14 - Use default locale in case the customer locale is not
*                        specified in shipping response
* yiqzhao     06/12/14 - refactor and code cleanup
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* abhinavs    06/03/14 - CAE add available shipping date during order create enhancement
* mkutiana    05/16/13 - retaining the values of the ShippingBeanModel upon
*                        error on the SelectShippingMethodSite
* yiqzhao     05/01/13 - Layout screens for window resizing.
* yiqzhao     05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
*                        the description to retail price
*                        modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
* yiqzhao     04/22/13 - Update the shipping charge while picking different
*                        shipping methods.
* yiqzhao     03/13/13 - Add reason code for shipping charge override for cross
*                        channel and store send.
* yiqzhao     07/16/12 - left alignment for shipping methods with shipping
*                        charges
* yiqzhao     07/09/12 - add shipping estimate delivery date
* yiqzhao     07/05/12 - Add ship item list on DisplayShippingMethod screen.
* yiqzhao     07/03/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Shipping Method Information $Revision:
 * /main/10 $
 */
public class XChannelShippingMethodBean extends XChannelStoreShippingMethodBean implements ListSelectionListener
{
    private static final long serialVersionUID = 1L;

    /**
     * business name string used to set name for this field
     */
    public static final String BUSINESS_NAME = "BusinessName";
    
    /** ship via label tag */
    protected static String SHIP_VIA_LABEL = "ShipViaLabel";
    /** ship charges label tag */
    protected static String SHIP_CHARGE_LABEL = "ShipChargeLabel";
    /** instructions label tag */
    protected static String INSTRUCTIONS_LABEL = "InstructionsLabel";
    /** instructions label tag */
    protected static String REASON_CODE_LABEL = "ReasonCodeLabel";

    /** last name label. Only be used when name is too long  */
    protected JLabel lastNameLabel = null;
    
    /** business name label */
    protected JLabel businessNameLabel = null;

    /** Shipping Method Label */
    protected JLabel shipViaLabel = null;
    /** Shipping Charge label */
    protected JLabel shipChargeLabel = null;
    /** Instructions label */
    protected JLabel instrLabel = null;
    /** empty label */
    protected JLabel emptyLabel = null;
    /** Label for Reason Code.  */
    protected JLabel reasonCodeLabel = null;
    
    /** Container of method lists */
    protected ValidatingList shipViaList = null;
    /** Scroll long list of departments */
    protected JScrollPane shipViaScrollPane = null;
    /** shipping charge */
    protected CurrencyTextField shipChargeField = null;
    /** the reason code of discount */
    protected ValidatingComboBox reasonList;
    /** special instructions field */
    protected ConstrainedTextAreaField instrField = null;
    /** Scroll long text area of departments */
    protected JScrollPane instrScrollPane = null;

   
    /**
     * Maximum Full Name length in one row
     */
    protected int MAX_NAME_LENGTH = 50;
    
    /** if name is over MAX_NAME_LENGTH, set the flag to true; */
    protected boolean isNameTooLong = false;
    
    /**
     * Default class Constructor and initializes its components.
     */
    public XChannelShippingMethodBean()
    {
        initialize();
    }

    /**
     * Initialize the class and its screen members.
     */
    protected void initialize()
    {
        setName("XChannelShippingMethodBean");
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
        super.initializeLabels();
        
        lastNameLabel = uiFactory.createTextLabel("lastNameLabel");
        businessNameLabel = uiFactory.createTextLabel(BUSINESS_NAME);
        addressLine1Field = uiFactory.createTextLabel("addressLine1Field");
        addressLine2Field = uiFactory.createTextLabel("addressLine2Field");
        addressLine3Field = uiFactory.createTextLabel("addressLine3Field");
        cityStateZipField = uiFactory.createTextLabel("cityStateZipField");

        shipViaLabel = uiFactory.createLabel("shipViaLabel", "shipViaLabel", null, UI_LABEL);
        shipChargeLabel = uiFactory.createLabel("shipChargeLabel", "shipChargeLabel", null, UI_LABEL);
        reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", "Reason Code:", null, UI_LABEL);
        
        instrLabel = uiFactory.createLabel("instrLabel", "instrLabel", null, UI_LABEL);
        
    }

    /**
     * Initialize the fields on this screens.
     */
    protected void initializeFields()
    {
        super.initializeFields();
        
        //set ship list with and high
        shipViaScrollPane = uiFactory.createSelectionList("shipViaList",  322, 60, "false");
        shipViaList = (ValidatingList)shipViaScrollPane.getViewport().getView();
        ((DefaultListCellRenderer)shipViaList.getCellRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);   

        //set shipping charge column size
        shipChargeField = uiFactory.createCurrencyField("shipChargeField", 28, "false", "false", "false");
        shipChargeField.setHorizontalAlignment(SwingConstants.RIGHT);
        
        reasonList = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "20");
        reasonList.setName("ReasonCodeField");
        reasonList.setLabel(reasonCodeLabel);
        
        instrScrollPane = uiFactory.createConstrainedTextAreaFieldPane("instrViaScrollPane", "0", "400", "80", "true",
                "true", JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instrField = (ConstrainedTextAreaField)instrScrollPane.getViewport().getView();
        
    }

    /**
     * Layout the components on this screen
     */
    protected void layoutComponents()
    {
        //Remove base class ui objects first
        this.removeAll();
        
        JLabel[] labels = { itemListLabel, shipToLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel, shipViaLabel,
                shipChargeLabel, reasonCodeLabel, estimatedShipDateLabel, instrLabel };

        JComponent[] components  = { itemListScrollPane, customerNameOrStoreIdLabel, lastNameLabel, businessNameLabel, addressLine1Field, addressLine2Field, addressLine3Field,
                    cityStateZipField, shipViaScrollPane, shipChargeField, reasonList, estimatedShipDateField, instrScrollPane };
        
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
        
        if (visible && !errorFound())
        {
            setCurrentFocus(shipViaList);
        }
    }

    /**
     * Calls setchangeStateValue to determine if any of the fields were changed.
     * Updates the model with the newest values from the bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setSelectedShipMethod(shipViaList.getSelectedIndex());

        String selectedReason = (String) reasonList.getSelectedItem();
        if ( selectedReason != null )
        {
            beanModel.setSelectedReasonCode( reasonList.getSelectedIndex() );
        }
        
        if (Util.isEmpty(shipChargeField.getText()))
        {
            beanModel.setShippingCharge(null);
        }
        else
        {
            beanModel.setShippingCharge(shipChargeField.getCurrencyValue());
        }
        beanModel.setInstructions(instrField.getText());

    }



    /**
     * Updates the bean and changes fields based upon whether a layaway is in
     * progress
     */
    @SuppressWarnings("unchecked")
    @Override
    public void updateBean()
    {
        super.updateBean();

        customerNameOrStoreIdLabel.setText(getCustomerNameOrStoreID());
        
        if (isNameTooLong)
        {
            lastNameLabel.setText(beanModel.getLastName());
        }
        else
        {
            lastNameLabel.setText("");
        }
        
        businessNameLabel.setText(beanModel.getOrgName());

        ShippingMethodIfc methods[] = beanModel.getShipMethodsList();

        if (methods != null)
        {
            String methodDesc[] = new String[methods.length];
            for (int i = 0; i < methods.length; i++)
            {
                String shippingType = methods[i].getShippingType(LocaleMap
                        .getLocale(LocaleConstantsIfc.USER_INTERFACE));
                if (StringUtils.isBlank(shippingType))
                {
                    shippingType = methods[i].getShippingType(LocaleMap
                            .getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
                }
                StringBuffer shipMethod = new StringBuffer(shippingType);
                shipMethod.append(" (").append(methods[i].getEstimatedShippingDate().toFormattedString()).append(")");
                methodDesc[i] = shipMethod.append(" : ").append(methods[i].getCalculatedShippingCharge()).toString();
            }
            shipViaList.setListData(methodDesc);
        }

        ShippingMethodIfc selection = beanModel.getSelectedShipMethod();
        if (selection != null)
        {
            shipViaList.setSelectedValue(
                    selection.getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)), true);
            shipViaList.setSelectedIndex(beanModel.getSelectedShipMethodindex());
        }

        CurrencyIfc charge = beanModel.getShippingCharge();
        shipChargeField.setValue(charge);
        instrField.setText(beanModel.getInstructions());
        setCurrentFocus(shipViaList);

        Vector<String> reasonCodelist = beanModel.getReasonCodes();
        reasonList.setModel(new ValidatingComboBoxModel(reasonCodelist));

        String selectedReasonCode = beanModel.getSelectedReason();
        if (!reasonList.isEditable() && selectedReasonCode.equals(""))
        {
            selectedReasonCode = beanModel.getDefaultValue();
        }
        reasonList.setSelectedItem(selectedReasonCode);
    }

    /**
     * get customer name
     * @return
     */
    protected String getCustomerNameOrStoreID()
    {
        isNameTooLong = false;
        String name = beanModel.getFirstName() + " " + beanModel.getLastName();
        if (name.length() > MAX_NAME_LENGTH)
        {
            isNameTooLong = true;
            name = beanModel.getFirstName();
        }
        return name;
    }
    
    /**
     * The framework calls this method just before display
     */
    @Override
    public void activate()
    {
        super.activate();
        shipViaList.addListSelectionListener(this);
        shipViaList.addFocusListener(this);
        instrField.addFocusListener(this);
        updateModel();
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        shipViaList.removeListSelectionListener(this);
        shipViaList.removeFocusListener(this);
        instrField.removeFocusListener(this);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        // we are interested in the final state of the selection
        if (!e.getValueIsAdjusting())
        {
            updateShippingCharge(e);
        }
    }

    /**
     * updateShippingCharge
     */
    public void updateShippingCharge(ListSelectionEvent e)
    {
        int indx = shipViaList.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }

        beanModel.setSelectedShipMethod(indx);
        ShippingMethodIfc shippingMethod = beanModel.getSelectedShipMethod();
        CurrencyIfc charge = shippingMethod.getCalculatedShippingCharge();
        shipChargeField.setValue(charge);
    }

    /**
     * Updates property-based fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        super.updatePropertyFields();

        if (shipViaLabel != null)
        {
            shipViaLabel.setText(retrieveText(SHIP_VIA_LABEL, shipViaLabel.getText()));
        }
        if (shipChargeLabel != null)
        {
            shipChargeLabel.setText(retrieveText(SHIP_CHARGE_LABEL, shipChargeLabel.getText()));
        }
        if (instrLabel != null)
        {
            instrLabel.setText(retrieveText(INSTRUCTIONS_LABEL, instrLabel.getText()));
        }
        shipViaList.setLabel(shipViaLabel);
        
        shipChargeField.setLabel(shipChargeLabel);
       
        itemList.setLabel(itemListLabel);
        
        reasonCodeLabel.setText(retrieveText(REASON_CODE_LABEL, reasonCodeLabel));
        reasonList.errorMessage = reasonCodeLabel.getText();
    }
    

}
