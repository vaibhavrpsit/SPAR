/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/destinationtaxrule/GetTaxRulesByPostalCodeSite.java /main/3 2014/06/10 10:12:42 rahravin Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* rahravin    06/05/14 - in case of ship to store, modified the code so as to
*                        get tax rules based on store id
* rabhawsa    11/09/12 - tax should be zero if pincode having no tax rules.
*                        send functionality.
* yiqzhao     10/19/12 - Get tax rules from shipping/send destination postal
*                        code.
* yiqzhao     10/15/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.destinationtaxrule;



import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.tax.SendTaxUtil;
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
public class GetTaxRulesByPostalCodeSite extends PosSiteActionAdapter
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

        ReadNewTaxRuleTransaction tx = (ReadNewTaxRuleTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);

        SendTaxUtil util = new SendTaxUtil();
        
        // Get the postal code..
        String postalCode = cargo.getShippingPostalCode();
        try
        {
            // Try to get tax rules off of postal code... Get all the tax rules,
            // because if this is for a transaction level send we dont know what tax group ids
            // are going to be entered in the future.
            TaxRulesVO taxRulesVO = new TaxRulesVO();
            taxRulesVO = getTaxRules(cargo, tx, postalCode);
            
            // If this was successful, set the tax rules..
            if(taxRulesVO.hasTaxRules())
            {
                cargo.setTaxRulesVO(taxRulesVO);
            }
            // If it was not successful, then the user must select a GeoCode, tell the tour
            // to show the screen that makes the user select...
            else if(taxRulesVO.getGeoCodes().length > 1)
            {
                // Set the letter so that the  DisplayMultipleGeoCodesSite is shown.
                letter = new Letter(CommonLetterIfc.MULTIPLE_MATCHES);
                cargo.setGeoCodes(taxRulesVO.getGeoCodes());
            }
            else if (taxRulesVO.getGeoCodes().length == 0)
            {
            	letter = new Letter(CommonLetterIfc.DONE);
            }
        }
        catch(DataException de)
        {
            // Database problems...
            logger.warn("Error getting tax rules "+ de);
            
            if (de.getErrorCode() == de.NO_DATA)
            {
                TaxRulesVO taxRulesVO = new TaxRulesVO();
                cargo.setTaxRulesVO(taxRulesVO);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Get a new TaxRules object. This method try to get rules based on the
     * ship-to store ID. If it cannot, the ship-to postalCode is used instead.
     */
    protected TaxRulesVO getTaxRules(DestinationTaxRuleCargo cargo, ReadNewTaxRuleTransaction tx, String postalCode)
            throws DataException
    {
        TaxRulesVO taxRulesVO;
        // in case of ship to customer we should get tax rules by postal code.
        if (cargo.getShiptoStoreID() == null || cargo.getShiptoStoreID().isEmpty() || cargo.getShiptoStoreID() == "")
        {
            taxRulesVO = tx.getTaxRulesByPostalCode(postalCode, null);
        }
        // in case of ship to store we should get tax rules by store ID.
        else
        {
            taxRulesVO = tx.getTaxRulesByStore(cargo.getShiptoStoreID(), null);
            cargo.setGeoCode(taxRulesVO.getGeoCode());
        }
        return taxRulesVO;
    }
}
