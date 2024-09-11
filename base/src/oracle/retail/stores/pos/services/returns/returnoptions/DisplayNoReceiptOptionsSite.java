/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/DisplayNoReceiptOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Returns flow refactor: deprecated the class;
 *                         replaced by DisplayTransactionSearchOptionsSite.java
 *    mchellap  01/11/10 - Set prompt length to IMEI length
 *    abondala  01/03/10 - update header date
 *    mchellap  12/16/09 - Changes for code review findings
 *    mchellap  12/10/09 - Serialisation return without receipt changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/28/2008 3:46:48 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    4    360Commerce 1.3         5/27/2008 7:37:28 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   05 Feb 2004 23:26:20   baa
 * return multiple items
 *
 *    Rev 1.2   29 Dec 2003 22:35:18   baa
 * more return enhacements
 *
 *    Rev 1.1   Dec 17 2003 11:20:50   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:06:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:04:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:25:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    This site displays the options available from the No Receipt Options
    screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated in version 13.3 
    @see oracle.retail.stores.pos.services.returns.returnoptions.DisplayTransactionSearchOptionsSite
**/
//--------------------------------------------------------------------------
public class DisplayNoReceiptOptionsSite extends PosSiteActionAdapter
{ // begin class DisplayNoReceiptOptionsSite

    /**
       site name constant
    **/
    public static final String SITENAME = "DisplayNoReceiptOptionsSite";

    //--------------------------------------------------------------------------
    /**
       Displays No Receipt options menu.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get ui reference and display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        boolean IMEIEnabled = util.getIMEIProperty();
        boolean serializationEnabled = util.getSerialisationProperty();
        String maxIMEILength = util.getIMEIFieldLengthProperty();

        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel prompt = new PromptAndResponseModel();

        if (IMEIEnabled && serializationEnabled)
        {
            prompt.setMaxLength(maxIMEILength);
        }

        beanModel.setPromptAndResponseModel(prompt);

        ui.showScreen(POSUIManagerIfc.RETURN_NO_RECEIPT, beanModel);

        try
        {
            POSBaseBeanModel tempBeanModel = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.RETURN_NO_RECEIPT);

            ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
            cargo.setMaxPLUItemIDLength(Integer.valueOf(tempBeanModel.getPromptAndResponseModel().getMaxLength()));

        }
        catch (Exception e)
        {
            logger.warn(
            "ShowSaleScreenSite.arrive() unable to get the maximum PLU item ID length", e);
        }

    }

} // end class DisplayNoReceiptOptionsSite
