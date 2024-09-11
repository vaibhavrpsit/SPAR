/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogFinancialCountTenderItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    6    360Commerce 1.5         4/17/2008 6:06:58 PM   Deepti Sharma   Added
 *          Euro and UK to tender type descriptor for POSLOG
 *    5    360Commerce 1.4         4/17/2008 2:36:47 PM   Deepti Sharma
 *         CR-31078:Changes to display correct values in POSLOG when Till
 *         pickup is done for Canadian Currency.
 *    4    360Commerce 1.3         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.5.4.3  2004/12/15 21:52:04  cdb
 *   @scr 7856 Backing out compliance changes from Branch build ONLY to support well tested POSLog format.
 *
 *   Revision 1.5.4.2  2004/12/09 23:22:55  mwright
 *   Updated to use tender type map from commerce services
 *
 *   Revision 1.5.4.1  2004/12/07 07:01:42  mwright
 *   Protected tender type lookup with try-catch so we can proceed even if the tender table is not up to date.
 *   NOTE: This is a temporary measure - a fix is still required (see central office SCR 2220)
 *
 *   Revision 1.5  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.4.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.4  2004/05/06 03:33:07  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.7  2004/05/05 02:27:37  mwright
 *   Updated tender type descriptors to use IXRetail predefined ones where possible
 *
 *   Revision 1.1.2.6  2004/04/28 11:25:12  mwright
 *   Added own tender type map to replace the one from the domain object factory.
 *   The ultimate solution is to extend the doman object factory to select between v1.0 and v2.1 descriptor maps.
 *
 *   Revision 1.1.2.5  2004/04/27 22:05:19  mwright
 *   Changed TenderSubType to SubTenderType in use of financial count element
 *
 *   Revision 1.1.2.4  2004/04/26 07:18:06  mwright
 *   Changed from FinancialCountTenderItemElement to FinancialCountTenderItemElement360, with number and count in/out replaced by POSLogTotals objects for incoming and outgoing.
 *
 *   Revision 1.1.2.3  2004/04/19 07:07:34  mwright
 *   Changed FinancialCountElement to FinancialCountTenderItemElement
 *
 *   Revision 1.1.2.2  2004/04/13 07:25:52  mwright
 *   Removed tabs
 *
 *   Revision 1.1.2.1  2004/03/21 14:31:25  mwright
 *   Initial revision for POSLog v2.1
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
//import oracle.retail.stores.domain.DomainGateway;
//import oracle.retail.stores.domain.tender.TenderTypeMapIfc;

import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;

import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItemIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountTenderItemElement360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTotalsIfc;

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
    /**
     * This tender type map replaces the one obtained from the domain object factory...we should look at extending the factory for v2.1,
     * in the same way as the IXRetail factory was extended.
     * All the strings below comply with the 360 schema for POSLogTenderType
     */
    String[] tenderTypeDescriptor = new String[]
    {
        "Cash",
        "c360:Credit",
        "Check",
        "TravelersCheck",
        "c360:GiftCertificate",
        "c360:SendCheck",
        "c360:Debit",
        "ManufacturersCoupon",
        "c360:GiftCard",
        "c360:StoreCredit",
        "c360:MallCertificate",
        "PurchaseOrder",
        "c360:MoneyOrder",
        "c360:ECheck",
        "Canadian Cash",
        "Canadian Check",
        "U.K. Cash",
        "U.K. Check",
        "Euro Cash",
        "Euro Check"
    };
    
    
    
    //----------------------------------------------------------------------------
    /**
        Constructs LogFinancialCountTenderItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogFinancialCountTenderItem()
    {
    }

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
    {
        // This will throw an exception if we are called with the wrong flavour of parameter:
        FinancialCountTenderItemElement360Ifc financialCountElement = (FinancialCountTenderItemElement360Ifc)el;

        POSLogTotalsIfc incoming = getSchemaTypesFactory().getPOSLogTotalsInstance();
        incoming.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(financialCountTenderItem.getAmountIn())));
        incoming.setCount(Integer.toString(financialCountTenderItem.getNumberItemsIn()));
        POSLogTotalsIfc outgoing = getSchemaTypesFactory().getPOSLogTotalsInstance();
        outgoing.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(financialCountTenderItem.getAmountOut())));
        outgoing.setCount(Integer.toString(financialCountTenderItem.getNumberItemsOut()));
        
        financialCountElement.setIncoming(incoming);
        financialCountElement.setOutgoing(outgoing);
        financialCountElement.setAmountTotal(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(financialCountTenderItem.getAmountTotal())));

        createTenderDescriptorElements(financialCountTenderItem.getTenderDescriptor(), financialCountElement);
        
        financialCountElement.setDescription(financialCountTenderItem.getDescription());
        financialCountElement.setSummaryDescription(financialCountTenderItem.getSummaryDescription());
        financialCountElement.setSummaryFlag(new Boolean(financialCountTenderItem.isSummary()));
        financialCountElement.setHasDenominationFlag(new Boolean(financialCountTenderItem.getHasDenominations()));

        return financialCountElement;
    }

    //---------------------------------------------------------------------
    /**
       Creates elements for tender descriptor. <P>
       @param el parent element
       @param tenderDescriptor tender descriptor
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createTenderDescriptorElements(TenderDescriptorIfc tenderDescriptor, Element el)
    throws XMLConversionException
    {
        
        // This will throw an exception if we are called with the wrong flavour of parameter:
        FinancialCountTenderItemElement360Ifc financialCountElement = (FinancialCountTenderItemElement360Ifc)el;
        int typeIndex = tenderDescriptor.getTenderType();
        try
        {
            financialCountElement.setTenderType(tenderTypeDescriptor[typeIndex]);
        }
        catch (IndexOutOfBoundsException e)
        {
            financialCountElement.setTenderType("Invalid:" + Integer.toString(typeIndex));
        }
        
        financialCountElement.setSubTenderType(tenderDescriptor.getTenderSubType());
        financialCountElement.setCountryCode(tenderDescriptor.getCountryCode());
        financialCountElement.setCurrencyID(tenderDescriptor.getCurrencyID()); //I18N

        return financialCountElement;
    }

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
    {
        return createElement(financialCountTenderItem, null, el, null);
    }

}
