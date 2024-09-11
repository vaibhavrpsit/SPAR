/* ===========================================================================
* Copyright (c) 2004, 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/EnterRedeemNumberUISite.java /main/16 2013/08/23 11:19:10 bhsuthar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     11/07/14 - allow 18 digit store credit number 
 *    bhsuthar  08/19/13 - prompt and response panel runtime arg
 *    rabhawsa  08/20/12 - removed placeholder from key RedeemNumberPrompt
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   08/12/09 - Configure Redeem Number screen to use barcode
 *                         scanner
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/24 15:31:38  blj
 *   @scr 5185 - Had to update gift card credit to get Amount from the tenderAttributes
 *
 *   Revision 1.7  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.6  2004/05/07 22:01:12  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/05/03 03:50:59  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.4  2004/04/12 18:37:47  blj
 *   @scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *   Revision 1.3  2004/04/07 22:49:40  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 */
public class EnterRedeemNumberUISite extends SiteActionAdapter
{
    private static final long serialVersionUID = -913376320672138453L;

    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(EnterRedeemNumberUISite.class);
    /**
     * This site displays the Redeem Number Site and collects this number from
     * the ui in the depart method.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // Append transaction id to prompt response
        String redeemTypeText = utility.retrieveText(
                "common",
                "commonText",
                cargo.getRedeemTypeSelected(),
                cargo.getRedeemTypeSelected());
        
        String pattern =
            utility.retrieveText(
                    "PromptAndResponsePanelSpec",
                    "redeemText",
                    "RedeemNumberPrompt",
            "Enter the {0} number, then press Next, or press Foreign.");
        
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String message = LocaleUtilities.formatComplexMessage(pattern, redeemTypeText.toLowerCase(locale));
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        promptModel.setPromptText(message);
        // The store credit number size was increased from a length of 14 to 18 for non-preprinted.
        if (CommonActionsIfc.STORE_CREDIT.equals(cargo.getRedeemTypeSelected()))            
        {
            try
            {
                ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
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
            	// Log error, set non-preprinted max length, and continue.
                promptModel.setMaxLength("18");
                logger.error("Error retrieving PrePrintedStoreCredit parameter, using non-PrePrintedStoreCredit maximum length of 18",e);
            }
        }
        beanModel.setPromptAndResponseModel(promptModel);
        ui.showScreen(POSUIManagerIfc.REDEEM_NUMBER, beanModel);
    }

    /**
     * Depart method gets the input
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     * @param bus BusIfc
     */
    @Override
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
        {
            // Get information from UI
            RedeemCargo cargo = (RedeemCargo)bus.getCargo();
            // reset the alternate currency to null if the validation failed
            cargo.getTenderAttributes().put(TenderTDOConstants.ALTERNATE_CURRENCY, null);
            String amount = (String)cargo.getTenderAttributes().get(TenderConstants.ALTERNATE_AMOUNT);
            if (amount != null)
            {
                cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);
            }
            
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            PromptAndResponseModel parModel = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel();

            cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
            if(parModel.isScanned())
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
