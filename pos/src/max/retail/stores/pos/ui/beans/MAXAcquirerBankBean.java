/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

public class MAXAcquirerBankBean extends ValidatingBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JLabel[] bankName;
	protected ConstrainedTextField[] amount;
	protected JLabel TOTAL_LABEL = uiFactory.createLabel("TOTAL", null, UI_LABEL);
	protected ConstrainedTextField total;
	protected MAXAcquirerBankBeanModel beanModel;

	private void buildScreen()
    {
    	initialize();
    }
	protected void initialize()
	{
		uiFactory.configureUIComponent(this, UI_PREFIX);
	    initializeFields();
	    initializeLabels();
	    initLayout();
        this.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        setCurrentFocus(amount[0]);
	}
	
	protected void initializeFields()
	{
		amount = new ConstrainedTextField[beanModel.getAmount().length];
		for(int i =0; i<beanModel.getAmount().length;i++)
		{
			amount[i] = uiFactory.createConstrainedField(beanModel.getBankName()[i],"0","10");
			amount[i].setText(beanModel.getAmount()[i].toFormattedString());
			amount[i].setEditable(false);
		}
		beanModel.updateTotal();
		total = uiFactory.createConstrainedField("TOTAL","0","10");
		total.setText(beanModel.getTotal().toFormattedString());
		total.setEditable(false);
	}
	protected void initializeLabels()
	{	
		bankName = new JLabel[beanModel.getBankName().length];
		for(int i =0; i<beanModel.getBankName().length;i++)
			bankName[i] = uiFactory.createLabel(beanModel.getBankName()[i], null, UI_LABEL);
	}
	protected void initLayout()
	{
		JLabel[] labels = bankName;
		JComponent[] components = amount;
		setLayout(new GridBagLayout());
		int xValue=0;
		for (int i = 0; i < labels.length; i++)
		{
			UIUtilities.layoutComponent(this, labels[i], components[i], 0, xValue, false);
	        xValue++;
		}
		xValue+=2;
		UIUtilities.layoutComponent(this, new JLabel(" "), total, 0, xValue, false);
		xValue+=2;
		UIUtilities.layoutComponent(this, TOTAL_LABEL, total, 0, xValue, false);

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
            if (model instanceof MAXAcquirerBankBeanModel)
            {
                beanModel = (MAXAcquirerBankBeanModel)model;
                removeAll();
        	    buildScreen();

            }
        }
    }
	public void setVisible(boolean aFlag)
    {
    	if(amount != null)
    	{
    		setCurrentFocus(amount[0]);
    	}
    }
}
