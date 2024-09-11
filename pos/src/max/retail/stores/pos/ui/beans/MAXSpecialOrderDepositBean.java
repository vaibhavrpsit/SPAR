/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.2	13/Aug/213		Prateek, Changes done for Special Order CR - Suggested Tender Type
  Rev 1.1	29/May/2013	  	Tanmaya, Bug 6049 - System Allow user to send home delivery in back date. 
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

// java imports

// javax imports
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextAreaField;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.EYSTimeField;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

//------------------------------------------------------------------------------
/**
 *  This bean is used for displaying the Special Order Deposit
 *  screen. The user enters a deposit amount.
 *  @version $Revision: 6$
 */
//------------------------------------------------------------------------------
public class MAXSpecialOrderDepositBean extends ValidatingBean
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: 6$";

    // field number constants
    public static final int CUSTOMER          = 0;
    public static final int SPECIAL_ORDER_NUM = 1;
    public static final int BALANCE_DUE       = 2;
    public static final int MINIMUM_DEPOSIT   = 3;
    public static final int DEPOSIT_AMOUNT    = 4;

    /** array of label text */
    protected static String[] labelText =
    {
        "Customer:", "Special Order Number:", "Balance Due:",
        "Minimum Deposit Due:", "Deposit Amount:" ,"Delivery Date(DD/MM/YY)", "Delivery Time(HH:MM)", "Suggested Tender"
    };

    protected static String[] labelTags =
    {
        "CustomerLabel", "SpecialOrderNumberLabel", "BalanceDueLabel",
        "MinimumDepositDueLabel", "DepositAmountLabel" , "ExpectedDeliveryDateLabel" , "ExpectedDeliveryTimeLabel", "SuggestedTender"
    };

    protected JLabel[] labels;

    /** display field for customer name */
    protected JLabel customerField;

    /** display field for special order number */
    protected JLabel specialOrderNumberField;

    /** display field for balance due */
    protected JLabel balanceDueField;

    /** display field for minimum deposit */
    protected JLabel minimumDepositField;
    
    protected JLabel expectedDeliveryDateLabel = null;

	protected JLabel expectedDeliveryTimeLabel = null;
	
	protected static String EXPECTED_DELIVERY_DATE_LABEL = "ExpectedDeliveryDateLabel";
	protected static String EXPECTED_DELIVERY_TIME_LABEL = "ExpectedDeliveryTimeLabel";
	
	protected EYSDateField expectedDeliveryDateField = null;

	protected EYSTimeField expectedDeliveryTimeField = null;

    /** data entry field for deposit amount */
    protected CurrencyTextField depositAmountField;
    
    /**MAX Rev 1.2 Change : Start**/
	/**Suggested Tender Type**/
    protected ConstrainedTextAreaField suggestedTender = null;
    //Scroll long  text area  of departments
    protected JScrollPane suggestedScrollPane = null;
    /**MAX Rev 1.2 Change : End**/
    
    /** the Bean model */
    protected UIModelIfc beanModel;

    //------------------------------------------------------------------------------
    /**
     *  Default Constructor.
     */
    //------------------------------------------------------------------------------
    public MAXSpecialOrderDepositBean()
    {
        super();
    }

    //------------------------------------------------------------------------------
    /**
     *  Configures the class.
     */
    //------------------------------------------------------------------------------
    public void configure()
    {
        beanModel = new MAXSpecialOrderDepositBeanModel();
        setName("SpecialOrderDeposit");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        labels = new JLabel[labelText.length];

        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        // create the fields
        customerField = uiFactory.createLabel("CustomerField",null,UI_LABEL);

        specialOrderNumberField = uiFactory.createLabel("SpecialOrderNumberField",null,UI_LABEL);

        balanceDueField = uiFactory.createLabel("BalanceDueField",null,UI_LABEL);

        minimumDepositField = uiFactory.createLabel("MinimumDepositField",null,UI_LABEL);

        depositAmountField =
            uiFactory.createCurrencyField("DepositAmountField", "true", "true", "true");

        
        expectedDeliveryDateField = 
        	uiFactory.createEYSDateField("ExpectedDeliveryDateField");
        expectedDeliveryTimeField = 
        	uiFactory.createEYSTimeField("ExpectedDeliveryTimeField");
        
        suggestedScrollPane = uiFactory.createConstrainedTextAreaFieldPane
        ("instrViaScrollPane",  "0", "100", "80",
         "true",
         "true",
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        suggestedTender = (ConstrainedTextAreaField)suggestedScrollPane.getViewport().getView();
        
        UIUtilities.layoutDataPanel(
            this,
            labels,
            new JComponent[] {customerField,
                              specialOrderNumberField,
                              balanceDueField,
                              minimumDepositField,
                              depositAmountField,
                              expectedDeliveryDateField,
                              expectedDeliveryTimeField,
                              suggestedScrollPane
            }
        );
    }

    //------------------------------------------------------------------------------
    /**
     *  Returns the base bean model.
     *  @return POSBaseBeanModel
     */
    //------------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (POSBaseBeanModel)beanModel;
    }


    //------------------------------------------------------------------------------
    /**
     *  Updates the model from the screen.
     */
    //------------------------------------------------------------------------------
    public void updateModel()
    {
        MAXSpecialOrderDepositBeanModel m = (MAXSpecialOrderDepositBeanModel)beanModel;

        m.setBalanceDueValue(balanceDueField.getText());
        if (depositAmountField.getCurrencyValue() != null)
        {
            m.setDepositAmountValue(depositAmountField.getCurrencyValue());
        }
        else
        {
            m.setDepositAmountValue(DomainGateway.getBaseCurrencyInstance("0.00"));
        }
        m.setExpectedDeliveryDate(expectedDeliveryDateField.getEYSDate());
        m.setExpectedDeliveryTime(new EYSTime(expectedDeliveryTimeField.getEYSDate()));
        m.setSuggestedTender(suggestedTender.getText());
        
        
    }

    //------------------------------------------------------------------------------
    /**
     *  Sets the model property value.
     *  @param model UIModelIfc the new value for the property.
     */
    //------------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set " +
                "SpecialOrderDepositBean model to null");
        }
        else
        {
           if (model instanceof MAXSpecialOrderDepositBeanModel)
            {
                beanModel = (MAXSpecialOrderDepositBeanModel) model;
                updateBean();
            }
        }
    }

    //------------------------------------------------------------------------------
    /**
     *  Updates the information displayed on the screen if the model's
     *  been changed.
     */
    //------------------------------------------------------------------------------
    protected void updateBean()
    {
        MAXSpecialOrderDepositBeanModel m = (MAXSpecialOrderDepositBeanModel)beanModel;

        customerField.setText(m.getCustomerValue());
        specialOrderNumberField.setText(m.getSpecialOrderNumberValue());
        balanceDueField.setText(m.getBalanceDueValue());
        minimumDepositField.setText(m.getMinimumDepositValue());
        depositAmountField.setValue(m.getDepositAmountValue());
        expectedDeliveryDateField.setDate(m.getExpectedDeliveryDate());
        expectedDeliveryTimeField.setTime(m.getExpectedDeliveryTime());
        suggestedTender.setText(m.getSuggestedTender());
    }

    //------------------------------------------------------------------------------
    /**
       Override ValidatingBean setVisible() to request focus. Uses the internal
       focusField attribute.
       @param  aFlag indicates if the component should be visible or not.
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // ValidatingBean sets the errorFound flag when it finds a
        // validation error. If an error has been found, ValidatingBean
        // sets the focus in the first error field.

        if (aFlag && !getErrorFound())
        {
            setCurrentFocus(depositAmountField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Activates this bean.
     */
    public void activate()
    {
        super.activate();
        depositAmountField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        depositAmountField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }
    }                                   // end updatePropertyFields()

    //------------------------------------------------------------------------------
    /**
     *  Returns a string representation of this object.
     *  @return String representation of object
     */
    //------------------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SpecialOrderDepositBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //------------------------------------------------------------------------------
    /**
     *  Retrieves the revision number.
     *  @return String representation of revision number
     */
    //------------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }


    //------------------------------------------------------------------------------
    /**
     *  Entry point for testing.
     *  @param args command line parameters
     */
    //------------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();


        MAXSpecialOrderDepositBean
            bean = new MAXSpecialOrderDepositBean();
            bean.configure();
            bean.updateBean();

        UIUtilities.doBeanTest(bean);
    }
}
