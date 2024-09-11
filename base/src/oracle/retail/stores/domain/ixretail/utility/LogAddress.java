/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/LogAddress.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:36:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:24   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 10:01:00   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:13:04   msg
 * Initial revision.
 * 
 *    Rev 1.3   08 Jun 2002 15:59:06   vpn-mpm
 * Made modifications to enhance extensibility.
 *
 *    Rev 1.2   08 Jun 2002 12:33:24   vpn-mpm
 * Added address type; added type name attribute.
 *
 *    Rev 1.1   May 27 2002 16:59:08   mpm
 * Modified naming convention for type constants.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 21 2002 15:21:14   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility;
// java imports
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a RetailTransactionAddress element.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogAddress
extends AbstractIXRetailTranslator
implements LogAddressIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogAddress object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogAddress()
    {                                   // begin LogAddress()
        setTypeName(IXRetailConstantsIfc.TYPE_ADDRESS_360);
    }                                   // end LogAddress()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified Address. <P>
       @param address Address
       @param doc parent document
       @param el parent element
       @return Element representing Address
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(AddressIfc address,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element addressElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_ADDRESS);

        addressElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           getTypeName());

        Iterator i = address.getLinesIterator();
        String addressLine = null;
        while (i.hasNext())
        {
            addressLine = (String) i.next();

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ADDRESS_LINE,
               addressLine,
               addressElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_CITY,
           address.getCity(),
           addressElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATE,
           address.getState(),
           addressElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_POSTAL_CODE,
           address.getPostalCode(),
           addressElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_COUNTRY_CODE,
           address.getCountry(),
           addressElement);

        if (!Util.isEmpty(address.getPostalCodeExtension()))
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_POSTAL_CODE_EXTENSION,
               address.getPostalCodeExtension(),
               addressElement);
        }

        // Delivery Address does not specify the address type.
        if (address.getAddressType() != AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED)
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ADDRESS_TYPE,
               AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[address.getAddressType()],
               addressElement);
        }
        
        appendElements(address, addressElement);

        parentElement.appendChild(addressElement);

        return(addressElement);

    }                                   // end createElement()

}
