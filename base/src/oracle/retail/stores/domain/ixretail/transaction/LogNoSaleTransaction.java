/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogNoSaleTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 17:13:48  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:36:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 22 2003 10:00:32   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Apr 30 2002 17:55:42   mpm
 * Added support for store-open.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 29 2002 17:53:56   mpm
 * Initial revision.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.transaction;
// java imports
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for a no-sale transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogNoSaleTransaction
extends LogTransaction
implements LogNoSaleTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        no sale transaction reference
    **/
    protected NoSaleTransactionIfc noSaleTransaction = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LogNoSaleTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogNoSaleTransaction()
    {                                   // begin LogNoSaleTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_NO_SALE_TRANSACTION_360;
        hasVersion = false;
    }                                   // end LogNoSaleTransaction()

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
        // set reference to no-sale transaction
        noSaleTransaction = (NoSaleTransactionIfc) transaction;

        super.createExtendedElements(transaction);

        // add reason code
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_REASON_CODE,
           noSaleTransaction.getLocalizedReasonCode().getCode());
    }                                   // end createExtendedElements()

}
