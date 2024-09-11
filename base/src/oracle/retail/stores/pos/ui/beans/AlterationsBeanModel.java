/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlterationsBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 05 2003 18:21:18   DCobb
 * Generalized names of alteration attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.3   Aug 22 2002 16:26:28   DCobb
 * Added Alteration model name so that it can be printed and journaled.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   Aug 22 2002 16:16:24   DCobb
 * Added alteration model name so that it can be printed and journaled.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Aug 21 2002 11:21:32   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//foundation imports

//---------------------------------------------------------------------
/**
    This model is used by AlterationsBean.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class AlterationsBeanModel extends POSBaseBeanModel
{
    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The alteration model type name
    **/
    protected String alterationsModelName = "";

    /**
        Alterations Information
    */
    protected String itemDescription = "";
    protected String itemNumber = "";
    protected String value1 = "";
    protected String value2 = "";
    protected String value3 = "";
    protected String value4 = "";
    protected String value5 = "";
    protected String value6 = "";

    /**
        editable indicator
    **/
    protected boolean editableFields = true;

    //---------------------------------------------------------------------
    /**
        AlterationsBeanModel constructor.
     */
    //---------------------------------------------------------------------
    public AlterationsBeanModel()
    {
            super();
    }

    //---------------------------------------------------------------------
    /**
        Gets the Alterations Model name (java.lang.String) value. <P>
        @return the alterations model name
     */
    //---------------------------------------------------------------------
    public String getAlterationsModel()
    {
            return alterationsModelName;
    }

    //---------------------------------------------------------------------
    /**
        Gets the ItemDescription (java.lang.String) value. <P>
        @return the item description
     */
    //---------------------------------------------------------------------
    public String getItemDescription()
    {
            return itemDescription;
    }

    //---------------------------------------------------------------------
    /**
        Gets the ItemNumber (java.lang.String) value. <P>
        @return the item number
     */
    //---------------------------------------------------------------------
    public String getItemNumber()
    {
            return itemNumber;
    }

    //---------------------------------------------------------------------
    /**
        Gets the first instruction value (java.lang.String) value. <P>
        @return the first instruction value
    **/
    //---------------------------------------------------------------------
    public String getValue1()
    {
        return value1;
    }

    //---------------------------------------------------------------------
    /**
        Gets the second instruction (java.lang.String) value. <P>
        @return the second instruction value
     */
    //---------------------------------------------------------------------
    public String getValue2()
    {
            return value2;
    }

    //---------------------------------------------------------------------
    /**
        Gets the third instruction (java.lang.String) value. <P>
        @return the third instruction value
     */
    //---------------------------------------------------------------------
    public String getValue3()
    {
            return value3;
    }

    //---------------------------------------------------------------------
    /**
        Gets the fourth instruction (java.lang.String) value. <P>
        @return the fourth instruction value
     */
    //---------------------------------------------------------------------
    public String getValue4()
    {
            return value4;
    }

    //---------------------------------------------------------------------
    /**
        Gets the fifth instruction (java.lang.String) value. <P>
        @return the fifth instruction value
     */
    //---------------------------------------------------------------------
    public String getValue5()
    {
            return value5;
    }

    //---------------------------------------------------------------------
    /**
        Gets the sixth instruction (java.lang.String) value. <P>
        @return the sixth instruction value
     */
    //---------------------------------------------------------------------
    public String getValue6()
    {
            return value6;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Alterations Model name (java.lang.String) value. <P>
     */
    //---------------------------------------------------------------------
    public void setAlterationsModel(String modelName)
    {
            alterationsModelName = modelName;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Item Description (java.lang.String) value. <P>
     */
    //---------------------------------------------------------------------
    public void setItemDescription(String value)
    {
            itemDescription = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Item Number (java.lang.String) value. <P>
     */
    //---------------------------------------------------------------------
    public void setItemNumber(String value)
    {
            itemNumber = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the first instruction (java.lang.String) value. <P>
     */
    //---------------------------------------------------------------------
    public void setValue1(String value)
    {
            value1 = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the second instruction (java.lang.String) value. <P>
        @param the second instruction value
     */
    //---------------------------------------------------------------------
    public void setValue2(String value)
    {
            value2 = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the third instruction (java.lang.String) value. <P>
        @param the third instruction value
     */
    //---------------------------------------------------------------------
    public void setValue3(String value)
    {
            value3 = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the fourth instruction (java.lang.String) value. <P>
        @param the fourth instruction value
     */
    //---------------------------------------------------------------------
    public void setValue4(String value)
    {
            value4 = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the fifth instruction (java.lang.String) value. <P>
        @param the fifth instruction value
     */
    //---------------------------------------------------------------------
    public void setValue5(String value)
    {
            value5 = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the sixth instruction (java.lang.String) value. <P>
        @param the sixth instruction value
     */
    //---------------------------------------------------------------------
    public void setValue6(String value)
    {
            value6 = value;
    }

    //---------------------------------------------------------------------
    /**
        Get the editableFields attribute. <P>
        @return boolean  editableFields returned

    **/
    //---------------------------------------------------------------------
    public boolean getEditableFields()
    {                                   // begin geteditableFields()
        return(editableFields);
    }                                  // end geteditableFields()

    //---------------------------------------------------------------------
    /**
     * Returns a String that represents the value of this object. <P>
     * @return a string representation of the receiver
     */
     //---------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buf=new StringBuffer("AlterationsBeanModel{");
        buf.append("itemDescription=").append(getItemDescription()).append(",");
        buf.append("itemNumber=").append(getItemNumber()).append(",");
        buf.append("alterationsModelName=").append(this.getAlterationsModel()).append(",");
        buf.append("value1=").append(getValue1()).append(",");
        buf.append("value2=").append(getValue2()).append(",");
        buf.append("value3=").append(getValue3()).append(",");
        buf.append("value4=").append(getValue4()).append(",");
        buf.append("value5=").append(getValue5()).append(",");
        buf.append("value6=").append(getValue6()).append(",");
        buf.append("}");
        return buf.toString();
    }


    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
