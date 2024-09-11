/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/moneyorder/MoneyOrderLimitActionSite.java /main/14 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ranojha   03/02/09 - Incorporated code review comments
 *    ranojha   03/02/09 - Fixed the text for tenderType based on the UI Locale
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:42 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/02 21:14:46  epd
 *   @scr 4263 Moved Money Order tender into new station
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 08 2003 09:16:54   blj
 * code review findings.
 * 
 *    Rev 1.1   Nov 07 2003 14:54:38   blj
 * cleaned up code and added javadoc
 * 
 *    Rev 1.0   Nov 04 2003 11:17:46   epd
 * Initial revision.
 * 
 *    Rev 1.2   Oct 26 2003 10:30:44   blj
 * removed debugging and fixed invalid errors.
 * 
 *    Rev 1.1   Oct 25 2003 16:09:06   blj
 * updated.
 * 
 *    Rev 1.0   Oct 24 2003 14:57:14   blj
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:52   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.moneyorder;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
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
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *  Validate Money Order tender limits  
 */
public class MoneyOrderLimitActionSite extends PosSiteActionAdapter
{
    /* This site validates the amount against money order tender limits.
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.MONEY_ORDER);
        
        // Use transaction to validate limits
        try
        {
            cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum errorCode = e.getErrorCode();
         
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                displayErrorDialog(bus, ui, "AmountExceedsMaximum");
                return;
            }
            else if (errorCode == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                displayErrorDialog(bus, ui, "AmountLessThanMinimum");
                return;
            }
            else if (errorCode == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
            {
                displayErrorDialog(bus, ui, "CashBackExceedsLimit");
                return;
            }
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
    
    //--------------------------------------------------------------------------
    /**
       Display the specified Error Dialog

       @param String name of the Error Dialog to display
       @param POSUIManagerIfc UI Manager to handle the IO
       @deprecated As of release v13.1 Use {@link #displayErrorDialog(BusIfc, POSUIManagerIfc, String)}
    **/
    //--------------------------------------------------------------------------
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        int screenType = DialogScreensIfc.CONFIRMATION;
        String args[] = new String[] {DomainGateway.getFactory()
                          .getTenderTypeMapInstance()
                          .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER)};
		        
        UIUtilities.setDialogModel(ui, screenType, name, args);
    }
    
    
    //--------------------------------------------------------------------------
    /**
       Display the specified Error Dialog
	   @param bus BusIfc
       @param String name of the Error Dialog to display
       @param POSUIManagerIfc UI Manager to handle the IO
    **/
    //--------------------------------------------------------------------------
    protected void displayErrorDialog(BusIfc bus, POSUIManagerIfc ui, String name)
    {
        int screenType = DialogScreensIfc.CONFIRMATION;
        String tenderType = DomainGateway.getFactory()
                          .getTenderTypeMapInstance()
                          .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
		String[] args = new String[1];
		args[0] = utility.retrieveText("tender", "tenderText", tenderType, "").toLowerCase(locale); 
		        
        UIUtilities.setDialogModel(ui, screenType, name, args);
    }
    
}
