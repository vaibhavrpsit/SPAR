/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;


public class MAXDetailCouponBean extends ValidatingBean{

	
	protected MAXDetailCouponBeanModel beanModel = null;
	protected JLabel[] couponLabel = null;
	protected ConstrainedTextField[] couponValue = null;
	protected ConstrainedTextField total = null;
	protected JLabel TOTAL_LABEL = uiFactory.createLabel("TOTAL", null, UI_LABEL);
	protected JPanel panel = new JPanel();
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
        setCurrentFocus(couponValue[0]);
	}
	protected void initializeFields()
	{
		couponValue = new ConstrainedTextField[beanModel.getCouponName().length];
		for(int i =0; i<beanModel.getCouponName().length;i++)
		{
			couponValue[i] = uiFactory.createConstrainedField(beanModel.getCouponName()[i],"0","10");
			couponValue[i].setText(beanModel.getCouponValue()[i].toFormattedString());
			couponValue[i].setEditable(false);
		}
		beanModel.updateTotal();
		total = uiFactory.createConstrainedField("TOTAL","0","10");
		total.setText(beanModel.getTotal().toFormattedString());
		total.setEditable(false);
	}
	protected void initializeLabels()
	{	
		couponLabel = new JLabel[beanModel.getCouponName().length];
		for(int i =0; i<beanModel.getCouponName().length;i++)
			couponLabel[i] = uiFactory.createLabel(beanModel.getCouponName()[i], null, UI_LABEL);
	}
	protected void initLayout()
	{
		JLabel[] labels = couponLabel;
		JComponent[] components = couponValue;
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
            if (model instanceof MAXDetailCouponBeanModel)
            {
                beanModel = (MAXDetailCouponBeanModel)model;
                removeAll();
        	    buildScreen();

            }
        }
    }
    public void setVisible(boolean aFlag)
    {
    	if(couponValue != null)
    	{
    		setCurrentFocus(couponValue[0]);
    	}
    }
}
