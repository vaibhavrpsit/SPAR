/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundTenderOverrideActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.tender;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 *  Checks whether able to do refund tender override. If not, the system will access
 *  manager override service.
 */
public class RefundTenderOverrideActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -5084002430587817361L;

  /*  Check if it is able to do refund tender override. If not, the system will access
   *  manager override service.   
   */
  public void arrive(BusIfc bus) 
  {
      TenderCargo cargo = (TenderCargo)bus.getCargo();

      SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher(). getManager(SecurityManagerIfc.TYPE);
      SaleReturnTransactionIfc transaction = (SaleReturnTransaction)cargo.getTransaction();
        
      cargo.setAccessFunctionID(RoleFunctionIfc.REFUND_TENDER_OVERRIDE);
      
      boolean overrideSuccess = false;    
      try 
      {
            overrideSuccess = securityManager.override(
                        cargo.getAppID(), 
                        transaction.getCashier().getLoginID(),
                        transaction.getCashier().getPasswordBytes(),
                        RoleFunctionIfc.REFUND_TENDER_OVERRIDE);
      } catch (LoginException e) 
      {
            // TODO Auto-generated catch block
            e.printStackTrace();
      }
      
      String letter = (overrideSuccess) ? "Success" : "Override";    
      bus.mail(new Letter(letter), BusIfc.CURRENT);
  }
}
