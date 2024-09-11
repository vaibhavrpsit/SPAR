/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2015 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * Rev 1.0    04 May, 2017  Kritica Agarwal    GST changes
 * 
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.transaction;

import oracle.retail.stores.domain.transaction.SearchCriteria;

public class MAXSearchCriteria extends SearchCriteria implements MAXSearchCriteriaIfc {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8171882882355588072L;
	protected Boolean interStateDelivery = false;
	protected String fromRegion;
	protected String toRegion;

	/**
	 * @return the fromRegion
	 */
	@Override
	public String getFromRegion() {
		return fromRegion;
	}

	/**
	 * @param fromRegion the fromRegion to set
	 */
	@Override
	public void setFromRegion(String fromRegion) {
		this.fromRegion = fromRegion;
	}

	/**
	 * @return the toRegion
	 */
	@Override
	public String getToRegion() {
		return toRegion;
	}

	/**
	 * @param toRegion the toRegion to set
	 */
	@Override
	public void setToRegion(String toRegion) {
		this.toRegion = toRegion;
	}

	/**
	 * @return the interStateDelivery
	 */
	@Override
	public Boolean getInterStateDelivery() {
		return interStateDelivery;
	}

	/**
	 * @param interStateDelivery the interStateDelivery to set
	 */
	@Override
	public void setInterStateDelivery(Boolean interStateDelry) {
		interStateDelivery = interStateDelry;
	}

	@Override
	public void setGiftCardNumber(String value) {
		// TODO Auto-generated method stub
		
	}

	protected boolean empID = false;

	public boolean getEmpID() {
		return empID;
	}

	public void setEmpID(boolean empID) {
		this.empID = empID;
	}
	
}