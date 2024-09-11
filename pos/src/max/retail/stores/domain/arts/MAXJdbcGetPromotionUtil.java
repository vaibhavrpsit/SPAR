/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  
 *  Rev 1.0  29/05/2013               Izhar                                      Discount rule
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import java.util.HashMap;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcGetPromotionUtil extends JdbcDataOperation
		implements MAXARTSDatabaseIfc, MAXDiscountRuleConstantsIfc {

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		// TODO Auto-generated method stub

		if (logger.isDebugEnabled())
			logger.debug("Inside HCJdbcGetPromotionUtil.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		HashMap map = (HashMap) action.getDataObject();
		PLUItemIfc itemId = (PLUItemIfc) map.get(PLUITEM_ID);
		int comparisonBasis = Integer.parseInt((String) map.get(COMPARISION_BASIS));

		String value = "";
		// JdbcPLUOperation pluOperation = new JdbcPLUOperation();

		switch (comparisonBasis) {

		/*case COMPARISON_BASIS_ITEM_GROUP:
			value = MAXJdbcPLUOperation.getItemGroups(itemId, (JdbcDataConnection) dataConnection);

			break;
*/
		default:

			break;
		}
		dataTransaction.setResult(new StringBuffer(value));

	}

}
