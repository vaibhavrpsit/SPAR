/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogTill.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
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
 *    Rev 1.2   Jul 01 2003 14:09:28   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:14   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 07 2002 18:05:22   mpm
 * Completed till suspend, resume.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   May 06 2002 19:39:46   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a till
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogTill
extends AbstractIXRetailTranslator
implements LogTillIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogTill object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogTill()
    {                                   // begin LogTill()
    }                                   // end LogTill()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified till object. <P>
       @param till till reference
       @param storeID store identifier
       @param doc parent document
       @param el parent element
       @param name element name
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(TillIfc till,
                                 String storeID,
                                 Document doc,
                                 Element el,
                                 String name)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element tillElement = parentDocument.createElement(name);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID,
           storeID,
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           AbstractFinancialEntityIfc.STATUS_DESCRIPTORS
             [till.getStatus()],
           tillElement);

        createDateTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
           till.getBusinessDate(),
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TILL_ID,
           till.getTillID(),
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ACCOUNTABILITY,
           AbstractStatusEntityIfc.ACCOUNTABILITY_DESCRIPTORS[till.getRegisterAccountability()],
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DRAWER_ID,
           till.getDrawerID(),
           tillElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TILL_TYPE,
           AbstractStatusEntityIfc.TILL_TYPE_DESCRIPTORS[till.getTillType()],
           tillElement);

        // only report financial totals if till is closed
        if (till.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN &&
            till.getStatus() != AbstractFinancialEntityIfc.STATUS_SUSPENDED)
        {
            createFinancialTotalsElements(till.getTotals(),
                                          tillElement);
        }

        parentElement.appendChild(tillElement);

        return(tillElement);
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified till object. <P>
       @param till till reference
       @param storeID store identifier
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(TillIfc till,
                                 String storeID,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        return(createElement(till,
                             storeID,
                             doc,
                             el,
                             IXRetailConstantsIfc.ELEMENT_TILL));
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates elements for financial totals.
       @param totals financial totals
       @param el element to which financial total elements are to be added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createFinancialTotalsElements(FinancialTotalsIfc totals,
                                                 Element el)
    throws XMLConversionException
    {                                   // begin createFinancialTotalsElements()
        if (totals != null)
        {
            LogFinancialTotalsIfc logTotals =
              IXRetailGateway.getFactory().getLogFinancialTotalsInstance();

            logTotals.createElement(totals, parentDocument, el);
        }
    }                                   // end createFinancialTotalsElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for tills assigned to this till.
       @param tills array of tills assigned to this till
       @param till till refefence
       @param el parent element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createTillsElements(TillIfc[] tills,
                                       TillIfc till,
                                       Element el)
    throws XMLConversionException
    {                                   // begin createTillsElements()
        // this will be done when till element is available
    }                                   // end createTillsElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for drawers
       @param drawers array of drawers
       @param el element to which drawers total elements are to be added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createDrawersElements(DrawerIfc[] drawers,
                                         Element el)
    throws XMLConversionException
    {                                   // begin createDrawersElements()
        if (drawers != null)
        {
            LogDrawerIfc logDrawer =
              IXRetailGateway.getFactory().getLogDrawerInstance();

            for (int i = 0; i < drawers.length; i++)
            {
                logDrawer.createElement(drawers[i],
                                        parentDocument,
                                        el,
                                        IXRetailConstantsIfc.ELEMENT_DRAWER);
            }
        }
    }                                   // end createDrawersElements()

}
