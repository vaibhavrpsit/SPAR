/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogTillAdjustmentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    ohorne 10/29/08 - Localization of Till related Reason Codes
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
 *    Rev 1.1   Jan 22 2003 10:00:20   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   May 06 2002 19:41:08   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a till adjustment transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTillAdjustmentTransaction
extends LogTransaction
implements LogTillAdjustmentTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogTillAdjustmentTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogTillAdjustmentTransaction()
    {                                   // begin LogTillAdjustmentTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_POS_360_TILL_ADJUSTMENT_TRANSACTION;
        hasVersion = false;
    }                                   // end LogTillAdjustmentTransaction()

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

        // set reference to TillAdjustmentTransactionIfc
        TillAdjustmentTransactionIfc tillAdjustmentTransaction =
          (TillAdjustmentTransactionIfc) transaction;

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TENDER_ID,
           tillAdjustmentTransaction.getTenderType());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ADJUSTMENT_AMOUNT,
           tillAdjustmentTransaction.getAdjustmentAmount());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ADJUSTMENT_COUNT,
           tillAdjustmentTransaction.getAdjustmentCount());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_EXPECTED_AMOUNT,
           tillAdjustmentTransaction.getExpectedAmount());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_EXPECTED_COUNT,
           tillAdjustmentTransaction.getExpectedCount());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_REASON_CODE,
           tillAdjustmentTransaction.getReason().getCode());

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNT_TYPE,
           FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[tillAdjustmentTransaction.getCountType()]);

        createFinancialCountElements(tillAdjustmentTransaction.getTenderCount(),
                                     parentDocument,
                                     parentElement,
                                     IXRetailConstantsIfc.ELEMENT_TENDER_COUNT);

    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates element for financial count. <P>
       @param financialCount financial count object
       @param doc parent document
       @param el parent element
       @param name name for count element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createFinancialCountElements
      (FinancialCountIfc financialCount,
       Document doc,
       Element el,
       String name)
    throws XMLConversionException
    {                                   // begin createFinancialCountElements()
        if (financialCount != null)
        {
            LogFinancialCountIfc logCount =
              IXRetailGateway.getFactory().getLogFinancialCountInstance();

            logCount.createElement(financialCount,
                                   doc,
                                   el,
                                   IXRetailConstantsIfc.ELEMENT_TENDER_COUNT);
        }
    }                                   // end createFinancialCountElements()

}
