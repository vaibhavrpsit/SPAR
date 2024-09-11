/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogTillOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:26   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:18   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:13:00   msg
 * Initial revision.
 * 
 *    Rev 1.5   Jul 18 2002 10:30:26   pdd
 * removed drawer updates on till operations.
 * Resolution for Domain SCR-45: TLog facility
 * 
 *    Rev 1.4   Jul 16 2002 17:38:20   pdd
 * moved drawer from till to transaction
 * Resolution for Domain SCR-45: TLog facility
 * 
 *    Rev 1.3   Jul 12 2002 21:09:58   pdd
 * replaced drawer ID with drawer element
 * Resolution for Domain SCR-45: TLog facility
 * 
 *    Rev 1.2   Jul 10 2002 10:03:10   pdd
 * set open time on till in all cases.
 * Resolution for Domain SCR-45: TLog facility
 * 
 *    Rev 1.1   May 12 2002 20:17:22   mpm
 * Implemented re-factored till transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:41:08   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a till open/close transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTillOpenCloseTransaction
extends LogTransaction
implements LogTillOpenCloseTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        financial count logger
    **/
    protected LogFinancialCountIfc countLogger = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LogTillOpenCloseTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogTillOpenCloseTransaction()
    {                                   // begin LogTillOpenCloseTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_TILL_OPEN_CLOSE_TRANSACTION_360;
        hasVersion = false;
    }                                   // end LogTillOpenCloseTransaction()

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

        // create till element
        Element tillElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_TILL);

        // get till-open transaction
        TillOpenCloseTransactionIfc tocTransaction =
          (TillOpenCloseTransactionIfc) transaction;
        TillIfc till = tocTransaction.getTill();

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID,
           transaction.getTransactionIdentifier().getStoreID(),
           tillElement);

        if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_TILL)
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_SIGN_ON_OPERATOR,
               till.getSignOnOperator().getEmployeeID(),
               tillElement);
        }
        else if (transaction.getTransactionType() == TransactionIfc.TYPE_CLOSE_TILL)
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_SIGN_OFF_OPERATOR,
               till.getSignOffOperator().getEmployeeID(),
               tillElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
             [till.getStatus()],
           tillElement);

        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_OPEN_TIME,
           till.getOpenTime(),
           tillElement);

        if (transaction.getTransactionType() == TransactionIfc.TYPE_CLOSE_TILL)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CLOSE_TIME,
               till.getCloseTime(),
               tillElement);
        }

        createDateTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
           till.getBusinessDate(),
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TILL_ID,
           till.getTillID(),
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TILL_TYPE,
           AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()],
           tillElement);

        // only report financial totals if till is closed
        if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_TILL)
        {
            createReconcilableCountElements(till.getTotals().getStartingFloatCount(),
                                            tillElement,
                                            IXRetailConstantsIfc.ELEMENT_STARTING_FLOAT_COUNT);
        }
        else if (transaction.getTransactionType() == TransactionIfc.TYPE_CLOSE_TILL)
        {
            createFinancialTotalsElements(till.getTotals(),
                                          tillElement);
        }

        parentElement.appendChild(tillElement);
    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for a financial totals object. <p>
       @param totals totals object
       @param el element to which till safe is to be added
       @param nodeName name of totals node
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createFinancialTotalsElements(FinancialTotalsIfc totals,
                                                 Element el)
    throws XMLConversionException
    {                                   // begin createFinancialTotalsElements()
        LogFinancialTotalsIfc totalsLogger =
          IXRetailGateway.getFactory().getLogFinancialTotalsInstance();
        totalsLogger.createElement(totals,
                                   parentDocument,
                                   el);

    }                                   // end createFinancialTotalsElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for a financial count object. <p>
       @param count count object
       @param el element to which till safe is to be added
       @param nodeName name of count node
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createFinancialCountElements(FinancialCountIfc count,
                                                Element el,
                                                String nodeName)
    throws XMLConversionException
    {                                   // begin createFinancialCountElements()
        if (countLogger == null)
        {
            countLogger =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();
        }
        countLogger.createElement(count,
                                  parentDocument,
                                  el,
                                  nodeName);

    }                                   // end createFinancialCountElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for a reconcilable count object. <p>
       @param count count object
       @param el element to which till safe is to be added
       @param nodeName name of count node
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createReconcilableCountElements(ReconcilableCountIfc count,
                                                   Element el,
                                                   String nodeName)
    throws XMLConversionException
    {                                   // begin createReconcilableCountElements()
        Element countElement = parentDocument.createElement(nodeName);

        if (countLogger == null)
        {
            countLogger =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();
        }

        // only log count if it exists
        if (count.getEntered() != null)
        {
            countLogger.createElement(count.getEntered(),
                                      parentDocument,
                                      countElement,
                                      IXRetailConstantsIfc.ELEMENT_ENTERED);
        }
        if (count.getExpected() != null)
        {
            countLogger.createElement(count.getExpected(),
                                      parentDocument,
                                      countElement,
                                      IXRetailConstantsIfc.ELEMENT_EXPECTED);
        }

        el.appendChild(countElement);
    }                                   // end createReconcilableCountElements()

}
