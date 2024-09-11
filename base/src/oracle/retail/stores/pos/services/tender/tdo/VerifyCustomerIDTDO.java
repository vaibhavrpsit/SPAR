/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/VerifyCustomerIDTDO.java /main/17 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/17/14 - Fixing wrongly used reason code type 
 *                         for capturing customer ID type.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    yiqzhao   03/19/10 - use default state
 *    yiqzhao   03/18/10 - get the default state instead of the first state
 *    abondala  01/03/10 - update header date
 *    blarsen   11/20/08 - POS was crashing when HouseAccount number was used.
 *                         Localized Id Type(s) were not being set. Note: This
 *                         path was unreachable in product tour. It was tested
 *                         by overriding values in debugger.
 *    abondala  11/06/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:32 PM  Robert Pearse
 *
 *   Revision 1.3  2004/07/14 18:47:08  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 19 2003 14:11:32   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:19:14   epd
 * Initial revision.
 *
 *    Rev 1.0   Nov 03 2003 11:44:02   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerIDBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 *
 */
public class VerifyCustomerIDTDO extends TDOAdapter
                                 implements TDOUIIfc
{
    protected static final Logger logger = Logger.getLogger(VerifyCustomerIDTDO.class);

    public static final String BUS = "bus";

    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        CustomerIDBeanModel customerIDModel = new CustomerIDBeanModel();
        BusIfc bus = (BusIfc)attributeMap.get(BUS);
        UtilityManagerIfc um = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // Get ID Types
        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc personalIDTypes =  um.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        customerIDModel.setIDTypes(personalIDTypes.getTextEntries(lcl));

        // setup country and states
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        customerIDModel.setCountries(um.getCountriesAndStates(pm));

        customerIDModel.setCountryIndex(0);

        String storeState = CustomerUtilities.getStoreState(pm);
        customerIDModel.setStateIndex(um.getStateIndex(0, storeState.substring(3,storeState.length()), pm));

        return customerIDModel;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
