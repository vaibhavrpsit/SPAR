package max.retail.stores.domain.SubInvReqRep;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 *   Changes to capture SubInvReqRep for Reporting purpose
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
 * @author Kajal Nautiyal
 *
 */
public interface MAXSubInvReqRepIfc extends EYSDomainIfc 
{

	
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
	public String getSUB_INV_REQ ();

	/**
	 * @param SUB_INV_REQ  the SUB_INV_REQ  to set
	 */
	public void setSUB_INV_REQ (String SUB_INV_REQ );

	public String getSUB_INV_REP();

	/**
	 * @param SUB_INV_REP the SUB_INV_REP to set
	 */
	public void setSUB_INV_REP(String SUB_INV_REP);

	
	


	
	
}