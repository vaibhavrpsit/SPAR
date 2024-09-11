/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/LineItemDataChangedEvent.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/01 22:22:17  rzurga
 *   @scr 5107 Customer Point of Interaction- Elements missing from CPOI screen
 *   
 *   Units sold and discount added to the bottom of the CPOI screen along with the taxes and total.
 *
 *   Revision 1.1  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

/**
 * Event fired when data in the ItemsModel class changes
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class LineItemDataChangedEvent
{
    /** All items in the data model have changed */
    public final static int ALL = 0;
    /** The list of line items has changed */
    public final static int ITEMS = 1;
    /** One of the existing line items has changed */
    public final static int EXISTING_ITEM = 2;
    /** The message has changed */
    public final static int MESSAGE = 3;
    /** The tax has changed */
    public final static int TAX = 4;
    /** The total has changed */
    public final static int TOTAL = 5;
    /** The subtotal has changed */
    public final static int SUBTOTAL = 6;
    /** The amount tendered has changed */
    public final static int AMOUNT_TENDERED = 7;
    /** The amount tendered has changed */
    public final static int ITEMS_ADDED = 8;
    /** The amount tendered has changed */
    public final static int ITEMS_REMOVED = 9;
    /** The units sold has changed */
    public final static int UNITS_SOLD = 10;
    /** The discount has changed */
    public final static int DISCOUNT = 11;
    
    private int dataType;
    private ItemsModel model;
    private Integer rowStart;
    private Integer rowEnd;
    
    /**
     * Constructor
     *  
     *  @param type DataChanged type
     */
    public LineItemDataChangedEvent(int type)
    {
        setDataChangedType(type);
    }
    
    /**
     * Set the type of data thats changed
     *  
     *  @param type
     */
    public void setDataChangedType(int type)
    {
        this.dataType = type;
    }
    
    /**
     * get the type of data thats changed
     *  
     * @return dataType
     */
    public int getDataChangedType()
    {
        return dataType;
    }

    /**
     * Sometimes data acts on certain rows, this tells what
     * row the action started on, so the renderer can
     * be more intelligent
     *  
     *  @param row
     */
    public void setRowStart(Integer row)
    {
        this.rowStart = row;
    }
    
    /**
     * Get the starting row this data acted on
     *  
     *  @return rowStart
     */
    public Integer getRowStart()
    {
        return this.rowStart;
    }
    /**
     * Sometimes data acts on certain rows, this tells what
     * row the change ended on
     *  
     *  @param row Row the data stops acting on
     */
    public void setRowEnd(Integer row)
    {
        this.rowEnd = row;
    }
    
    /**
     * Get the ending row this data acted on
     *  
     *  @return rowEnd
     */
    public Integer getRowEnd()
    {
        return this.rowEnd;
    }
    
    /**
     * Set the data model this event was fired for
     *  
     *  @param model
     */
    public void setDataModel(ItemsModel model)
    {
        this.model = model;
    }

    /**
     * Get the data model that fired this event
     *  
     *  @return model
     */
    public ItemsModel getDataModel()
    {
        return this.model;
    }
}
