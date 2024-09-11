/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogLayaway.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:28   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:22   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   May 08 2002 17:33:54   mpm
 * Added columns to layaway.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 28 2002 13:35:38   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the basic elements for a layaway
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogLayaway
extends AbstractIXRetailTranslator
implements LogLayawayIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogLayaway object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogLayaway()
    {                                   // begin LogLayaway()
    }                                   // end LogLayaway()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified layaway.  The verbose flag indicates
       if anything more than the ID is to be presented. <P>
       @param doc parent document
       @param el parent element
       @param layaway layaway object
       @param verboseFlag true if complete layaway data is to be presented, false otherwise
       @return Element representing layaway
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(Document doc,
                                 Element el,
                                 LayawayIfc layaway,
                                 boolean verboseFlag)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element layawayElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_LAYAWAY);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_INVENTORY_RESERVATION_ID,
           layaway.getLayawayID(),
           layawayElement);

        if (verboseFlag)
        {
            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_EXPIRATION_DATE,
               layaway.getExpirationDate(),
               layawayElement);

            createDateTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_LAYAWAY_GRACE_PERIOD_DATE,
               layaway.getGracePeriodDate(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_MINIMUM_DOWN_PAYMENT,
               layaway.getMinimumDownPayment(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CREATION_FEE,
               layaway.getCreationFee(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_DELETION_FEE,
               layaway.getDeletionFee(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TOTAL,
               layaway.getTotal(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BALANCE_DUE,
              layaway.getBalanceDue(),
              layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_PAYMENT_COUNT,
               layaway.getPaymentCount(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TOTAL_AMOUNT_PAID,
               layaway.getTotalAmountPaid(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_STORAGE_LOCATION,
               layaway.getLocationCode().getCode(),
               layawayElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_STATUS,
               LayawayConstantsIfc.IXRETAIL_STATUS_DESCRIPTORS[layaway.getStatus()],
               layawayElement);

            if (layaway.getLastStatusChange() != null)
            {
                createTimestampTextNodeElement
                  (IXRetailConstantsIfc.ELEMENT_STATUS_CHANGE,
                   layaway.getLastStatusChange(),
                   layawayElement);
            }

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_LEGAL_STATEMENT,
               layaway.getLegalStatement(),
               layawayElement);
        }

        parentElement.appendChild(layawayElement);

        return(layawayElement);
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates complete element for the specified layaway.
       @param doc parent document
       @param el parent element
       @param layaway layaway object
       @return Element representing layaway
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(Document doc,
                                 Element el,
                                 LayawayIfc layaway)
    throws XMLConversionException
    {                                   // begin createElement()
        return(createElement(doc, el, layaway, true));
    }                                   // end createElement()

}
