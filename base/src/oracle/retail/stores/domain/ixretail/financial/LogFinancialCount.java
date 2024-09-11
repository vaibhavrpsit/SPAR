/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogFinancialCount.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    Rev 1.1   Jan 22 2003 09:57:26   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   May 02 2002 17:31:44   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// java imports
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

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
    {                                   // begin LogFinancialCount()
    }                                   // end LogFinancialCount()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified financial count object. <P>
       @param financialCount financial count reference
       @param doc parent document
       @param el parent element
       @param name element name
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(FinancialCountIfc financialCount,
                                 Document doc,
                                 Element el,
                                 String name)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element financialCountElement = parentDocument.createElement(name);

        createTenderItemElements(financialCount,
                                 financialCountElement);

        parentElement.appendChild(financialCountElement);

        return(financialCountElement);
    }                                   // end createElement()

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
    {                                   // begin createElement()
        return(createElement(financialCount,
                             doc,
                             el,
                             IXRetailConstantsIfc.ELEMENT_FINANCIAL_COUNT));
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
        Creates elements for count tender items. <P>
        @param financialCount financial count object
        @param financialCountElement financial count element
        @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createTenderItemElements(FinancialCountIfc financialCount,
                                            Element financialCountElement)
    throws XMLConversionException
    {                                   // begin createTenderItemElements()
        Iterator iter = financialCount.getTenderItemsIterator();

        LogFinancialCountTenderItemIfc logTenderItem = null;
        if (iter.hasNext())
        {
            logTenderItem =
              IXRetailGateway.getFactory().getLogFinancialCountTenderItemInstance();
        }
        while (iter.hasNext())
        {
            logTenderItem.createElement
              ((FinancialCountTenderItemIfc) iter.next(),
               parentDocument,
               financialCountElement,
               IXRetailConstantsIfc.ELEMENT_TENDER_ITEM);
        }

    }                                   // end createTenderItemElements()



}
