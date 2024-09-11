/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		8/July/2013		Changes done for BUG 6887
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

public class MAXTidCaptureBean extends ValidatingBean{
	
	protected MAXTidCaptureBeanModel beanModel = null;
	protected JLabel tidLabel = null;
	protected JLabel batchidLabel = null;
	protected JLabel amountLabel = null;
	
	protected ConstrainedTextField tidField = null;
	protected ConstrainedTextField batchidField = null;
	protected CurrencyTextField amountField = null;
	
	
	public MAXTidCaptureBean()
	{
		super();
		buildScreen();
	}
	public void buildScreen()
	{
		initializeLabel();
		initializeField();
		initialize();
	}
//	public void initialize()
//	{
//		
//		initializeLayout();
//	}
	public void initializeField()
	{
		tidField = uiFactory.createConstrainedField("Tid", "1", "20");
		tidField.setName("Tid");
		tidField.setLabel(tidLabel);
		batchidField = uiFactory.createConstrainedField("Batchid","1","20");
		batchidField.setName("Batch ID");
		batchidField.setLabel(batchidLabel);
		
		amountField = uiFactory.createCurrencyField("Amount", "false", "false", "false");
		amountField.setName("Amount");
		amountField.setLabel(amountLabel);

	}
	public void initializeLabel()
	{
		tidLabel = uiFactory.createLabel("TID", null, UI_LABEL);
		batchidLabel = uiFactory.createLabel("Batch Id", null, UI_LABEL);
		amountLabel = uiFactory.createLabel("Amount", null, UI_LABEL);
	}
	public void initialize()
	{
		uiFactory.configureUIComponent(this, UI_PREFIX);
		 setLayout(new GridBagLayout());
	     JLabel[] label ={tidLabel, batchidLabel, amountLabel  };
	     JComponent[] componenet ={tidField, batchidField, amountField};
	     UIUtilities.layoutDataPanel(this, label, componenet, false);
	}
	
	public void setModel(UIModelIfc model)
	{
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set MAXDetailCouponBeanModel" + " to null");
        }
        else
        {
            if (model instanceof MAXTidCaptureBeanModel)
            {
            	beanModel = (MAXTidCaptureBeanModel)model;
            	super.beanModel = (MAXTidCaptureBeanModel)model;
                removeAll();
        	    buildScreen();
        	    updateBean();

            }
        }
	}
	
	public void updateModel()
	{
		if(beanModel instanceof MAXTidCaptureBeanModel)
		{
			MAXTidCaptureBeanModel model = (MAXTidCaptureBeanModel)beanModel;
			if (!tidField.getText().equals(""))
				model.setTid(tidField.getText());
			
			if (!batchidField.getText().equals(""))
			   model.setBatchid(batchidField.getText());
			
			if (!amountField.getText().equals(""))
				   model.setAmount(DomainGateway.getBaseCurrencyInstance(amountField.getText()));
		}
	}
	
	public void updateBean()
	{
		if(beanModel instanceof MAXTidCaptureBeanModel)
		{
			MAXTidCaptureBeanModel model = (MAXTidCaptureBeanModel)beanModel;
			if(model!=null)
			{
				if(model.getTid()!=null)
					tidField.setText(model.getTid());
				if(model.getBatchid()!=null)
					batchidField.setText(model.getBatchid());
				if(model.getAmount()!=null)
					amountField.setText(model.getAmount().getStringValue());
			}
			setFieldRequired(tidField,true);
			setFieldRequired(batchidField,true);
			setFieldRequired(amountField,true);
		}
	}
	
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);
	    if (aFlag && !errorFound())
	    {
	    	if (tidField.isVisible())
	        {
	    		setCurrentFocus(tidField);
	        }
	    }
	}

	/**MAX Rev 1.1 Change : Start**/
	
	/*protected boolean validateFields()
    {
        errorFound = false;
        // Clear out the error message array.
        for (int i = 0; i < MAX_ERROR_MESSAGES; i++)
        {
            errorMessage[i] = "";
        }
        getPOSBaseBeanModel().setFieldInErrorName(null);

        // first make sure all the required fields have valid data
        int errorCount = 0;
        Iterator requiredEnum = requiredFields.iterator();
        while (requiredEnum.hasNext() && errorCount < MAX_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)requiredEnum.next();
            String name = ((Component)field).getName();

            // if input is not valid set error message
            if (!field.isInputValid())
            {
                if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                {
                    getPOSBaseBeanModel().setFieldInErrorName(name);
                }

                errorMessage[errorCount] = invalidDataMsg(field);
                errorCount++;
            }
            else if (field instanceof CurrencyTextField)
            {
            	CurrencyTextField tField = (CurrencyTextField)field;
            	if(tField.getCurrencyValue().getStringValue().equals("0.00"))
            	{
            		if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
            	}
            }
        }

        // now make sure all non-null optional fields have valid data
        Iterator optionalEnum = optionalFields.iterator();
        while (optionalEnum.hasNext() && errorCount < MAX_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)optionalEnum.next();
            String name = ((Component)field).getName();
            if (field instanceof ValidatingTextField)
            {
                ValidatingTextField tField = (ValidatingTextField) field;

                // if a field is non-null and the data is invalid, set error message
                if(!tField.getText().equals("") && !tField.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else if (field instanceof ValidatingComboBox
                && ((ValidatingComboBox) field).isEditable())
            {
                ValidatingComboBox comboBox = (ValidatingComboBox) field;

                // if a field is non-null and the data is invalid, set error message
                if(!"".equals(comboBox.getEditor().getItem()) && !comboBox.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else if (field instanceof ValidatingPasswordField)
            {
                ValidatingPasswordField tField = (ValidatingPasswordField) field;

                // if a field is non-null and the data is invalid, set error message
                if((tField.getPassword().length > 0) && !tField.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else
            {
                // if a field is non-null and the data is invalid set error message
                if(!field.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }

                    errorMessage[errorCount] =  invalidDataMsg(field);
                    errorCount++;
                }
            }
        }

        boolean valid = true;
        if (errorCount > 0)
        {
            // There were errors, show the error screen.
            showErrorScreen();
            valid = false;
        }
        return valid;
    }*/
    
	//---------------------------------------------------------------------
    /**
        Builds text message for invalid data according to the field type
        @param field the field being validated.
        @return Invalid Data Message
    **/
    //---------------------------------------------------------------------
/*    protected String invalidDataMsg(ValidatingFieldIfc field)
    {
        // Change string depending on field type
        String msg = null;
        Object[] data = null;
        if ( field instanceof EYSDateField)
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidDate",
                                          "{0} is not a valid calendar date.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else if (field instanceof EYSTimeField)
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidTime",
                                          "{0} is not a valid time.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else if (field instanceof ValidatingTextField)
        {
            // Determine if we need the "exact" or "at least" message
            int minLength = ((ValidatingTextField)field).getMinLength();
            int maxLength = Integer.MAX_VALUE;
            if (field instanceof ConstrainedTextField)
            {
                maxLength = ((ConstrainedTextField)field).getMaxLength();
            }

            if (minLength == maxLength)
            {
                msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.NotExactLength",
                                          "{0} must be exactly {1} {1,choice,1#character|2#characters} long.");
            }
            else
            {
                msg =  UIUtilities.retrieveText("DialogSpec",
                        BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                        "InvalidData.InvalidLength",
                        "{0} must be at least {1} {1,choice,1#character|2#characters} long.");
            }
            if(field instanceof CurrencyTextField)
            {
            	msg =  UIUtilities.retrieveText("DialogSpec",
                        BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                        "InvalidData.InvalidDenomination",
                        "{0} must have valid denomination.");
            }

            data = new Object[2];
            data[0]= getFieldName(field);
            data[1]= new Integer(minLength);  // at times 'minLength' == 'maxLength'

        }
        else if (field instanceof ValidatingTextAreaField)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidLength",
                                          "{0} must be at least {1} {1,choice,1#character|2#characters} long.");
           data = new Object[2];
           data[0]= getFieldName(field);
           data[1]= new Integer(((ValidatingTextAreaField)field).getMinLength());

        }
        else if (field instanceof ValidatingPasswordField)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidLength",
                                          "{0} must be at least {1} {1, choice,1#character|2#characters} long.");
           data = new Object[2];
           data[0]= getFieldName(field);
           data[1]= new Integer(((ValidatingPasswordField)field).getMinLength());
        }
        else if (field instanceof ValidatingComboBox)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidSelection",
                                          "{0} does not contain a valid selection.");
           data = new Object[1];
           data[0]= getFieldName(field);
        }
        else
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.IncorrectData",
                                          "{0} does not contain valid data.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        msg = LocaleUtilities.formatComplexMessage(msg,data,getLocale());
        return msg;
    }*/
	/**MAX Rev 1.1 Change : End**/
}
