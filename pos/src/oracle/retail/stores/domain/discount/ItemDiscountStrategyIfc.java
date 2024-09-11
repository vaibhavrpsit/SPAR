package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;

public abstract interface ItemDiscountStrategyIfc extends DiscountRuleIfc {
	public static final String revisionNumber = "$Revision: /main/18 $";

	public abstract CurrencyIfc calculateItemDiscount(CurrencyIfc paramCurrencyIfc, BigDecimal paramBigDecimal);

	public abstract ItemDiscountStrategyIfc getProratedOrderItemDiscountForPickupOrCancel(BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3,
			OrderItemDiscountStatusIfc paramOrderItemDiscountStatusIfc);

	public abstract ItemDiscountStrategyIfc getProratedOrderItemDiscountForReturn(BigDecimal paramBigDecimal1,
			BigDecimal paramBigDecimal2, OrderItemDiscountStatusIfc paramOrderItemDiscountStatusIfc);

	/** @deprecated */
	public abstract CurrencyIfc calculateItemDiscount(CurrencyIfc paramCurrencyIfc);

	public abstract CurrencyIfc getItemDiscountAmount();

	public abstract void setItemDiscountAmount(CurrencyIfc paramCurrencyIfc);

	public abstract void setDamageDiscount(boolean paramBoolean);

	public abstract boolean isDamageDiscount();

	public abstract void setDiscountEmployee(EmployeeIfc paramEmployeeIfc);

	public abstract void setDiscountEmployee(String paramString);

	public abstract boolean isEmployeeDiscount();

	public abstract EmployeeIfc getDiscountEmployee();

	public abstract String getDiscountEmployeeID();

	/** @deprecated */
	public abstract int getPromotionId();

	/** @deprecated */
	public abstract void setPromotionId(int paramInt);

	/** @deprecated */
	public abstract int getPromotionComponentId();

	/** @deprecated */
	public abstract void setPromotionComponentId(int paramInt);

	/** @deprecated */
	public abstract int getPromotionComponentDetailId();

	/** @deprecated */
	public abstract void setPromotionComponentDetailId(int paramInt);

	public abstract int getPricingGroupID();

	public abstract void setPricingGroupID(int paramInt);
	
	//Changes to resolve missing txn issues in prod : Starts
	public HashMap getCapillaryCoupon();
	public void setCapillaryCoupon(HashMap capillaryCoupon);
	//Changes to resolve missing txn issues in prod : Ends
	

	
}