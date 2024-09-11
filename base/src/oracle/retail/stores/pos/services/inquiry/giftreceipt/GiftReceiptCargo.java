/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftreceipt/GiftReceiptCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/25/14 - Implement the giftcode lookup by running a tour
 *                         to reuse the eJournaling flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         1/25/2006 4:11:02 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:45:10 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/4/2005 10:44:37     Jason L. DeLeau 4201:
           Services Impact - Fix PriceCodeConverter extensibility issues.
      3    360Commerce1.2         3/31/2005 15:28:18     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:57     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:16     Robert Pearse
     $
     Revision 1.3  2004/02/12 16:50:27  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:11  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:59:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:22:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:22   msg
 * Initial revision.
 * 
 *    Rev 1.4   Feb 05 2002 16:42:28   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.3   Dec 10 2001 17:23:32   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftreceipt;

import oracle.retail.stores.commerceservices.common.currency.PriceCodeConverter;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

import java.math.BigDecimal;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class GiftReceiptCargo extends AbstractFinancialCargo implements CargoIfc, TourCamIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3715297485936599031L;

    /** price Code */
    protected String priceCode = null;

    /** price */
    protected BigDecimal price = null;

    /** price code converter instance */
    PriceCodeConverter pcConverter = PriceCodeConverter.getInstance();


    /**
     * Set the price code string
     */
    public void setPriceCode(String value)
    {
        priceCode = value;
    }


    /**
     * Get the price code string
     * 
     * @return a string that holds the price code
     */
    public String getPriceCode()
    {
        return priceCode;
    }


    /**
     * Set the price string
     */
    public void setPrice(BigDecimal value)
    {
        price = value;
    }


    /**
     * Get the price string
     * 
     * @return a string that holds the price code
     */
    public BigDecimal getPrice()
    {
        return price;
    }


    /**
     * Convert Price Code to Price.
     * 
     * @return a BigDecimal that holds the price code
     */
    public BigDecimal convertPriceCodeToPrice(String priceCode)
    {
        return (pcConverter.convertPriceCodeToPrice(priceCode));
    }


    /**
     * Convert price to price code.
     * 
     * @return a string that holds the price code
     */
    public String convertPriceToPriceCode(BigDecimal price)
    {
        return (pcConverter.convertPriceToPriceCode(price));
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     * 
     * @return an object which stores the current state of the cargo.
     * @see com.cornerstoneretail.bedrock.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Reset the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     * 
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
        GiftReceiptCargo savedCargo = (GiftReceiptCargo)snapshot.restoreObject();
        this.setPriceCode(savedCargo.getPriceCode());
        this.setPrice(savedCargo.getPrice());
    }

}
