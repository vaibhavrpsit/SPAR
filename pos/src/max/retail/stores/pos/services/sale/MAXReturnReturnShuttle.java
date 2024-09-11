/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     1 Dec , 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.sale;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import max.retail.stores.domain.employee.MAXEmployee;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import org.apache.log4j.Logger;

/**
 * This shuttle updates the POS service with the information from the Return
 * service.
 * <p>
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 */
//--------------------------------------------------------------------------
public class MAXReturnReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4686166716844288887L;


    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXReturnReturnShuttle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

    /**
     * Returns cargo
     */
    protected ReturnOptionsCargo returnsCargo;

    //----------------------------------------------------------------------
    /**
     * Copies information needed from child service.
     * <P>
     * 
     * @param bus
     *            Child Service Bus to copy cargo from.
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child(ReturnOptions Cargo)
        returnsCargo = (ReturnOptionsCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Stores information needed by parent service.
     * 
     * @param bus
     *            Parent Service Bus to copy cargo to.
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        if (returnsCargo.getTransferCargo() == true)
        {
            SaleReturnTransactionIfc transaction = returnsCargo.getTransaction();
            
            if (transaction != null)
            {    
                // retrieve cargo from the parent(POS Cargo)
                SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
// Changes start for Rev 1.0 (Ashish : Employee Discount)
                SaleReturnTransactionIfc[] returnTransactions = returnsCargo.getOriginalReturnTransactions();
                if (returnTransactions != null && returnsCargo.getTransferCargo())
                {
                    // If returnCargo's originalReturnTransactions is not equal in length to the saleCargo's
                    // originalReturnTransactions, then we add the returnTenderElements.  We don't add the
                    // returnTenderElements when they're equal because the refund tender options looks at these
                    // to calculate if the original transaction has only one tender.
                    if(cargo.getOriginalReturnTransactions() == null ||
                       returnTransactions.length != cargo.getOriginalReturnTransactions().length)
                    {
                        transaction.appendReturnTenderElements(returnsCargo.getOriginalTenders());
                    }
                    cargo.setOriginalReturnTransactions(returnTransactions);
                    // Changes ends for Rev 1.0 (Ashish : Employee Discount)                  
                    /** Changes for Rev 1.0 : Starts **/
                    Vector lineItemsVector = transaction.getLineItemsVector();
                    for(int j=0;j<lineItemsVector.size();j++)
                    {
                    	Enumeration lineItems = lineItemsVector.elements();
                    	while(lineItems.hasMoreElements()){
                    		SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems.nextElement();
                    		SaleReturnTransactionIfc trans = null;
                    		// Changes start for Rev 1.0 (Ashish : Employee Discount)
                    		if(returnTransactions != null && returnTransactions.length != 0){
                    			// Changes end for Rev 1.0 (Ashish : Employee Discount)
                    		Vector originalLineItemsVector = returnTransactions[0].getLineItemsVector();
                    		Enumeration originalLineItemVector = originalLineItemsVector.elements();
                    		while(originalLineItemVector.hasMoreElements()){
                    			SaleReturnLineItemIfc originalLineItem = (SaleReturnLineItemIfc)originalLineItemVector.nextElement();
                    			if(originalLineItem.getPLUItemID() == lineItem.getPLUItemID() ){
                    				ItemDiscountStrategyIfc[] itemDiscount = lineItem.getItemPrice().getItemDiscounts();
                    				ItemDiscountStrategyIfc[] originalItemDiscount = originalLineItem.getItemPrice().getItemDiscounts();
                    				for(int z= 0; z<itemDiscount.length; z++){
                    					for(int z1= 0; z1<originalItemDiscount.length; z1++){
                    						if(itemDiscount[z].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE
                    								&& originalItemDiscount[z1].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE){
                    							itemDiscount[z].setDiscountEmployee(originalItemDiscount[z].getDiscountEmployee());
                    							MAXEmployee.employeeIDreturn = originalItemDiscount[z].getDiscountEmployeeID();
                    						}
                        				}
                    				}
                    			}
                    		}
                    		// Changes start for Rev 1.0 (Ashish : Employee Discount)
                    		}
                    		// Changes ends for Rev 1.0 (Ashish : Employee Discount)
                    	}
                    }
                    /** Changes for Rev 1.0 : Ends **/

                }
    
                updateReturnItems(bus, returnsCargo.isTransactionFound());
                //loop throw the
                cargo.setCustomerInfo(returnsCargo.getCustomerInfo());
                //link the customer to apply any preferred customer discounts
                transaction.linkCustomer(transaction.getCustomer());
                transaction.setCustomerInfo(returnsCargo.getCustomerInfo());
                // update the transaction object in the pos cargo
                cargo.setTransaction(transaction);

                 // setRefreshNeeded(true) in order to update CPOI display with possible
                 // linked customer discounts when we return to the show sale screen
                cargo.setRefreshNeeded(true);
            }
        }

        //clear the line display device
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
        }
    }

    /**
     * @param bus
     * @param fromRetrievedTrans
     */
    protected void updateReturnItems(BusIfc bus, boolean fromRetrievedTrans)
    {
        // retrieve cargo from the parent(POS Cargo)
        ReturnItemIfc[] returnItems = returnsCargo.getReturnItems();
        SaleReturnLineItemIfc[] returnLineItems = returnsCargo.getReturnSaleLineItems();
        if (returnItems != null)
        {
            for (int i = 0; i < returnItems.length; ++i)
            {
                returnItems[i].setFromRetrievedTransaction(fromRetrievedTrans);
                addToLineDisplay(bus, returnLineItems[i]);
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Add item to line display
     * 
     * @param bus
     * @param item
     */
    //----------------------------------------------------------------------
    public void addToLineDisplay(BusIfc bus, SaleReturnLineItemIfc item)
    {
        //Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        try
        {
            pda.lineDisplayItem(item);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage());
        }

    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  ReturnReturnShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}
