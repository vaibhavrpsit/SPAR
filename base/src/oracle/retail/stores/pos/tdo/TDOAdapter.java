/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/tdo/TDOAdapter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
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
package oracle.retail.stores.pos.tdo;

import org.apache.log4j.Logger;

/**
 * Base Class for TDO implementations
 */
public abstract class TDOAdapter implements TDOIfc
{
    /**
     * Protected Constructor for TDOAdapter
     */
    protected TDOAdapter()
    {
        super();
    }

    /**
     * 
     * @return reference to Log4J Logger
     */
    protected Logger getLogger()
    {
        return Logger.getLogger(getClass());
    }
}
