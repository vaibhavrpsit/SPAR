/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgUtils.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:23 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/02/12 16:52:16  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:16   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

// java imports
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 *  Class to load Images.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class ImgUtils
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //------------------------------------------------------------------------------
    /**
     *  Loads image
     *  @param imageName the image to be loaded
     *  @param c component where to place the image
     */
    //------------------------------------------------------------------------------
    public static Image loadImage(String imageName, Component c)
    {
        MediaTracker tracker = new MediaTracker(c);
        Image img = Toolkit.getDefaultToolkit().createImage(imageName);
    tracker.addImage(img, 0);

    try
    {
            tracker.waitForID(0);
        }
        catch(InterruptedException ie)
        {
           // ignore
        }
        return img;
    }
    //--------------------------------------------------------------------------
    /**
     *    Retrieves the PVCS revision number.
     *    @return String representation of revision number
     */
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
