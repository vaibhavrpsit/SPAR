/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/registry/RegistryEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:39 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:05  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/24 16:21:28  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 06 2003 11:25:22   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 10 2002 08:30:56   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:16:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   13 Mar 2002 17:07:38   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.1   Mar 10 2002 08:52:06   mpm
 * Removed unnecessary reference to PromptAndResponseModel.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:30:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.registry;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RegistryDataTransaction;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class RegistryEnteredAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the Registry number in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        RegistryIDIfc giftRegistry = (RegistryIDIfc) DomainGateway.getFactory().getGiftRegistryInstance();
        // retrieve cargo
        ModifyTransactionGiftRegistryCargo cargo = (ModifyTransactionGiftRegistryCargo) bus.getCargo();
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // retrieve the data(gift registry number) from the UI
        String inputRegistryNumber = new String("");
        inputRegistryNumber = ui.getInput();
        // set in GiftRegistry
        giftRegistry.setID(inputRegistryNumber);
        // put the gift registry in the cargo
        cargo.setNewRegistry(giftRegistry);
        RegistryIDIfc giftRegistryID = null;

        // attempt to do the database lookup
        try
        {
            RegistryDataTransaction gr = null;
            
            gr = (RegistryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.REGISTRY_DATA_TRANSACTION);
            
            giftRegistryID = gr.readRegistryID(inputRegistryNumber);

            if ( giftRegistryID != null)
            {
                // set in GiftRegistry
                giftRegistry.setID(inputRegistryNumber);
                // put the gift registry in the cargo
                cargo.setNewRegistry(giftRegistry);
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
            else
            {
               giftRegistry.setID("");
               cargo.setNewRegistry(giftRegistry);
            }
        }
        catch (DataException e)
        {
            logger.warn( "" + e + "");
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            //letterName = CommonLetterIfc.FAILURE;
            String[] args = new String[1];
            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            args[0] = utility.getErrorCodeString(DataException.CONNECTION_ERROR);

            if (e.getErrorCode() == 6)
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
                dialogModel.setType(DialogScreensIfc.ERROR);
                giftRegistry.setID("");
                cargo.setNewRegistry(giftRegistry);
                //display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("GiftRegConfirm");
                dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                dialogModel.setArgs(args);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  RegistryEnteredAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
