/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcertificate/issue/GetCardNumForGiftCertificateIssueSite.java /main/12 2011/12/05 12:16:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.1  2004/02/20 14:15:17  crain
 *   @scr 3814 Issue Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcertificate.issue;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 *  This site displays the screen that gets the gift certificate number
 *  @version $Revision: /main/12 $
 */
//--------------------------------------------------------------------------
public class GetCardNumForGiftCertificateIssueSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
     *  @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        //create the transaction if it doesn't exist.
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        if (transaction == null)
        {
            cargo.initializeTransaction(bus);
            transaction = cargo.getTransaction();
            transaction.setSalesAssociate(cargo.getTransaction().getCashier());
        }

        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel promptModel = new PromptAndResponseModel();

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String arg = utility.retrieveCommonText("Gift", "Gift");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        arg = arg.toLowerCase(locale);
        
        // Set the argText in the PromptArea
        promptModel.setArguments(arg);
        beanModel.setPromptAndResponseModel(promptModel);
        
        ui.showScreen(POSUIManagerIfc.CERTIFICATE_ENTRY, beanModel);        

    }
}
