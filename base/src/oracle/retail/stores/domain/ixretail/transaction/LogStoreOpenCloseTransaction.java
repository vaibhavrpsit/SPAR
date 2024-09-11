/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogStoreOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:10 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:22   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:58   msg
 * Initial revision.
 * 
 *    Rev 1.4   May 09 2002 18:27:06   mpm
 * Completed re-factoring of store open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   May 08 2002 17:33:58   mpm
 * Added columns to layaway.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   May 06 2002 19:40:32   mpm
 * Completed work on open/close store, open/close register, open/close till and till adjustment transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   May 01 2002 18:11:22   mpm
 * Added partial support for financial totals in store open, close.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 30 2002 17:57:06   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a store open/close transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogStoreOpenCloseTransaction
extends LogTransaction
implements LogStoreOpenCloseTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogStoreOpenCloseTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogStoreOpenCloseTransaction()
    {                                   // begin LogStoreOpenCloseTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_STORE_OPEN_CLOSE_TRANSACTION_360;
        hasVersion = false;
    }                                   // end LogStoreOpenCloseTransaction()

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

        StoreOpenCloseTransactionIfc socTransaction =
          (StoreOpenCloseTransactionIfc) transaction;

        StoreStatusIfc storeStatus = socTransaction.getStoreStatus();
        int statusCode = storeStatus.getStatus();

        Element storeStatusElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_STORE_STATUS);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID,
           storeStatus.getStore().getStoreID(),
           storeStatusElement);

        if (statusCode == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_SIGN_ON_OPERATOR,
               storeStatus.getSignOnOperator().getEmployeeID(),
               storeStatusElement);
        }
        else
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_SIGN_OFF_OPERATOR,
               storeStatus.getSignOffOperator().getEmployeeID(),
               storeStatusElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
             [storeStatus.getStatus()],
           storeStatusElement);

        if (statusCode == AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_OPEN_TIME,
               storeStatus.getOpenTime(),
               storeStatusElement);

            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
               storeStatus.getBusinessDate(),
               storeStatusElement);

            createSafeCountElements
              (IXRetailConstantsIfc.ELEMENT_STARTING_SAFE_COUNT,
               socTransaction.getStartingSafeCount(),
               storeStatusElement);
        }
        else
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CLOSE_TIME,
               storeStatus.getCloseTime(),
               storeStatusElement);

            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
               storeStatus.getBusinessDate(),
               storeStatusElement);

            createFinancialTotalsElements(socTransaction.getEndOfDayTotals(),
                                          storeStatusElement);

            createSafeCountElements
              (IXRetailConstantsIfc.ELEMENT_ENDING_SAFE_COUNT,
               socTransaction.getEndingSafeCount(),
               storeStatusElement);
        }



        parentElement.appendChild(storeStatusElement);

    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for safe counts. <P>
       @param nodeName name of node
       @param safeCount safe count object
       @param el element to which store safe is to be added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createSafeCountElements(String nodeName,
                                           FinancialCountIfc safeCount,
                                           Element el)
    throws XMLConversionException
    {                                   // begin createSafeCountElements()
        if (safeCount != null)
        {
            LogFinancialCountIfc logSafeCount =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();

            logSafeCount.createElement(safeCount, parentDocument, el, nodeName);
        }
    }                                   // end createSafeCountElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for financial totals.
       @param totals financial totals
       @param el element to which financial total elements are to be added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createFinancialTotalsElements(FinancialTotalsIfc totals,
                                                 Element el)
    throws XMLConversionException
    {                                   // begin createFinancialTotalsElements()
        if (totals != null)
        {
            LogFinancialTotalsIfc logTotals =
              IXRetailGateway.getFactory().getLogFinancialTotalsInstance();

            logTotals.createElement(totals,
                                    parentDocument,
                                    el,
                                    IXRetailConstantsIfc.ELEMENT_END_OF_DAY_TOTALS);
        }
    }                                   // end createFinancialTotalsElements()


}
