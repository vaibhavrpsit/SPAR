/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/EnterInquirySite.java /main/11 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   08/30/11 - reuse model is ssn is blank
 *    cgreene   08/29/11 - formatting
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:50:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 03 2003 17:27:36   nrao
 * Removed status messages because now a single status message is displayed and this is done in instantcredituicfg.xml
 *
 *    Rev 1.1   Nov 24 2003 19:19:10   nrao
 * Changed copyright message.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterManager;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.InstantCreditInquiryCriteriaBeanModel;

/**
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class EnterInquirySite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InstantCreditInquiryCriteriaBeanModel model = null;
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // if the phone and postal code has been set but the ssn is null,
        // then the site is coming back from the invalid ssn retry. Reuse the model.
        if (cargo.getGovernmentId() == null && (cargo.getHomePhone() != null || cargo.getZipCode() != null))
        {
            model = (InstantCreditInquiryCriteriaBeanModel)ui.getModel(POSUIManagerIfc.INSTANT_CREDIT_INQUIRY_CRITERIA);
            model.setGovernmentId(null); // make sure government Id is cleared
        }
        else
        {
            // create a blank model
            model = new InstantCreditInquiryCriteriaBeanModel();            
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManager.TYPE);
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManager.TYPE);

            model.setCountries(utility.getCountriesAndStates(pm));
            String storeCountry = CustomerUtilities.getStoreCountry(pm);
            int countryIndx = utility.getCountryIndex(storeCountry, pm);
            model.setCountryIndex(countryIndx);

            boolean referenceInquiry = cargo.getProcess() == InstantCreditCargo.PROCESS_REFERENCE;
            model.setReferenceNumberSearch(referenceInquiry);
        }

        cargo.setTransactionSaved(false);
        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_INQUIRY_CRITERIA, model);
    }
}