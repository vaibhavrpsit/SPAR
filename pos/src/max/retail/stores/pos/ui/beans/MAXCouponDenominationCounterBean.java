/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		10/July/2013	Changes done for BUG 6984
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;

public class MAXCouponDenominationCounterBean extends BaseBeanAdapter {

	protected MAXCouponDenominationCounterBeanModel beanModel = null;
	protected String QUANTITY_LABEL = "Quantity";
	protected JLabel TOTAL_LABEL = null;
	protected CurrencyTextField total = null;
	protected JPanel jpanel= new JPanel();
	protected JScrollPane scrollPane = null;
	
	protected JLabel couponName= null;
	protected JLabel[] denmLabel = null;
	protected ConstrainedTextField countTotal = null;
	protected JComponent[] quantity = null;

	protected	JPanel		topPanel;
	protected	JTable		table;
	
	protected String columnNames[] = { "Denomination", "Quantity", "Amount" };
	protected String dataValues[][] = null;
	
	protected void buildScreen()
	{
		initialize();
	}
	protected void initialize()
	{
		uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new GridBagLayout());
        initializeDataValues();
        
        topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		topPanel.setSize(300,200);
		
		table = new JTable( dataValues, columnNames );
		table.setEnabled(false);
		
		scrollPane = new JScrollPane( table );
		scrollPane.setMaximumSize(new Dimension(300,200));
		scrollPane.setMinimumSize(new Dimension(300,200));
		scrollPane.setBackground(Color.decode("#cfe0f1"));
		topPanel.add(scrollPane, BorderLayout.CENTER);
		topPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		topPanel.setBackground(UIManager.getColor("beanBackground"));
        
        
		JPanel totalPanel = new JPanel();
		TOTAL_LABEL.setFont(UIManager.getFont("labelFont"));
		
		totalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		totalPanel.add(TOTAL_LABEL);
		totalPanel.add(total);
		totalPanel.setBackground(UIManager.getColor("beanBackground"));
		
		jpanel = new JPanel();
		jpanel.setLayout(new BorderLayout(5,5));
		jpanel.add(topPanel, BorderLayout.CENTER);
		jpanel.add(totalPanel, BorderLayout.SOUTH);
		jpanel.setBackground(UIManager.getColor("beanBackground"));
        
		UIUtilities.layoutComponent(this, new JLabel(""), jpanel, 0, 0, false);
        
        /**Code Commented for BUG 6984**/
        
       /* scrollPane = new JScrollPane();
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
		UIUtilities.layoutComponent(this, new JLabel(TOTAL_LABEL), countTotal, 0, i+5, false);*/
	}
	
	protected void initializeDataValues()
	{
		TOTAL_LABEL = uiFactory.createLabel("Total", null, UI_LABEL);
        total = uiFactory.createCurrencyField("Total", "false", "false", "false");
        total.setEditable(false);
		dataValues = new String[beanModel.getDenominationCount().size()][3];
		CurrencyIfc totals = DomainGateway.getBaseCurrencyInstance();
		MAXCouponDenominationCountSummaryBeanModel model = null;
		
		for(int i=0;i<beanModel.getDenominationCount().size();i++)
		{
			model = (MAXCouponDenominationCountSummaryBeanModel)beanModel.getDenominationCount().get(i);
			dataValues[i][0]	= model.getLabel();
			dataValues[i][1]	= model.getQuantity()+"";  
			dataValues[i][2] 	= model.getAmount().multiply(new BigDecimal(model.getQuantity()))+"";
			totals = totals.add(model.getAmount().multiply(new BigDecimal(model.getQuantity())));
		}
		total.setText(totals.getStringValue());
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
            if (model instanceof MAXCouponDenominationCounterBeanModel)
            {
                beanModel = (MAXCouponDenominationCounterBeanModel)model;
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
