/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryMenuBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import oracle.retail.stores.foundation.utility.Util;
// application imports

//----------------------------------------------------------------------------
/**
     Class description. <P>
     @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class SummaryMenuBeanModel extends POSBaseBeanModel
{                                       // begin class SummaryMenuBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        holds description and expected amount for each tender or charge
    **/
    protected SummaryCountBeanModel[] summaryCountBeanModel = null;

    //---------------------------------------------------------------------
    /**
        Constructs SummaryMenuBeanModel object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public SummaryMenuBeanModel()
    {                                   // begin SummaryMenuBeanModel()
    }                                   // end SummaryMenuBeanModel()

    //----------------------------------------------------------------------------
    /**
        Retrieves holds description and expected amount for each tender or charge. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public SummaryCountBeanModel[] getSummaryCountBeanModel()
    {                                   // begin getSummaryCountBeanModel[]()
        return(summaryCountBeanModel);
    }                                   // end getSummaryCountBeanModel[]()

    //----------------------------------------------------------------------------
    /**
        Sets holds description and expected amount for each tender or charge. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public void setSummaryCountBeanModel(SummaryCountBeanModel[] value)
    {                                   // begin setSummaryCountBeanModel[]()
        summaryCountBeanModel = value;
    }                                   // end setSummaryCountBeanModel[]()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  SummaryMenuBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (summaryCountBeanModel == null)
        {
            strResult += "summaryCountBeanModel[]:            [null]";
        }
        else
        {
            for(int i = 0; i < summaryCountBeanModel.length; i++)
            {
                strResult += summaryCountBeanModel[i].toString();
            }
        }
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        SummaryMenuBeanModel main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        SummaryMenuBeanModel c = new SummaryMenuBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryMenuBeanModel
