/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/LogQuantity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    4    360Commerce 1.3         1/22/2006 11:41:36 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:58   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:13:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Apr 28 2002 13:36:16   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility;
// java imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a Quantity type. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogQuantity
extends AbstractIXRetailTranslator
implements LogQuantityIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogQuantity object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogQuantity()
    {                                   // begin LogQuantity()
    }                                   // end LogQuantity()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified quantity and unit of measure. <P>
       @param quantity quantity reference
       @param uom unit of measure
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(BigDecimal quantity,
                                 UnitOfMeasureIfc uom,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element quantityElement =
          parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_QUANTITY);

        quantityElement.setAttribute
          (IXRetailConstantsIfc.ELEMENT_UNITS,
           quantity.toString());

        // handle unit of measure if it exists
        if (uom != null)
        {
            String uomName =
              IXRetailConstantsIfc.ELEMENT_UNIT_OF_MEASURE_CODE_DEFAULT_VALUE;
            // use IXRetail standard each if necessary
            if (!uom.getUnitID().equals
                 (UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS))
            {
                uomName = uom.getUnitName();
                // add element
                quantityElement.setAttribute
                  (IXRetailConstantsIfc.ELEMENT_UNIT_OF_MEASURE_CODE,
                   uomName);
            }
        }

        // create redundant text node for quantity (redundant)
        Text elementText =
          parentDocument.createTextNode(quantity.toString());
        // append node to element
        quantityElement.appendChild(elementText);

        el.appendChild(quantityElement);

        return(quantityElement);

    }                                   // end createElement()

}
