/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. 
* All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualDisplaySaleBean.java /main/2 2013/02/15 15:46:16 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    02/14/13 - setPreferredSize on the bean
 *    vbongu    01/02/13 - dual display initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

/**
 * The dual display sale bean is duplicate of sale bean.
 * 
 * @author vbongu
 * @since 14.0
 */
public class DualDisplaySaleBean extends SaleBean
{
    /** Serial verison id */
    private static final long serialVersionUID = -3254762215057503969L;

    /**
     * Constructor
     */
    public DualDisplaySaleBean()
    {
        super();
        setName("DualDisplaySaleBean");
    }

    /*
     * (non-Javadoc)
     * 
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {
        super.configure();
        setPreferredSize(getPreferredSize());
    }

    @Override
    public void activate() 
    {
        super.activate();
        list.setEnabled(false);
        list.removeMouseMotionListener(descriptionMouseListener);
        list.removeMouseListener(descriptionMouseListener);
    }
}
