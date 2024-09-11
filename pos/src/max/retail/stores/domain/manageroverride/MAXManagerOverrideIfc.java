package max.retail.stores.domain.manageroverride;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

*  Changes to capture ManagerOverride for Reporting purpose
* 	Rev 1.0 						Kamlesh Pant 	Employee discount validation through OTP.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
import java.io.*;

import com.extendyourstore.foundation.manager.data.DataException;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * @author Karandeep.singh
 *
 */
public interface MAXManagerOverrideIfc extends EYSDomainIfc 
{

	/**
	 * @return the sequenceNumber
	 */
	public abstract String getSequenceNumber();

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public abstract void setSequenceNumber(String sequenceNumber);

	/**
	 * @return the managerId
	 */
	public abstract String getManagerId();

	/**
	 * @param managerId the managerId to set
	 */
	public abstract void setManagerId(String managerId);

	/**
	 * @return the featureId
	 */
	public abstract String getFeatureId();

	/**
	 * @param featureId the featureId to set
	 */
	public abstract void setFeatureId(String featureId);

	/**
	 * @return the storeCreditId
	 */
	public abstract String getStoreCreditId();

	/**
	 * @param storeCreditId the storeCreditId to set
	 */
	public abstract void setStoreCreditId(String storeCreditId);

	/**
	 * @return the itemId
	 */
	public abstract String getItemId();

	/**
	 * @param itemId the itemId to set
	 */
	public abstract void setItemId(String itemId);

	/**
	 * @return the transactionID
	 */
	public String getTransactionID();

	/**
	 * @param transactionID the transactionID to set
	 */
	public void setTransactionID(String transactionID);

	/**
	 * @return the storeID
	 */
	public String getStoreID();

	/**
	 * @param storeID the storeID to set
	 */
	public void setStoreID(String storeID);

	/**
	 * @return the wsID
	 */
	public String getWsID();

	/**
	 * @param wsID the wsID to set
	 */
	public void setWsID(String wsID);

	/**
	 * @return the businessDay
	 */
	public EYSDate getBusinessDay();

	/**
	 * @param businessDay the businessDay to set
	 */
	public void setBusinessDay(EYSDate businessDay);
	
	

	/**
	 * @return the CashierID
	 */
	public String getCashierID();

	/**
	 * @param CashierID the CashierID to set
	 */
	public void setCashierID(String CashierID);

	
//	void setAmountMO(TransactionTotalsIfc transactionTotalsIfc);
	public String getAmountMO();
	public void setAmountMO(String AmountMO);
	
	public String getLMR_ID();
	public void setLMR_ID(String LMR_ID);
	
	//Rev 1.0
	public String getAdditionalInfo();
	public void setAdditionalInfo(String AdditionalInfo);
	public String getAdditionalValue();
	public void setAdditionalValue(String AdditionalValue);
	public String getAccessFunctionTitle();
	public void setAccessFunctionTitle(String AccessFunctionTitle);
	
	//Added by kamlesh pant for manager override
	public long getTransactionNO();
	public void setTransactionNO(long transactionNO);

}