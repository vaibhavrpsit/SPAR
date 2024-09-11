/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemLookupLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/10/13 08:59:52 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  10/10/11 - Added skipUOMCheckFlag
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:05 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     10/31/2005 11:49:26    Deepanshu       CR
 *         6092: Set the Sales Associate in the ItemInquiryCargo
 *    3    360Commerce1.2         3/31/2005 15:28:32     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:28     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:39     Robert Pearse
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/08/23 16:15:57  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.4  2004/05/27 17:12:48  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Dec 12 2003 14:05:54   lzhao
 * get store, registration information from sale cargo.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Nov 13 2003 13:08:48   jriggins
 * assigning SCR
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 13 2003 13:06:44   jriggins
 * refactoring the item inquiry service so that plu lookups can be performed without having to go through the entire item inquiry flow
 *
 *    Rev 1.1   Nov 07 2003 12:37:16   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class ItemLookupLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 286725333388100988L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Calling service's cargo
    protected SaleCargoIfc saleCargo = null;

    /**
     * Loads the item cargo.
     * 
     * @param bus Service Bus to copy cargo from.
     */
    @Override
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);

        saleCargo = (SaleCargoIfc) bus.getCargo();
    }

    /**
     * Transfers the item cargo to the item inquiry cargo for the item inquiry
     * service.
     * 
     * @param bus Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);

        ItemInquiryCargo inquiryCargo = (ItemInquiryCargo) bus.getCargo();
        inquiryCargo.setRegister(saleCargo.getRegister());
        inquiryCargo.setTransaction(saleCargo.getTransaction());
        inquiryCargo.setModifiedFlag(true);
        inquiryCargo.setStoreStatus(saleCargo.getStoreStatus());
        inquiryCargo.setRegister(saleCargo.getRegister());
        inquiryCargo.setOperator(saleCargo.getOperator());
        inquiryCargo.setCustomerInfo(saleCargo.getCustomerInfo());
        inquiryCargo.setTenderLimits(saleCargo.getTenderLimits());
        inquiryCargo.setPLUItem(saleCargo.getPLUItem());
        inquiryCargo.setSalesAssociate(saleCargo.getEmployee());
        inquiryCargo.skipUOMCheck(saleCargo.isSkipUOMCheck());
        if (saleCargo.getItemQuantity() != null)
        {
            inquiryCargo.setItemQuantity(saleCargo.getItemQuantity());
        }

        String geoCode = null;
        if (saleCargo.getStoreStatus() != null && saleCargo.getStoreStatus().getStore() != null)
        {
            geoCode = saleCargo.getStoreStatus().getStore().getGeoCode();
        }

        inquiryCargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), saleCargo.getPLUItemID(), "",
                "", geoCode);

        inquiryCargo.setIsRequestForItemLookup(true);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return "Class:  InquiryOptionsLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode();
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
