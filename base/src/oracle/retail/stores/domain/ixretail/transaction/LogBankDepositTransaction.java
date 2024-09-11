/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogBankDepositTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:34   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   May 23 2002 14:12:28   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a bank deposit transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogBankDepositTransaction
extends LogTransaction
implements LogBankDepositTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogBankDepositTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogBankDepositTransaction()
    {                                   // begin LogBankDepositTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_BANK_DEPOSIT_TRANSACTION_360;
        hasVersion = false;
    }                                   // end LogBankDepositTransaction()

    //---------------------------------------------------------------------
    /**
       Create transaction elements for 360 extensions and append to document.
       @param transaction TransactionIfc object
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createExtendedElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createExtendedElements()
        super.createExtendedElements(transaction);

        BankDepositTransactionIfc depositTransaction =
          (BankDepositTransactionIfc) transaction;

        createDepositCountElements
          (IXRetailConstantsIfc.ELEMENT_DEPOSIT_COUNT,
           depositTransaction.getDepositCount());

    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for deposit counts. <P>
       @param nodeName name of node
       @param depositCount deposit count object
       @param el element to which store deposit is to be added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createDepositCountElements(String nodeName,
                                              FinancialCountIfc depositCount)
    throws XMLConversionException
    {                                   // begin createDepositCountElements()
        if (depositCount != null)
        {
            LogFinancialCountIfc logDepositCount =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();

            logDepositCount.createElement(depositCount,
                                          parentDocument,
                                          parentElement,
                                          nodeName);
        }
    }                                   // end createDepositCountElements()

}
