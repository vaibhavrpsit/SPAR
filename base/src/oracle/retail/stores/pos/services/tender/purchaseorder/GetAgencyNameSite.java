/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/GetAgencyNameSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:09 PM  Robert Pearse   
 *
 *Revision 1.2  2004/07/14 18:47:09  epd
 *@scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *Revision 1.1  2004/04/02 22:13:51  epd
 *@scr 4263 Updates to move Purchase Order tender to its own tour
 *
 *Revision 1.5  2004/03/02 19:47:48  crain
 **** empty log message ***
 *
 *Revision 1.4  2004/02/27 02:43:33  crain
 *@scr 3421 Tender redesign
 *
 *Revision 1.3  2004/02/12 16:48:22  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:22:51  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Jan 23 2004 12:33:34   crain
 * Fixed log
 * Resolution for 3421: Tender redesign
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

// java imports
import java.util.ArrayList;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;


//--------------------------------------------------------------------------
/**
    Gets the agency name.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetAgencyNameSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
       TenderCargo cargo = (TenderCargo) bus.getCargo();
       String otherAgencyName = (String)cargo.getTenderAttributes().get(TenderConstants.OTHER_AGENCY_NAME); 
       
       if (otherAgencyName != null)
       { 

           POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
           UtilityManagerIfc utility =
             (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                               
           // create list dropdown values, "yes" and "no"
           ArrayList aList = new ArrayList();
           aList.add(utility.retrieveCommonText("Yes", "Yes"));
           aList.add(utility.retrieveCommonText("No", "No"));
           // populate model
           DataInputBeanModel dModel = new DataInputBeanModel();     
           dModel.setSelectionChoices("transactionTaxableField", aList);
           
           // display the appropriate screen
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
           if ("Y".equals(util.getParameterValue("CaptureTransactionTaxStatus", "N")))
           {
               ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_NAME_360, dModel);
           }
           else
           {
               ui.showScreen(POSUIManagerIfc.PURCHASE_ORDER_AGENCY_NAME, dModel);
           }
       }
       else
       {
           bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
       }
    }   
}
