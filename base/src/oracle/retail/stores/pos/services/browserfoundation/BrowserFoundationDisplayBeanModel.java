/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/browserfoundation/BrowserFoundationDisplayBeanModel.java /main/8 2012/09/04 15:22:16 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 08/30/12 - added homeurl for javafx
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    nkgaut 11/14/08 - Changes for removing JDIC Components from POS Installed
 *                      Directories
 *    nkgaut 09/29/08 - A new beanmodel class for browserfoundationdisplaybean
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.browserfoundation;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class BrowserFoundationDisplayBeanModel extends DialogBeanModel
{
	private static final long serialVersionUID = 1L;
	
	protected String homeURL = null;

    public String getHomeURL()
    {
        return homeURL;
    }

    public void setHomeURL(String homeURL)
    {
        this.homeURL = homeURL;
    }

}
