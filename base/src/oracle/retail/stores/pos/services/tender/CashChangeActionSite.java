/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/CashChangeActionSite.java /main/18 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   06/12/09 - corrected javadoc
 *    ranojha   03/02/09 - Fixed the text for tenderType based on the UI Locale
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/31/2008 1:55:40 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse   
 *
 *   Revision 1.6.2.2  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.6.2.1  2004/10/15 18:50:27  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.7  2004/10/07 18:56:00  bwf
 *   @scr 7314, 7315 Cash is not change, but a refund in return and redeem.
 *
 *   Revision 1.6  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.5  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.4  2004/06/15 21:54:45  bwf
 *   @scr 5476 Fixed validateRefundLimits so that redeems dont crash when
 *                     using mbc or credit.  Use correct error message for cash.
 *
 *   Revision 1.3  2004/06/08 18:23:46  dfierling
 *   @scr 5358 - added catch for new exception for "CashRedeemAmountExceedsMaximum"
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 20 2003 16:57:24   epd
 * updated to use new ADO Factory Complex
 * 
 *    Rev 1.0   Nov 04 2003 11:17:36   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:44   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:42   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderCashADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Create a cash tender and attempt to add it to the transaction. If validation
 * fails, either punt, or attempt override, depending on the problem.
 */
public class CashChangeActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 4340745363476760442L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If we already have the cash tender in cargo, we have used it to
        // try and override the tender limits, attempt to add it to the txn
        // again.
        TenderCashADO cashTender = null;
        if (cargo.getTenderADO() == null)
        {
            // Get tender attributes
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            // add tender type
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CASH);

            try
            {
                // create a new cash tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                cashTender = (TenderCashADO)factory.createTender(tenderAttributes);
            }
            catch (ADOException adoe)
            {
                logger.error("Unable to create TenderCashADO for change", adoe);
            }
            catch (TenderException e)
            {
                TenderErrorCodeEnum error = e.getErrorCode();
                assert(error != TenderErrorCodeEnum.INVALID_AMOUNT) : "This should never happen, because UI enforces proper format";
            }
        }
        else
        {
            cashTender =  (TenderCashADO)cargo.getTenderADO();
        }

        // set to a refund cash tender because this class is only called during a refund, not during change
        cashTender.setRefundCash(true);

        // attempt to add Cash tender to transaction
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

            boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
            boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
            txnADO.validateRefundLimits(cashTender.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);
            txnADO.addTender(cashTender);
            // set for linedisplay
            cargo.setLineDisplayTender(cashTender);

            // journal the added tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(cashTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            // There was a problem parsing the tender attributes data.
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(cashTender);                

                displayErrorDialog(bus, "AmountExceedsMaximum", DialogScreensIfc.CONFIRMATION);
            }
            if (error == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(cashTender);  
                displayErrorDialog(bus, "CashRedeemAmountExceedsMaximum", DialogScreensIfc.CONFIRMATION);
            }
            if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(bus, "OvertenderInARedeem", DialogScreensIfc.ERROR);
            }
        }
    }

    /**
     * Show an error dialog
     * @param bus
     * @param name
     * @param dialogType
     */
    protected void displayErrorDialog(BusIfc bus, String name, int dialogType)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);

        if (dialogType == DialogScreensIfc.ERROR)
        {
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        }
        String tenderType =  DomainGateway.getFactory()
        						.getTenderTypeMapInstance()
        						.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
		String[] args = new String[1];
		args[0] = utility.retrieveText("tender", "tenderText", tenderType, "").toLowerCase(locale); 
		dialogModel.setArgs(args);        
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
}
