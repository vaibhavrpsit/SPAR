/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/CheckManagerOverrideRequiredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  04/11/09 - Fix for able to return deniable items for RM-POS
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

import javax.security.auth.login.LoginException;
import javax.swing.ListModel;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.ReturnResponseLineItem;
import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 * Aisle checks if manager override is required for denied return items.
 *
 */
public class CheckManagerOverrideRequiredAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 7958025160212224035L;

    //--------------------------------------------------------------------------
    /**
       This aisle checks if require manager override for denied return items.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        boolean bManagerOverrideNotReqd = false;
        boolean override = false;
        String letter = "CallManagerOverrideStation";

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnAuthorizationCargoIfc cargo = (ReturnAuthorizationCargoIfc) bus.getCargo();

        LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.RETURN_RESPONSE_ITEM);
        int [] selectedRows = beanModel.getSelectedRows();

        for(int i =0 ; i < selectedRows.length ; i++)
        {
          if(beanModel.getListModel().get(selectedRows[i]) instanceof ReturnResponseLineItemIfc)
          {
            ReturnResponseLineItemIfc returnResponseItem = (ReturnResponseLineItem)beanModel.getListModel().get(selectedRows[i]);
            if(returnResponseItem.getApproveDenyCode().equalsIgnoreCase("Denial"))
            {
              bManagerOverrideNotReqd = true;
            }
          }
        }


        // get the security manager to be able to check the access to the code
        // function of the current user.

        if(!bManagerOverrideNotReqd)
        {
          SecurityManagerIfc securityManager =
            (SecurityManagerIfc) Gateway.getDispatcher().getManager(
                SecurityManagerIfc.TYPE);

          // Check the access of the user to the code function
          SaleReturnTransactionIfc transaction = (SaleReturnTransaction)cargo.getTransaction();

          try {
            override = securityManager.override(
                cargo.getAppID(),
                transaction.getCashier().getLoginID(),
                transaction.getCashier().getPasswordBytes(),
                RoleFunctionIfc.RETURN_MANAGER_OVERRIDE);
            cargo.setAccessFunctionID(RoleFunctionIfc.RETURN_MANAGER_OVERRIDE);
          } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

        if (override)
        {
            bus.mail(CommonLetterIfc.SUCCESS, BusIfc.CURRENT);
        }
        else if(bManagerOverrideNotReqd)
        {
          bus.mail(CommonLetterIfc.SUCCESS, BusIfc.CURRENT);
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}

