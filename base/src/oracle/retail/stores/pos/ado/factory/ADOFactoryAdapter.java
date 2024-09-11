/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/factory/ADOFactoryAdapter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.factory;

import org.apache.log4j.Logger;

/**
 * @author rwh
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ADOFactoryAdapter implements ADOFactoryIfc
{

    /**
     *  
     */
    public ADOFactoryAdapter()
    {
        super();
    }

    protected Logger getLogger()
    {
        return Logger.getLogger(getClass());
    }

}
