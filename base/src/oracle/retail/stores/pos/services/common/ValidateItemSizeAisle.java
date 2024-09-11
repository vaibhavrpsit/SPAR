/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ValidateItemSizeAisle.java /main/14 2013/01/29 16:18:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/29/13 - Remove item size check to allow alpha numeric
 *                         string.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.8  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.7  2004/07/01 16:01:33  bwf
 *   @scr 5421 Remove unused imports.
 *
 *   Revision 1.6  2004/07/01 13:57:31  jeffp
 *   @scr 5646 removed all validation logic
 *
 *   Revision 1.5  2004/06/25 15:37:56  aschenk
 *   @scr 5661 - Size field will now add leading zeros.
 *
 *   Revision 1.4  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.3  2004/04/26 15:42:34  awilliam
 *   @scr 4315 fix for Item size not validated
 *
 *   Revision 1.2  2004/03/18 15:55:00  baa
 *   @scr 3561 Add changes to support giftcard returns
 *
 *   Revision 1.1  2004/02/20 15:51:42  baa
 *   @scr 3561  size enhancements
 *
 *   Revision 1.5  2004/02/19 15:37:31  baa
 *   @scr 3561 returns
 *
 *   Revision 1.4  2004/02/18 20:36:20  baa
 *   @scr 3561 Returns changes to support size
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
 *    Rev 1.1   Dec 19 2003 13:23:06   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Dec 17 2003 11:37:44   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;
//------------------------------------------------------------------------------
/**
     
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------

public class ValidateItemSizeAisle extends LaneActionAdapter implements LaneActionIfc
{   
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3657981255976084677L;

    
    protected static Logger logger = Logger.getLogger(ValidateItemSizeAisle.class);
    //--------------------------------------------------------------------------
    /**
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DisplayTextBeanModel model = ((DisplayTextBeanModel) ui.getModel(POSUIManagerIfc.ITEM_SIZE));
        //boolean matches = false;
        
        //extract the prompt and response model
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();

        // get the response text
        String input = parModel.getResponseText();
        ItemSizeCargoIfc cargo = (ItemSizeCargoIfc) bus.getCargo();
       
        // set into cargo
        cargo.setItemSizeCode(input);
        bus.mail(new Letter(CommonLetterIfc.SEARCH), BusIfc.CURRENT);
            
    }
}
