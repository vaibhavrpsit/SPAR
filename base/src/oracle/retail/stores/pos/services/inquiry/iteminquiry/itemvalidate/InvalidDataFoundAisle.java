/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemvalidate/InvalidDataFoundAisle.java /main/11 2013/01/10 14:12:46 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     01/09/13 - Deprecated due to Item Manager changes.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/7/2006 1:36:51 PM    Brett J. Larsen CR
 *         17286 - fix issues with advance price search - next button not
 *         enabled when it should be
 *
 *         v7x->360Commerce
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse   
 *
 *
 *    5    .v7x      1.3.1.0     6/6/2006 3:11:54 PM    Deepanshu       CR
 *         17286: Used new dialog screen 'More Info Needed'
 *
 *   Revision 1.3  2004/02/12 16:50:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   13 Nov 2003 10:38:54   jriggins
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

// java imports
import java.util.Vector;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the item number is not valid.
    @version $Revision: /main/11 $
    @deprecated in version 14.0; no longer used
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class InvalidDataFoundAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       item description label tag
    **/
    public static final String ITEM_DESCRIPTION_TAG  = "ItemDescription";    
    /**
       item description label
    **/
    public static final String ITEM_DESCRIPTION   = "Item Description";
    /**
       item number label tag
    **/
    public static final String ITEM_NUMBER_TAG        = "ItemNumber";
    /**
       item number label
    **/
    public static final String ITEM_NUMBER        = "Item Number";
    /**
    item manufacturer label tag
    **/
    public static final String ITEM_MANUFACTURER_TAG  = "ItemManufacturer";    
    /**
    item manufacturer label
    **/
    public static final String ITEM_MANUFACTURER   = "Item Manufacturer";
    /**
       item number label
    **/
    public static final String EMPTY_LABEL        = "";
    /**
       constant to identify the item number field
    **/
    public static final int ITEM_NUMBER_FIELD = 1;
    /**
       constant to identify the item description field
    **/
    public static final int ITEM_DESC_FIELD   = 2;
    /**
    constant to identify the item manufacturer field
    **/
    public static final int ITEM_MANUFAC_FIELD   = 3;
    /**
       constant to identify the number of fields on the ui
    **/
    public static final int FIELD_COUNT   = 4;

    //----------------------------------------------------------------------
    /**
        Displays an error screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get list of fields with invalid data
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        Vector fields = (Vector) cargo.getInvalidFields();

        // the array with the field names found to be invalid
        String msg[] = new String[FIELD_COUNT];

        // get arguments for dialog
        for (int i=0; i < fields.size(); i++)
        {
           Integer field = (Integer)fields.elementAt(i);
           switch(field.intValue())
           {
              case  ITEM_NUMBER_FIELD:
              {
                  UtilityManagerIfc utility = 
                    (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                  String itemNumberStr = 
                    utility.retrieveDialogText(ITEM_NUMBER_TAG,
                                               ITEM_NUMBER);
                  msg[i] = itemNumberStr;
                  break;
              }
              case  ITEM_DESC_FIELD:
              {
                  UtilityManagerIfc utility = 
                    (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                  String itemDescStr = 
                    utility.retrieveDialogText(ITEM_DESCRIPTION_TAG,
                                               ITEM_DESCRIPTION);              
                  msg[i] = itemDescStr;
                  break;
              }
              case  ITEM_MANUFAC_FIELD:
              {
                  UtilityManagerIfc utility = 
                    (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                  String itemManufStr = 
                    utility.retrieveDialogText(ITEM_MANUFACTURER_TAG,
                    							ITEM_MANUFACTURER);              
                  msg[i] = itemManufStr;
                  break;
              }
              default:
              {
                      msg[i]= EMPTY_LABEL;
              }

           }
        }
        for (int i=fields.size(); i < FIELD_COUNT ; i++)
        {
            msg[i] = EMPTY_LABEL;
        }

        // initialize model bean
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("MORE_INFO_NEEDED");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");

        dialogModel.setArgs(msg);

        // display dialog
        POSUIManagerIfc  ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);

    }
}
