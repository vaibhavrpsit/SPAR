/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogFinancialCount.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:11:12  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/04/27 22:06:00  mwright
 *   Factory done
 *
 *   Revision 1.1.2.1  2004/04/19 07:06:02  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;

import  oracle.retail.stores.domain.ixretail.financial.LogFinancialCountIfc;
import  oracle.retail.stores.domain.ixretail.financial.LogFinancialCountTenderItemIfc;
import  oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountTenderItemElement360Ifc;
import  oracle.retail.stores.commerceservices.ixretail.schematypes.v21.FinancialCountElement360Ifc;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a FinancialCount. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogFinancialCount
extends AbstractIXRetailTranslator
implements LogFinancialCountIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogFinancialCount object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogFinancialCount()
    {
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial count object. <P>
       @param financialCount financial count reference
       @param doc parent document  (unused)
       @param el Financial count schema type element to populate
       @param name element name (unused)
       @return Updated financial count schema type element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialCountIfc financialCount,
                                 Document doc,
                                 Element el,
                                 String name)
    throws XMLConversionException
    {
        FinancialCountElement360Ifc financialCountElement = (FinancialCountElement360Ifc)el;

        createTenderItemElements(financialCount, financialCountElement);

        return financialCountElement;
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial count object. <P>
       @param financialCount financial count reference
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialCountIfc financialCount,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {
        return createElement(financialCount, null,  el, null);
    }

    //---------------------------------------------------------------------
    /**
        Creates elements for count tender items. <P>
        @param financialCount financial count object
        @param financialCountElement financial count element
        @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createTenderItemElements(FinancialCountIfc financialCount, FinancialCountElement360Ifc financialCountElement)
    throws XMLConversionException
    {
        Iterator iter = financialCount.getTenderItemsIterator();

        if (iter.hasNext())
        {
            LogFinancialCountTenderItemIfc logTenderItem = IXRetailGateway.getFactory().getLogFinancialCountTenderItemInstance();
            while (iter.hasNext())
            {
                FinancialCountTenderItemElement360Ifc tenderItem = getSchemaTypesFactory().getFinancialCountTenderItemElementInstance();
                logTenderItem.createElement((FinancialCountTenderItemIfc) iter.next(), null, tenderItem, null);
                financialCountElement.addTenderItem(tenderItem);
            }
        }
    }



}
