/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/PurchaseOrderTDO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:43 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:30 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:23:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Nov 19 2003 14:11:00   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.1   Nov 14 2003 15:58:04   bwf
 * Fixed balance due.
 * Resolution for 3472: System crashes when PO is selected to make payment on new layaway
 * Resolution for 3474: System crashes when PO is selected to tender a new special order
 * Resolution for 3478: System crashes when PO is selected to tender an existing Layaway
 *
 *    Rev 1.0   Nov 04 2003 11:19:12   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 24 2003 14:52:26   bwf
 * Initial revision.
 * Resolution for 3418: Purchase Order Tender Refactor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

// Java imports
import java.util.HashMap;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This tdo build the purchase order enter amount screen.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PurchaseOrderTDO extends TDOAdapter
                              implements TDOUIIfc
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** attributeMap constants **/
    public static final String BUS = "Bus";
    public static final String TRANSACTION = "Transaction";

    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        CurrencyIfc balance = txnRDO.getTenderTransactionTotals().getBalanceDue();
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        //use default locale for currency display
        responseModel.setResponseText(balance.toFormattedString());
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setPromptAndResponseModel(responseModel);

        return baseModel;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
