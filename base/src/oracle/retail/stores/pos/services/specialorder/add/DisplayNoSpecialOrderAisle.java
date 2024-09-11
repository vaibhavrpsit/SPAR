/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/DisplayNoSpecialOrderAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:52:01  mcs
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
 *    Rev 1.0   Aug 29 2003 16:07:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:02:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Dec 04 2001 14:55:10   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add; 
 
//foundation imports 
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------ 
/** 
    Displays the special order offline screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------ 
public class DisplayNoSpecialOrderAisle extends PosLaneActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String LANENAME = "DisplayNoSpecialOrderAisle"; 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        no link customer screen name 
    **/ 
    private static final String RESOURCE_ID = "SpecialOrderOffline";
 
    //-------------------------------------------------------------------------- 
    /** 
       Displays the special order offline screen. Sets the Ok button letter to 
       Failure.
       <P> 
       @param bus the bus arriving at this site 
    **/ 
    //-------------------------------------------------------------------------- 
    public void traverse(BusIfc bus) 
    { 
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set buttons and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }  
} 
