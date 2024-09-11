/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/KeyTable.java /main/17 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   07/14/10 - fix key handling by forwarding from frame to
 *                         rootpane
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:59 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:12 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains table from which integer key event values can be
 * retrieved.
 * 
 * @version $Revision: /main/17 $
 */
public class KeyTable
{
    /** revision number supplied by source-code-control system. **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /** The table containing the event values. **/
    protected static final Map<String,Integer> table;
    /** The event values. **/
    static
    {
        table = new HashMap<String,Integer>(103);
        table.put("VK_ENTER"          , KeyEvent.VK_ENTER);
        table.put("VK_Enter"          , KeyEvent.VK_ENTER);
        table.put("VK_BACK_SPACE"     , KeyEvent.VK_BACK_SPACE);
        table.put("VK_TAB"            , KeyEvent.VK_TAB);  
        table.put("VK_CANCEL"         , KeyEvent.VK_CANCEL);
        table.put("VK_CLEAR"          , KeyEvent.VK_CLEAR);
        table.put("VK_SHIFT"          , KeyEvent.VK_SHIFT);
        table.put("VK_CONTROL"        , KeyEvent.VK_CONTROL);       
        table.put("VK_ALT"            , KeyEvent.VK_ALT);     
        table.put("VK_PAUSE"          , KeyEvent.VK_PAUSE);         
        table.put("VK_CAPS_LOCK"      , KeyEvent.VK_CAPS_LOCK);
        table.put("VK_ESCAPE"         , KeyEvent.VK_ESCAPE);   
        table.put("VK_Esc"            , KeyEvent.VK_ESCAPE);   
        table.put("VK_SPACE"          , KeyEvent.VK_SPACE);      
        table.put("VK_PAGE_UP"        , KeyEvent.VK_PAGE_UP);       
        table.put("VK_PAGE_DOWN"      , KeyEvent.VK_PAGE_DOWN);     
        table.put("VK_END"            , KeyEvent.VK_END);   
        table.put("VK_HOME"           , KeyEvent.VK_HOME);         
        table.put("VK_LEFT"           , KeyEvent.VK_LEFT);        
        table.put("VK_UP"             , KeyEvent.VK_UP);        
        table.put("VK_RIGHT"          , KeyEvent.VK_RIGHT);
        table.put("VK_DOWN"           , KeyEvent.VK_DOWN);       
        table.put("VK_COMMA"          , KeyEvent.VK_COMMA);        
        table.put("VK_PERIOD"         , KeyEvent.VK_PERIOD);       
        table.put("VK_SLASH"          , KeyEvent.VK_SLASH);      
        table.put("VK_0"              , KeyEvent.VK_0);       
        table.put("VK_1"              , KeyEvent.VK_1);           
        table.put("VK_2"              , KeyEvent.VK_2);           
        table.put("VK_3"              , KeyEvent.VK_3);           
        table.put("VK_4"              , KeyEvent.VK_4);           
        table.put("VK_5"              , KeyEvent.VK_5);           
        table.put("VK_6"              , KeyEvent.VK_6);           
        table.put("VK_7"              , KeyEvent.VK_7);           
        table.put("VK_8"              , KeyEvent.VK_8);           
        table.put("VK_9"              , KeyEvent.VK_9);           
        table.put("VK_SEMICOLON"      , KeyEvent.VK_SEMICOLON);
        table.put("VK_EQUALS"         , KeyEvent.VK_EQUALS);   
        table.put("VK_A"              , KeyEvent.VK_A);      
        table.put("VK_B"              , KeyEvent.VK_B);           
        table.put("VK_C"              , KeyEvent.VK_C);           
        table.put("VK_D"              , KeyEvent.VK_D);           
        table.put("VK_E"              , KeyEvent.VK_E);           
        table.put("VK_F"              , KeyEvent.VK_F);           
        table.put("VK_G"              , KeyEvent.VK_G);           
        table.put("VK_H"              , KeyEvent.VK_H);           
        table.put("VK_I"              , KeyEvent.VK_I);           
        table.put("VK_J"              , KeyEvent.VK_J);           
        table.put("VK_K"              , KeyEvent.VK_K);           
        table.put("VK_L"              , KeyEvent.VK_L);           
        table.put("VK_M"              , KeyEvent.VK_M);           
        table.put("VK_N"              , KeyEvent.VK_N);           
        table.put("VK_O"              , KeyEvent.VK_O);           
        table.put("VK_P"              , KeyEvent.VK_P);           
        table.put("VK_Q"              , KeyEvent.VK_Q);           
        table.put("VK_R"              , KeyEvent.VK_R);           
        table.put("VK_S"              , KeyEvent.VK_S);           
        table.put("VK_T"              , KeyEvent.VK_T);           
        table.put("VK_U"              , KeyEvent.VK_U);           
        table.put("VK_V"              , KeyEvent.VK_V);           
        table.put("VK_W"              , KeyEvent.VK_W);           
        table.put("VK_X"              , KeyEvent.VK_X);           
        table.put("VK_Y"              , KeyEvent.VK_Y);           
        table.put("VK_Z"              , KeyEvent.VK_Z);           
        table.put("VK_OPEN_BRACKET"   , KeyEvent.VK_OPEN_BRACKET);
        table.put("VK_BACK_SLASH"     , KeyEvent.VK_BACK_SLASH);
        table.put("VK_CLOSE_BRACKET"  , KeyEvent.VK_CLOSE_BRACKET);
        table.put("VK_NUMPAD0"        , KeyEvent.VK_NUMPAD0);
        table.put("VK_NUMPAD1"        , KeyEvent.VK_NUMPAD1);     
        table.put("VK_NUMPAD2"        , KeyEvent.VK_NUMPAD2);     
        table.put("VK_NUMPAD3"        , KeyEvent.VK_NUMPAD3);     
        table.put("VK_NUMPAD4"        , KeyEvent.VK_NUMPAD4);     
        table.put("VK_NUMPAD5"        , KeyEvent.VK_NUMPAD5);     
        table.put("VK_NUMPAD6"        , KeyEvent.VK_NUMPAD6);     
        table.put("VK_NUMPAD7"        , KeyEvent.VK_NUMPAD7);     
        table.put("VK_NUMPAD8"        , KeyEvent.VK_NUMPAD8);     
        table.put("VK_NUMPAD9"        , KeyEvent.VK_NUMPAD9);     
        table.put("VK_MULTIPLY"       , KeyEvent.VK_MULTIPLY);     
        table.put("VK_ADD"            , KeyEvent.VK_ADD);    
        table.put("VK_SEPARATER"      , KeyEvent.VK_SEPARATER);
        table.put("VK_SUBTRACT"       , KeyEvent.VK_SUBTRACT);   
        table.put("VK_DECIMAL"        , KeyEvent.VK_DECIMAL);    
        table.put("VK_DIVIDE"         , KeyEvent.VK_DIVIDE);     
        table.put("VK_F1"             , KeyEvent.VK_F1);      
        table.put("VK_F2"             , KeyEvent.VK_F2);          
        table.put("VK_F3"             , KeyEvent.VK_F3);          
        table.put("VK_F4"             , KeyEvent.VK_F4);          
        table.put("VK_F5"             , KeyEvent.VK_F5);          
        table.put("VK_F6"             , KeyEvent.VK_F6);          
        table.put("VK_F7"             , KeyEvent.VK_F7);          
        table.put("VK_F8"             , KeyEvent.VK_F8);          
        table.put("VK_F9"             , KeyEvent.VK_F9);          
        table.put("VK_F10"            , KeyEvent.VK_F10);          
        table.put("VK_F11"            , KeyEvent.VK_F11);         
        table.put("VK_F12"            , KeyEvent.VK_F12);         
        table.put("VK_DELETE"         , KeyEvent.VK_DELETE);      
        table.put("VK_NUM_LOCK"       , KeyEvent.VK_NUM_LOCK);      
        table.put("VK_SCROLL_LOCK"    , KeyEvent.VK_SCROLL_LOCK);
        table.put("VK_PRINTSCREEN"    , KeyEvent.VK_PRINTSCREEN); 
        table.put("VK_INSERT"         , KeyEvent.VK_INSERT); 
        table.put("VK_HELP"           , KeyEvent.VK_HELP);      
        table.put("VK_META"           , KeyEvent.VK_META);        
        table.put("VK_BACK_QUOTE"     , KeyEvent.VK_BACK_QUOTE);
        table.put("VK_QUOTE"          , KeyEvent.VK_QUOTE);  
    }

    /**
     * Returns the event integer based on the key value.
     * 
     * @param keyString the table key.
     * @return event int.
     */
    static public int getKeyEvent(String keyString)
    {
        Integer found = table.get(keyString);
        if (found == null)
        {
            return KeyEvent.VK_UNDEFINED;
        }
        return found;
    }
}