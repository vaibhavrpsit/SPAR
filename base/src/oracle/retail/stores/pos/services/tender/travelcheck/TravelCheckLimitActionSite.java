/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/travelcheck/TravelCheckLimitActionSite.java /main/15 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
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
 *    3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/13 22:10:26  bwf
 *   @scr 4263 Decomposition of travel check.
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:17:58   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:56   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:52   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.travelcheck;

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
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *  Validate Travel Check tender limits  
 */
public class TravelCheckLimitActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8556291483789278767L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get tender attributes from cargo and add tender type
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.TRAVELERS_CHECK);
        
        // Use transaction to validate limits for tranveller
        try
        {
            cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum errorCode = e.getErrorCode();
         
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (errorCode == TenderErrorCodeEnum.INVALID_AMOUNT)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("InvalidTravelerCheckAmount");
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                return;
            }   
            else if (errorCode == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
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

    /**
     * Display the specified Error Dialog
     * 
     * @param bus BusIfc
     * @param String name of the Error Dialog to display
     * @param POSUIManagerIfc UI Manager to handle the IO
     */
    protected void displayErrorDialog(BusIfc bus, POSUIManagerIfc ui, String name)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        String tenderType = DomainGateway.getFactory()
                          .getTenderTypeMapInstance()
                          .getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
		String[] args = new String[1];
		args[0] = utility.retrieveText("tender", "tenderText", tenderType, "").toLowerCase(locale); 
		dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
}
