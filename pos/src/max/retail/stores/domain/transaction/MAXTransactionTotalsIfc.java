/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package max.retail.stores.domain.transaction;

import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

public abstract interface MAXTransactionTotalsIfc extends TransactionTotalsIfc {
	public static final String revisionNumber = "$Revision: /main/21 $";
	
	// Changes done for code merging(added below method)
	public boolean isTransactionLevelSendAssigned();
	public SendPackageLineItemIfc[] getSendPackages();
	public Vector getSendPackageVector();  
	public ShippingMethodIfc getShippingMethod();
	public void addSendPackage(SendPackageLineItemIfc sendPackage);
	public void setShippingMethod(ShippingMethodIfc value);
	public int getItemSendPackagesCount();
	public CurrencyIfc getCalculatedShippingCharge();
	public CurrencyIfc getOffTotal();
	
	public void setOffTotal(CurrencyIfc offTotal);
	/*public void setOffTotal(CurrencyIfc offTotal);*/
	// Changes ends for code merging
}