/* ===========================================================================
* Copyright (c) 2008, 2012, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/GetStoreCreditNumberUISite.java /main/13 2012/03/27 10:57:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     11/07/14 - cleaned up code for setting store credit number.
 *    icole     08/20/14 - changed max prompt size to 18 to allow for mmdd added to ID.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    03/24/09 - setting setMaxLength(14) instead of setMinLength(14)
 *                         in PrePrintedStoreCredit block
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 */
@SuppressWarnings("serial")
public class GetStoreCreditNumberUISite extends PosSiteActionAdapter
{
    /**
     * commonSpec
     */
    public static final String COMMON = "Common";
    /**
     * default
     */
    public static final String DEFAULT = "";

    /**
     * This is the arrive method which will display the screen.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            if (pm.getBooleanValue(ParameterConstantsIfc.TENDER_PrePrintedStoreCredit))
            {
                promptModel.setMaxLength("12");
            }
            else
            {
                promptModel.setMaxLength("18");
            }
        }
        catch (ParameterException e)
        {
            // set not preprinted store credit max length
            promptModel.setMaxLength("18");
            logger.error("Error retrieving PrePrintedStoreCredit parameter, using non-PrePrintedStoreCredit maximum length of 18",e);            
        }
            beanModel.setPromptAndResponseModel(promptModel);

            ui.showScreen(POSUIManagerIfc.STORE_CREDIT_TENDER_NUMBER, beanModel);
 
    }

    /**
     * Depart method retrieves input.
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If the user entered a gift certificate number
        if (letter.getName().equals(CommonLetterIfc.NEXT))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            boolean isScanned = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel().isScanned();

            // Get the gift certificate number and put in the cargo
            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput().trim());

            if (isScanned)
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
            }
            else
            {
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            }
        }
    }
}
