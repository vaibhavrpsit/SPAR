/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/LogBatchTotal.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:58:48   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Apr 28 2002 15:25:18   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the a batch total.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogBatchTotal
extends AbstractIXRetailTranslator
implements LogBatchTotalIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogBatchTotal object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogBatchTotal()
    {                                   // begin LogBatchTotal()
    }                                   // end LogBatchTotal()

    //---------------------------------------------------------------------
    /**
       Creates LogBatchTotal element. <P>
       @param batchTotal for which the element is to be created
       @param doc parent document
       @return Element batch total
    **/
    //---------------------------------------------------------------------
    public Element createBatchTotalElement(BatchTotalIfc batchTotal,
                                           Document doc)
    throws XMLConversionException
    {                                   // begin testCreateRetailBatchTotalElement()
        setParentDocument(doc);

        parentElement = parentDocument.createElement
          (LogBatchTotalIfc.ELEMENT_BATCH_360);

        createTextNodeElement
          (LogBatchTotalIfc.ELEMENT_BATCH_ID,
           batchTotal.getBatchID());

        if (batchTotal.getFirstTransactionTimestamp() != null)
        {
            createTimestampTextNodeElement
              (LogBatchTotalIfc.ELEMENT_FIRST_TRANSACTION_TIMESTAMP,
               batchTotal.getFirstTransactionTimestamp());
        }

        if (batchTotal.getLastTransactionTimestamp() != null)
        {
            createTimestampTextNodeElement
              (LogBatchTotalIfc.ELEMENT_LAST_TRANSACTION_TIMESTAMP,
               batchTotal.getLastTransactionTimestamp());
        }

        if (batchTotal.getBatchCompleteTimestamp() != null)
        {
            createTimestampTextNodeElement
              (LogBatchTotalIfc.ELEMENT_BATCH_COMPLETE_TIMESTAMP,
               batchTotal.getBatchCompleteTimestamp());
        }

        createTextNodeElement
          (LogBatchTotalIfc.ELEMENT_TRANSACTION_COUNT,
           batchTotal.getTransactionCount());

        createTextNodeElement
          (LogBatchTotalIfc.ELEMENT_TRANSACTION_TOTAL,
           batchTotal.getTransactionTotal());

        return(parentElement);
    }                                   // end testCreateRetailBatchTotalElement()


}
