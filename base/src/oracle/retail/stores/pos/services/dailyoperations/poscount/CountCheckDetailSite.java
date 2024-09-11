/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CountCheckDetailSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:33 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.6  2004/07/12 13:55:22  jriggins
 *   @scr 5421 Removed unused imports which were causing local build errors under our accepted Eclipse settings
 *
 *   Revision 1.5  2004/07/09 23:27:02  dcobb
 *   @scr 5190 Crash on Pickup Canadian Checks
 *   @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 *   Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 *   Revision 1.4  2004/06/18 22:19:34  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add Foreign currency count.
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 23 2003 13:44:04   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.0   Apr 29 2002 15:30:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:16   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
     Determines what type of count to perform on checks and performs that count.<p>
**/
//--------------------------------------------------------------------------
public class CountCheckDetailSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        string for currency value of zero
    **/
    public static final String ZERO_STRING = "0";

    //----------------------------------------------------------------------
    /**
        Tests the contents of the cargo the determine the next step.<p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();

        OtherTenderDetailBeanModel beanModel = new OtherTenderDetailBeanModel();
        String countryCode = cargo.getTenderNationality();
        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        if (!countryCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
        {
            zero = DomainGateway.getAlternateCurrencyInstance(countryCode);
        }
        zero.setStringValue(ZERO_STRING);
        beanModel.setTotal(zero);
        cargo.setOtherTenderDetailBeanModel(beanModel);

        beanModel.setDescription(cargo.getCurrentFLPTender());
        if (cargo.getCurrentActivity().equals(PosCountCargo.CHECK))
        {
            beanModel.setSummaryDescription(PosCountCargo.CHECK);
        }
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        pandrModel.setArguments(beanModel.getDescription());
        beanModel.setPromptAndResponseModel(pandrModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.OTHER_TENDER_DETAIL, beanModel);
    }
}
