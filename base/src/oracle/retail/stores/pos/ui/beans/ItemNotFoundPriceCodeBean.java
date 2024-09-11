/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemNotFoundPriceCodeBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/08/10 - enhance ItemNotFoundPriceCodeBean to display
 *                         external order quantity and description
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    sgu       02/25/09 - fix max lenght for all item quantity fields
 *    nkgautam  02/10/09 - Fix for the case when department list is null in the
 *                         model
 *
 * ===========================================================================
 * $Log:
 *    4    I18N_P2    1.1.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    3    I18N_P2    1.1.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *         alphanumerice fields for I18N purpose
 *    2    360Commerce 1.1         8/8/2007 5:48:37 PM    Michael P. Barnett
 *         Specify max length of NumericDecimal field.
 *    1    360Commerce 1.0         7/28/2006 5:34:04 PM   Brett J. Larsen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This class prompts for item information to be entered in
 * ITEM_NOT_FOUND_PRICE_CODE screen.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ItemNotFoundPriceCodeBean extends ValidatingBean
{
    private static final long serialVersionUID = 2117562910711717863L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** size label tag */
    protected static final String ITEMNOTFOUND_SIZE_LABEL_TAG = "ItemNotFoundSizeLabel";

    /** Botton Filler */
    protected JPanel bottomFiller = null;
    /** Left Filler */
    protected JPanel leftFiller = null;
    /** Right Filler */
    protected JPanel rightFiller = null;
    /** Bean Model */
    protected ItemNotFoundPriceCodeBeanModel beanModel = null;
    /** Boolean Model */
    protected BooleanComboModel taxableModel = null;
    /** Department Label */
    protected JLabel departmentLabel = null;
    /** Description label */
    protected JLabel itemDescriptionLabel = null;
    /** Item Number Label */
    protected JLabel itemNumberLabel = null;
    /** Price Label */
    protected JLabel priceLabel = null;
    /** item  SizeLabel */
    protected JLabel itemSizeLabel = null;
    /** Quanty Label */
    protected JLabel quantityLabel = null;
    /** Unit of Measure Label */
    protected JLabel unitOfMeasureLabel = null;
    /** Taxable Lable */
    protected JLabel taxableLabel = null;
    /** Item serial label */
    protected JLabel itemSerialLabel = null;
    /** Department Field */
    protected ValidatingComboBox departmentField = null;
    /** ItemSize Field */
    protected NumericTextField itemSizeField = null;
    /** Description Field */
    protected ConstrainedTextField itemDescriptionField = null;
    /** Item number field */
    protected JLabel itemNumberField = null;
    /** Price Field */
    protected CurrencyTextField priceField = null;
    /** Quantity field */
    protected NumericDecimalTextField quantityField = null;
    /** UOM Field */
    protected ValidatingComboBox unitOfMeasureField = null;
    /** Taxable combo box */
    protected YesNoComboBox taxableField = null;
    /** Item serial field */
    protected AlphaNumericTextField itemSerialField = null;
    /** Price Code  Field */
    protected PriceCodeTextField priceCodeField   = null;
    /** Price Code  label tag */
    protected JLabel priceCodeLabel = null;

    /**
     * Class constructor
     */
    public ItemNotFoundPriceCodeBean()
    {
        initialize();
    }

    /**
     * Initializes the class.
     */
    protected void initialize()
    {
        setName("CreditCardBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initializes the components.
     */
    protected void initComponents()
    {
        itemNumberLabel = uiFactory.createLabel("Item Number :","Item Number :", null, UI_LABEL);
        itemDescriptionLabel = uiFactory.createLabel("Item Description :","Item Description :", null, UI_LABEL);

        priceCodeLabel=uiFactory.createLabel("Price Code ","Price Code ", null, UI_LABEL);

        itemSizeLabel = uiFactory.createLabel("Size :","Size :", null, UI_LABEL);
        departmentLabel = uiFactory.createLabel("Department :","Department :", null, UI_LABEL);
        quantityLabel = uiFactory.createLabel("Quantity :","Quantity :", null, UI_LABEL);
        unitOfMeasureLabel = uiFactory.createLabel("Unit Of Measure :","Unit Of Measure :", null, UI_LABEL);
        taxableLabel = uiFactory.createLabel("Taxable :","Taxable :", null, UI_LABEL);
        itemSerialLabel = uiFactory.createLabel("Serial Number :","Serial Number :", null, UI_LABEL);

        itemNumberField = uiFactory.createLabel("not available","not available", null, UI_LABEL);

        itemDescriptionField = uiFactory.createConstrainedField("ItemDescriptionField", "1", "60", "40");
        itemSizeField = uiFactory.createNumericField("ItemSizeField", "1", "4");
        priceCodeField = new PriceCodeTextField("", 3, 8);
        priceCodeField.setName("priceCodeField");
        uiFactory.configureUIComponent(priceCodeField, "ValidatingField");
        priceCodeField.setColumns(10);

        departmentField = uiFactory.createValidatingComboBox("DepartmentField", "false", "20");
        departmentField.setEditable(false);

        quantityField = uiFactory.createNumericDecimalField("Quantity", 3, false);

        unitOfMeasureField = uiFactory.createValidatingComboBox("UnitOfMeasureField", "false", "15");
        unitOfMeasureField.setEditable(false);

        taxableField = uiFactory.createYesNoComboBox("TaxableField", 10);
        taxableField.setEditable(false);

        itemSerialField = uiFactory.createAlphaNumericField("ItemSerialField", "1", "25", false);

    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = uiFactory.getInsets("defaultLabelTop");
        add(itemNumberLabel, constraints);

        constraints.insets.top = 0;
        add(itemDescriptionLabel, constraints);
        add(priceCodeLabel, constraints);
        add(departmentLabel, constraints);
        add(unitOfMeasureLabel, constraints);
        add(quantityLabel, constraints);
        add(taxableLabel, constraints);
        add(itemSerialLabel, constraints);

        constraints.insets.bottom = 15;
        add(itemSizeLabel, constraints);

        constraints.gridx = 1;
        constraints.insets = uiFactory.getInsets("defaultFieldTop");
        add(itemNumberField, constraints);

        constraints.insets.top = 0;
        add(itemDescriptionField, constraints);
        add(priceCodeField, constraints);
        add(departmentField, constraints);
        add(unitOfMeasureField, constraints);
        add(quantityField, constraints);
        add(taxableField, constraints);
        add(itemSerialField, constraints);
        add(itemSizeField, constraints);
    }

    /**
     * Updates the model property from the fields in the bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setItemDescription(itemDescriptionField.getText());
        beanModel.setPriceCode(priceCodeField.getText());
        beanModel.setItemSize(itemSizeField.getText());
        beanModel.setDepartmentName((String) departmentField.getSelectedItem());
        beanModel.setUnitOfMeasure((String)(unitOfMeasureField.getSelectedItem()));
        try
        {
            beanModel.setQuantity(quantityField.getDecimalValue());
        }
        catch (NumberFormatException excp)
        {
        }

        beanModel.setTaxable(getTaxableModel().valueOf((String)taxableField.getSelectedItem()));
        if (!Util.isEmpty(itemSerialField.getText()))
        {
            beanModel.setItemSerial(itemSerialField.getText());
        }
        else
        {
            beanModel.setItemSerial(null);
        }
    }

    /**
     * Returns the dataModel being used by the TaxableField, which is a
     * YesNoComboBox.
     *
     * @return BooleanComboModel
     */
    protected BooleanComboModel getTaxableModel()
    {
        // explicit cast from ComboBoxModel to BooleanComboModel
        if (taxableModel == null)
        {
            taxableModel = (BooleanComboModel) taxableField.getModel();
        }

        return taxableModel;
    }

    /**
     * Sets the model property (Object) value.
     *
     * @param model The new value for the property.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ItemNotFoundPriceCodeBean model to null");
        }
        if (model instanceof ItemNotFoundPriceCodeBeanModel)
        {
            beanModel = (ItemNotFoundPriceCodeBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the model if It's been changed
     */
    @Override
    protected void updateBean()
    {
        // combo boxes must be initialized before use.
        initializeUOM(beanModel.getUnitOfMeasureStrings());
        if(beanModel.getDepartmentStrings() != null)
        {
          initializeDepartments(beanModel.getDepartmentStrings());
        }

        itemNumberField.setText(beanModel.getItemNumber());
        itemDescriptionField.setText(beanModel.getItemDescription());
        priceCodeField.setText(beanModel.getPriceCode());
        if (beanModel.getItemSize() == null)
        {
            itemSizeField.setText("");
        }
        else
        {
            itemSizeField.setText(beanModel.getItemSize());
            setFieldRequired(itemSizeField, beanModel.isItemSizeRequired());

        }
        quantityField.setDecimalValue(beanModel.getQuantity());
        quantityField.setEditable(beanModel.isQuantityModifiable());
        if (beanModel.getDepartmentName() != null)
        {
            departmentField.setSelectedItem(beanModel.getDepartmentName());
        }
        if (beanModel.getUnitOfMeasure() != null)
        {
            unitOfMeasureField.setSelectedItem(beanModel.getUnitOfMeasure());
        }
        if (beanModel.getTaxable())
        {
            taxableField.setSelectedItem(getTaxableModel().getTrueValue());
        }
        else
        {
            taxableField.setSelectedItem(getTaxableModel().getFalseValue());
        }

        if (beanModel.getItemSerial() == null)
        {
            itemSerialField.setText("");
        }
        else
        {
            itemSerialField.setText(beanModel.getItemSerial());
        }

        if(beanModel.getPriceCode() == null)
        {
            priceCodeField.setText("");
        }
        else
        {
            priceCodeField.setText(beanModel.getPriceCode());
        }
        priceCodeField.setEnabled(true);


    }

    /**
     * Updates the UOM object
     *
     * @param uom an array of string holding uom values
     */
    protected void initializeUOM(String[] uom)
    {
        //For User Interface Locale Change, removing elements
        DefaultComboBoxModel defaultModel=(DefaultComboBoxModel)unitOfMeasureField.getModel();
        defaultModel.removeAllElements();
        unitOfMeasureField.addItems(uom);
    }

    /**
     * Updates the Department object
     *
     * @param depts an array of string holding department name values
     */
    protected void initializeDepartments(String[] depts)
    {
        // For User Interface Locale Change, removing elements
        DefaultComboBoxModel defaultModel=(DefaultComboBoxModel)departmentField.getModel();
        defaultModel.removeAllElements();
        departmentField.addItems(depts);
    }

    /**
     * Gets the bean model to set up return from the error screen.
     *
     * @return the model
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Set the properties to be used by this bean
     *
     * @param props the propeties object
     */
    @Override
    public void setProps(Properties props)
    {
        getTaxableModel().setProps(props);
        super.setProps(props);
    }

    /**
     *  Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        itemNumberLabel.setText(retrieveText("ItemNumberLabel", itemNumberLabel));
        itemDescriptionLabel.setText(retrieveText("ItemDescriptionLabel", itemDescriptionLabel));
        priceCodeLabel.setText(retrieveText("PriceCodeLabel", priceCodeLabel));
        departmentLabel.setText(retrieveText("DepartmentLabel", departmentLabel));
        quantityLabel.setText(retrieveText("QtyLabel", quantityLabel));
        unitOfMeasureLabel.setText(retrieveText("UnitOfMeasureLabel", unitOfMeasureLabel));
        taxableLabel.setText(retrieveText("TaxableLabel", taxableLabel));
        itemSerialLabel.setText(retrieveText("ItemSerialLabel", itemSerialLabel));
        itemSizeLabel.setText(retrieveText("ItemNotFoundSizeLabel", itemSizeLabel));

        itemSerialField.setLabel(itemSerialLabel);
        taxableField.setLabel(taxableLabel);
        quantityField.setLabel(quantityLabel);
        priceCodeField.setLabel(priceCodeLabel);
        itemDescriptionField.setLabel(itemDescriptionLabel);
    }

    /**
     *  The framework calls this method just before display.
     *  This sets document and default value as per selection done
     */
    @Override
    public void activate()
    {
        unitOfMeasureField.addActionListener(this);
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        beanModel = null;
        unitOfMeasureField.removeActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == unitOfMeasureField)
        {
            if (beanModel.getDefaultUOM().equals(unitOfMeasureField.getSelectedItem()))
            {
                NumericDocument numDoc =
                  quantityField.getNumericDocument(3, false);
                quantityField.setDocument(numDoc);
            }
            else
            {
                DecimalDocument decDoc =
                  quantityField.getDecimalDocument(7, false, 2);
                quantityField.setDocument(decDoc);
            }
            quantityField.setDecimalValue(beanModel.getDefaultQuantity());
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new ItemNotFoundPriceCodeBean());
    }
}
