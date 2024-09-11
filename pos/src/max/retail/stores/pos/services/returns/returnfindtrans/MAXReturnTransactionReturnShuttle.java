/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   
*  Rev 1.0   12/08/2014  Shruti Singh   Initial Draft	Centralized Employee Discount 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.returns.returnfindtrans;

import java.math.BigDecimal;
import java.util.Vector;

import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.ReturnItem;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

//--------------------------------------------------------------------------
/**
    This shuttle gets the data from the Return Transaction Service.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXReturnTransactionReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 251294877847834834L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    /**
       Child cargo
    **/
    protected ReturnTransactionCargo rtCargo = null;

    //----------------------------------------------------------------------
    /**
       Store data from child service in the shuttle
       <P>
       @param  bus     Child Service Bus.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        rtCargo = (ReturnTransactionCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Transfer child data to parent cargo.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        if (rtCargo.getTransferCargo())
        {
            cargo.setTransferCargo(true);
            ReturnData rd = new ReturnData();

            PLUItemIfc[] pluItems = rtCargo.getPLUItems();
            Integer[] indexes = getGiftCardNotActiveIndexes(pluItems);
            if (indexes != null)
            {
                Vector vector = getReturnArray(rtCargo.getReturnItems(), indexes);
                int size = vector.size();
                ReturnItemIfc[] rItems = new ReturnItemIfc[size];
                vector.copyInto(rItems);
                rd.setReturnItems(rItems);

                vector = getReturnArray(pluItems, indexes);
                PLUItemIfc[] pItems = new PLUItemIfc[size];
                vector.copyInto(pItems);
                rd.setPLUItems(pItems);

                vector = getReturnArray(rtCargo.getReturnSaleLineItems(), indexes);
                SaleReturnLineItemIfc[] sItems = new SaleReturnLineItemIfc[size];
                vector.copyInto(sItems);
                rd.setSaleReturnLineItems(sItems);
            }
            else
            {
                rd.setReturnItems(rtCargo.getReturnItems());
                rd.setPLUItems(pluItems);
                rd.setSaleReturnLineItems(rtCargo.getReturnSaleLineItems());
                /** Changes for Rev 1.0 : Starts **/
				
				
		        ReturnItemIfc[] ritems = rd.getReturnItems();
		        ReturnItem ritem = null;
		       
		        SaleReturnLineItemIfc[] sr = rd.getSaleReturnLineItems();
//		        SaleReturnLineItemIfc lineItem = null;
		        MAXSaleReturnLineItemIfc lineItem = null;
		        String empId = null;
		        //--
		        BigDecimal purchasedQuantity,returnQuantity = null;
		        //--
		        for(int i=0; i<sr.length; i++)
		        {   
		        	//--
		        	ritem = (ReturnItem) ritems[i];
		        	purchasedQuantity = ritem.getQuantityPurchased();
		        	returnQuantity = ritem.getItemQuantity();
		        	//extendedDiscountSellingAmount = extendedDiscountSellingAmount.divide(purchasedQuantity);
		        	//extendedDiscountSellingAmount = extendedDiscountSellingAmount.multiply(returnQuantity);
		        	//--
		        	lineItem = (MAXSaleReturnLineItemIfc) sr[i];
		        	
		        	ItemDiscountStrategyIfc[] k = lineItem.getItemPrice().getItemDiscounts();
		        	if(k!=null){
		        		for(int disc=0; disc<k.length; disc++){
		        			if(k[disc].getDiscountEmployee()!=null &&
		        					k[disc].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE){
		        				empId=k[disc].getDiscountEmployee().getEmployeeID();
		        				break;
		        			}
		        		}
		        	}
		        	MAXEmployee.isUpdatedAmount = true;
					rtCargo.getOriginalTransaction().setEmployeeDiscountID(empId);
					MAXEmployee.employeeIDreturn = empId;
					break;
				}
				
				/** Changes for Rev 1.0 : Ends **/
			}
            cargo.setReturnData(rd);
            cargo.setOriginalTransaction(rtCargo.getOriginalTransaction());
            cargo.setOriginalTenders(rtCargo.getOriginalTenders());
            cargo.setOriginalTransactionId(rtCargo.getOriginalTransactionId());
            
            // Return items need to have no customer associated with the transaction, because it may not
            // be the same customer.
            cargo.getOriginalTransaction().setCustomer(null);
            
        }
        cargo.setSearchCriteria(rtCargo.getSearchCriteria());
        cargo.setTransactionFound(rtCargo.isTransactionFound());

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ReturnTransactionReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns an array of non returnable gift card indexes
       <P>
       @return int[]
    **/
    //----------------------------------------------------------------------
    protected Integer[] getGiftCardNotActiveIndexes(PLUItemIfc[] pluItems)
    {
        Vector indexes = new Vector();

        for (int i = 0; i< pluItems.length; i++)
        {
            if (pluItems[i] instanceof GiftCardPLUItemIfc)
            {
                // if the status of the gift card is not active add the index to the indexes vector
                if(!((GiftCardPLUItemIfc)pluItems[i]).getGiftCard().getStatus().equals(MAXTenderAuthConstantsIfc.ACTIVE) &&
                   !((GiftCardPLUItemIfc)pluItems[i]).getGiftCard().getStatus().equals(MAXTenderAuthConstantsIfc.APPROVED))
                {
                    indexes.addElement(new Integer(i));
                }
            }
        }

        int size = indexes.size();
        Integer[] indexesArray = null;
        if (size != 0)
        {
            indexesArray = new Integer[size];
            indexes.copyInto(indexesArray);
        }

        return indexesArray;
    }

    //----------------------------------------------------------------------
    /**
       Removes from the array the objects with the specified indexes
       <P>
       @return Vector
    **/
    //----------------------------------------------------------------------
    protected Vector getReturnArray(Object[] returnArray, Integer[] indexes)
    {
      //Vector list = (Vector)Arrays.asList(returnArray);
      Vector list = new Vector();
      for (int i = 0; i < returnArray.length; i++)
      {
          list.addElement(returnArray[i]);
      }

      for (int i = indexes.length - 1; i >= 0; i--)
      {
          list.removeElementAt(indexes[i].intValue());
      }

      return list;
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
    
// Base Defect, Added By Chiranjib and Akanksha Start
	
	public String getDiscountPercentage(String sellingPrice, String discountAmount){
		
		String discountPercent = null;
		double sprice=0,discamount=1,percent=0;
		
		sprice = Double.parseDouble(sellingPrice);
		discamount = Double.parseDouble(discountAmount);
		
		percent = (discamount*100)/sprice;
		discountPercent = String.valueOf(percent);
		
		return discountPercent;
	}
	
	// Base Defect, Added By Chiranjib and Akanksha End
}
