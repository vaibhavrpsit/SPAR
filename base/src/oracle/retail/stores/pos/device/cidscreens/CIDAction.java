/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/CIDAction.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:40 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.8.2.1  2004/11/09 17:30:41  rzurga
 *   @scr 6552 SwipeAnytime - Cant invoke Select Paymnt screen on CPOI dev
 *   Add 3 and 4-button screens
 *
 *   Revision 1.8  2004/09/17 23:00:01  rzurga
 *   @scr 7218 Move CPOI screen name constants to CIDAction to make it more generic
 *
 *   Revision 1.7  2004/07/21 19:48:05  rzurga
 *   @scr 6395 Ingenico signature capture not working
 *   
 *   Moved start of signature capture to IngenicoSigCap module
 *
 *   Revision 1.6  2004/07/20 23:29:25  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *   
 *   Added constant need for IngenicoTender to allow for both the message and 
 *   the balance due text to be used at the same time.
 *
 *   Revision 1.5  2004/07/07 22:15:03  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *   
 *   Enabled adding tender line items to the scrolling screen as line items
 *
 *   Revision 1.4  2004/07/01 22:22:17  rzurga
 *   @scr 5107 Customer Point of Interaction- Elements missing from CPOI screen
 *   
 *   Units sold and discount added to the bottom of the CPOI screen along with the taxes and total.
 *
 *   Revision 1.3  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.2  2004/02/12 16:48:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Sep 03 2003 13:11:44   RSachdeva
 * Initial revision.
 * Resolution for POS SCR-3355: Add CIDScreen support
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
/**
 * This class represents an action, which is a request for
 * some operation to be performed on the CID Device.
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CIDAction
{
    /** Reset the model behind the screen */
    public static final int RESET = 0;
    /** Clear the screen, view only is cleared model is intact */
    public static final int CLEAR = 1;
    /** Show the screen, using the model to display UI */
    public static final int SHOW = 2;
    /** Hide the screen, and also destroy the model behind the screen UI */
    public static final int HIDE = 3;
    /** Add an item to the end of the line item list */
    public static final int ADD_ITEM = 4;
    /** Remove an item */
    public static final int REMOVE_ITEM = 5;
    /** Remove multiple items */
    public static final int REMOVE_ITEMS = 6;
    /** Udate Items */
    public static final int UPDATE_ITEM = 7;
    /** Set tax */
    public static final int SET_TAX = 8;
    /** Set Total */
    public static final int SET_TOTAL = 9;
    /** Set Subtotal */
    public static final int SET_SUBTOTAL = 10;
    /** Set Amount Tendered */
    public static final int SET_AMOUNT_TENDERED = 11;
    /** Set Message */
    public static final int SET_MESSAGE = 12;
    /** Refresh Line Items */
    public static final int REFRESH_LINE_ITEMS = 13;
    /** Used in Ingenico2Buttons, it sets the button label */
    public static final int SET_BUTTON1_LABEL = 14;
    /** Used in Ingenico2Buttons, it sets the button 2 label */
    public static final int SET_BUTTON2_LABEL = 15;
    /** Used in Ingenico2Buttons, it sets the button 1 action */
    public static final int SET_BUTTON1_ACTION = 16;
    /** Used in Ingenico2Buttons, it sets the button 2 action */
    public static final int SET_BUTTON2_ACTION = 17;
    /** Used to set the visibility of a screen without having to call show() method */
    public static final int SET_VISIBLE = 18;
    /** Used to set the units sold */
    public static final int SET_UNITS_SOLD = 19;
    /** Used to set the discount */
    public static final int SET_DISCOUNT = 20;
    /** Used to set the balance due text */
    public static final int SET_BALANCEDUE = 21;
    /** Used to start signature capture */
    public static final int SIGNATURE_CAPTURE = 22;
    /** Used in Ingenico2Buttons, it sets the button 3 label */
    public static final int SET_BUTTON3_LABEL = 23;
    /** Used in Ingenico2Buttons, it sets the button 4 label */
    public static final int SET_BUTTON4_LABEL = 24;
    /** Used in Ingenico2Buttons, it sets the button 3 action */
    public static final int SET_BUTTON3_ACTION = 25;
    /** Used in Ingenico2Buttons, it sets the button 4 action */
    public static final int SET_BUTTON4_ACTION = 26;
    
    /**
     * High priority request
     */
    public static final int HIGH = 1;
    
    /**
     * Normal priority request
     */
    public static final int NORMAL = 0;
    
    /*
     * CID Screen names
     */
    public final static String CPOI_TWO_BUTTON_SCREEN_NAME = "TwoButtonScreen";
    public final static String CPOI_THREE_BUTTON_SCREEN_NAME = "ThreeButtonScreen";
    public final static String CPOI_FOUR_BUTTON_SCREEN_NAME = "FourButtonScreen";
    public final static String CPOI_TENDER_SCREEN_NAME = "TenderScreen";
    public final static String CPOI_MESSAGE_SCREEN_NAME = "MessageScreen";

    
    /** Revision Number set by CVS */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** One of the public constants, describe what type of action
     * request this is
     */
    protected int command;
    
    /**
     * The priority of the request, either HIGH or NORMAL
     */
    protected int priority;
    
    /**
     * Screen the action is to be applied to, typical examples are
     * IngenicoItems.SCREEN_NAME, IngenicoMessage.SCREEN_NAMe, CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME
     */
    protected String screenName;
    
    /** @deprecated 7.0, just screenName is stored */
    protected CIDScreenIfc screen;

    /**
     * Some actions act on lineItems, if so this specifies
     * the line item the action is to act on.  This is used
     * on the ADD_ITEM
     */
    protected LineItemDisplayIfc lineItem;
    
    /**
     * Collection of multiple line items.  This is used
     * for the REFRESH_LINE_ITEMS and REMOVE_ITEMS actions
     */
    protected Collection lineItems = new ArrayList();
    
    /**
     * Currency object associated with this action,
     * this value is used for the SET_TAX, SET_TOTAL,
     * SET_SUBTOTAL, and SET_AMOUNT_TENDERED actions
     */
    protected CurrencyIfc currency;
    
    /**
     * Line Number, currently this applies to the UPDATE_EXISTING_ITEM
     * or REMOVE_ITEM
     * action, telling what line to update with new data.
     */
    protected Integer lineNumber;
    
    /**
     * The String associated with this action, this is
     * only used in the SET_MESSAGE action.
     */
    protected String stringValue;
    /**
     * @deprecated 7.0, replaced with specific objects the vector may
     * contain rather than a mishmash of types.
     */
    Vector data;

    /**
     * Constructor, the default priority is normal
     *  
     * @param screenName The screen this action is for
     * @param command The action to take
     */
    public CIDAction(String screenName, int command)
    {
        this(screenName, command, CIDAction.NORMAL);
    }

    /**
     * Constructor
     *  
     * @param screenName The screen this action is for
     * @param command The action to take
     * @param priority The priority of this action
     */
    public CIDAction(String screenName, int command, int priority)
    {
        setScreenName(screenName);
        setCommand(command);
        setPriority(priority);
    }
    
    /**
     * Constructor
     *  
     * @param screen The screen this action is for
     * @param command The action to take
     * @deprecated 7.0
     */
    public CIDAction(CIDScreenIfc screen, int command)
    {
        setScreen(screen);
        setCommand(command);
        setPriority(priority);
        data = new Vector();
    }

    /**
     * Constructor
     *  
     * @param screen The screen this action is for
     * @param command The action to take
     * @param priority The priority of this action
     * @deprecated 7.0
     * */
    public CIDAction(CIDScreenIfc screen, int command, int priority)
    {
        setScreen(screen);
        setCommand(command);
        setPriority(priority);
        data = new Vector();
    }
    /**
     * Set the screen this action works on
     *  
     *  @param screenName The screen the action works on
     *  @since 7.0
     */
    public void setScreenName(String screenName)
    {
        this.screenName = screenName;
    }
    
    /**
     * Get the screen the action is for
     *  
     *  @return screen the action is for
     */
    public String getScreenName()
    {
        return this.screenName;
    }
    
    /**
     * Set the screen this action works on
     *  
     *  @param screen The screen the action works on
     *  @deprecated 7.0
     */
    public void setScreen(CIDScreenIfc screen)
    {
        this.screen = screen;
        setScreenName(this.screen.getName());
    }
    
    /**
     * Get the screen the action is for
     *  
     *  @return screen
     *  @deprecated screen the action is for
     */
    public CIDScreenIfc getScreen()
    {
        return this.screen;
    }

    /**
     * Set the command this action represents
     *  
     *  @param aCommand the command to execute
     *  @since 7.0
     */
    public void setCommand(int aCommand)
    {
        command = aCommand;
    }
    
    /**
     * Retrieve the command this action represents.
     * This is one of the constants defined in this
     * file, such as ADD_ITEM
     *  
     *  @return the command 
     */
    public int getCommand()
    {
        return command;
    }

    /**
     * Set the priority of the object
     *  
     *  @param aPriority
     *  @since 7.0
     */
    
    public void setPriority(int aPriority)
    {
        priority = aPriority;
    }
    
    /**
     * Get the priority of the action, it will
     * either be CIDAction.HIGH or CIDAction.NORMAL
     *  
     *  @return priority
     */
    public int getPriority()
    {
        return priority;
    }
    
    /**
     * Set the data the action is performed on.  This also makes
     * the list of lineItems contain only the passed in lineItem.
     * LineItem is used in conjunction with the 
     * ADD_ITEM action
     *  
     *  @param lineItem of PLUITemIfc type
     */
    public void setLineItem(PLUItemIfc lineItem)
    {
        setLineItem( new LineItemDisplay(lineItem));
    }
    
    /**
     * Set the data the action is performed on.  This also makes
     * the list of lineItems contain only the passed in lineItem.
     * LineItem is used in conjunction with the 
     * ADD_ITEM action
     *  
     *  @param lineItem of SaleReturnLineItemIfc type
     */
    public void setLineItem(SaleReturnLineItemIfc lineItem)
    {
        setLineItem( new LineItemDisplay(lineItem));
    }
    
    /**
     * Set the data the action is performed on.  This also makes
     * the list of lineItems contain only the passed in lineItem.
     * LineItem is used in conjunction with the 
     * ADD_ITEM and REMOVE_ITEM actions
     *  
     *  @param lineItem of SaleReturnLineItemIfc type
     */
    public void setLineItem(LineItemDisplayIfc lineItem)
    {
        this.lineItem = lineItem;
        getLineItems().clear();
        getLineItems().add(this.lineItem);
    }
    
    /**
     * Set the data the action is performed on.  This also makes
     * the list of lineItems contain only the passed in lineItem.
     * LineItem is used in conjunction with the 
     * ADD_ITEM and REMOVE_ITEM actions
     *  
     *  @param lineItem of SaleReturnLineItemIfc type
     */
    public void setLineItem(TenderADOIfc lineItem)
    {
        setLineItem( new LineItemDisplay(lineItem));
    }
    
    /**
     * Get the LineItem that this action is being performed on.
     * LineItem is used in conjunction with the 
     * ADD_ITEM and REMOVE_ITEM actions
     *  
     *  @return
     */
    public LineItemDisplayIfc getLineItem()
    {
        return this.lineItem;
    }
    
    /**
     * Set a collection of line items this action is acting on.
     * The list is used on REFRESH_LINE_ITEMS and REMOVE_ITEMS actions.
     *  
     * @param items The items being set
     * @throws DeviceException if an item in the collection is of an unexpected type
     */
    public void setLineItems(Collection items) throws DeviceException
    {
        this.lineItems.clear();
        Iterator iter = items.iterator();
        while(iter.hasNext())
        {
            Object obj = iter.next();
            if(obj instanceof SaleReturnLineItemIfc)
            {
                this.lineItems.add(new LineItemDisplay((SaleReturnLineItemIfc) obj));
            }
            else if(obj instanceof PLUItemIfc)
            {
                this.lineItems.add(new LineItemDisplay((PLUItemIfc) obj));
            }
            else if(obj instanceof LineItemDisplayIfc)
            {
                this.lineItems.add(obj);
            }
            else if(obj != null)
            {
                throw new DeviceException(obj.getClass().getName()+" is an unsupported LineItemType");
            }
                
        }
        this.lineItems = items;
    }
    
    /**
     * Return a collection of lineItems that this action is acting on.
     * The list is used on REFRESH_LINE_ITEMS and REMOVE_ITEMS actions.
     *  
     *  @return Collection of line items
     */
    public Collection getLineItems()
    {
        return this.lineItems;
    }
    
    /**
     * Set the CurrencyIfc value associated with this action.
     * Decimal values are used for the SET_TAX, SET_TOTAL,
     * SET_SUBTOTAL, and SET_AMOUNT_TENDERED actions
     *  
     *  @param currency Currency value
     */
    public void setCurrency(CurrencyIfc currency)
    {
        this.currency = currency;
    }
    
    /**
     * Return the decimal value associated with the
     * given action
     * Decimal values are used for the SET_TAX, SET_TOTAL,
     * SET_SUBTOTAL, and SET_AMOUNT_TENDERED actions
     *  
     *  @return decimalValue
     */
    public CurrencyIfc getCurrency()
    {
        return this.currency;
    }
    
    /**
     * Return the lineNumber this action acts on.
     * Line number is used with the UPDATE_EXISTING_ITEM
     * or REMOVE_ITEM actions
     *  
     *  @return lineNumber
     */
    public Integer getLineNumber()
    {
        return this.lineNumber;
    }
    
    /**
     * Set the line number this action needs to work on
     * Line number is used with the UPDATE_EXISTING_ITEM
     * or REMOVE_ITEM actions
     *  
     *  @param lineNumber
     */
    public void setLineNumber(Integer lineNumber)
    {
        this.lineNumber = lineNumber;
    }
    
    /**
     * Set the string value associated with this action.  In the
     * default implementation this is the line that appears
     * on the bottom of the screen that says something like
     * "Swipe your card whenever you are ready".  This is
     * used with the SET_MESSAGE action.
     *  
     *  @param stringVal The message to set
     */
    public void setStringValue(String stringVal)
    {
        this.stringValue = stringVal;
    }
    
    /**
     * Return the string value set for this action.  This
     * string value is used with the SET_MESSAGE action.
     *  
     *  @return stringValue
     */
    public String getStringValue()
    {
       return this.stringValue;
    }
    
    /**
     * Return whether or not this data vector has info
     * This only exists so that deprecated code can run,
     * once the setData etc methods are deleted this can
     * be deleted as well.
     * 
     * @return whether or not deprecated way of storing data is used
     * @since 7.0
     */
    public boolean isDeprecated()
    {
        return (data != null);
    }

    /**
     * Set the data vector
     *  @param data
     *  @deprecated 7.0, use the specific setMethods instead, which
     *  one you use will depend on what type of command you have
     */
    public void setData(Vector data)
    {
        this.data = data;
    }

    /**
     * Add an object to the data vector
     *  
     *  @deprecated 7.0
     *  @param object
     */
    public void pushData(Object object)
    {
        data.add(object);
    }

    /**
     * Pop an object off the beginning of the
     * vector.
     *  
     *  @deprecated 7.0
     *  @return
     */
    public Object popData()
    {
        return data.remove(0);
    }
}

