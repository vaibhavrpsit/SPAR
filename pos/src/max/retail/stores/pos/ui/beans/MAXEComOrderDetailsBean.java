/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
  
 	Rev 1.2     12/12/2017      Karni Singh        Change to save ECOM order type.
    Rev 1.1     31/03/2017      Nitika Arora        Changes for correct invalid error message.
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
	
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;

public class MAXEComOrderDetailsBean extends ValidatingBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String revisionNumber = "$Revision: 7$ $EKW;";
	
	protected MAXEComOrderDetailsBeanModel beanModel;
	
	protected JLabel ecomTransNoLabel = null;
	protected JLabel ecomOrderNoLabel = null;
	protected JLabel eComOrderAmountLabel = null;
	protected JLabel ecomOrderTypeLabel = null;
	
	protected CurrencyTextField txtEComOrderAmountField = null;
	protected NumericTextField txtEComTransNoField = null;
	protected AlphaNumericTextField txtEComOrderNoField = null;
	
	/** the list of order types names */
	protected JList choiceList;

	protected ValidatingComboBox orderTypeList = null;
	
	protected JScrollPane scrollPane = null;
	
	public MAXEComOrderDetailsBean()
	{
		super();
		setName("MAXEComOrderDetailsBean");
		initialize();
	}
	
	protected void initialize()
	{
		initializeLabels();
		initializeFields();
		initLayout();
	}

	public POSBaseBeanModel getPOSBaseBeanModel()
	{
	    return this.beanModel;
	}
	
	public void initializeFields()
	{
		txtEComOrderNoField = uiFactory.createAlphaNumericField("txtEComOrderNoField", "1", "13");
		txtEComOrderNoField.setLabel(ecomOrderNoLabel);
		txtEComOrderNoField.setErrorMessage("ECom Order Number");
		txtEComOrderAmountField = uiFactory.createCurrencyField("txtEComOrderAmountField", "false", "false","false");
		txtEComOrderAmountField.setLabel(eComOrderAmountLabel);
		txtEComOrderAmountField.setErrorMessage("ECom Order Amount");
		txtEComTransNoField = uiFactory.createNumericField("txtEComTransNoField", "1", "14");
		txtEComTransNoField.setLabel(ecomTransNoLabel);
		txtEComTransNoField.setErrorMessage("ECom Transaction Number");
		orderTypeList = uiFactory.createValidatingComboBox("EComOrderTypeList");
		orderTypeList.setLabel(ecomOrderTypeLabel);
		orderTypeList.setErrorMessage("ECom Order Type");
		
		scrollPane = uiFactory.createSelectionList("orderTypeList", "large");
		scrollPane.setSize(100, 200);
		choiceList = (JList)scrollPane.getViewport().getView();
		choiceList.setBorder(BorderFactory.createLineBorder(Color.gray));
		choiceList.setSize(15, 5);
	}
	
	public void initializeLabels()
	{
		ecomOrderNoLabel = uiFactory.createLabel("ecomOrderNoLabel","ecomOrderNoLabel", null, UI_LABEL);
		eComOrderAmountLabel = uiFactory.createLabel("eComOrderAmountLabel","eComOrderAmountLabel", null, UI_LABEL);
		ecomTransNoLabel = uiFactory.createLabel("ecomTransNoLabel","ecomTransNoLabel", null, UI_LABEL);
		ecomOrderTypeLabel = uiFactory.createLabel("ecomOrderTypeLabel","ecomOrderTypeLabel", null, UI_LABEL);
	}
	
	public void initLayout()
	{
		uiFactory.configureUIComponent(this, UI_PREFIX);
		 setLayout(new GridBagLayout());
	     JLabel[] label ={ecomOrderNoLabel, eComOrderAmountLabel, ecomTransNoLabel , ecomOrderTypeLabel};
	     JComponent[] componenet ={txtEComOrderNoField, txtEComOrderAmountField, txtEComTransNoField,orderTypeList};
	     UIUtilities.layoutDataPanel(this, label, componenet, false);
	}
	
	public void setModel(UIModelIfc model)
	{
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set MAXEComOrderDetailsBean" + " to null");
        }
        else
        {
            if (model instanceof MAXEComOrderDetailsBeanModel)
            {
            	beanModel = (MAXEComOrderDetailsBeanModel)model;
        	    updateBean();

            }
        }
	}
	
	public void updateModel()
	{
		if(beanModel instanceof MAXEComOrderDetailsBeanModel)
		{
			MAXEComOrderDetailsBeanModel model = (MAXEComOrderDetailsBeanModel)beanModel;
			if(!txtEComOrderNoField.getText().trim().equals(""))
			model.setTxtEComOrderNoField(txtEComOrderNoField.getText());
			
			if(!txtEComOrderAmountField.getText().trim().equals(""))
			model.setTxtEComOrderAmountField(DomainGateway.getBaseCurrencyInstance(txtEComOrderAmountField.getText()));
			
			if(!txtEComTransNoField.getText().trim().equals(""))
			model.setTxtEComTransNoField(txtEComTransNoField.getText());
			
			model.setSelectedOrderType(this.orderTypeList.getSelectedIndex());
			String orderType = String.valueOf(this.orderTypeList.getSelectedItem());
			model.setOrderType(orderType);
		//	model.setBankName(String.valueOf(""));
		}
	}
	
	protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex)
	{
		if (data != null)
		{
			ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);
			field.setModel(model);
			field.setSelectedIndex(selectedIndex);
		}
	}
	
	public void updateBean()
	{
		if(beanModel instanceof MAXEComOrderDetailsBeanModel)
		{
			MAXEComOrderDetailsBeanModel model = (MAXEComOrderDetailsBeanModel)beanModel;
			if(model!=null)
			{
				if(model.getTxtEComOrderNoField()!=null)
					txtEComOrderNoField.setText(model.getTxtEComOrderNoField());
				else
					txtEComOrderNoField.setText("");
				if(model.getTxtEComOrderAmountField()!=null)
					txtEComOrderAmountField.setText(model.getTxtEComOrderAmountField().getStringValue());
				else
					txtEComOrderAmountField.setText("");
				if(model.getTxtEComTransNoField()!=null)
					txtEComTransNoField.setText(model.getTxtEComTransNoField());
				else
					txtEComTransNoField.setText("");
				
				String[] choices = null;
				
					choices = new String[model.getOrderTypes().length];
					choices = model.getOrderTypes();
					
				choiceList.setListData(choices);
				choiceList.setSelectedIndex(model.getSelectedOrderType());
				setComboBoxModel(choices, orderTypeList, model.getSelectedOrderType());
			}
		}
	}
	
	
	public void updatePropertyFields()
	{
		ecomOrderNoLabel.setText(retrieveText("ecomOrderNoLabel", ecomOrderNoLabel));
		eComOrderAmountLabel.setText(retrieveText("eComOrderAmountLabel", eComOrderAmountLabel));
		ecomTransNoLabel.setText(retrieveText("ecomTransNoLabel", ecomTransNoLabel));	
		ecomOrderTypeLabel.setText(retrieveText("ecomOrderTypeLabel", ecomOrderTypeLabel));
	}
	
	public void setVisible(boolean aFlag)
	{
	    if (aFlag && !getErrorFound())
	    {
	    	setCurrentFocus(txtEComOrderNoField);  
	    }
	}
	
	/* * Returns default display string.
	 * <P>
	 * 
	 * @return String representation of object
	 */
	// ---------------------------------------------------------------------
	public String toString() {
		String strResult = new String("Class: MAXEComOrderDetailsBean (Revision " + getRevisionNumber() + ") @" + hashCode());
		return (strResult);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <P>
	 * 
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}
}
