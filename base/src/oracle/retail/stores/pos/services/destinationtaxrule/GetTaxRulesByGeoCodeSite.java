/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/destinationtaxrule/GetTaxRulesByGeoCodeSite.java /main/3 2014/06/12 19:16:00 subrdey Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* subrdey     06/06/14 - creating instance of GeoCodeVO
* yiqzhao     10/19/12 - Get tax rule from destination geo code.
* yiqzhao     10/15/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.destinationtaxrule;



import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 *
 * Retrieves the tax rules when there is shipping
 * done from the warehouse
 */
public class GetTaxRulesByGeoCodeSite extends PosSiteActionAdapter
{
    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = -2448850741863674494L;

    /**
    * The system searches for a Geo code associated with the zip code in the shipping address.
    */
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.NEXT);
        DestinationTaxRuleCargo cargo = (DestinationTaxRuleCargo) bus.getCargo();

		GeoCodeVO geoCodeVO = cargo.getGeoCodes()[0];
		
		//Mpos - Only one geo code
		if(geoCodeVO == null){
			geoCodeVO = new GeoCodeVO();
			geoCodeVO.setGeoCode(cargo.getGeoCode());
		}
        try
        {
            ReadNewTaxRuleTransaction tx = (ReadNewTaxRuleTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);

            // Get all tax rules for the tax Groups I care about at this geoCode.
            TaxRulesVO taxRulesVO = tx.getTaxRulesByGeoCode(geoCodeVO.getGeoCode(), cargo.getTaxGroupIDs());

            cargo.setTaxRulesVO(taxRulesVO);
        }
        catch(DataException de)
        {
            // Database errors... use default rule
            logger.error("Error retrieving tax rules "+de);
        }


        bus.mail(letter, BusIfc.CURRENT);
    }

}
