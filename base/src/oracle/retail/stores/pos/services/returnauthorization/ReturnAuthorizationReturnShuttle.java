/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/ReturnAuthorizationReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/12 10:28:09 mchellap Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  08/12/11 - BUG#11854626 Customer Information not send to RM for
 *                         retrieved transactions
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

//foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//------------------------------------------------------------------------------
/**
  Shuttles the required data from the Return Authorization cargo to the Sale Cargo.
  <P>
  @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------
public class ReturnAuthorizationReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 3575674202259060935L;

/**
      revision number of this class
  **/
  public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
  
  public static final String SHUTTLENAME = "ReturnAuthorizationReturnShuttle";

  /**
      return authorization cargo
  **/
  protected ReturnAuthorizationCargo returnAuthorizationCargo;

  //---------------------------------------------------------------------
  /**
     Get a local copy of the Return Authorization cargo.
     <P>
     @param bus the bus being loaded
  **/
  //---------------------------------------------------------------------
  public void load(BusIfc bus)
  {
      super.load(bus);
      // retrieve Return Authorization cargo
      returnAuthorizationCargo = (ReturnAuthorizationCargo) bus.getCargo();
  }

  //---------------------------------------------------------------------
  /**
     Copy required data from the Return Authorization cargo to the Sale Cargo.
     sets the retailtransaction to the Sale Return transaction.

     @param bus the bus being unloaded
  **/
  //---------------------------------------------------------------------
  public void unload(BusIfc bus)
  {
      super.unload(bus);

      // retrieve Sale cargo
      SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
      cargo.setRetailTransactionIfc(returnAuthorizationCargo.getTransaction());
      cargo.setOriginalReturnTransactions(returnAuthorizationCargo.getOriginalReturnTransactions());
      cargo.setReturnResponse(returnAuthorizationCargo.getReturnResponse());
      cargo.setReturnRequest(returnAuthorizationCargo.getReturnRequest());
      cargo.setReturnResult(returnAuthorizationCargo.getReturnResult());
      cargo.setCustomerInfo(returnAuthorizationCargo.getCustomerInfo());
  }

}   // end class ReturnAuthorizationReturnShuttle

