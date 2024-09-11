/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/tdo/VoidReasonCodeTDO.java /main/16 2011/12/05 12:16:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    10/27/08 - use localized price override reason codes
 *    acadar    10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         3/29/2007 5:33:39 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/8/2007 10:02:34 PM   Brett J. Larsen
           CR 4530
           - save the default reason code value so the bean can later use it
           (instead of always using no-value)
      3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:47 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:48:16  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:28:20  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Jan 21 2004 14:53:22   epd
 * removed code no longer needed
 *
 *    Rev 1.1   Nov 19 2003 14:10:42   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:16:36   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:40:54   epd
 * Initial revision.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.postvoid.tdo;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.VoidConfirmBeanModel;

/**
 *
 *  TDO for Void Reason codes
 */
public class VoidReasonCodeTDO extends TDOAdapter
                               implements TDOUIIfc
{
    // HashMap attributes keys for this TDO
    public static final String TRANSACTION = "TRANSACTION";
    public static final String BUS           = "BUS";



    /**
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {

        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        // set stuff on the model
        VoidConfirmBeanModel model = new VoidConfirmBeanModel();
        model.setTransactionNumber(txnRDO.getTransactionID());
        model.setAmountString
          (txnRDO.getTenderTransactionTotals().getGrandTotal().toFormattedString());


        CodeListIfc list = (CodeListIfc)attributeMap.get(CodeConstantsIfc.CODE_LIST_POST_VOID_REASON_CODES);

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        model.inject(list, null, LocaleMap.getBestMatch(lcl));
        model.setTransactionType(TransactionIfc.TYPE_DESCRIPTORS[txnRDO.getTransactionType()]);

        return model;
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
