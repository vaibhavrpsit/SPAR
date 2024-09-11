/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogNoSaleTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.5.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.5  2004/05/06 03:43:36  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.2.2.3  2004/04/13 07:32:16  mwright
 *   Removed tabs
 *
 *   Revision 1.2.2.2  2004/03/21 14:00:19  mwright
 *   Implemented schema type objects to store data prior to building XML
 *
 *   Revision 1.2.2.1  2004/03/17 00:20:41  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.1  2004/03/15 09:41:21  mwright
 *   Initial revision for POSLog v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;


import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogNoSaleTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 *
 */
//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a no-sale transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogNoSaleTransaction
extends LogControlTransaction
implements LogNoSaleTransactionIfc  // a fairly useless interface...
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();

        NoSaleTransactionIfc noSaleTransaction = (NoSaleTransactionIfc) transaction;
        controlTransactionElement.setReason(noSaleTransaction.getLocalizedReasonCode().getCode());

        EYSDate ts = transaction.getTimestampBegin();
        controlTransactionElement.setNoSale(dateValue(ts));        // this determines that the control transaction is a no sale transaction
    }

}
