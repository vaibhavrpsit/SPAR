/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Nitesh Kumar		4/Jan/2016	Changes done for Till reconcillation
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryTenderMenuBean;

public class MAXSummaryTenderMenuBean extends SummaryTenderMenuBean
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6101253959985844335L;
	/**
    	The bean model for this bean
	**/
	protected MAXSummaryTenderMenuBeanModel beanModel = new MAXSummaryTenderMenuBeanModel();

	 //----------------------------------------------------------------------------
    /**
        Constructs the bean.
    **/
    //----------------------------------------------------------------------------
	public MAXSummaryTenderMenuBean()
	    {
	        super();
	        setName("MAXSummaryTenderMenuBean");
	    }
	 
	   //----------------------------------------------------------------------------
    /**
        Calls methods to set up the labels, fields and button bar. <P>
    **/
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        removeAll();

        uiFactory.configureUIComponent(this, UI_PREFIX);

        setLayout(new GridBagLayout());

        SummaryCountBeanModel sc[] = beanModel.getSummaryCountBeanModel();
        CurrencyIfc total          = null;
        
        try
        {
            total =  DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database or server may be offline, using default number of fraction digits", e);
        }


        fieldLabels                = new JLabel[sc.length + 2];
        totalFields                = new JComponent[sc.length + 2];
        String labelText = null;

        // labels and fields for base currency
        for(int i = 0; i < sc.length; i++)
        {
            if (total != null)
            {
                total = total.add(sc[i].getAmount());
            }

            // create label and field
            labelText = retrieveText(sc[i].getLabelTag(),
                                     sc[i].getLabel());
            fieldLabels[i] = uiFactory.createLabel(labelText, null, UI_LABEL);
           
            
            ConstrainedTextField  currencyField = uiFactory.createConstrainedField(sc[i].getDescription() +  "AmountField", "1", "10");
            
            
            currencyField.setText(sc[i].getAmount().toFormattedString());
            currencyField.setEditable(false);
            
            currencyField.setEnabled(false);
            totalFields[i] = currencyField;

            if (sc[i].isFieldDisabled())
            {
                totalFields[i].setEnabled(false);
            }
            
            // layout label field pair
            int col = 0;
            int row = i;
            
            UIUtilities.layoutComponent(this,fieldLabels[i],totalFields[i],col,row,false);
        }

        // blank line - There must be at least one character in order for the line to show
        int pos = sc.length;
        
        fieldLabels[pos] = uiFactory.createLabel(" ", null, UI_LABEL);
        JLabel spacer = uiFactory.createLabel("", null, UI_LABEL);
        spacer.setEnabled(false);
        spacer.setVisible(true);
        totalFields[pos] = spacer;
        
        // total field
        int totalPos = sc.length + 1;

        labelText = retrieveText(TOTAL_LABEL, TOTAL_TEXT);
        fieldLabels[totalPos] = uiFactory.createLabel(labelText, null, UI_LABEL);
        ConstrainedTextField  currencyField = uiFactory.createConstrainedField(TOTAL_TEXT +  "AmountField", "1", "10");
        
        currencyField.setEditable(false);
        if (total != null)
        {
            currencyField.setText(total.toFormattedString());
        }
        
        currencyField.setEnabled(false);
        totalFields[totalPos] = currencyField;

        UIUtilities.layoutDataPanel(this,fieldLabels,totalFields);
    }

	
	 //----------------------------------------------------------------------------
    /**
        Adds the counted amout fields to the panel; the number of fields and
        their values depends on the data in the model. <P>
    **/
    //----------------------------------------------------------------------------
    protected void updateBean()
    {
      initialize();
    } 
	
	  //----------------------------------------------------------------------------
    /**
        Set the bean model into the bean. <P>
        @param model  The bean model
    **/
    //----------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryTenderMenuBean"
                                           + " to null");
        }
        if (model instanceof MAXSummaryTenderMenuBeanModel)
        {
            beanModel = (MAXSummaryTenderMenuBeanModel)model;
            updateBean();
        }
    }
    
    /**
    Displays the bean for test purposes. <P>
    <B>Pre-Condition(s)</B>
    <UL>
    <LI> none.
    </UL>
    <B>Post-Condition(s)</B>
    <UL>
    <LI> None.
    </UL>
    @param args  command-line parameters
**/
//----------------------------------------------------------------------------
public static void main(String[] args)
{
    SummaryCountBeanModel sArray[] = new SummaryCountBeanModel[8];

    SummaryCountBeanModel scbm   = new SummaryCountBeanModel();
    scbm.setDescription("Cash");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("123.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[0] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Charge");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("223.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[1] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Check");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("323.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[2] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Gift Certificate");
    scbm.setAmount(DomainGateway.getBaseCurrencyInstance("423.47"));
    sArray[3] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Store Credit");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("523.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[4] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Traveler's Check");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("623.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    
    sArray[5] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Manufacturer's Coupon");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("723.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[6] = scbm;

    scbm = new SummaryCountBeanModel();
    scbm.setDescription("Retailer's Coupon");
    try
    {
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("823.47"));
    }
    catch (Exception e)
    {
        logger.error("Currency Information is not available, server or database connectivity problems"); 
    }
    sArray[7] = scbm;

    SummaryMenuBeanModel smbm = new SummaryMenuBeanModel();
    smbm.setSummaryCountBeanModel(sArray);
    javax.swing.JFrame jframe = new JFrame();

    final MAXSummaryTenderMenuBean bean = new MAXSummaryTenderMenuBean();
    bean.testOnly = true;
    bean.setModel(smbm);
    bean.activate();

    jframe.setSize(bean.getSize());
    jframe.getContentPane().add(bean);
    jframe.setVisible(true);
}

//Changes for Rev 1.0 Starts
public void updatePropertyFields()
{                                   // begin updatePropertyFields()
    if (beanModel != null)
    {
        SummaryCountBeanModel[] sc = beanModel.getSummaryCountBeanModel();

        if (fieldLabels != null)
        {
            for(int i = 0; i < sc.length; i++)
            {
                fieldLabels[i].setText(retrieveText(sc[i].getLabelTag(),
                                                    sc[i].getLabel()));
            }
            
            fieldLabels[sc.length].setText(retrieveText(TOTAL_LABEL,TOTAL_TEXT));
        }
    }

}
//Changes for rev 1.0 Ends

}
