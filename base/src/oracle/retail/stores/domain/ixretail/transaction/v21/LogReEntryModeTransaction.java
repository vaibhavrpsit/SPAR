/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogReEntryModeTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *  1    360Commerce 1.0         3/31/2005 4:45:45 PM   Robert Pearse   
 *
 * Revision 1.1.2.1  2004/12/08 00:06:06  mwright
 * Initial revision
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;


import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.transaction.TransactionIfc;

import oracle.retail.stores.domain.ixretail.transaction.LogTransactionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogControlTransactionIfc;
/**
*
*/
//--------------------------------------------------------------------------
/**
  This class creates the TLog in IXRetail format for a enter/exit re-entry mode transaction.
  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogReEntryModeTransaction
extends LogControlTransaction
implements LogTransactionIfc
{
  /**
     revision number supplied by source-code-control system
  **/
  public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  
  protected void createBaseElements()
  throws XMLConversionException
  {
      super.createBaseElements();

      if (transaction.getTransactionType() == TransactionIfc.TYPE_ENTER_TRANSACTION_REENTRY)
      {
          controlTransactionElement.setCashierMode(POSLogControlTransactionIfc.CASHIER_MODE_ENTER_TRANSACTION_REENTRY);
      }
      else
      {
          controlTransactionElement.setCashierMode(POSLogControlTransactionIfc.CASHIER_MODE_EXIT_TRANSACTION_REENTRY);
      }
      
  }    

}
