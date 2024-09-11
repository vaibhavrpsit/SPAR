/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemNotFoundBean.java /main/22 2014/03/18 16:18:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    cgreene   12/01/10 - switch to 40 char length for itemDescriptionField
 *                         since 40 x 3 = length of DE_ITM_SHRT
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    sgu       02/25/09 - fix max lenght for all item quantity fields
 *    ranojha   12/02/08 - Fixed POS crashes due to returned null CodeList and
 *                         Department Strings
 *
 * ===========================================================================
 * $Log:
 *    6    .v8x      1.4.2.0     3/12/2007 6:19:58 PM   Brett J. Larsen CR 4530
 *          - adding support for default unit-of-measure code value
 *
 *         note: making an empty selection happen with a non-editable combobox
 *          was tricky - had to modify the control's model directly (this was
 *         required when no default is designated)
 *    5    360Commerce1.4         1/25/2006 4:11:05 PM   Brett J. Larsen merge
 *         7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:29 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     9/19/2005 13:48:09     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:28:32     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:29     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39     Robert Pearse
 *
 *Log:
 *    6    .v8x      1.4.2.0     3/12/2007 6:19:58 PM   Brett J. Larsen CR 4530
 *          - adding support for default unit-of-measure code value
 *
 *         note: making an empty selection happen with a non-editable combobox
 *          was tricky - had to modify the control's model directly (this was
 *         required when no default is designated)
 *    5    360Commerce1.4         1/25/2006 4:11:05 PM   Brett J. Larsen merge
 *         7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:29 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39 PM  Robert Pearse
 *: ItemNotFoundBean.java,v $
 *Log:
 *    6    .v8x      1.4.2.0     3/12/2007 6:19:58 PM   Brett J. Larsen CR 4530
 *          - adding support for default unit-of-measure code value
 *
 *         note: making an empty selection happen with a non-editable combobox
 *          was tricky - had to modify the control's model directly (this was
 *         required when no default is designated)
 *    5    360Commerce1.4         1/25/2006 4:11:05 PM   Brett J. Larsen merge
 *         7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:29 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:24:57    Charles Suehs   Merge
 *         from ItemNotFoundBean.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:28:32     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:29     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39     Robert Pearse
 *
 *   Revision 1.11  2004/08/09 22:12:41  kmcbride
 *   @scr 2958: According to requirements, price must be >=0 in the ItemNoutFoundScreen.  This screen is unfortuantely configured in the java source, so I had to make this change here rather than in a ui_cfg.xml file.
 *
 *   Revision 1.10  2004/05/07 15:37:29  tfritz
 *   @scr 2960 Changed serial number field to alphanumeric
 *
 *   Revision 1.9  2004/04/21 20:18:46  rsachdeva
 *   @scr 3906 JavaDoc comment added
 *
 *   Revision 1.8  2004/03/17 14:44:51  rsachdeva
 *   @scr 3906 Sale Item Size Label
 *
 *   Revision 1.7  2004/03/16 20:10:28  rsachdeva
 *   @scr  3906 Sale Item Size
 *
 *   Revision 1.6  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.5  2004/03/12 22:51:30  rsachdeva
 *   @scr 3906 Sale Item Size
 *
 *   Revision 1.4  2004/03/05 15:36:05  rsachdeva
 *   @scr 3906 Quantity and Unit of Measure
 *
 *   Revision 1.3  2004/03/05 15:10:48  rsachdeva
 *   @scr Quantity and Unit of Measure
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 17 2003 11:22:00   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:10:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jan 24 2003 10:36:48   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.2   Jan 15 2003 12:43:56   bwf
 * In initComponents, changed constrained field for serial number from 40 to 25.
 *
 *    Rev 1.2   Jan 15 2003 11:18:00   bwf
 * Changed constrained field for item serial number from 40 to 25.
 *
 *    Rev 1.2   Jan 15 2003 11:05:36   bwf
 * In initComponents, changed constrained field for serial number from 40 to 25.
 *
 *    Rev 1.2   Jan 15 2003 11:03:52   bwf
 * In initComponents, changed constrained field for serial number from 40 to 25.
 *
 *    Rev 1.2   Jan 15 2003 11:03:02   bwf
 * In initComponents, changed length of constrained field for serial number from 40 to 25.
 *
 *    Rev 1.1   Aug 14 2002 18:17:52   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:53:30   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:04   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:44   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 24 2002 13:45:14   mpm
 * Externalized text for default, common and giftcard config files.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This class prompts for item information to be entered.
 *
 * @version $Revision: /main/22 $
 */
public class ItemNotFoundBean extends ValidatingBean
{
    private static final long serialVersionUID = -3289082284034841052L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/22 $";
    /** Botton Filler **/
    protected JPanel bottomFiller = null;
    /** Left Filler **/
    protected JPanel leftFiller = null;
    /** Right Filler **/
    protected JPanel rightFiller = null;
    /** Bean Model **/
    protected ItemNotFoundBeanModel beanModel = null;
    /** Boolean Model **/
    protected BooleanComboModel taxableModel = null;
    /** Department Label **/
    protected JLabel departmentLabel = null;
    /** Description label **/
    protected JLabel itemDescriptionLabel = null;
    /** Item Number Label **/
    protected JLabel itemNumberLabel = null;
    /** Price Label **/
    protected JLabel priceLabel = null;
    /** item  SizeLabel **/
    protected JLabel itemSizeLabel = null;
    /** Quanty Label **/
    protected JLabel quantityLabel = null;
    /** Unit of Measure Label **/
    protected JLabel unitOfMeasureLabel = null;
    /** Taxable Lable **/
    protected JLabel taxableLabel = null;
    /** Item serial label **/
    protected JLabel itemSerialLabel = null;
    /** Department Field **/
    protected ValidatingComboBox departmentField = null;
    /** ItemSize Field **/
    protected NumericTextField itemSizeField = null;
    /** Description Field **/
    protected ConstrainedTextField itemDescriptionField = null;

    /** Item number field **/
    protected JLabel itemNumberField = null;
    /** Price Field **/
    protected CurrencyTextField priceField = null;
    /**
       Quantity field
    **/
    protected NumericDecimalTextField quantityField = null;
    /** UOM Field **/
    protected ValidatingComboBox unitOfMeasureField = null;
    /** Taxable combo box **/
    protected YesNoComboBox taxableField = null;
    /** Item serial field **/
    protected AlphaNumericPlusTextField itemSerialField = null;
    /**
     * size label tag
     */
    protected  static final String ITEMNOTFOUND_SIZE_LABEL_TAG =
      "ItemNotFoundSizeLabel";

    /**
     * Class constructor
     */
    public ItemNotFoundBean()
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
        itemNumberLabel = uiFactory.createLabel("Item Number :", "Item Number :", null, UI_LABEL);
        itemDescriptionLabel = uiFactory.createLabel("Item Description :", "Item Description :", null, UI_LABEL);
        priceLabel = uiFactory.createLabel("Price :", "Price :", null, UI_LABEL);
        itemSizeLabel = uiFactory.createLabel("Size :", "Size :", null, UI_LABEL);
        departmentLabel = uiFactory.createLabel("Department :", "Department :", null, UI_LABEL);
        quantityLabel = uiFactory.createLabel("Quantity :", "Quantity :", null, UI_LABEL);
        unitOfMeasureLabel = uiFactory.createLabel("Unit Of Measure :", "Unit Of Measure :", null, UI_LABEL);
        taxableLabel = uiFactory.createLabel("Taxable :", "Taxable :", null, UI_LABEL);
        itemSerialLabel = uiFactory.createLabel("Serial Number :", "Serial Number :", null, UI_LABEL);

        itemNumberField = uiFactory.createLabel("not available", "not available", null, UI_LABEL);

        itemDescriptionField = uiFactory.createConstrainedField("ItemDescriptionField", "1", "40", "40");
        itemSizeField = uiFactory.createNumericField("ItemSizeField", "1", "10");

        priceField = uiFactory.createCurrencyField("PriceField","false","false","true");
        priceField.setHorizontalAlignment(JTextField.RIGHT);

        departmentField = uiFactory.createValidatingComboBox("DepartmentField", "false", "20");
        departmentField.setEditable(false);

        quantityField = uiFactory.createNumericDecimalField("Quantity", 3, false);

        unitOfMeasureField = uiFactory.createValidatingComboBox("UnitOfMeasureField", "false", "15");
        unitOfMeasureField.setEditable(false);

        taxableField = uiFactory.createYesNoComboBox("TaxableField", 10);
        taxableField.setEditable(false);

        itemSerialField = uiFactory.createAlphaNumericPlusField("ItemSerialField", "1", "25", false, '-');
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
        add(priceLabel, constraints);
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
        add(priceField, constraints);
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
        beanModel.setPrice(priceField.getDecimalValue());
        beanModel.setItemSize(itemSizeField.getText());
        beanModel.setDepartmentName((String) departmentField.getSelectedItem());
        if (departmentField.getSelectedItem()!=null && departmentField.getSelectedItem()!="")
        {	String departmentID = beanModel.getDepartmentIDs()[departmentField.getSelectedIndex()];
        	beanModel.setDepartmentID(departmentID);
        }
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
            throw new NullPointerException("Attempt to set ItemNotFoundBean model to null");
        }
        if (model instanceof ItemNotFoundBeanModel)
        {
            beanModel = (ItemNotFoundBeanModel)model;
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
        initializeDepartments(beanModel.getDepartmentStrings());

        itemNumberField.setText(beanModel.getItemNumber());
        itemDescriptionField.setText(beanModel.getItemDescription());
        priceField.setDecimalValue(beanModel.getPrice());
        priceField.setEditable(beanModel.isPriceModifiable());

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
        else
        {
            if (beanModel.getDefaultUOM() != null && !beanModel.getDefaultUOM().equals(""))
            {
                unitOfMeasureField.setSelectedItem(beanModel.getDefaultUOM());
            }
            else
            {
                unitOfMeasureField.setRequired(true);
                unitOfMeasureField.setSelectedItem("");
                unitOfMeasureField.setSelectedIndex(-1);
                ValidatingComboBoxModel defaultModel=(ValidatingComboBoxModel)unitOfMeasureField.getModel();
                defaultModel.setSelectedItem("");
            }
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
    }

    /**
     * Updates the UOM object
     *
     * @param uom an array of string holding uom values
     */
    protected void initializeUOM(String[] uom)
    {
        // For User Interface Locale Change, removing elements
        ValidatingComboBoxModel model = new ValidatingComboBoxModel(uom);
        unitOfMeasureField.setModel(model);
    }

    /**
     * Updates the Department object
     *
     * @param depts an array of string holding department name values
     */
    protected void initializeDepartments(String[] depts)
    {
        ValidatingComboBoxModel model = new ValidatingComboBoxModel(depts);
        departmentField.setModel(model);
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
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        itemNumberLabel.setText(retrieveText("ItemNumberLabel", itemNumberLabel));
        itemDescriptionLabel.setText(retrieveText("ItemDescriptionLabel", itemDescriptionLabel));
        priceLabel.setText(retrieveText("PriceLabel", priceLabel));
        departmentLabel.setText(retrieveText("DepartmentLabel", departmentLabel));
        quantityLabel.setText(retrieveText("QuantityLabel", quantityLabel));
        unitOfMeasureLabel.setText(retrieveText("UnitOfMeasureLabel", unitOfMeasureLabel));
        taxableLabel.setText(retrieveText("TaxableLabel", taxableLabel));
        itemSerialLabel.setText(retrieveText("ItemSerialLabel", itemSerialLabel));
        itemSizeLabel.setText(retrieveText(ITEMNOTFOUND_SIZE_LABEL_TAG, itemSizeLabel));

        itemSerialField.setLabel(itemSerialLabel);
        taxableField.setLabel(taxableLabel);
        quantityField.setLabel(quantityLabel);
        priceField.setLabel(priceLabel);
        itemDescriptionField.setLabel(itemDescriptionLabel);
    }


    /**
     * The framework calls this method just before display.
     * This sets document and default value as per selection done
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
        UIUtilities.doBeanTest(new ItemNotFoundBean());
    }
}
