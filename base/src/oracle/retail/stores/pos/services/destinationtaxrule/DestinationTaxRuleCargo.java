/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/destinationtaxrule/DestinationTaxRuleCargo.java /main/2 2014/06/10 10:12:42 rahravin Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* rahravin    06/05/14 - Added setter and getter methods for geoCode and
*                        shiptoStoreID
* yiqzhao     10/19/12 - The cargo for getting send/shipping destination tax
*                        rules for store send, xchannel shipping and external
*                        (siebel) send.
* yiqzhao     10/15/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.destinationtaxrule;


import java.util.Collection;
import java.util.HashSet;


import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

/**
    This is the cargo used by the process order send service
**/
public class DestinationTaxRuleCargo extends AbstractFinancialCargo implements CargoIfc
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Shipping postal code
     */
    protected String shippingPostalCode;
    
    /**
     * unique group ids
     */
    Collection<Integer> taxGroupIDs = new HashSet<Integer>();
    
	/**
     * List of geo codes
     */
    protected GeoCodeVO[] geoCodes;
    
    /**
     * The final tax rule from postal code
     */
    protected TaxRulesVO taxRulesVO;
    
       /** shiptoStoreID */
    protected String shiptoStoreID;
    
    /** geoCode */
    protected String geoCode;

    /**
     * @return the shippingPostalCode
     */
    public String getShippingPostalCode()
    {
        return shippingPostalCode;
    }

    /**
     * @param shippingPostalCode the shippingPostalCode to set
     */
    public void setShippingPostalCode(String shippingPostalCode)
    {
        this.shippingPostalCode = shippingPostalCode;
    }

    /**
     * @return Returns the geoCodes.
     */
    public GeoCodeVO[] getGeoCodes()
    {
        return geoCodes;
    }

    /**
     * @param geoCodes The geoCodes to set.
     */
    public void setGeoCodes(GeoCodeVO[] geoCodes)
    {
        this.geoCodes = geoCodes;
    }
    
    /**
     * get tax group id for retrieving tax GEO code
     * @return
     */
    public Collection<Integer> getTaxGroupIDs() {
		return taxGroupIDs;
	}

    /**
     * set tax group id for getting tax GEO code
     * @param taxGroupIDs
     */
	public void setTaxGroupIDs(Collection<Integer> taxGroupIDs) {
		this.taxGroupIDs = taxGroupIDs;
	}

	/**
	 * Get Shipping/Send destination tax rule
	 * @return
	 */
    public TaxRulesVO getTaxRulesVO() {
		return taxRulesVO;
	}

    /**
     * Set Shipping/Send destination tax rule
     * @param taxRulesVO
     */
	public void setTaxRulesVO(TaxRulesVO taxRulesVO) {
		this.taxRulesVO = taxRulesVO;
	}
	
    /**
     * Returns the <code>shiptoStoreID</code> value.
     * 
     * @return the shiptoStoreID
     */
    public String getShiptoStoreID()
    {
        return shiptoStoreID;
    }

    /**
     * Sets the <code>shiptoStoreID</code> value.
     * 
     * @param shiptoStoreID the shiptoStoreID to set
     */
    public void setShiptoStoreID(String shiptoStoreID)
    {
        this.shiptoStoreID = shiptoStoreID;
    }
    
    /**
     * Returns the <code>geoCode</code> value.
     * 
     * @return the geoCode
     */
    public String getGeoCode()
    {
        return geoCode;
    }

    /**
     * Sets the <code>geoCode</code> value.
     * 
     * @param geoCode the geoCode to set
     */
    public void setGeoCode(String geoCode)
    {
        this.geoCode = geoCode;
    }
}



