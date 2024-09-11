/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/CaptureScreenDataAisle.java /main/15 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/14/09 - use decimal format to set string value of a currency
 *                         object
 *    ohorne    10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterTillPayInBeanModel;

/**
 * Grabs Till Pay-In Amount and Reason Code from the
 * EnterTillPayInPayOutBeanModel and sets into the Cargo
 *
 * @version $Revision: /main/15 $
 */
public class CaptureScreenDataAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8736137410513895235L;

    public static final String LANENAME = "CaptureScreenDataAisle";

    /**
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TillPayInCargo cargo = (TillPayInCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Get the model for the bean
        EnterTillPayInBeanModel model = (EnterTillPayInBeanModel)ui.getModel(POSUIManagerIfc.PAY_IN);

        // Set PayIn Amount and ReasonCode into the Cargo
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
        amount.setStringValue(LocaleUtilities.parseCurrency(model.getAmount().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString());
        cargo.setAmount(amount);

        String        reason = model.getSelectedReasonKey();
        CodeListIfc   list = cargo.getReasonCodes();
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode (reason);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
            cargo.setSelectedLocalizedReasonCode(localizedCode);
        }

        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        bus.mail(letter, BusIfc.CURRENT);
    }
}
