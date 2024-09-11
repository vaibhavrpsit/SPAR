
package max.retail.stores.pos.reports;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.reports.TillCountReportItem;

public class MAXTillCountReportItem extends TillCountReportItem
  implements Serializable
{
  private static final long serialVersionUID = 9122667808077374881L;

  public MAXTillCountReportItem(MAXTillCountReport report)
  {
    this(report, null);
  }

  public MAXTillCountReportItem(MAXTillCountReport report, CurrencyIfc amount)
  {
	  super(report,amount);
  }

  private String couponSubType;
  public String getCouponSubType()
  {
    return couponSubType;
  }
  public void setCouponSubType(String couponSubType)
  {
	  this.couponSubType=couponSubType;
  }

}