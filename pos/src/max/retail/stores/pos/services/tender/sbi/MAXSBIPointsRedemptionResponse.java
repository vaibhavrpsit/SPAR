package max.retail.stores.pos.services.tender.sbi;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

public class MAXSBIPointsRedemptionResponse implements Serializable{

	private static final long serialVersionUID = -2029553686949689092L;
	public CurrencyIfc convertedAmt = DomainGateway.getBaseCurrencyInstance();
	public CurrencyIfc totalPoint = DomainGateway.getBaseCurrencyInstance();

	public CurrencyIfc getConvertedAmt() {
		return convertedAmt;
	}
	public void setConvertedAmt(CurrencyIfc convertedAmt) {
		this.convertedAmt = convertedAmt;
	}
	public CurrencyIfc getTotalPoint() {
		return totalPoint;
	}
	public void setTotalPoint(CurrencyIfc totalPoint) {
		this.totalPoint = totalPoint;
	}


}
