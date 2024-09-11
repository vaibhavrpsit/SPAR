/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. 
* All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualDisplayStackedTotalBean.java /main/1 2013/02/15 15:46:16 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)   
 *    vbongu    02/14/13 - dual display stacked total bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

/**
 * The dual display stacked total bean is used to set the preferred size of the
 * StackedTotalBean
 * 
 * @author vbongu
 * @since 14.0
 * 
 */
public class DualDisplayStackedTotalBean extends StackedTotalBean
{
    /** Serial verison id */
    private static final long serialVersionUID = -1837923967670874694L;

    /**
     * Constructor
     */
    public DualDisplayStackedTotalBean()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure() 
    {
        setPreferredSize(getPreferredSize());
    }

}
