/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogStoreSafe.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 10:00:51 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/17 16:18:57  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:42  mcs
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
 *    Rev 1.0   Aug 29 2003 15:36:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:16   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Apr 30 2002 17:57:42   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a StoreSafe. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogStoreSafe
extends AbstractIXRetailTranslator
implements LogStoreSafeIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        tender type map
    **/
    protected TenderTypeMapIfc tenderTypeMap =
      DomainGateway.getFactory().getTenderTypeMapInstance();

    //----------------------------------------------------------------------------
    /**
        Constructs LogStoreSafe object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogStoreSafe()
    {                                   // begin LogStoreSafe()
    }                                   // end LogStoreSafe()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified store safe object. <P>
       @param storeSafe store safe reference
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(StoreSafeIfc storeSafe,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element storeSafeElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_SAFE);

        TenderDescriptorIfc[] tenderTypes = storeSafe.getValidTenderDescList();
        for (int i = 0; i < tenderTypes.length; ++i)
        {
            createStoreSafeTenderElements(storeSafe,
                                          tenderTypes[i],
                                          storeSafeElement);
        }

        parentElement.appendChild(storeSafeElement);

        return(storeSafeElement);
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates element for the store safe tender. <P>
       @param storeSafe store safe reference
       @param tenderType tender type
       @param el parent element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createStoreSafeTenderElements(StoreSafeIfc storeSafe,
                                                 TenderDescriptorIfc tenderType,
                                                 Element el)
    throws XMLConversionException
    {                                   // begin createStoreSafeTenderElements()
        // Note:  the following mechanics are also present in JdbcUpdateStoreSafeTotals().

        // this flag indicates if any values exist for the specified tender.  if
        // they do not, the tender is not logged.
        boolean usedTender = false;
        // starting float at till open
        CurrencyIfc openFloatAmount = DomainGateway.getBaseCurrencyInstance();
        // ending float at till close
        CurrencyIfc closeFloatAmount = DomainGateway.getBaseCurrencyInstance();
        // closing funds at till close
        CurrencyIfc closeFundAmount = DomainGateway.getBaseCurrencyInstance();
        // open float at store open
        CurrencyIfc openOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        // close float at store close
        CurrencyIfc closeOperationalAmount = DomainGateway.getBaseCurrencyInstance();
        // till pickup amount
        CurrencyIfc pickupAmount = DomainGateway.getBaseCurrencyInstance();
        // till loan amount
        CurrencyIfc loanAmount     = DomainGateway.getBaseCurrencyInstance();
        // amount for deposit
        CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();

        // get counts from safe
        FinancialCountTenderItemIfc tenderItem =
            storeSafe.getDepositCounts().getTenderItem(tenderType, false);
        if (tenderItem != null)
        {
            usedTender = true;
            depositAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = storeSafe.getLoanCounts().getTenderItem(tenderType, false);
        if (tenderItem != null)
        {
            usedTender = true;
            loanAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = storeSafe.getPickupCounts().getSummaryTenderItemByDescriptor(tenderType);
        if (tenderItem != null)
        {
            usedTender = true;
            pickupAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = storeSafe.getOpenTillCounts().getTenderItem(tenderType,false);
        if (tenderItem != null)
        {
            usedTender = true;
            openFloatAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = storeSafe.getCloseTillCounts().getTenderItem(tenderType,false);
        if (tenderItem != null)
        {
            usedTender = true;
            closeFloatAmount = tenderItem.getAmountOut().abs();
            closeFundAmount = tenderItem.getAmountIn().abs();
        }

        tenderItem = storeSafe.getOpenOperatingFunds().getTenderItem(tenderType,false);
        if (tenderItem != null)
        {
            usedTender = true;
            openOperationalAmount = tenderItem.getAmountTotal().abs();
        }

        tenderItem = storeSafe.getCloseOperatingFunds().getTenderItem(tenderType,false);
        if (tenderItem != null)
        {
            usedTender = true;
            closeOperationalAmount = tenderItem.getAmountTotal().abs();
        }

        CurrencyIfc currentAmount = DomainGateway.getBaseCurrencyInstance();

        // Make the necessary adjustments before inserting the new data
        currentAmount = currentAmount.add(pickupAmount).add(closeFundAmount);
        currentAmount = currentAmount.subtract(loanAmount).subtract(depositAmount);

        if (usedTender)
        {

            Element storeSafeTenderElement = parentDocument.createElement
              (IXRetailConstantsIfc.ELEMENT_SAFE_TENDER);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID,
               storeSafe.getStoreID(),
               storeSafeTenderElement);

            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
               storeSafe.getBusinessDay(),
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TENDER_REPOSITORY_ID,
               storeSafe.getStoreSafeID(),
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TENDER_ID,
               tenderTypeMap.getDescriptor(tenderType.getTenderType()),
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CURRENCY_CODE,
               tenderType.getCountryCode(),
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CURRENT_AMOUNT,
               currentAmount,
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_OPEN_OPERATING_BALANCE,
               openOperationalAmount,
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CLOSE_OPERATING_BALANCE,
               closeOperationalAmount,
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TILL_OPEN_FLOAT_TOTAL,
               openFloatAmount,
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TILL_CLOSE_FLOAT_TOTAL,
               closeFloatAmount,
               storeSafeTenderElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_DEPOSIT_AMOUNT,
               depositAmount,
               storeSafeTenderElement);

            el.appendChild(storeSafeTenderElement);
        }

    }                                   // end createStoreSafeTenderElements()

}
