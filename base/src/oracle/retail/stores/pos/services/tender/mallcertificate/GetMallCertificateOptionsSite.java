/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mallcertificate/GetMallCertificateOptionsSite.java /main/14 2011/12/19 08:02:20 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     12/15/11 - Move letter dependent depart code to road lane
 *                         action and always reset the evaluate flag as there
 *                         are other areas of exposure other than UnDo.
 *    icole     12/13/11 - Reset evaluate tender limits to handle case of UNDO
 *                         from tender after a manager override.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/2/2008 3:18:39 PM    Jack G. Swan    ReSA
 *         requires all "Vouchers" (Mall Cert., Gift Cert) etc have unique
 *         voucher numbers.  Sears requires us not to collect one.  This code
 *         generates a voucher number based on the transaction id and line
 *         item id.
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/14 18:47:10  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.1  2004/04/02 22:34:35  epd
 *   @scr 4263 Updates to move Mall Cert. tender into sub tour
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 11 2003 13:17:06   bwf
 * Initial revision.
 * Resolution for 3538: Mall Certificate Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mallcertificate;

import java.io.Serializable;
import java.util.HashMap;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderMallCertificateADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class displays the sub tender options if necessary.
 * 
 * @version $Revision: /main/14 $
 */
public class GetMallCertificateOptionsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8640771887964594605L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * This method checks to see if it should display sub tender options screen.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        UtilityIfc util;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        if(util.getParameterValue("MallCertificateSubTenderRequired", "Y").equals("Y"))
        {
            NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
            Serializable subTenders[] = util.getParameterValueList("MallCertificateSubTenderAcceptedList");
            navModel.setButtonEnabled(CommonActionsIfc.MGC_AS_CHECK, isStringListed("Check", subTenders));
            navModel.setButtonEnabled(CommonActionsIfc.MGC_AS_PO, isStringListed("PurchaseOrder", subTenders));            
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            POSBaseBeanModel model = new POSBaseBeanModel();
            model.setLocalButtonBeanModel(navModel);
            ui.showScreen(POSUIManagerIfc.MALL_GIFT_TENDER_OPTIONS, model);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    /**
     * Returns true if the specified string is found in the list, false
     * otherwise
     * 
     * @return boolean if string exists in list
     */
    public boolean isStringListed(String str, Object[] list)
    {
        boolean found = false;
        if (list != null)
        {
            for (int i=0; i < list.length; i++)
            {
                if (str.equals(list[i]))
                {
                   found = true;
                   break;
                }
            }
        }
        return found;
    }
    
    /**
     * This method handles the letter from the screen.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        // moved letter dependent code to road laneactions
        // Always set to evaluate limits to prevent opportunities to tender with Mall Cert without the override.
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        if(tenderAttributes!=null && 
           cargo.getCurrentTransactionADO()!=null && 
           cargo.getCurrentTransactionADO() instanceof SaleReturnTransactionADO)
        {   
            SaleReturnTransactionADO txnADO =(SaleReturnTransactionADO) cargo.getCurrentTransactionADO();
            txnADO.resetEvaluateTenderLimits((TenderTypeEnum) tenderAttributes.get(TenderConstants.TENDER_TYPE)); 
        }

        // Initialize the gift certificate number to null; this forces a record
        // to be genetated int he gift certificate tender line item table.
        cargo.getTenderAttributes().put(TenderConstants.NUMBER, null);
    }    
}
