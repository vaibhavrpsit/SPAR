/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogInstantCreditTransaction.java /rgbustores_13.4x_generic_branch/3 2011/08/23 15:32:26 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/23/11 - check nullpointer for approval status
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     5    360Commerce 1.4         4/24/2008 11:25:52 PM  Manikandan Chellapan
 *           Updated copyright header
 *     4    360Commerce 1.3         4/22/2008 5:28:52 AM   Manikandan Chellapan
 *           CR#30328 Added code to set instant credit to control transaction
 *     3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:23:12 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:12:23 PM  Robert Pearse
 *    $
 *    Revision 1.2  2004/08/10 07:17:09  mwright
 *    Merge (3) with top of tree
 *
 *    Revision 1.1.2.1  2004/08/09 12:38:10  mwright
 *    Initial revision
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.ixretail.transaction.LogInstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransaction;
import oracle.retail.stores.domain.utility.InstantCredit;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.InstantCredit360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorID;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorIDIfc;

/**
 *
 */
//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for an instant credit enrollment transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/3 $
**/
//--------------------------------------------------------------------------
public class LogInstantCreditTransaction
extends LogControlTransaction
implements LogInstantCreditTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";


    protected void createBaseElements()
    throws XMLConversionException
    {
        super.createBaseElements();

        //Get intant credit instance
        InstantCredit360Ifc instantCredit = getSchemaTypesFactory().getInstantCredit360Instance();
        POSLogOperatorIDIfc salesAssociate = new POSLogOperatorID();

        //Get the instant credit transaction and instant credit object
        InstantCreditTransaction txn = (InstantCreditTransaction) transaction;
        InstantCredit credit = (InstantCredit) txn.getInstantCredit();

        //Add sales associate details
        salesAssociate.setEmployeeID(credit.getInstantCreditSalesAssociate().getEmployeeID());
        salesAssociate.setOperatorID(credit.getInstantCreditSalesAssociate().getAlternateID());

        //Populate instantCredit
        instantCredit.setSalesAssociate(salesAssociate);
        //Instant credit table does not store customer, set it to null
        instantCredit.setCustomer(null);
        if (credit.getApprovalStatus() != null)
        {
            instantCredit.setAuthorizationCode(Integer.toString(credit.getApprovalStatus().getCode()));
        }

        //Add instantCredit to control transaction
        controlTransactionElement.setInstantCredit360(instantCredit);
    }
}
