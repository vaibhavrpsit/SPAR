/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/KeyData.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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

public class KeyData
{
    int keyCode;
    char keyChar;
    public static final int KEY_DATA = 1;


    public KeyData()
    {
    }
  
    public KeyData(int i, char c)
    {
        keyCode = i;
        keyChar = c;
    }

    public void setKeyCode(int i)
    {
        keyCode = i;
    }

    public void setKeyChar(char c)
    {
        keyChar = c;
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public char getKeyChar()
    {
        return keyChar;
    }
}

