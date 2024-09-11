/* ===========================================================================
* Copyright (c) 2001, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemListTableModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//---------------------------------------------------------------------
/**
    This class is used to setup the table model for a JTable
    @return String representation of object
**/
//---------------------------------------------------------------------
public class ItemListTableModel extends OrderListTableModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6024520734622151623L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "ItemListTableModel";


   //---------------------------------------------------------------------
   /**
    Constructor for class.
   **/
   //---------------------------------------------------------------------
    public ItemListTableModel()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
    Constructor for class.
    @param columnName the array of column names
    @param rowSize the number of rows
    **/
    //---------------------------------------------------------------------
    public ItemListTableModel(String[] columnName, int rowSize)
    {
        super(columnName,rowSize);
    }

    //---------------------------------------------------------------------
    /**
       Constructor for class.
       @param rowSize the number of rows
       @param columnSize the number of columns
    **/
    //---------------------------------------------------------------------
    public ItemListTableModel(int rowSize, int columnSize)
    {
        super(rowSize, columnSize);
    }

    //---------------------------------------------------------------------
    /**
    Constructor for class.
    @param values a 2-dimensional array of Objects
     **/
    //---------------------------------------------------------------------
    public ItemListTableModel(Object[][] values)
    {
        super(values);
    }
 }
