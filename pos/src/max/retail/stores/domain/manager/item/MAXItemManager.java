/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	20-Feb-2017		Kritica.Agarwal		GST Changes	
*
********************************************************************************/
 package max.retail.stores.domain.manager.item;
 
 import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.manager.item.ItemManager;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

import org.apache.log4j.Logger;
 public class MAXItemManager extends ItemManager
   implements MAXItemManagerIfc
 {
  private static final Logger logger = Logger.getLogger(MAXItemManager.class);
  
  public PLUItemIfc getPluItem(MAXSearchCriteriaIfc cri) throws DataException 
		    {
		       return getStorePluItem(cri);
		    }
  protected PLUItemIfc getStorePluItem(MAXSearchCriteriaIfc itemSearchCriteria)
		      throws DataException
		    {
		       MAXSearchCriteriaIfc searchCriteria = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
		       searchCriteria.setItemID(itemSearchCriteria.getItemID());
		       searchCriteria.setPosItemID(itemSearchCriteria.getPosItemID());
		       searchCriteria.setSearchItemByPosItemID(itemSearchCriteria.isSearchItemByPosItemID());
		       searchCriteria.setPLURequestor(itemSearchCriteria.getPLURequestor());
		       searchCriteria.setItemNumber(itemSearchCriteria.getItemNumber());
		       searchCriteria.setSearchItemByItemNumber(itemSearchCriteria.isSearchItemByItemNumber());
		       searchCriteria.setStoreNumber(itemSearchCriteria.getStoreNumber());
		       searchCriteria.setGeoCode(itemSearchCriteria.getGeoCode());
		       searchCriteria.setLocaleRequestor(itemSearchCriteria.getLocaleRequestor());
		       searchCriteria.setLookupStoreCoupon(itemSearchCriteria.isLookupStoreCoupon());
		       searchCriteria.setPricingDate(itemSearchCriteria.getPricingDate());		       
		       searchCriteria.setInterStateDelivery(itemSearchCriteria.getInterStateDelivery());
		       if(itemSearchCriteria.getInterStateDelivery()){
			       searchCriteria.setFromRegion(itemSearchCriteria.getFromRegion());
			       searchCriteria.setToRegion(itemSearchCriteria.getToRegion());
		       }
		       itemSearchCriteria.setSearchFromItemDetail(false);
		       if(itemSearchCriteria.getEmpID())
		       {
		    	   searchCriteria.setEmpID(itemSearchCriteria.getEmpID());
		    	   //System.out.println("MAXItemManager============51 ::"+searchCriteria.getEmpID());
		       }
		       PLUTransaction pluTransaction = (PLUTransaction)DataTransactionFactory.create("persistence_PLUTransaction");
		  
		       PLUItemIfc pluItem = null;
		      try
		      {
		         pluItem = pluTransaction.getPLUItem(searchCriteria);
		      }
		      catch (DataException ex) {
		         logger.error(ex);
		      throw ex;
		      }
		      return pluItem;
		    }

  		

 }
