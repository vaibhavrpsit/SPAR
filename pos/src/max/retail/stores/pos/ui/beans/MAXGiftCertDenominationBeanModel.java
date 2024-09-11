/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.List;

import max.retail.stores.domain.tender.MAXDenominationCount;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXGiftCertDenominationBeanModel extends POSBaseBeanModel {

	protected List denomination;
	protected CurrencyIfc total;
	
	
	public MAXGiftCertDenominationBeanModel() {
		super();
	}

	public MAXGiftCertDenominationBeanModel(List denomination) {
		super();
		this.denomination = denomination;
	}

	public List getDenomination() {
		return denomination;
	}

	public void setDenomination(List denomination) {
		this.denomination = denomination;
	}
	
	public CurrencyIfc getTotal() {
		return total;
	}

	public void setTotal(CurrencyIfc total) {
		this.total = total;
	}

	public void updateTotals()
	{
		total = DomainGateway.getBaseCurrencyInstance();
		for(int i=0;i<denomination.size();i++)
		{
			MAXDenominationCount count = (MAXDenominationCount)denomination.get(i);
			total = total.add(count.getCurrency().multiply(new BigDecimal(count.getQuantity())));
		}
	}
}
