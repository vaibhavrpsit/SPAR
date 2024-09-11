/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/CheckRPIEnabledSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   12/03/08 - POS to RM Integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Site checks whether Oracle Retail Returns Management server should be used
 * for returns.
 */
public class CheckRPIEnabledSite extends PosSiteActionAdapter 
{
    /**
     * 
     */
    private static final long serialVersionUID = -3927344885933580628L;

    //--------------------------------------------------------------------------
    /**
       This site checks if use oracle retail return manager server.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    { 
        String letter = "RPIDisabled";
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
            Boolean manageReturns  = pm.getBooleanValue("UseOracleRetailReturnManagement");
              if (manageReturns.booleanValue() == true)
              {
                  ReturnAuthorizationCargo cargo = (ReturnAuthorizationCargo)bus.getCargo();
                  boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
                  if ( !trainingModeOn )
                  {
                      if ( cargo.getReturnRequest()!=null && cargo.getReturnResponse()==null )
                      {
                          letter = "RPIDisabled";
                      }
                      else
                      {
                          letter = "RPIEnabled";
                      }
                  }
              }
        }
        catch (ParameterException e)
        {
            System.out.println(e.getMessage());
        }               
        bus.mail( new Letter(letter), BusIfc.CURRENT);
    }
}
