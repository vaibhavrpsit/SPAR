/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import max.retail.stores.pos.services.pricing.MAXModifyTransactionDiscountPercentLaunchShuttle;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    Moves cargo for ModifyTransactionDiscountPercent service. <P>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXModifyEmployeeTransactionDiscountPercentLaunchShuttle
    extends MAXModifyTransactionDiscountPercentLaunchShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7736013436317769790L;
	/**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 3$";
    
    //----------------------------------------------------------------------
    /**
     Gets Manual Discounts by Percentage from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     @return An array of transaction discount strategies
     **/
    //----------------------------------------------------------------------
    

    /**
	 * 
	 */

	/**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.pricing.MAXModifyTransactionDiscountPercentLaunchShuttle.class);

    /**
       revision number of this class
    **/

    /**
       the default sales associate
    **/
    protected EmployeeIfc salesAssociate;

    /**
       discount object
    **/
    protected TransactionDiscountStrategyIfc discountPercent;

    /**
       Flag to determine whether a transaction can be created by the
       child service
    **/
    protected boolean createTransaction;
    
    /**
        Employee Discount ID      
     **/
    protected String employeeDiscountID;
    
    protected String spclEmpDisc;

    boolean isEmployeeRemoveSelected = false;      //Rev 1.0 changes  

    /**
     * Transaction to check against for transaction discounts
     */
    protected SaleReturnTransactionIfc transaction;
    //----------------------------------------------------------------------
    /**
       Loads data from service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve the parent cargo
        MAXPricingCargo cargo = (MAXPricingCargo)bus.getCargo();  //Rev 1.0 changes 
        isEmployeeRemoveSelected = cargo.isEmployeeRemoveSelected(); //Rev 1.0 changes 
        // get transaction
        transaction = (SaleReturnTransactionIfc)cargo.getTransaction();
        employeeDiscountID = cargo.getEmployeeDiscountID();
        spclEmpDisc = cargo.getSpclEmpDisc();

//        // get sales associate
//        salesAssociate = (transaction == null) ?  null :
//                                                  transaction.getSalesAssociate();

        // the percent discount is retrieved; if none exists a null is returned
        // In Quarry, the discount will be the first in the array
        if (transaction != null)
        {
            TransactionDiscountStrategyIfc[] discountArray;
            discountArray = getDiscounts(transaction);

            discountPercent = null;
            if (discountArray != null)
            {
                if (discountArray.length > 0)
                {
                    discountPercent = discountArray[0];
                }
            }
            createTransaction = false;
        }
        else
        {
            createTransaction = true;
        }
      //changes for manageroverride start
        cargo.setAccessFunctionID(MAXRoleFunctionIfc.DISCOUNT);
		MAXUtilityManagerIfc MAXUtilityManager = (MAXUtilityManagerIfc) bus
				.getManager("UtilityManager");
		SaleReturnLineItemIfc[] MAXSaleReturnLineItem = cargo.getItems();

		for (int i = 0; i < MAXSaleReturnLineItem.length; i++) {
			/*
			 * MAXUtilityManager.updateManagerOverrideMap( MAXRoleFunctionIfc.DISCOUNT,
			 * MAXSaleReturnLineItem[i] .getPLUItem().getItemID()); logger.assertLog(true,
			 * "Mukesh");
			 */
			//changes for set value in manager override start
		//	 SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
			
		}
		RetailTransactionIfc trans =cargo.getTransaction();
		MAXManagerOverride mgo= new MAXManagerOverride();
		mgo.setBusinessDay(cargo.getTransaction().getBusinessDay());
		mgo.setStoreCreditId(null);
		mgo.setFeatureId("251");
		mgo.setItemId(null);
		mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
		mgo.setSequenceNumber("-1");
		mgo.setTransactionID(cargo.getTransaction().getTransactionID());
		mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		MAXSaveManagerOverrideTransaction dbTrans = null;
		dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
		try {
			dbTrans.saveManagerOverride(mgo);
		} catch (DataException | com.extendyourstore.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//changes for set value in manager override end
		
		 //changes for manageroverride end
    }

    //----------------------------------------------------------------------
    /**
       Unloads data to service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        // retrieve the child cargo
        MAXModifyTransactionDiscountCargo cargo; //Rev 1.0 changes 
        cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo(); //Rev 1.0 changes 

        cargo.setSpclEmpDisc(spclEmpDisc);
        System.out.println("MAXModifyEmployeeTransactionDiscountPercentLaunchShuttle======== "+cargo.getSpclEmpDisc());
        //cargo.setSalesAssociate(salesAssociate);
        cargo.setDiscount(discountPercent);
        cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
        cargo.setCreateTransaction(createTransaction);
        cargo.setEmployeeDiscountID(employeeDiscountID);
        cargo.setEmployeeRemoveSelected(isEmployeeRemoveSelected); //Rev 1.0 changes 
        if(createTransaction == false)
        {
            cargo.setTransaction(transaction);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     Gets Manual Discounts by Percentage from transaction. <P>
     @param  transaction  SaleReturnTransaction with potential discounts
     @return An array of transaction discount strategies
     **/
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction)
    {
        TransactionDiscountStrategyIfc[] discountArray =
            transaction.getTransactionDiscounts(
                    DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
                    DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        return discountArray;
    }
        
}
