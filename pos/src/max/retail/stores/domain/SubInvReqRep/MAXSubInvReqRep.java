package max.retail.stores.domain.SubInvReqRep;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Changes to capture SubInvReqRep for Reporting purpose
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
import java.io.*;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public class MAXSubInvReqRep implements MAXSubInvReqRepIfc {
	/**
	 * @author Kajal Nautiyal
	 */
	private static final long serialVersionUID = -4870190604565850563L;

	
		
	/**
	 * 
	 */
	public MAXSubInvReqRep() {
		//System.out.println("Going inside MAXSubInvReqRep");
		
	}
	
	protected String  transactionID = "";
    protected String  storeID 		= "";
    protected String  wsID    		= "";
    protected EYSDate businessDay 	= null;
    protected String  SUB_INV_REQ 		= "";
    protected String  SUB_INV_REP		= "";
    public Object clone()
    {
        // instantiate new object
    	MAXSubInvReqRep SubInvReqRep = new MAXSubInvReqRep();

        // set values
        setCloneAttributes(SubInvReqRep);

        // pass back Object
        return SubInvReqRep;
    }

    public void setCloneAttributes(MAXSubInvReqRep newClass)
    {
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
	
	/**
	 * @return the SUB_INV_REQ 
	 */
	public String getSUB_INV_REQ () {
		return SUB_INV_REQ ;
	}
	/**
	 * @param SUB_INV_REQ  the SUB_INV_REQ  to set
	 */
	public void setSUB_INV_REQ(String SUB_INV_REQ) {
		this.SUB_INV_REQ= SUB_INV_REQ;
	}
	/**
	 * @return the SUB_INV_REP
	 */
	public String getSUB_INV_REP() {
		return SUB_INV_REP;
	}
	/**
	 * @param SUB_INV_REP the SUB_INV_REP to set
	 */
	public void setSUB_INV_REP(String SUB_INV_REP) {
		this.SUB_INV_REP = SUB_INV_REP;
	}



	
}
