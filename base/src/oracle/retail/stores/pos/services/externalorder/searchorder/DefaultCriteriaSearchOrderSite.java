/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/DefaultCriteriaSearchOrderSite.java /main/5 2012/05/15 13:15:37 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/08/12 - use DomainObjectFactory
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/14/10 - repackage external order classes
 *    acadar    05/03/10 - added logic for searching for external orders by the
 *                         default search criteria
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteria;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site populates an external order search criteria object with the default search
 * criteria for external orders
 *
 * @author acadar
 */
public class DefaultCriteriaSearchOrderSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8980307899443116976L;

    /**
     * Display the ui to collect a serial number for a serialized item.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SearchOrderCargo cargo = (SearchOrderCargo)bus.getCargo();
        int maxNumberOfMatches = 0;
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            maxNumberOfMatches = pm.getIntegerValue(ParameterConstantsIfc.EXTERNALORDER_ExternalOrderMaximumMatches);
        }
        catch (ParameterException pe)
        {
            logger.warn("Unable to retrieve parameter: ExternalOrderMaximumMatches.", pe);
        }

        //default search criteria consists of max number of matches and this store only flag
        // the external order manager API will retrieve all the active orders with a ready for funding status for
        //this store only
        ExternalOrderSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getExternalOrderSearchCriteriaInstance();
        searchCriteria.setMaxRecordCount(maxNumberOfMatches);
        searchCriteria.setThisStoreOnly(true);
        cargo.setSearchCriteria(searchCriteria);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);

    }

}
