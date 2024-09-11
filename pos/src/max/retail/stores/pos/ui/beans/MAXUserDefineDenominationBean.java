/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.EYSTimeField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingFieldIfc;
import oracle.retail.stores.pos.ui.beans.ValidatingPasswordField;
import oracle.retail.stores.pos.ui.beans.ValidatingTextAreaField;
import oracle.retail.stores.pos.ui.beans.ValidatingTextField;

public class MAXUserDefineDenominationBean extends ValidatingBean {

	protected JLabel DENOMINATION = null;
	protected JLabel QUANTITY = null;
	protected String DENOMINATION_LABEL = "Denomination";
	protected String QUANTITY_LABEL = "Quantity";
	protected CurrencyTextField denmField = null;
	protected NumericTextField qntyField = null;
	protected MAXUserDefineDenominationBeanModel beanModel;
	
	public MAXUserDefineDenominationBean()
	{
		super();
		buildScreen();
	}
	protected void buildScreen()
	{
		initializeLabel();
		initializeField();
		initialize();
	}
	protected void initializeLabel()
	{
		DENOMINATION = uiFactory.createLabel(DENOMINATION_LABEL, null, UI_LABEL);
		QUANTITY = uiFactory.createLabel(QUANTITY_LABEL, null, UI_LABEL);
	}
	protected void initializeField()
	{
		denmField = uiFactory.createCurrencyField(DENOMINATION_LABEL, "false", "false", "false");
		denmField.setName(DENOMINATION_LABEL);
		denmField.setLabel(DENOMINATION);
		denmField.setText("");
		qntyField = uiFactory.createNumericField(QUANTITY_LABEL, "1", "11");
		qntyField.setName(QUANTITY_LABEL);
		qntyField.setLabel(QUANTITY);
		
	}
	protected void initialize()
	{
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new GridBagLayout());
        JLabel[] label ={DENOMINATION, QUANTITY };
        JComponent[] componenet ={denmField, qntyField};
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
            if (model instanceof MAXUserDefineDenominationBeanModel)
            {
            	beanModel = (MAXUserDefineDenominationBeanModel)model;
            	super.beanModel = (MAXUserDefineDenominationBeanModel)model;
                removeAll();
        	    buildScreen();
        	    updateBean();

            }
        }
	}
	public void updateModel()
	{
		if(beanModel instanceof MAXUserDefineDenominationBeanModel)
		{
			MAXUserDefineDenominationBeanModel model = (MAXUserDefineDenominationBeanModel)beanModel;
			if (!denmField.getText().equals(""))
				model.setCurrency(DomainGateway.getBaseCurrencyInstance(denmField.getText()));
			
			if (!qntyField.getText().equals(""))
			   model.setQuantity(Integer.parseInt(qntyField.getText()));
		}
	}
	
	public void updateBean()
	{
		if(beanModel instanceof MAXUserDefineDenominationBeanModel)
		{
			MAXUserDefineDenominationBeanModel model = (MAXUserDefineDenominationBeanModel)beanModel;
			denmField.setText(model.getCurrency()+"");
			qntyField.setText(model.getCurrency()+"");
			setFieldRequired(denmField,true);
			setFieldRequired(qntyField,true);
		}
	}
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);
	    if (aFlag && !errorFound())
	    {
	    	if (denmField.isVisible())
	        {
	    		setCurrentFocus(denmField);
	        }
	    }
	}
	  //---------------------------------------------------------------------
    /**
        Determines if all the required fields have non-null, valid
        data; and determines if all the non-null optional fields have
        valid data; if so, it fires a "validated" event, otherwise it
        fires an "invalidated" event.
        @return True if no errors
    **/
    //---------------------------------------------------------------------
    protected boolean validateFields()
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
    }
    
    //---------------------------------------------------------------------
    /**
        Builds text message for invalid data according to the field type
        @param field the field being validated.
        @return Invalid Data Message
    **/
    //---------------------------------------------------------------------
    protected String invalidDataMsg(ValidatingFieldIfc field)
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
    }
}


