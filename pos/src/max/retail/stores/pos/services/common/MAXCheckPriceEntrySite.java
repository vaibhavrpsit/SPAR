/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Rev 1.1	Izhar		29/05/2013		Izhar  Discount rule
 *  Rev 1.0  12/April/2013               Himanshu              MAX-POS-PLU-ITEM-FES_v1.0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.common;
// foundation imports

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site checks to see if the item requires a manual price entry.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCheckPriceEntrySite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Checks the item to see if manual price entry is required.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        /*
         * Grab the item from the cargo
         */
        CargoIfc cargo = (CargoIfc)bus.getCargo();
// Changes starts for code merging (changing PLUItemIfc to MAXPLUItemIfc)
       // PLUItemIfc pluItem = null;
        MAXPLUItemIfc pluItem = null;
// Changes for code merging ends
        Letter letter = null;

        try
        {
            pluItem = (MAXPLUItemIfc) ReflectionUtility.getAttribute(cargo, "PLUItem");
        }
        catch (Exception e)
        {
            logger.error( e.getMessage());
        }

        
        
        
        if (pluItem.getItemClassification().isPriceEntryRequired() )
        {
        	
        	
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter("GiftCard");    
            }
            
            // <!-- MAX Rev 1.0 Change : Start -->
            if(pluItem instanceof MAXPLUItemIfc && (((MAXPLUItemIfc) pluItem).IsWeightedBarCode()))
        	{
            	letter = new Letter(CommonLetterIfc.CONTINUE);
        	}
          //<!-- MAX Rev 1.0 Change : end -->
            
            else
            {
                /*
                 * Setup the bean model and display the screen
                 */
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                PromptAndResponseModel responseModel = new PromptAndResponseModel();
// Changes start for code merging(commenting below line as it accept locale in base 14, so referring MAX locale is added)
               // responseModel.setArguments(pluItem.getDescription());
                responseModel.setArguments(pluItem.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
                
// Changes ends for code merging
                baseModel.setPromptAndResponseModel(responseModel);
                POSUIManagerIfc ui;
                ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				 // <!-- MAX Rev 1.1 Change : Start -->
               // ui.showScreen(POSUIManagerIfc.ENTER_PRICE, baseModel);
			   letter = new Letter("Continue");
			    // <!-- MAX Rev 1.1 Change : end -->
            }
        }
        else
        {
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter("GiftCard");    
            }
            else
            {
                letter = new Letter(CommonLetterIfc.CONTINUE);
            }
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
