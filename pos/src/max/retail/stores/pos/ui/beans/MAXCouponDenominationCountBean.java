/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;

public class MAXCouponDenominationCountBean extends BaseBeanAdapter {

	protected MAXCouponDenominationCounterBeanModel countBeanModel = null;
	protected String QUANTITY_LABEL = "Quantity";
	protected String TOTAL_LABEL = "Total";
	protected JPanel jpanel= new JPanel();
	protected JScrollPane scrollPane = null;
	
	protected JLabel couponName= null;
	protected JLabel[] denmLabel = null;
	protected ConstrainedTextField countTotal = null;
	protected JComponent[] quantity = null;
		
	protected void buildScreen()
	{
		initialize();
	}
	protected void initialize()
	{
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new GridBagLayout());
        ArrayList denmCount = countBeanModel.getDenominationCount();
        MAXCouponDenominationCountSummaryBeanModel model = new MAXCouponDenominationCountSummaryBeanModel();
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMaximumSize(new Dimension(400,300));
        jpanel.setLayout(new GridBagLayout());
        jpanel.setSize(400,300);
        jpanel.setVisible(true);
        jpanel.setOpaque(false);
        scrollPane.add(jpanel);
        denmLabel = new JLabel[denmCount.size() ];
        quantity = new JComponent[denmCount.size()];
        int i=0;
        for(i=0; i< denmCount.size();i++)
        {
        	model = (MAXCouponDenominationCountSummaryBeanModel)denmCount.get(i);
        	CurrencyIfc amount = model.getAmount();
        	int qnty = model.getQuantity();
        	total = total.add(model.getAmount().multiply(new BigDecimal(qnty)));
        	denmLabel[i] = new JLabel();
        	denmLabel[i] = uiFactory.createLabel(model.getLabel(), null, UI_LABEL);
        	
        	ConstrainedTextField count = uiFactory.createConstrainedField(model.getLabel(),"0", "10");
        	count.setEditable(false);
        	

        	
        	count.setText(amount.multiply(new BigDecimal(qnty))+"");
        	quantity[i] = count;
        	int row=i;
        	int col=0;
    		UIUtilities.layoutComponent(jpanel, denmLabel[i], quantity[i], col, row, false);
        } 
    	UIUtilities.layoutComponent(this, new JLabel(""), jpanel, 0, i+5, false);
        i+=2;
		UIUtilities.layoutComponent(this, new JLabel(" "), new JLabel(" "), 0, i+5, false);
		i+=2;
		countTotal = uiFactory.createConstrainedField(TOTAL_LABEL,"0", "10");
		countTotal.setEditable(false);
		countTotal.setText(total+"");
		UIUtilities.layoutComponent(this, new JLabel(TOTAL_LABEL), countTotal, 0, i+5, false);
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
            if (model instanceof MAXCouponDenominationCountBeanModel)
            {
                countBeanModel = (MAXCouponDenominationCounterBeanModel)model;
                remove();
                removeAll();
        	    buildScreen();

            }
        }
    }
    public void remove()
    {
    	//this.remove();
    	jpanel.removeAll();
    }    
}
