/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

// foundation imports
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the POS service to the Pricing service. <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPricingLaunchShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6856443585679436389L;

	/** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXPricingLaunchShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: 3$";

    /** class name constant **/
    public static final String SHUTTLENAME = "MAXPricingLaunchShuttle";

    /** Pos Cargo **/
    protected SaleCargoIfc saleCargo = null;

    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the POS service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        saleCargo = (SaleCargoIfc)bus.getCargo();
        saleCargo.getPLUItem().getSpclEmpDisc();
    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the Pricing service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        MAXPricingCargo pricingCargo = (MAXPricingCargo)bus.getCargo();
        pricingCargo.setOriginalPriceAdjustmentTransactions(((MAXSaleCargo) saleCargo).getOriginalPriceAdjustmentTransactions());
               
        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
        pricingCargo.setTransaction(transaction);
        /**
         * Rev 1.0 changes start here
         */
        if(saleCargo.getPLUItem().getSpclEmpDisc().equalsIgnoreCase("SpecEmpDisc"))
		{
			//System.out.println("pricingCargo 87 ===============");
			pricingCargo.setSpclEmpDisc(saleCargo.getPLUItem().getSpclEmpDisc());
			//System.out.println("pricingCargo 90 ==============="+pricingCargo.getSpclEmpDisc());
		}
        String pmValue = null;
        //System.out.println("((MAXSaleCargo)saleCargo).getEmpID()=====================:"+((MAXSaleCargo)saleCargo).getEmpID());
        if(((MAXSaleCargo)saleCargo).getEmpID())
        {
        	pricingCargo.setEmpID(true);
        }
		ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			pmValue = pm2.getStringValue("EmployeeDiscountMethod");
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXPricingLaunchShuttle.unload(), cannot find EmployeeDiscountMethod parameter.");
		}
		if ("Manual".equalsIgnoreCase(pmValue)) {
			pricingCargo.setEmployeeDiscountMethod("Manual");
		} 
		 /**
         * Rev 1.0 changes end here
         */
        if (transaction != null)
        {                        
            // Get the selected lines items from the sale cargo
            ArrayList itemList = new ArrayList();
            SaleReturnLineItemIfc[] cargoItems = saleCargo.getLineItems();
            MAXSaleReturnLineItem ovdItems = null;        
            if (cargoItems != null)
            {
                for (int i = 0; i < cargoItems.length; i++)
                {
                    if ( !(cargoItems[i].isPriceAdjustmentLineItem() || cargoItems[i].isPartOfPriceAdjustment()) )
                    {
                    		ovdItems = (MAXSaleReturnLineItem)cargoItems[i];
                    	 Iterator itr = (((SaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItemsVector()).iterator();
                         while(itr.hasNext())
                         { 
                               MAXSaleReturnLineItem txnitm = (MAXSaleReturnLineItem)(itr.next());
                               if(ovdItems.getPLUItemID().equals(txnitm.getPLUItemID()))
                                {
                                        		((MAXSaleReturnLineItem)cargoItems[i]).setScansheetCategoryID(txnitm.getScansheetCategoryID());
                                        		((MAXSaleReturnLineItem)cargoItems[i]).setScansheetCategoryDesc(txnitm.getScansheetCategoryDesc());
                                 }
                               }
                        itemList.add(cargoItems[i]);
                    }
                }
            }
                        
            SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);     
        
            if (items != null)
            {
                int[] indices = new int[items.length];
    
                if (items.length > 0)
                {
                    pricingCargo.setItems(items);
                    for (int j = 0; j < items.length; j++)
                    {
                         indices[j] = items[j].getLineNumber();
                    }
                    pricingCargo.setIndices(indices);
                }
            }
    
            pricingCargo.setEmployeeDiscountID(transaction.getEmployeeDiscountID());
        }
        
    }
}
