/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/StoreCreditLimitActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/16/2008 5:35:09 AM   Neeraj Gautam
 *         Updated arrive(Bus busIfc) method to display the Error Message -
 *            CR 31526
 *    3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:26 PM  Robert Pearse   
 *   $ 
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;


public class StoreCreditLimitActionSite extends PosSiteActionAdapter 
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);
    
        // Use transaction to validate limits
        try
        {
            cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum errorCode = e.getErrorCode();
            String name = null;
            
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                name = "StoreCreditMinimum";
            }
            else if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                name = "OvertenderNotAllowed";            	
            }
            
            assert(name != null);
            displayErrorDialog(ui, name);
            return;
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
    
    //--------------------------------------------------------------------------
    /**
       Display the specified Error Dialog

       @param String name of the Error Dialog to display
       @param POSUIManagerIfc UI Manager to handle the IO
    **/
    //--------------------------------------------------------------------------
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        String[] args = new String[]{DomainGateway.getFactory().getTenderTypeMapInstance()
                               .getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)};
                               
        int type = DialogScreensIfc.ERROR;
        UIUtilities.setDialogModel(ui, type, name, args);
    }
}
