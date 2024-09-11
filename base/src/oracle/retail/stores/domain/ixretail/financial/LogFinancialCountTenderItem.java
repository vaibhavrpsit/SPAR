/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogFinancialCountTenderItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:28   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:26   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:42   msg
 * Initial revision.
 * 
 *    Rev 1.2   May 30 2002 17:17:22   mpm
 * Added support for transaction type map.
 * Resolution for Domain SCR-76: Add transaction type map
 *
 *    Rev 1.1   May 02 2002 17:29:08   mpm
 * Completed financial totals.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 01 2002 18:13:12   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a FinancialCountTenderItem. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogFinancialCountTenderItem
extends AbstractIXRetailTranslator
implements LogFinancialCountTenderItemIfc
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
        Constructs LogFinancialCountTenderItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogFinancialCountTenderItem()
    {                                   // begin LogFinancialCountTenderItem()
    }                                   // end LogFinancialCountTenderItem()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial count tender line item object. <P>
       @param financialCountTenderItem financial count tender line item reference
       @param doc parent document
       @param el parent element
       @param name element name
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialCountTenderItemIfc financialCountTenderItem,
                                 Document doc,
                                 Element el,
                                 String name)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element financialCountTenderItemElement =
          parentDocument.createElement(name);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_ITEMS_IN,
           financialCountTenderItem.getNumberItemsIn(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NUMBER_ITEMS_OUT,
           financialCountTenderItem.getNumberItemsOut(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_IN,
           financialCountTenderItem.getAmountIn(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_OUT,
           financialCountTenderItem.getAmountOut(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT_TOTAL,
           financialCountTenderItem.getAmountTotal(),
           financialCountTenderItemElement);

        createTenderDescriptorElements(financialCountTenderItem.getTenderDescriptor(),
                                       financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DESCRIPTION,
           financialCountTenderItem.getDescription(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SUMMARY_DESCRIPTION,
           financialCountTenderItem.getSummaryDescription(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SUMMARY_FLAG,
           financialCountTenderItem.isSummary(),
           financialCountTenderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_HAS_DENOMINATIONS_FLAG,
           financialCountTenderItem.getHasDenominations(),
           financialCountTenderItemElement);

        parentElement.appendChild(financialCountTenderItemElement);

        return(financialCountTenderItemElement);
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates elements for tender descriptor. <P>
       @param el parent element
       @param tenderDescriptor tender descriptor
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createTenderDescriptorElements
      (TenderDescriptorIfc tenderDescriptor,
       Element el)
    throws XMLConversionException
    {                                   // begin createTenderDescriptorElements()
        Element descriptorElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_TENDER_DESCRIPTOR);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TENDER_ID,
           tenderTypeMap.getIXRetailDescriptor(tenderDescriptor.getTenderType()),
           descriptorElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TENDER_SUB_CODE,
           tenderDescriptor.getTenderSubType(),
           descriptorElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNTRY_CODE,
           tenderDescriptor.getCountryCode(),
           descriptorElement);

        el.appendChild(descriptorElement);

        return(descriptorElement);
    }                                   // end createTenderDescriptorElements()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial count tender line item object. <P>
       @param financialCountTenderItem financial count tender line item reference
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialCountTenderItemIfc financialCountTenderItem,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        return(createElement(financialCountTenderItem,
                             doc,
                             el,
                             IXRetailConstantsIfc.ELEMENT_FINANCIAL_COUNT_TENDER_ITEM));
    }                                   // end createElement()

}
