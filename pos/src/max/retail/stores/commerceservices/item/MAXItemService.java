package max.retail.stores.commerceservices.item;

import oracle.retail.stores.commerceservices.item.ItemService;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
// add test comment
public class MAXItemService extends ItemService {
	public boolean isEligibleForSend(SaleReturnLineItemIfc lineItem) {
		// start out being true and try to invalidate
		boolean eligible = true;
		// not eligible if this is a service item
		String merchandiseCodesString = lineItem.getPLUItem()
				.getMerchandiseCodesString();

		String[] split = merchandiseCodesString.split(",");
		if (split.length == 10) {
			if (split[1].equalsIgnoreCase("'yes'"))
				eligible = true;
			else
				eligible = false;
		}

		// not eligible if this is a return item
		eligible = (eligible && !isReturnItem(lineItem)) ? true : false;
		eligible = eligible && !lineItem.isPriceAdjustmentLineItem()
				&& !lineItem.isPartOfPriceAdjustment();

		return eligible;
	}
}
