/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/registry/EnterRegistryNumberSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:02:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.registry;

//java imports
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


//--------------------------------------------------------------------------
/**
    ##COMMENT##
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterRegistryNumberSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Shows the UI so the user can enter a Registry number.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ModifyTransactionGiftRegistryCargo cargo =
            (ModifyTransactionGiftRegistryCargo) bus.getCargo();

        RegistryIDIfc gr = cargo.getDefaultRegistry();
        String defaultRegistry = new String("");
        if (gr != null)
        {
            defaultRegistry = gr.getID();
        }

        POSBaseBeanModel model = new POSBaseBeanModel();
        PromptAndResponseModel parm = new PromptAndResponseModel();
        parm.setResponseText(defaultRegistry);
        model.setPromptAndResponseModel(parm);

        ui.showScreen(POSUIManagerIfc.TRANSACTION_GIFT_REGISTRY, model);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  EnterRegistryNumberSite (Revision " +
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
