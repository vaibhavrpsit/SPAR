/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mpbarnet  04/03/09 - Log the reentry flag value.
 *    ranojha   11/12/08 - Fixed PosLog for PostVoided Transactions
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:41:36 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.6.2.1  2004/07/29 01:44:41  mwright
 *   Added re-entry mode flag to export element
 *
 *   Revision 1.6  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.5.2.3  2004/06/23 00:42:12  mwright
 *   Moved getStatus() here (from retail transaction) because control transactions also use it now
 *
 *   Revision 1.5.2.2  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.5.2.1  2004/05/31 04:34:23  mwright
 *   Removed static Document from schema types base class
 *
 *   Revision 1.5  2004/05/06 03:43:36  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.2.2.8  2004/05/05 02:36:16  mwright
 *   Set the static Document in the schema types base class once, before building any elements with the schema types
 *
 *   Revision 1.2.2.7  2004/04/26 22:12:41  mwright
 *   Added createFinancialCountElement() method 
 *   Removed (unused) static method that did the same thing
 *
 *   Revision 1.2.2.6  2004/04/19 07:39:45  mwright
 *   Changed FinancialCountElement to FinancialCountTenderItemElement
 *
 *   Revision 1.2.2.5  2004/04/13 07:32:16  mwright
 *   Removed tabs
 *
 *   Revision 1.2.2.4  2004/03/21 14:00:17  mwright
 *   Implemented schema type objects to store data prior to building XML
 *
 *   Revision 1.2.2.3  2004/03/18 02:19:37  mwright
 *   Implemented use of schema type factory
 *
 *   Revision 1.2.2.2  2004/03/17 04:13:50  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.1  2004/03/15 09:41:21  mwright
 *   Initial revision for POSLog v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import java.util.Iterator;

import java.math.BigDecimal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;


import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;


import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.transaction.LogTransactionIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslatorIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItemIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTransactionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorIDIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogQuantityIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountTenderItemElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View.
**/
//--------------------------------------------------------------------------
public class LogTransaction 
extends AbstractIXRetailTranslator
implements LogTransactionIfc, IXRetailConstantsV21Ifc, AbstractIXRetailTranslatorIfc
{
    protected TransactionIfc transaction;
    protected POSLogTransactionIfc trnElement;          // replaces transactionElement;    
    
    //----------------------------------------------------------------------------
    /**
        Constructs LogTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogTransaction()
    {                                   // begin LogTransaction()
    }                                   // end LogTransaction()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified transaction. <P>
       This class is the base for many flavours of transaction. They all use
       this method to create an element. Several methods defined in the base
       may be extended. The extended implementation must not extend this method,
       it should only extend the createBaseElements() and createExtendedElements()
       methods. This method creates the main schema type object (trnElement) as a class
       attribute that can be used by extended methods. (It replaces the v1.0
       transactionElement).
       @param transaction transaction for which Transaction is to be created
       @param doc Document containing transactions
       @return Element representing transaction
    **/
    //---------------------------------------------------------------------
    public Element createTransactionElement(TransactionIfc transaction, Document doc)  // this is the only public in v1.0
    throws XMLConversionException
    {
        this.transaction = transaction;
        
        trnElement = getSchemaTypesFactory().getPOSLogTransactionInstance();
        
        initializeTransactionElement();                 // creates ELEMENT_TRANSACTION (no LRT implementation)

        createBaseElements();                           // extend this to add flavour-specific objects to transaction
                                                        // Note: LRT ignores this and extends createElements() instead...
        createExtendedElements();
        
        // not used, should get rid of this?
        appendElements(transaction, parentElement);     // does nothing in this class
        
        return trnElement.createElement(doc, ELEMENT_TRANSACTION);
        
    }

    //---------------------------------------------------------------------
    /**
       Initializes transaction element.
    **/
    //---------------------------------------------------------------------
    protected void initializeTransactionElement()
    throws XMLConversionException
    {
        // SchemaTypes version:
        trnElement.setCancelFlag(new Boolean(transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED));
        trnElement.setTrainingModeFlag(new Boolean(transaction.isTrainingMode()));
        trnElement.setOfflineFlag(Boolean.FALSE);  // offline status is not used
        trnElement.setReentryFlag(new Boolean(transaction.isReentryMode()));
    }

    //---------------------------------------------------------------------
    /**
       Create standard transaction elements and append to document.
    **/
    //---------------------------------------------------------------------
    protected void createBaseElements()
    throws XMLConversionException
    {
        
        trnElement.setRetailStoreID(transaction.getFormattedStoreID());
        // optional: transactionElement.setRevenueCenterID(null);
        trnElement.setWorkstationID(transaction.getFormattedWorkstationID());
        
        String tillID = transaction.getTillID();
        if (tillID != null && tillID.length() > 0)
        {
            trnElement.setTillID(tillID);
        }
        
        trnElement.setSequenceNumber(transaction.getFormattedTransactionSequenceNumber());
        trnElement.setBusinessDayDate(dateValue(transaction.getBusinessDay()));
        trnElement.setBeginDateTime(dateValue(transaction.getTimestampBegin()));
        trnElement.setEndDateTime(dateValue(transaction.getTimestampEnd()));
        
        POSLogOperatorIDIfc operator = getSchemaTypesFactory().getPOSLogOperatorIDInstance();
        operator.setOperatorID(transaction.getCashier().getEmployeeID());
        trnElement.setOperatorID(new POSLogOperatorIDIfc[] { operator });
        
        // we can also set the following (not used in v1.0):
        // transaction.getCashier().getFullName(), 
        // transaction.getCashier().getRole().getDescription());
        
        
        // optional: transactionElement.setCurrencyCode(null);
        
    }

    //---------------------------------------------------------------------
    /**
       Create transaction elements for 360 extensions and append to document.
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createExtendedElements()
    throws XMLConversionException
    {
        // create elements for transaction status and type
        // TODO: in v2.1 these are in retail transaction as well, but with different values.... refer LogRetailTransaction.addAttributes() 
        //createTextNodeElement(ELEMENT_TRANSACTION_STATUS, TransactionIfc.IXRETAIL_STATUS_DESCRIPTORS[transaction.getTransactionStatus()]);
        //createTextNodeElement(ELEMENT_TRANSACTION_TYPE, DomainGateway.getFactory().getTransactionTypeMapInstance().getDescriptor(transaction.getTransactionType()));
        
        // create customer info element
        if (transaction.getCustomerInfo() != null)
        {
            // there is no customer element in v2.1 transaction...
            // TODO: 360 extension (maybe) to have customer in transaction here, despite fact that it is set in retail trx
            // createCustomerInfoElement(transaction.getCustomerInfo());
        }
    }

    String getStatus(TransactionIfc trans)
    {
       String status;
       switch (trans.getTransactionStatus())
       {
       case TransactionIfc.STATUS_UNKNOWN:
           status = ENUMERATION_TRANSACTION_STATUS_UNKNOWN;
           break;
           
       case TransactionIfc.STATUS_IN_PROGRESS:
           status = ENUMERATION_TRANSACTION_STATUS_IN_PROCESS;
           break;
           
       case TransactionIfc.STATUS_COMPLETED:
           status = ENUMERATION_TRANSACTION_STATUS_COMPLETE_360;
           break;
           
       case TransactionIfc.STATUS_CANCELED:
           status = ENUMERATION_TRANSACTION_STATUS_VOIDED;         // this means cancelled before completion...???
           break;
           
       case TransactionIfc.STATUS_SUSPENDED:
           status = ENUMERATION_TRANSACTION_STATUS_SUSPENDED;
           break;
           
       case TransactionIfc.STATUS_FAILED:
           status = ENUMERATION_TRANSACTION_STATUS_FAILED;
           break;
           
       case TransactionIfc.STATUS_SUSPENDED_RETRIEVED:
           status = ENUMERATION_TRANSACTION_STATUS_SUSPENDED_RETRIEVED;
           break;
           
       case TransactionIfc.STATUS_SUSPENDED_CANCELED:
           status = ENUMERATION_TRANSACTION_STATUS_SUSPENDED_DELETED;
           break;
           
       /**
        * When a transaction is post voided, the transaction status code of the original transaction would be updated to the post-voided status
        * (which is ???8???) in TR_TRN table. So, whenever the Original transaction is being logged into the POSLog in the same time interval as
        * that of its post void transaction, the transaction status of the Original Transaction would be logged as ???PostVoided".
        */
       case TransactionIfc.STATUS_VOIDED:
    	   status = ENUMERATION_TRANSACTION_STATUS_POSTVOIDED;
    	   break;
      
       default:
           status = ENUMERATION_TRANSACTION_STATUS_UNKNOWN;
       break;
       }
       return status;
    }  

    
    
    //---------------------------------------------------------------------
    /**
       Creates CustomerInfo360 element.
       @param customerInfo customer information object
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
/*  
 * This method may be required (see note in createExtendedElements() above) 
 *    
    protected void createCustomerInfoElement(CustomerInfoIfc customerInfo)
    throws XMLConversionException
    {
        Element customerInfoElement = parentDocument.createElement(ELEMENT_CUSTOMER_INFO);
        String infoType = ELEMENT_INFO_TYPE_VALUE_NONE;
        // derive info type
        switch(customerInfo.getCustomerInfoType())
        {
            case CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER:
              infoType = ELEMENT_INFO_TYPE_VALUE_PHONE_NUMBER;
              break;
            case CustomerInfoIfc.CUSTOMER_INFO_TYPE_POSTAL_CODE:
              infoType = ELEMENT_INFO_TYPE_VALUE_POSTAL_CODE;
              break;
            default:
              break;
        }
        // set type element
        createTextNodeElement(ELEMENT_INFO_TYPE, infoType, customerInfoElement);
        // set data element
        createTextNodeElement(ELEMENT_CUSTOMER_DATA, customerInfo.getCustomerInfo(), customerInfoElement);

        // If an id had been entered for a return, log it.
        if (customerInfo.getPersonalIDNumber() != null)
        {
            // set type element
            createTextNodeElement(ELEMENT_PERSONAL_ID_REQUIRED_TYPE, customerInfo.getPersonalIDType(), customerInfoElement);

            // set id element
            createTextNodeElement(ELEMENT_PERSONAL_ID_NUMBER, customerInfo.getPersonalIDNumber(), customerInfoElement);

            // set state element
            createTextNodeElement(ELEMENT_PERSONAL_ID_STATE, customerInfo.getPersonalIDState(), customerInfoElement);

            // set country element
            createTextNodeElement(ELEMENT_PERSONAL_ID_COUNTRY, customerInfo.getPersonalIDCountry(), customerInfoElement);
        }            

        parentElement.appendChild(customerInfoElement);
    }                                   // end createCustomerInfoElement()
*/

    
    /**
     * This method creates an array of  LogFinancialCountTenderItemIfc objects from a FinancialCountIfc object.
     * It replaces LogSafeCount, which did the same in v1.0 
     * @param financialCount The financial count object that is to be analyzed
     * @param financialCountElement the element to populate
     * @return true if items were added
     * @throws XMLConversionException
     */
    public boolean createFinancialCountElement(FinancialCountIfc financialCount, FinancialCountElement360Ifc financialCountElement)
    throws XMLConversionException
    {
       boolean something = false;
       
       Iterator iter = financialCount.getTenderItemsIterator();

       if (iter.hasNext())
       {
           LogFinancialCountTenderItemIfc logTenderItem = IXRetailGateway.getFactory().getLogFinancialCountTenderItemInstance();
       
           while (iter.hasNext())
           {
               FinancialCountTenderItemElement360Ifc el = getSchemaTypesFactory().getFinancialCountTenderItemElementInstance();
               logTenderItem.createElement((FinancialCountTenderItemIfc)iter.next(), null, el, "");
               financialCountElement.addTenderItem(el);
               something = true;
           }
       }
           
       return something;
    }
    
    
    
/**
 * This method is used by other log objects to add the unit of measure stuff to a quantity object.
 */   
 public static void setupQuantityElement(POSLogQuantityIfc quantityElement,
                                         BigDecimal quantity,
                                         UnitOfMeasureIfc uom)
 throws XMLConversionException
 {
    // v1.0 had quantity inside units...

    // handle unit of measure if it exists
    if (uom != null)
    {
        // "Each" is the default value in the schema, so it does not need to be set.
        if (!Util.isEmpty(uom.getUnitID()) &&
            !Util.isEmpty(uom.getUnitName()) &&
            !uom.getUnitID().equals(UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS))
        {
            String uomName = uom.getUnitName();
            quantityElement.setUnitOfMeasureCode(uomName);
        }
    }
    
    quantityElement.setQuantity(quantity);
 }

   
}
