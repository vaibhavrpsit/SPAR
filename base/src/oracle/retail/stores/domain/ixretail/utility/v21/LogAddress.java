/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/v21/LogAddress.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    5    360Commerce 1.4         2/6/2007 11:14:02 AM   Anil Bondalapati
 *         Merge from LogAddress.java, Revision 1.2.1.0 
 *    4    360Commerce 1.3         12/12/2006 12:06:09 AM Brett J. Larsen CR
 *         21298 - updated createElement to convert country name to country
 *         code for c360:CountryCode element
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:54  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:22:17  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.3  2004/04/26 22:21:46  mwright
 *   Replaced ixretail address element with extended 360-specific element.
 *
 *   Revision 1.1.2.2  2004/04/19 07:41:02  mwright
 *   Added postal code extension
 *
 *   Revision 1.1.2.1  2004/04/13 07:31:54  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility.v21;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionAddress360Ifc;
import oracle.retail.stores.domain.utility.CustomerCountryCodeMap;

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
    {
    }

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
    {
        RetailTransactionAddress360Ifc addressElement = (RetailTransactionAddress360Ifc)el;

        Iterator i = address.getLinesIterator();
        while (i.hasNext())
        {
            addressElement.addAddressLine((String) i.next());
        }
        addressElement.setCity(address.getCity());
        addressElement.setState(address.getState());
        addressElement.setPostalCode(address.getPostalCode());
        addressElement.setCountryCode(address.getCountry());

        if (!Util.isEmpty(address.getPostalCodeExtension()))
        {
            addressElement.setPostalCodeExtension(address.getPostalCodeExtension());
        }
        else    // the postal code extension is mandatory in the schema
        {
            addressElement.setPostalCodeExtension("");
        }

        // Delivery Address does not specify the address type.
        if (address.getAddressType() != AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED)
        {
            addressElement.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[address.getAddressType()]);
        }
        
        return addressElement;

    }

}
