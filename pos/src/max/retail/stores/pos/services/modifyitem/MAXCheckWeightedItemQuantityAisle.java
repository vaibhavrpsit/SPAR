/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.1  26/July/2013				 Prateek			   Changes done for BUG 7265	
 *  Rev 1.0  12/April/2013               Himanshu              MAX-POS-PLU-ITEM-FES_v1.0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.modifyitem;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


public class MAXCheckWeightedItemQuantityAisle extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8394216201089591177L;
	/**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 9$";
    /**
       lane name constant
    **/
    public static final String LANENAME = "ItemQuantityModifiedAisle";

    //----------------------------------------------------------------------
    /**
       ##COMMENT-TRAVERSE##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        SaleReturnLineItemIfc lineItem = cargo.getItem();
        PLUItemIfc pluItem = lineItem.getPLUItem();
        /**MAX Rev 1.1 Change : Start**/
        if(lineItem.isServiceItem())
        {
        	DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ERROR);
            dModel.setResourceID("ServiceItemQuantityError");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
        /**MAX Rev 1.1 Change : End**/
        else if(pluItem instanceof MAXPLUItemIfc && (((MAXPLUItemIfc) pluItem).IsWeightedBarCode()))
        {
        	DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("QuantityCannotBeModified");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
       
       else
       {
    	   bus.mail(new Letter("QuantityModify"), BusIfc.CURRENT);
       }
       
    }

    //---------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of class
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        StringBuffer strResult = new StringBuffer("Class:  ");
        strResult.append(LANENAME)
            .append(" (Revision ").append(getRevisionNumber())
            .append(") @").append(hashCode());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Returns the revision number of this class.
       <p>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
       Main to run a test..
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        MAXCheckWeightedItemQuantityAisle clsItemQuantityModifiedAisle = new MAXCheckWeightedItemQuantityAisle();

        System.out.println(clsItemQuantityModifiedAisle.toString());
    }                                   // end main()
}
