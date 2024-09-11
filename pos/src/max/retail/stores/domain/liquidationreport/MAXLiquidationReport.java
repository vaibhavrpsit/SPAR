package max.retail.stores.domain.liquidationreport;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Changes to capture Liquidation Item for Reporting purpose
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
import java.io.*;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;



/**
 * @author Kumar Vaibhav
 *
 */
public class MAXLiquidationReport implements MAXLiquidationReportIfc {
	/**
	 * 
	 */
	

	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public MAXLiquidationReport() {
		
		
	}
	
	
	protected String liquidationbarcode = "";
	protected String itemId = "";
    protected String  transactionID = "";
    protected String  storeID 		= "";
    protected String  wsID    		= "";
    protected String itemprice = "";
    protected EYSDate businessDay 	= null;
   
    public Object clone()
    {
        // instantiate new object
    	MAXLiquidationReport liquidationrReport = new MAXLiquidationReport();

        // set values
        setCloneAttributes(liquidationrReport);

        // pass back Object
        return liquidationrReport;
    }

    public void setCloneAttributes(MAXLiquidationReport newClass)
    {
    }

	public String getLiquidationbarcode() {
		return liquidationbarcode;
	}

	public void setLiquidationbarcode(String liquidationbarcode) {
		this.liquidationbarcode = liquidationbarcode;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	public String getWsID() {
		return wsID;
	}

	public void setWsID(String wsID) {
		this.wsID = wsID;
	}

	public String getItemprice() {
		return itemprice;
	}

	public void setItemprice(String itemprice) {
		this.itemprice = itemprice;
	}
    
	/**
	 * @return the businessDay
	 */
	public EYSDate getBusinessDay() {
		return businessDay;
	}
	/**
	 * @param businessDay the businessDay to set
	 */
	public void setBusinessDay(EYSDate businessDay) {
		this.businessDay = businessDay;
	}
	
}
