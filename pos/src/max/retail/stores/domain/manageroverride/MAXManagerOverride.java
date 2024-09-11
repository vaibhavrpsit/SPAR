package max.retail.stores.domain.manageroverride;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Changes to capture ManagerOverride for Reporting purpose
*	Rev 1.0 						Kamlesh Pant 	Employee discount validation through OTP.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
import java.io.*;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;


/**
 * @author Karandeep.singh
 *
 */
public class MAXManagerOverride implements MAXManagerOverrideIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4870190604565850563L;

	
		
	/**
	 * 
	 */
	public MAXManagerOverride() {
		//System.out.println("Going inside MAXManagerOverride");
		
	}
	
	protected String sequenceNumber = "";
	protected String managerId = "";
	protected String featureId = "";
	protected String storeCreditId = "";
	protected String itemId = "";

	// Additional transaction-related information. Used for database updates.
    protected String  transactionID = "";
    protected String  storeID 		= "";
    protected String  wsID    		= "";
    protected EYSDate businessDay 	= null;
    protected String cashierID 	= "";   //MO KAJAL NAUTIYAL
	//protected TransactionTotalsIfc amountMO=null;
   protected String AmountMO="";
   protected String LMR_ID="";
    public Object clone()
    {
        // instantiate new object
    	MAXManagerOverride managerOverride = new MAXManagerOverride();

        // set values
        setCloneAttributes(managerOverride);

        // pass back Object
        return managerOverride;
    }

    public void setCloneAttributes(MAXManagerOverride newClass)
    {
    }
    
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#getSequenceNumber()
	 */
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#setSequenceNumber(java.lang.String)
	 */
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#getManagerId()
	 */
	public String getManagerId() {
		return managerId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#setManagerId(java.lang.String)
	 */
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#getFeatureId()
	 */
	public String getFeatureId() {
		return featureId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#setFeatureId(java.lang.String)
	 */
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#getStoreCreditId()
	 */
	public String getStoreCreditId() {
		return storeCreditId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#setStoreCreditId(java.lang.String)
	 */
	public void setStoreCreditId(String storeCreditId) {
		this.storeCreditId = storeCreditId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#getItemId()
	 */
	public String getItemId() {
		return itemId;
	}
	/* (non-Javadoc)
	 * @see com.MAX.pos.ado.utility.MAXManagerOverrideIfc#setItemId(java.lang.String)
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the transactionID
	 */
	public String getTransactionID() {
		return transactionID;
	}
	/**
	 * @param transactionID the transactionID to set
	 */
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	/**
	 * @return the storeID
	 */
	public String getStoreID() {
		return storeID;
	}
	/**
	 * @param storeID the storeID to set
	 */
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}
	/**
	 * @return the wsID
	 */
	public String getWsID() {
		return wsID;
	}
	/**
	 * @param wsID the wsID to set
	 */
	public void setWsID(String wsID) {
		this.wsID = wsID;
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
	//MO KAJAL NAUTIYAL
	public String getCashierID() {
		return cashierID;
	}

	
	@Override
	public void setCashierID(String cashierID) {
		// TODO Auto-generated method stub
		this.cashierID= cashierID;
	}
	public String getAmountMO() {
		return AmountMO;
	}

	
	@Override
	public void setAmountMO(String AmountMO) {
		// TODO Auto-generated method stub
		this.AmountMO= AmountMO;
	}
	public String getLMR_ID() {
		return LMR_ID;
	}

	
	public void setLMR_ID(String LMR_ID) {
		// TODO Auto-generated method stub
		this.LMR_ID= LMR_ID;
	}

	//public TransactionTotalsIfc getAmountMO() {
		//return amountMO;
	//}

	
	//public void setAmountMO(TransactionTotalsIfc transactionTotalsIfc) {
		// TODO Auto-generated method stub
	//	this.amountMO= transactionTotalsIfc;
	//}
	
	//Rev 1.0

	 protected String additionalInfo;
	 protected String additionalValue;
	 protected String accessFunctionTitle;
	 
	 public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getAdditionalValue() {
		return additionalValue;
	}

	public void setAdditionalValue(String additionalValue) {
		this.additionalValue = additionalValue;
	}

	public String getAccessFunctionTitle() {
		return accessFunctionTitle;
	}

	public void setAccessFunctionTitle(String accessFunctionTitle) {
		this.accessFunctionTitle = accessFunctionTitle;
	}
	
	protected long transactionNO ;

	public long getTransactionNO() {
		return transactionNO;
	}

	public void setTransactionNO(long transactionNO) {
		this.transactionNO = transactionNO;
	}
}
