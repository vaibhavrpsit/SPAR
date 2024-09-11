/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/IssueStoreCreditActionSite.java /rgbustores_13.4x_generic_branch/2 2011/07/12 15:58:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/12/11 - update generics
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ranojha   02/11/09 - Fixed NullPointerException for Issuing StoreCredit
 *    jswan     01/29/09 - Modified to correct issues with printing store
 *                         credit.
 *    sgu       01/14/09 - convert user entered currency amount into non locale
 *                         sensitive, decimal format before calling
 *                         CurrencyIfc.setStringValue
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         3/31/2008 1:58:51 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    7    360Commerce 1.6         8/2/2007 5:21:02 PM    Ranjan X Ojha   Added
 *          check for a refundable transaction ifc and if not then do not try
 *         to call the has receipt method.  For layaway this is not valid
 *         method.
 *    6    360Commerce 1.5         5/30/2007 9:01:57 AM   Anda D. Cadar   code
 *         cleanup
 *    5    360Commerce 1.4         5/18/2007 9:19:18 AM   Anda D. Cadar
 *         always use decimalValue toString
 *    4    360Commerce 1.3         4/25/2007 8:52:44 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse
 *
 *   Revision 1.6  2004/05/16 20:54:18  blj
 *   @scr 4476 rework,postvoid and cleanup
 *
 *   Revision 1.5  2004/05/11 16:08:47  blj
 *   @scr 4476 - more rework for store credit tender.
 *
 *   Revision 1.4  2004/04/13 16:30:07  bwf
 *   @scr 4263 Decomposition of store credit.
 *
 *   Revision 1.3  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.2  2004/03/04 23:28:49  nrao
 *   Code review changes for Issue Store Credit.
 *
 *   Revision 1.1  2004/02/17 17:56:49  nrao
 *   New site for Issue Store Credit
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

// java imports
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//----------------------------------------------------------------------------
/**
 *   This site validates different conditions and throws exceptions based on
 *   various conditions
 *   $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
//----------------------------------------------------------------------------
public class IssueStoreCreditActionSite extends PosSiteActionAdapter
{
    /**  */
    private static final long serialVersionUID = -7352626056576449111L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Static Strings
    public static final String SUCCESS_LETTER = "Success";
    public static final String FAILURE_LETTER = "RefundOptions";
    public static final String STORE_CREDIT_MINIMUM = "StoreCreditMinimum";
    public static final String OVERTENDER_IN_A_RETURN = "OvertenderInAReturn";
    public static final String YES_LETTER = "Yes";
    public static final String NO_LETTER = "No";

    //------------------------------------------------------------------------
    /*
     * @param bus  The bus arriving at this site.
     */
    //------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If we already have the store credit tender in cargo, we use that
        // other create new store credit tender
        TenderStoreCreditADO storeCreditTender = null;
        if (cargo.getTenderADO() == null)
        {
            // Get tender attributes
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            // add tender type
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);
            tenderAttributes.put(TenderConstants.STATE, TenderConstants.ISSUE);

            try
            {
                // create a new store credit tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                storeCreditTender = (TenderStoreCreditADO)factory.createTender(tenderAttributes);

                // update cargo with new tender.
                cargo.setTenderADO(storeCreditTender);
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                logger.error("Error creating Issue Store Credit Tender", e);
            }
        }
        else
        {
            storeCreditTender =  (TenderStoreCreditADO)cargo.getTenderADO();
        }

        // use transaction to validate refund limits
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            cargo.setTenderADO(storeCreditTender);
            if(txnADO instanceof ReturnableTransactionADOIfc )
            {
              boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
              boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
              txnADO.validateRefundLimits(storeCreditTender.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);
            }
            storeCreditTender.checkPrePrintedStoreCreditParameter();
        }
        catch (TenderException e)
        {
            // There was a problem parsing the tender attributes data.
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(storeCreditTender);

                displayErrorDialog(bus, STORE_CREDIT_MINIMUM, DialogScreensIfc.ERROR);
            }
            else if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(bus, OVERTENDER_IN_A_RETURN, DialogScreensIfc.ERROR);
            }

            else if (error == TenderErrorCodeEnum.CREATE_STORE_CREDIT)
            {
                // create new store credit number
                bus.mail(new Letter(NO_LETTER), BusIfc.CURRENT);
            }

            else if (error == TenderErrorCodeEnum.ENTER_STORE_CREDIT)
            {
                // enter store credit number
                bus.mail(new Letter(YES_LETTER), BusIfc.CURRENT);
            }
        }
    }

    //--------------------------------------------------------------------------------------
    /**
     * Show an error dialog
     * @param bus The bus arriving at the site.
     * @param name Name of the dialog message to be displayed
     * @param dialogType Type of error dialog
     */
    //--------------------------------------------------------------------------------------
    protected void displayErrorDialog(BusIfc bus, String name, int dialogType)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);

        if (dialogType == DialogScreensIfc.ERROR)
        {
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, FAILURE_LETTER);
        }
        dialogModel.setArgs(
            new String[] {DomainGateway.getFactory()
                                       .getTenderTypeMapInstance()
                                       .getDescriptor(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT)});
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
      /**
          Depart method retrieves input.
          @param  bus     Service Bus
      **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // Grab the entered or displayed amount
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Get the amount and put in the cargo
        String amount = null;
        Number value = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT));
        if (value!=null)
        {
        	amount = value.toString();
        }
        CurrencyIfc balance = DomainGateway.getBaseCurrencyInstance(amount);
        cargo.getTenderAttributes().put(TenderConstants.AMOUNT, balance.getDecimalValue().toString());
    }
}
