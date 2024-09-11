/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogRegisterOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.3.2.1  2004/07/29 01:22:07  mwright
 *   No functional change, marked problem with TO-DO tag
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.2  2004/06/23 00:31:13  mwright
 *   Attempt to get the register open time into the XML.
 *   getRegister.getOpentime() returns null, so we have to use the (wrong for close trx) transaction start time instead.
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.1  2004/04/26 22:09:46  mwright
 *   Initial revision for v2.1 - Renamed from LogRegisterOpenTransaction
 *   Extended to perform close operations as well
 *   Now uses 360-specific POSSOD360 element instead of non-extensible ixretail element
 *
 *   Revision 1.2.2.4  2004/04/19 07:35:16  mwright
 *   Implemented LogRegister
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
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;

import oracle.retail.stores.domain.ixretail.financial.LogRegisterIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSSOD360Ifc;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a register open transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogRegisterOpenCloseTransaction
extends LogControlTransaction
implements LogRegisterOpenCloseTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    

    protected void createBaseElements()
    throws XMLConversionException
    {
        LogRegisterIfc logRegister = IXRetailGateway.getFactory().getLogRegisterInstance();
        RegisterOpenCloseTransactionIfc rocTransaction = (RegisterOpenCloseTransactionIfc) transaction;

        
        super.createBaseElements();
        POSSOD360Ifc possod = getSchemaTypesFactory().getPOSSOD360Instance();
        
        
        
        if (rocTransaction.getRegister().getStatus() == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            controlTransactionElement.setPOSSOD360(possod);       // this makes the control transaction a 360-specific register open transaction
            possod.setStartDateTimeStamp(dateValue(transaction.getTimestampBegin()));
        }
        else
        {
            // TODO This seems to always be null:
            EYSDate openTime = rocTransaction.getRegister().getOpenTime();
            // the field is required, so we make up value using the transaction start time
            if (openTime == null)
            {
                openTime = transaction.getTimestampBegin();
            }
                    
            possod.setStartDateTimeStamp(dateValue(openTime));
            
            
            // the end timestamp is mandatory, so we just put the start time in there as well - it will be ignored
            possod.setEndDateTimeStamp(dateValue(transaction.getTimestampBegin()));
            controlTransactionElement.setPOSEOD360(possod);       // this makes the control transaction a 360-specific register close transaction
        }
        

        
        logRegister.createElement(rocTransaction.getRegister(),
                                  null,
                                  possod,     // this is the old register element in an ixretail guise
                                  null);
    
    }
    
}
