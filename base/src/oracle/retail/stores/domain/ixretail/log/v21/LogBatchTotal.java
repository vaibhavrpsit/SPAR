/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/v21/LogBatchTotal.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 * Revision 1.3.4.3  2005/01/21 22:41:15  jdeleau
 * @scr 7888 merge Branch poslogconf into v700
 *
 * Revision 1.3.4.2.2.1  2005/01/20 16:37:23  jdeleau
 * @scr 7888 Various POSLog fixes from mwright
 *
 * Revision 1.3.4.1  2004/12/09 05:03:05  mwright
 * Updated to IXRetail 2.1 compliance
 *
 * Revision 1.3  2004/06/24 09:15:10  mwright
 * POSLog v2.1 (second) merge with top of tree
 *
 * Revision 1.2.2.1  2004/06/10 10:52:00  mwright
 * Made to act like v1.0 version
 *
 * Revision 1.2  2004/05/06 03:15:15  mwright
 * Initial revision for POSLog v2.1 merge with top of tree
 *
 * Revision 1.1.2.1  2004/05/05 23:32:47  mwright
 * Initial revision for v2.1
 * This logger does nothing except return null when called
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log.v21;

//XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.log.LogBatchTotalIfc;
import oracle.retail.stores.domain.ixretail.log.BatchTotalIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;

import oracle.retail.stores.commerceservices.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogBatchIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogActivitySummaryIfc;


//--------------------------------------------------------------------------
/**
  This class now implements the IXRetail batch total element.
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
  {
  }

  //---------------------------------------------------------------------
  /**
     Creates LogBatchTotal element. <P>
     @param batchTotal for which the element is to be created
     @param doc parent document
     @return null
  **/
  //---------------------------------------------------------------------
  public Element createBatchTotalElement(BatchTotalIfc batchTotal, Document doc)
  throws XMLConversionException
  {
      POSLogBatchIfc            batch   = getSchemaTypesFactory().getPOSLogBatchInstance();
      POSLogActivitySummaryIfc  summary = getSchemaTypesFactory().getPOSLogActivitySummaryInstance();

      summary.setTransactionAmount(batchTotal.getTransactionTotal().toString());
      summary.setTransactionCount(Integer.toString(batchTotal.getTransactionCount()));
      
      batch.setBatchID(batchTotal.getBatchID());
      
      if (batchTotal.getFirstTransactionTimestamp() != null)
      {
          batch.setFirstTransactionTimestamp(batchTotal.getFirstTransactionTimestamp().dateValue());
      }
      
      if (batchTotal.getLastTransactionTimestamp() != null)
      {
          batch.setLastTransactionTimestamp(batchTotal.getLastTransactionTimestamp().dateValue());
      }
      
      if (batchTotal.getBatchCompleteTimestamp() != null)
      {
          batch.setBatchCompleteTimestamp(batchTotal.getBatchCompleteTimestamp().dateValue());
      }
      
      batch.setPOSLogActivitySummary(summary);
      
      return batch.createElement(doc, IXRetailConstantsV21Ifc.ELEMENT_BATCH);
  }


}
