/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogLayaway.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    4    360Commerce 1.3         4/27/2006 7:29:46 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse   
 *
 *   Revision 1.4.2.1  2004/11/11 22:31:48  mwright
 *   Merge from top of tree
 *
 *   Revision 1.5  2004/11/11 11:05:35  mwright
 *   Add inventory location
 *
 *   Revision 1.4  2004/08/10 07:17:10  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.3.2.2  2004/08/01 22:36:36  mwright
 *   Set layaway previous status and inventory state
 *
 *   Revision 1.3.2.1  2004/07/09 04:05:13  mwright
 *   No functional change, marked problem with TO DO
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
 *   Revision 1.1.2.1  2004/04/13 06:47:07  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import oracle.retail.stores.domain.ixretail.financial.LogLayawayIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLayaway360Ifc;

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
    {
        RetailTransactionLayaway360Ifc layawayElement = (RetailTransactionLayaway360Ifc)el;

        layawayElement.setInventoryReservationID(layaway.getLayawayID());

        if (verboseFlag)
        {
            layawayElement.setExpirationDate(dateValue(layaway.getExpirationDate()));
            layawayElement.setGracePeriodDate(dateValue(layaway.getGracePeriodDate()));
            layawayElement.setMinimumDownPayment(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getMinimumDownPayment())));
            layawayElement.setCreationFee(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getCreationFee())));
            layawayElement.setDeletionFee(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getDeletionFee())));
            layawayElement.setTotal(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getTotal())));
            layawayElement.setBalanceDue(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getBalanceDue())));
            layawayElement.setPaymentCount(Integer.toString(layaway.getPaymentCount()));
            layawayElement.setDeposit(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(layaway.getTotalAmountPaid())));
            layawayElement.setStorageLocation(layaway.getLocationCode().getCode());
            layawayElement.setStatus(LayawayConstantsIfc.IXRETAIL_STATUS_DESCRIPTORS[layaway.getStatus()]);
            if (layaway.getLastStatusChange() != null)
            {
                layawayElement.setPreviousStatus(LayawayConstantsIfc.IXRETAIL_STATUS_DESCRIPTORS[layaway.getPreviousStatus()]);
                layawayElement.setStatusChange(dateValue(layaway.getLastStatusChange()));
            }
            layawayElement.setLegalStatement(layaway.getLegalStatement());
        }

        return layawayElement;
    }

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
    {
        return(createElement(doc, el, layaway, true));
    }

}
