package max.retail.stores.domain.liquidationreport;



import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * @author Kumar Vaibhav
 *
 */
public interface MAXLiquidationReportIfc extends EYSDomainIfc {
	
	
	
	

	
	/**
	 * @return the itemId
	 */
	public  String getItemId();

	/**
	 * @param itemId the itemId to set
	 */
	public  void setItemId(String itemId);

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
	
	public String getLiquidationbarcode();

	public void setLiquidationbarcode(String liquidationbarcode) ;

	public String getItemprice();

	public void setItemprice(String itemprice);
	

}
