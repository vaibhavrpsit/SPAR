/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnByCreditBean.java /main/2 2014/03/18 16:18:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    ohorne    08/04/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to capture credit card details for return
 */
@SuppressWarnings("serial")
public class ReturnByCreditBean extends ValidatingBean
{
    protected JLabel firstCardDigitsLabel;
    protected JLabel lastCardDigitsLabel;
    protected JLabel itemNumberLabel;
    protected JLabel dateRangeLabel;

    protected NumericTextField firstCardDigitsField;
    protected NumericTextField lastCardDigitsField;    
    protected ValidatingTextField itemNumberField;
    protected ValidatingComboBox dateRangeField;
    
    /**
     * Card masking property values. 
     */
    
    /**
     * Default Constructor.
     */
    public ReturnByCreditBean()
    {
        beanModel = new ReturnByCreditBeanModel();
        
        initialize();
        setTabOrder();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        //obtain max length values from domain.properties 
        String firstDigitsMaxLength = Integer.toString(DomainUtil.getNumberOfCardFirstDigits());
        String lastDigitsMaxLength = Integer.toString(DomainUtil.getNumberOfCardLastDigits());

        firstCardDigitsField = uiFactory.createNumericField("firstCardDigitsField", "0", firstDigitsMaxLength);
        lastCardDigitsField = uiFactory.createNumericField("lastCardDigitsField", lastDigitsMaxLength, lastDigitsMaxLength);
        itemNumberField = uiFactory.createAlphaNumericPlusField("itemNumberField", "1", "14", false, '-');
        dateRangeField = uiFactory.createValidatingComboBox("dateRangeField", "false", "10");
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        firstCardDigitsLabel = uiFactory.createLabel("FirstCardDigitsLabel", "FirstCardDigitsLabel", null, UI_LABEL);
        lastCardDigitsLabel = uiFactory.createLabel("LastCardDigitsLabel", "LastCardDigitsLabel", null, UI_LABEL);
        itemNumberLabel = uiFactory.createLabel("ItemNumberLabel", "ItemNumberLabel", null, UI_LABEL);
        dateRangeLabel = uiFactory.createLabel("DateRangeLabel", "DateRangeLabel", null, UI_LABEL);
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
        if (beanModel instanceof ReturnByCreditBeanModel)
        {
            ReturnByCreditBeanModel model = (ReturnByCreditBeanModel) beanModel;
            model.setFirstCardDigits(firstCardDigitsField.getText());
            model.setLastCardDigits(lastCardDigitsField.getText());
            model.setItemNumber(itemNumberField.getText());
            model.setDateRangeIndex(dateRangeField.getSelectedIndex());
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
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ReturnByCreditBeanModel model to null");
        }

        if (model instanceof ReturnByCreditBeanModel)
        {
            beanModel = (ReturnByCreditBeanModel) model;
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
        if (beanModel instanceof ReturnByCreditBeanModel)
        {
            ReturnByCreditBeanModel model = (ReturnByCreditBeanModel) beanModel;
            
            firstCardDigitsField.setText(model.getFirstCardDigits());
            dateRangeField.setRequired(false);
            
            lastCardDigitsField.setText(model.getLastCardDigits());
            lastCardDigitsField.setRequired(true);

            itemNumberField.setText(model.getItemNumber());
            itemNumberField.setRequired(true);

            DefaultComboBoxModel aModel = new DefaultComboBoxModel(model.getDateRanges());
            dateRangeField.setModel(aModel);
            dateRangeField.setSelectedIndex(model.getDateRangeIndex());
            dateRangeField.setRequired(true);
        }
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("ReturnByCredit");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        
        JLabel[] labels = new JLabel[] { firstCardDigitsLabel, lastCardDigitsLabel, itemNumberLabel, dateRangeLabel};

        JComponent[] components = new JComponent[] { firstCardDigitsField, lastCardDigitsField, itemNumberField, dateRangeField };
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }

    /**
     * Override the tab key ordering scheme of the default focus manager where
     * appropriate. The default is to move in a zig-zag pattern from left to
     * right across the screen. In some cases, however, it makes more sense to
     * move down column one on the screen then start at the top of column 2.
     */
    protected void setTabOrder()
    {
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        NumberFormat nf = NumberFormat.getInstance(getLocale());
        
        //fetch/format label text.
        String fcdLabel = retrieveText("FirstCardDigitsLabel", firstCardDigitsLabel);
        Object[] parm = {nf.format(DomainUtil.getNumberOfCardFirstDigits())};
        firstCardDigitsLabel.setText(LocaleUtilities.formatComplexMessage(fcdLabel, parm));
        firstCardDigitsField.setLabel(firstCardDigitsLabel);

        String lcdLabel = retrieveText("LastCardDigitsLabel", lastCardDigitsLabel);
        parm[0] = nf.format(DomainUtil.getNumberOfCardLastDigits());
        lastCardDigitsLabel.setText(LocaleUtilities.formatComplexMessage(lcdLabel, parm));
        lastCardDigitsField.setLabel(lastCardDigitsLabel);

        itemNumberLabel.setText(retrieveText("ItemNumberLabel", itemNumberLabel));
        itemNumberField.setLabel(itemNumberLabel);
        
        dateRangeLabel.setText(retrieveText("DateRangeLabel", dateRangeLabel));
        dateRangeField.setLabel(dateRangeLabel);
    }

    /**
     * main entry point - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        ReturnByCreditBeanModel aModel = new ReturnByCreditBeanModel();
        aModel.setFirstCardDigits("123456");
        aModel.setLastCardDigits("4321");
        aModel.setItemNumber("10000000001234");
        ArrayList<String> dateRangeList = new ArrayList<String>();
        dateRangeList.add("All");
        dateRangeList.add("Within 14 days");
        dateRangeList.add("Within 30 days");
        aModel.setDateRangeList(dateRangeList);
        aModel.setDateRangeIndex(1);
        ReturnByCreditBean aBean = new ReturnByCreditBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }

}
