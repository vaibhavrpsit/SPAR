  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved.
 * Rev 1.2  07 Mar,2017             Nitika Arora         PLU item qty should not be changed at the time of return(decimal precsion shoud be 3digits)
 * Rev 1.1  17 Feb,2017             Nitika Arora         Changes for removing the Tender Type and Item Condition from Return Item Info screen.
 * Rev 1.0  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.JTextField;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalDocument;
import oracle.retail.stores.pos.ui.beans.NumericDocument;
import oracle.retail.stores.pos.ui.beans.ReturnItemInfoBean;
import oracle.retail.stores.pos.ui.beans.ReturnItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;

// Java imports


//----------------------------------------------------------------------------
/**
    This class is used to display and gather Return Item Information. <p>
    @version $Revision: 10$
*/
//----------------------------------------------------------------------------
public class MAXReturnItemInfoBean extends ReturnItemInfoBean
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2076080225404715548L;

    /** 
     * indicates decimal qty
     */
    final static int DECIMAL_TYPE = 0;
 
    /**
     * indicates integer qty
     */
    final static int INTEGER_TYPE = 1;
	
	public MAXReturnItemInfoBean()
	{
		super();
	}
	
	//<!-- MAX Rev 1.0 Change : start -->
	protected void initFields()
    {   
        // initialize the constraints
        GridBagConstraints constraints = uiFactory.getConstraints("DataEntryBean");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, iLABEL, 5);
        
        // Item Number field
        itemNumberField = uiFactory.createLabel("not available", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemNumberField, constraints);

        // update constraints
        constraints.gridy = GridBagConstraints.RELATIVE;

        // Item Description field
        itemDescriptionField = uiFactory.createLabel("no description available", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemDescriptionField, constraints);

        // Price field + display
        priceField = uiFactory.createCurrencyField("PriceField", "true", "false", "false");
        priceField.setEnabled(true);
        priceField.setEditable(false);
        constraints.insets.bottom = iFIELD;
        add(priceField, constraints);

        priceFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(priceFieldDisplayOnly, constraints);

        // Item Size field + display
        itemSizeField = uiFactory.createNumericField("ItemSizeField", "1","10");
        constraints.insets.bottom = iFIELD;
        add(itemSizeField, constraints);
 
        itemSizeFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemSizeFieldDisplayOnly, constraints);

        // Unit Of Measure field
        unitOfMeasureField = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(unitOfMeasureField, constraints);
        
        // Quantity field + display
        quantityField = uiFactory.createNumericDecimalField("QuantityField", 8, false);
        quantityField.setHorizontalAlignment(JTextField.LEFT);  // force it to take input like the others
        quantityField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(quantityField, constraints);
        
        quantityFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(quantityFieldDisplayOnly, constraints);

        // Store Number field + display
        storeNumberField = uiFactory.createAlphaNumericField("StoreNumberField", "1", "5");
        storeNumberField.setEnabled(true);
        storeNumberField.setEditable(false);
        constraints.insets.bottom = iFIELD;
        add(storeNumberField, constraints);
        
        storeNumberFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(storeNumberFieldDisplayOnly, constraints);

        // Sales Associate field + display
        salesAssociateField = uiFactory.createAlphaNumericField("SalesAssociateField", "0", "10");
        salesAssociateField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(salesAssociateField, constraints);
        
        salesAssociateFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(salesAssociateFieldDisplayOnly, constraints);

        // Receipt Number field + display
        String transIDLength = Integer.toString(TransactionID.getTransactionIDLength());
        receiptNumberField = uiFactory.createAlphaNumericField("ReceiptNumberField", "0", transIDLength);
        receiptNumberField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(receiptNumberField, constraints);
        
        receiptNumberFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(receiptNumberFieldDisplayOnly, constraints);

        // Reason Code combo box
        reasonCodeComboBox = uiFactory.createValidatingComboBox("ReasonCodeField");
        reasonCodeComboBox.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(reasonCodeComboBox, constraints);

        // Serial Number field + display
        serialNumberField = uiFactory.createAlphaNumericPlusField("SerialNumberField", "1", "25",true,"");
        constraints.insets.bottom = iFIELD;
        add(serialNumberField, constraints);

        serialNumberFieldDisplayOnly = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(serialNumberFieldDisplayOnly, constraints);

        // Gift Card Number label
        giftCardNumberField = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.top = iLABEL;
        constraints.insets.bottom = iLABEL;
        add(giftCardNumberField, constraints);
        constraints.insets.top = 0;            // reset

        // Gift Card Balance label
        giftCardBalanceField = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(giftCardBalanceField, constraints);

        // Restocking Fee label
        /*restockingFeeField = uiFactory.createLabel("N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(restockingFeeField, constraints);*/
        
     // Item condition code combo box
      /*  itemConditionCodeComboBox = uiFactory.createValidatingComboBox("ItemConditionCodeField", "false", "20");
        itemConditionCodeComboBox.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(itemConditionCodeComboBox, constraints);
        
        tenderTypeComboBox = 
                uiFactory.createValidatingComboBox("TenderType", "true", "15");
            tenderTypeComboBox.setShowDisabled(true);
            constraints.insets.bottom = iFIELD;
            add(tenderTypeComboBox, constraints);
*/    }

	//<!-- MAX Rev 1.0 Change : end -->
	public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set ReturnItemInfoBean " +
                                           "model to null");
        }
        if (model instanceof ReturnItemInfoBeanModel)
        {
            beanModel = (ReturnItemInfoBeanModel)model;
            updateBean();
        }
    }
	
	

	/**MAX Changes Rev 1.1: Start
	Function Overrided to change the focuse from store id to size field
	**/

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
    	
//    	 Reason Code combo box
       
        // Utility manager
        UtilityManagerIfc utility =
                (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        itemNumberField.setText(beanModel.getItemNumber());
        itemDescriptionField.setText(beanModel.getItemDescription());
        
        itemSizeField.setText(beanModel.getItemSize());
        itemSizeFieldDisplayOnly.setText(beanModel.getItemSize());
  // Changes start for code emrging(commenting if and else statment as it is not clear)
       /* if (beanModel.isItemFromRetrievedTransaction())
        {
            itemSizeField.setVisible(false);
            setFieldRequired(itemSizeField, false);
            itemSizeFieldDisplayOnly.setVisible(true);
        }
        else
        {*/
            itemSizeField.setVisible(true);
            setFieldRequired(itemSizeField, beanModel.isItemSizeRequired());
            itemSizeFieldDisplayOnly.setVisible(false);
        /*}*/
  // Changes ends for code merging

        priceField.setValue(beanModel.getPrice());
       
        // Use the proper unitOfMeasure text description.
        if (beanModel.getUnitOfMeasure().equalsIgnoreCase(none) ||
                beanModel.getUnitOfMeasure().equalsIgnoreCase(units))
        {
            // (In theory. we at most go through here once.)
            beanModel.setUnitOfMeasure(retrieveText("EachLabel","Each")); // from units to none
        }
        
        // Control the type of text field input based on if we allow fractions
        if (!beanModel.isUOM())                   // "no fractions allowed"
        {
            // Set quantity type integer
            setQuantityFieldType(INTEGER_TYPE);
        }
        else
        {
            // Set quantity type decimal
            setQuantityFieldType(DECIMAL_TYPE);
        }
        unitOfMeasureField.setText(beanModel.getUnitOfMeasure());
        
        quantityField.setDecimalValue(beanModel.getQuantity());
        storeNumberField.setText(beanModel.getStoreNumber());
        salesAssociateField.setText(beanModel.getSalesAssociate());
        receiptNumberField.setText(beanModel.getReceiptNumber());
        //restockingFeeField.setText(beanModel.getRestockingFee());
        serialNumberField.setText(beanModel.getSerialNumber());

        reasonCodeComboBox.setModel(new ValidatingComboBoxModel(beanModel.getReasonCodes()));
        //int index = -1;
        //beanModel.setSelectedReasonCode(-1);
       if (beanModel.isSelected())
        {
            reasonCodeComboBox.setSelectedIndex(beanModel.getSelectedIndex());
        }
       else
       {
    	   reasonCodeComboBox.setSelectedIndex(-1);
    	   beanModel.setSelectedReasonCode(-1);
       }
   //    	setComboBoxModel(beanModel.getReasonCodes(),reasonCodeComboBox,index);
       	

        if (beanModel.getEnableOnlyQuantityReason())
        {
            //++ CR 27666
            String formattedPrice = getCurrencyService().formatCurrency(priceField.getText(), LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            priceFieldDisplayOnly.setText(formattedPrice);
            //-- CR 27666
            priceFieldDisplayOnly.setVisible(true);
            priceField.setVisible(false);

            storeNumberFieldDisplayOnly.setText(storeNumberField.getText());
            storeNumberFieldDisplayOnly.setVisible(true);
            storeNumberField.setVisible(false);
            storeNumberField.setRequired(false);

            salesAssociateFieldDisplayOnly.setText(salesAssociateField.getText());
            salesAssociateFieldDisplayOnly.setVisible(true);
            salesAssociateField.setVisible(false);

            receiptNumberFieldDisplayOnly.setText(receiptNumberField.getText());
            receiptNumberFieldDisplayOnly.setVisible(true);
            receiptNumberField.setVisible(false);

            String serialNumber = serialNumberField.getText();
            if ((serialNumber == null) || ("".equals(serialNumber)))
            {
                serialNumberFieldDisplayOnly.setText(retrieveText("NoneLabel","(none)"));
            }
            else
            {
                serialNumberFieldDisplayOnly.setText(serialNumber);
            }
            serialNumberFieldDisplayOnly.setVisible(true);
            serialNumberField.setVisible(false);
            setFieldRequired(serialNumberField, false);
        }
        else
        {
            priceFieldDisplayOnly.setVisible(false);
            priceField.setVisible(true);
            priceField.setEnabled(true);

            storeNumberFieldDisplayOnly.setVisible(false);
            storeNumberField.setVisible(true);
            storeNumberField.setRequired(true);

            salesAssociateFieldDisplayOnly.setVisible(false);
            salesAssociateField.setVisible(true);

            receiptNumberFieldDisplayOnly.setVisible(false);
            receiptNumberField.setVisible(true);

            serialNumberFieldDisplayOnly.setVisible(false);
            serialNumberField.setVisible(true);

            if (beanModel.getSerialNumberRequired())
            {
                setFieldRequired(serialNumberField, true);
            }
            else
            {
                setFieldRequired(serialNumberField, false);
            }
        }

        if(beanModel.getGiftCardSerialNumber() != null)
        {
            giftCardNumberField.setText(beanModel.getGiftCardSerialNumber());
            giftCardBalanceField.setText(LocaleUtilities.formatDecimal(beanModel.getGiftCardBalance(),
                                         getLocale()));


            giftCardNumberField.setVisible(true);
            giftCardBalanceField.setVisible(true);


            giftCardNumberLabel.setVisible(true);
            giftCardBalanceLabel.setVisible(true);


            /*restockingFeeLabel.setVisible(false);
            restockingFeeField.setVisible(false);*/

            serialNumberFieldDisplayOnly.setVisible(false);
            serialNumberLabel.setVisible(false);
            serialNumberField.setVisible(false);

            priceLabel.setText(retrieveText("CurrentBalanceLabel",
                                            "Current Balance:"));

            quantityFieldDisplayOnly.setText(quantityField.getText());
            quantityFieldDisplayOnly.setVisible(true);
            quantityField.setVisible(false);
            quantityField.setEnabled(false);

            //++ CR 27666
            String formattedPrice = getCurrencyService().formatCurrency(priceField.getText(), LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            priceFieldDisplayOnly.setText(formattedPrice);
            //-- CR 27666
            priceFieldDisplayOnly.setVisible(true);
            priceField.setVisible(false);
            priceField.setEnabled(false);
        }
        else
        {
            giftCardNumberField.setVisible(false);
            giftCardBalanceField.setVisible(false);


            giftCardNumberLabel.setVisible(false);
            giftCardBalanceLabel.setVisible(false);


            /*restockingFeeLabel.setVisible(true);
            restockingFeeField.setVisible(true);*/

            serialNumberLabel.setVisible(true);

            priceLabel.setText(retrieveText("ReturnPriceLabel",
                                            "Price :"));

            quantityFieldDisplayOnly.setVisible(false);
            quantityField.setVisible(true);
            quantityField.setEnabled(true);
        }
        
        // Final things
		/**MAX Changes Rev 1.1: Start**/
        setCurrentFocus(quantityField);
		/**MAX Changes Rev 1.1: End**/
        tenderTypeLabel.setVisible(false);
        itemConditionCodeLabel.setVisible(false);
    }
	/**MAX Changes Rev 1.1: End**/


	/**MAX Changes Rev 1.1: Start**/
    public void setCurrentFocus(JComponent current)
    {
        currentComponent = current;
        
        if (current != null)
        {
          current.requestFocusInWindow();
        }
    }
	/**MAX Changes Rev 1.1: End**/
    /**
     * Sets the correct type for the quantity field
     * 
     * @param type whether this field should be integer or decimal
     */
    protected void setQuantityFieldType(int type)
    {
        switch (type)
        {
        case (DECIMAL_TYPE):
            DecimalDocument decDoc = quantityField.getDecimalDocument(7, false, 3);
            decDoc.setZeroAllowed(false);
            quantityField.setDocument(decDoc);
            break;

        case (INTEGER_TYPE):
        default:
            NumericDocument numDoc = quantityField.getNumericDocument(3, false);
            quantityField.setDocument(numDoc);
        }
    }
    
    /**
     * Updates property-based fields.
     */
    public void updatePropertyFields()
    {
        itemNumberLabel.setText(retrieveText("ItemIDLabel", itemNumberLabel));
        itemDescriptionLabel.setText(retrieveText("ItemDescriptionLabel", itemDescriptionLabel));
        priceLabel.setText(retrieveText("ReturnPriceLabel", priceLabel));
        itemSizeLabel.setText(retrieveText("ItemSizeLabel", itemSizeLabel));
        quantityLabel.setText(retrieveText("ReturnQuantityLabel", quantityLabel));
        unitOfMeasureLabel.setText(retrieveText("UnitOfMeasureLabel", unitOfMeasureLabel));
        storeNumberLabel.setText(retrieveText("StoreNumberLabel", storeNumberLabel));
        salesAssociateLabel.setText(retrieveText("SalesAssociateLabel", salesAssociateLabel));
        receiptNumberLabel.setText(retrieveText("ReceiptNumberLabel", receiptNumberLabel));
        returnReasonCodeLabel.setText(retrieveText("ReturnReasonCodeLabel", returnReasonCodeLabel));
      //  itemConditionCodeLabel.setText(retrieveText("ItemConditionCodeLabel", itemConditionCodeLabel));
        giftCardNumberLabel.setText(retrieveText("GiftCardNumberLabel", giftCardNumberLabel));
        giftCardBalanceLabel.setText(retrieveText("GiftCardBalanceLabel", giftCardBalanceLabel));
        serialNumberLabel.setText(retrieveText("SerialNumberLabel", serialNumberLabel));
       // tenderTypeLabel.setText(retrieveText("TenderTypeLabel", tenderTypeLabel));
        
        // associate fields with labels
        receiptNumberField.setLabel(receiptNumberLabel);
        salesAssociateField.setLabel(salesAssociateLabel);
        storeNumberField.setLabel(storeNumberLabel);
        quantityField.setLabel(quantityLabel);
        priceField.setLabel(priceLabel);
        itemSizeField.setLabel(itemSizeLabel);
        reasonCodeComboBox.setLabel(returnReasonCodeLabel);
       // itemConditionCodeComboBox.setLabel(itemConditionCodeLabel);
        serialNumberField.setLabel(serialNumberLabel);
       // tenderTypeComboBox.setLabel(tenderTypeLabel);
    }
    
    /**
     * Gets the model property (java.lang.Object) value.
     */
    public void updateModel()
    {
        // Note: The fieldReasonCodes of the model are alread set.
        beanModel.setItemNumber(itemNumberField.getText());
        beanModel.setItemDescription(itemDescriptionField.getText());
        if (beanModel.isPriceEnabled())
        {
            beanModel.setPrice(priceField.getCurrencyValue());
        }

        beanModel.setItemSize(itemSizeField.getText());
        beanModel.setQuantity(quantityField.getDecimalValue());
        beanModel.setUnitOfMeasure(unitOfMeasureField.getText());
        beanModel.setStoreNumber(storeNumberField.getText());
        beanModel.setSalesAssociate(salesAssociateField.getText());
        beanModel.setReceiptNumber(receiptNumberField.getText());
        beanModel.setSerialNumber(serialNumberField.getText());

        if (beanModel.getGiftCardSerialNumber() != null)
        {
            beanModel.setGiftCardSerialNumber(giftCardNumberField.getText());
            if (giftCardBalanceField.getText().length() > 0)
            {
                Number balance = LocaleUtilities.parseCurrency(giftCardBalanceField.getText(), getDefaultLocale());
                beanModel.setGiftCardBalance(new BigDecimal(balance.toString()));
            }

        }

        // set index for item selected
        int reasonIndex = reasonCodeComboBox.getSelectedIndex();
        if (reasonIndex >= 0)
        {
            beanModel.setSelectedReasonCode(reasonIndex);
            beanModel.setSelected(true);
        }
        else
        {
            beanModel.setSelected(false);
        }

 /*       // set index for item condition
        int itemConditionIndex = itemConditionCodeComboBox.getSelectedIndex();
        if (itemConditionIndex >= 0)
        {
            beanModel.getItemConditionModel().setSelectedItemConditionCode(itemConditionIndex);
            beanModel.getItemConditionModel().setSelected(true);
        }
        else
        {
            beanModel.getItemConditionModel().setSelected(false);
        }

        // set index for item selected
        int tenderIndex = tenderTypeComboBox.getSelectedIndex();
        if (tenderIndex >= 0)
        {
            beanModel.setTenderSelectedIndex(tenderIndex);
        }*/
    }
}
