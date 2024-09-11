/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import max.retail.stores.domain.tender.MAXDenominationCount;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;

public class MAXGiftCertDetailBean extends BaseBeanAdapter {

	private static final long serialVersionUID = 1L;
	protected JPanel jpanel = null;
	protected	JPanel		topPanel;
	protected	JTable		table;
	protected	JScrollPane scrollPane;
	protected 	JLabel TOTAL_LABEL = null;
	protected 	CurrencyTextField total = null;
	
	protected String columnNames[] = { "Denomination", "Quantity", "Amount" };
	protected String dataValues[][] = null;
	
	protected MAXGiftCertDenominationBeanModel beanModel = null;
	
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
				
				//Color.decode("#cfe0f1"));
		
		
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
	} 
	
	protected void initializeDataValues()
	{
		TOTAL_LABEL = uiFactory.createLabel("Total", null, UI_LABEL);
        total = uiFactory.createCurrencyField("Total", "false", "false", "false");
        total.setEditable(false);
		dataValues = new String[beanModel.getDenomination().size()][3];
		MAXDenominationCount count = null;
		for(int i=0;i<beanModel.denomination.size();i++)
		{
			count = new MAXDenominationCount();
			count = (MAXDenominationCount)beanModel.getDenomination().get(i);
			dataValues[i][0]	= count.getCurrency().getStringValue();
			dataValues[i][1]	= count.getQuantity()+"";  
			dataValues[i][2] 	= count.getCurrency().multiply(new BigDecimal(count.getQuantity())).getStringValue();
		}
		beanModel.updateTotals();
		if(beanModel.getTotal().getStringValue() != null)
			total.setText(beanModel.getTotal().getStringValue());
	}
	public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set MAXGiftCertDenominationBeanModel" + " to null");
        }
        else
        {
            if (model instanceof MAXGiftCertDenominationBeanModel)
            {
                beanModel = (MAXGiftCertDenominationBeanModel)model;
                remove();
                removeAll();
        	    buildScreen();
            }
        }
    }
    public void remove()
    {
    	//this.remove();
    	if(topPanel!=null)
    		topPanel.removeAll();
    }    
	
}
