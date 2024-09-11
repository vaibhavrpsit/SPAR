/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/GetIssuingAgencySite.java /main/14 2013/09/19 10:25:01 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  08/27/13 - wptg fixes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    06/22/09 - use the parameter list values
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.2.1.1     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    4    I18N_P2    1.2.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
 *          text fix for POS
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse
 *
 *   Revision 1.1  2004/04/02 22:13:51  epd
 *   @scr 4263 Updates to move Purchase Order tender to its own tour
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
 *    Rev 1.4   Jan 14 2004 08:58:58   cdb
 * Updated to allow invalid amount exception.
 * Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 * Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 *
 *    Rev 1.3   Jan 13 2004 17:35:26   cdb
 * Corrected flow and updated behavior to match current requirements.
 * Resolution for 3682: Invalid PO Amount displays when PO tender > Balance Due
 * Resolution for 3686: Invalid PO Amount displays when PO tender < Balance Due
 *
 *    Rev 1.2   Dec 29 2003 12:18:48   crain
 * Fixed a flow defect
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.1   Dec 18 2003 21:13:36   crain
 * Made transaction non-taxable
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.0   Dec 17 2003 12:04:44   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import java.io.Serializable;
import java.util.ArrayList;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterIfc;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedListValidator;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the screen to get the currency/country and then reads it in.
    $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class GetIssuingAgencySite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //----------------------------------------------------------------------
    /**
        Arrive method displays screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        try
        {
            // get parameter
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            UtilityManagerIfc um = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            ParameterIfc agencyList = pm.getSource().getParameter("Agencies");

            Serializable[] list = null;
            if (agencyList.getValidator() instanceof EnumeratedListValidator)
            {
                list = agencyList.getValues();
            }

            // create list of agencies
            ArrayList aList = new ArrayList();
            for (int i=0; i<list.length; i++)
            {
            	String tag = list[i].toString();
            	String agencyName = um.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME, tag, tag);
            	aList.add(agencyName);
            }

            // populate model
            DataInputBeanModel model = new DataInputBeanModel();
            model.setSelectionChoices("agencyNameField", aList);

            ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_LIST, model);
        }
        catch (ParameterException e)
        {
            logger.error( "GetIssuingAgencySite: Parameter exception: " + e.getMessage() + "");
            // display empty dropdown box if parameter unavailable
            ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_LIST);
        }
    }

    //----------------------------------------------------------------------
    /**
        Depart method retrieves input.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

        if (letter.getName().equals("Next"))
        {

            TenderCargo cargo = (TenderCargo) bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc um = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String otherAgency = um.retrieveText("Common", BundleConstantsIfc.PARAMETER_BUNDLE_NAME, "Other", "Other");

            DataInputBeanModel model = (DataInputBeanModel)ui.getModel(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_LIST);

            String agencyName = (String)model.getSelectionValue("agencyNameField");
            if (otherAgency.equals(agencyName))
            {
               cargo.getTenderAttributes().put(TenderConstants.OTHER_AGENCY_NAME, agencyName);
            }
            else
            {
               cargo.getTenderAttributes().put(TenderConstants.AGENCY_NAME, agencyName);
               cargo.getTenderAttributes().put(TenderConstants.OTHER_AGENCY_NAME, null);
            }

            // make the transation non-taxable
            cargo.getTenderAttributes().put(TenderConstants.TRANSACTION_NON_TAXABLE, new Boolean(true));
        }
    }
}
