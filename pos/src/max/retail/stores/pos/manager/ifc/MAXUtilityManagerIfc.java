/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package max.retail.stores.pos.manager.ifc;

import java.util.HashMap;
import java.util.Vector;

import max.retail.stores.domain.utility.MAXCodeListMapIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

public abstract interface MAXUtilityManagerIfc extends UtilityManagerIfc {
	
	public static final String TYPE = "MAXUtilityManager";
	public CreditTypeEnum determineCreditType(String cardNumber);
	
	// Changes start for code merging(Added below method as they are not present in base 14)
	public void initializeTransaction(TransactionIfc trans,
            BusIfc bus,
            long seq);
	public void saveTransaction(TransactionIfc trans)
		    throws DataException;
	public MAXCodeListMapIfc getCodeListMap();
	public TransactionTaxIfc getInitialTransactionTax(BusIfc bus);
	public String hasEmployeeDiscounts(SaleReturnTransactionIfc transaction);
	public Vector getReasonCodeTextEntries(CodeListIfc codeList);
	public void setCountries();
	// Changes ends for code merging
	// Changed for Manager Override requirement : Start
		/**
		 * @return Get Manager Override Hashmap
		 */
		public HashMap getManagerOverrideMap();

		/**
		 * Sets Manager Override Map
		 * 
		 * @param managerOverrideMap
		 */
		public void setManagerOverrideMap(HashMap managerOverrideMap);

		/**
		 * Manager Override Function ID
		 * 
		 * @param functionId
		 * @param entry
		 */
		public void updateManagerOverrideMap(int functionId, String entry);
		//Change for manager override new
		public void saveTransaction(TransactionIfc paramTransactionIfc, FinancialTotalsIfc paramFinancialTotalsIfc, TillIfc paramTillIfc, RegisterIfc paramRegisterIfc) throws DataException;

		public void writeHardTotals(BusIfc paramBusIfc) throws DeviceException;


		// Changed for Manager Override requirement : End

}