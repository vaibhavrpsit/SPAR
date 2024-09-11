/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogControlTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
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

import oracle.retail.stores.domain.ixretail.transaction.LogTransactionIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogControlTransactionIfc;


public abstract class LogControlTransaction
extends LogTransaction
implements LogTransactionIfc, IXRetailConstantsV21Ifc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    
    
    protected POSLogControlTransactionIfc controlTransactionElement = null;

    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();     // creates trnElement
        
        // create the control transaction schema type object, and add it to the transaction object:
        controlTransactionElement = getSchemaTypesFactory().getPOSLogControlTransactionInstance();
        trnElement.setControlTransaction(controlTransactionElement);
        
        controlTransactionElement.setVersion(ATTRIBUTE_CONTROL_TRANSACTION_VERSION_DATA);
        // shift, dayPart and ReasonCode may be set by extended flavours of control trx....
        
    }
    

}
