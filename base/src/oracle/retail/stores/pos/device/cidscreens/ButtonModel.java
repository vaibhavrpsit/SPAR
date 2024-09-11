/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/ButtonModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:43 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:31:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 11 2003 17:06:28   epd
 * Initial revision.
 * 
 *    Rev 1.0   Sep 03 2003 13:11:58   RSachdeva
 * Initial revision.
 * Resolution for POS SCR-3355: Add CIDScreen support
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.pos.device.POSDeviceActionGroup;

public class ButtonModel
    extends POSDeviceActionGroup 
    implements DeviceModelIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4115169748192508806L;


    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    String buttonLabel = "";
    String buttonAction = "";

    public void setButtonLabel(String s)
    {
        buttonLabel = s;
    }

    public String getButtonLabel()
    {
        return buttonLabel ;
    }


    public void setButtonAction(String s)
    {
        buttonAction = s;
    }

    public String getButtonAction()
    {
        return buttonAction ;
    }

    public void reset()
    {
        buttonLabel = "";
        buttonAction = "";
    }
    
}

