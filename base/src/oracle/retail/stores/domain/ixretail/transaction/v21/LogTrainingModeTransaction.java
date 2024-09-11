/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogTrainingModeTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.1.2.2  2004/08/01 23:45:34  mwright
 *   Removed TO-DO tags on completed tasks
 *
 *   Revision 1.1.2.1  2004/07/29 01:21:10  mwright
 *   Initial revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;


import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.transaction.TransactionIfc;

import oracle.retail.stores.domain.ixretail.transaction.LogTrainingModeTransactionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogControlTransactionIfc;
/**
 *
 */
//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a enter/exit training mode transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTrainingModeTransaction
extends LogControlTransaction
implements LogTrainingModeTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    
    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();

        if (transaction.getTransactionType() == TransactionIfc.TYPE_ENTER_TRAINING_MODE)
        {
            controlTransactionElement.setCashierMode(POSLogControlTransactionIfc.CASHIER_MODE_ENTER_TRAINING);
        }
        else
        {
            controlTransactionElement.setCashierMode(POSLogControlTransactionIfc.CASHIER_MODE_EXIT_TRAINING);
        }
        
    }    

}
