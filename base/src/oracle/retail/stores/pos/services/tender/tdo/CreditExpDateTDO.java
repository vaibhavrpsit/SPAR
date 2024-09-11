/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/CreditExpDateTDO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
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
 *    4    360Commerce 1.3         5/21/2007 2:22:02 PM   Mathews Kochummen use
 *          credit card exp. date format
 *    3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 19 2003 14:10:58   epd
 * TDO refactoring to use factory
 * 
 *    Rev 1.0   Nov 04 2003 11:19:10   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 22 2003 19:21:18   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.beans.DateDocument;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 *  
 */
public class CreditExpDateTDO extends TDOAdapter
                              implements TDOUIIfc
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        POSBaseBeanModel model = new POSBaseBeanModel();
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        parModel.setResponseTypeDate();
        parModel.setResponseTypeDateFormat(DateDocument.CREDITCARD_MONTH_YEAR);
        model.setPromptAndResponseModel(parModel);
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
