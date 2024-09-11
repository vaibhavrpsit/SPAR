/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/v21/LogPhone.java /main/12 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
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
 *   Revision 1.1.2.1  2004/04/13 07:31:54  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.utility.LogPhoneIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.Telephone360Ifc;

/**
 * This class creates the elements for a phone element.
 * 
 * @version $Revision: /main/12 $
 */
public class LogPhone extends AbstractIXRetailTranslator implements LogPhoneIfc
{
    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /main/12 $";

    /**
     * Constructs LogPhone object.
     */
    public LogPhone()
    {
    }

    /**
     * Creates element for the specified Phone.
     * 
     * @param phone Phone
     * @param doc parent document
     * @param el parent element
     * @return Element representing Phone
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(PhoneIfc phone, Document doc, Element el) throws XMLConversionException
    {
        Telephone360Ifc phoneElement = (Telephone360Ifc)el;

        phoneElement.setPhoneType(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[phone.getPhoneType()]);
        phoneElement.setPhoneNumber(phone.getPhoneNumber());
        phoneElement.setExtension(phone.getExtension());
        return phoneElement;
    }

}
