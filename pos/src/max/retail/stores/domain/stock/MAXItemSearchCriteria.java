/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	May 04, 2017		Kritica.Agarwal		GST Changes	
*
********************************************************************************/
 package max.retail.stores.domain.stock;
 import oracle.retail.stores.domain.stock.ItemSearchCriteria;
 
 public class MAXItemSearchCriteria extends ItemSearchCriteria
   implements MAXItemSearchCriteriaIfc 
 {
   private static final long serialVersionUID = 2423311222020699988L;
   public static final String revisionNumber = "$Revision: /main/12 $";
   protected Boolean interStateDelivery = false;

	/**
	 * @return the interStateDelivery
	 */
	public Boolean getInterStateDelivery() {
		return interStateDelivery;
	}

	/**
	 * @param interStateDelivery the interStateDelivery to set
	 */
	public void setInterStateDelivery(Boolean interStateDelry) {
		interStateDelivery = interStateDelry;
	}
 
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
  
 
 }
