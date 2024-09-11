/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/utility/LogPhone.java /main/12 2012/09/12 11:57:12 blarsen Exp $
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
 *    Rev 1.0   Jan 22 2003 10:05:40   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a phone element.
 * 
 * @version $Revision: /main/12 $
 */
public class LogPhone extends AbstractIXRetailTranslator implements LogPhoneIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

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
        setParentDocument(doc);
        setParentElement(el);

        Element phoneElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_PHONE);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PHONE_TYPE,
           PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[phone.getPhoneType()],
           phoneElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PHONE_NUMBER,
           phone.getPhoneNumber(),
           phoneElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_EXTENSION,
           phone.getExtension(),
           phoneElement);

        appendElements(phone, phoneElement);

        parentElement.appendChild(phoneElement);

        return(phoneElement);
    }
}