/* ===========================================================================
* Copyright (c) 2001, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreListTableModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
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
public class StoreListTableModel extends OrderListTableModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 630041412099870512L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "StoreListTableModel";


   //---------------------------------------------------------------------
   /**
        Constructor for class.
   **/
   //---------------------------------------------------------------------
    public StoreListTableModel()
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
    public StoreListTableModel(String[] columnName, int rowSize)
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
    public StoreListTableModel(int rowSize, int columnSize)
    {
        super(rowSize, columnSize);
    }

    //---------------------------------------------------------------------
    /**
        Constructor for class.
        @param values a 2-dimensional array of Objects
     **/
    //---------------------------------------------------------------------
    public StoreListTableModel(Object[][] values)
    {
        super(values);
    }
 }
