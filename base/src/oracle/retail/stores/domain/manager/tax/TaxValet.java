/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/manager/tax/TaxValet.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:51 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:55  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:14:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:58  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:33  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:38:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:59:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:25:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:16:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:38:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.manager.tax;

import java.io.Serializable;

import oracle.retail.stores.domain.manager.ifc.tax.SalesTaxRateIfc;
import oracle.retail.stores.foundation.tour.manager.ValetIfc;

//------------------------------------------------------------------------
/**
    Transports requests from the TaxManager and the TaxTechnician and
    returns a reference to a tax rate or a tax group calculation.
*/
//------------------------------------------------------------------------

public class TaxValet implements ValetIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9124294963052872797L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
 
    /**
        action constants used by the TaxManager and TaxTechnician
    */
    public static final int UNKNOWN = -1;
    public static final int NONE = 0;
    public static final int TAXGROUPBYID = 1;
    public static final int TAXGROUPBYNAME = 2;
    public static final int TAXRATEBYID = 3;
    public static final int TAXRATEBYNAME = 4;
    
    /**
        tax group reference
    */
    protected TaxGroupCalculation taxGroupCalc = null;
    /**
        tax rate reference
    */
    protected SalesTaxRateIfc taxRate = null;
    
    /**
        id code for the target object
    */
    protected int id;
    /**
        name of the target object
    */
    protected String name;
    /**
        action code - must be one of the defined constants
    */
    protected int action;
    
    //-------------------------------------------------------------------
    /**
        Default constructor for the TaxValet. Intializes the name, action,
        and id attributes.
    */
    //-------------------------------------------------------------------
    public TaxValet()
    {
        this.name = "";
        this.action = NONE;
        this.id = UNKNOWN;
    }
    
    //-------------------------------------------------------------------
    /**
        Returns a serializable object. Not currently used
        @param taxReq - not currently used
        @return null
    */
    //-------------------------------------------------------------------
    public Serializable execute(Object taxReq)
    {
        return null;   
    }
    
    //-------------------------------------------------------------------
    /**
        Sets a reference to the TaxGroupCalculation
        @param taxGroupCalc - reference to a TaxGroupCalculation
    */
    //-------------------------------------------------------------------
    public void setTaxGroupCalculation(TaxGroupCalculation taxGroupCalc)
    {
        this.taxGroupCalc = taxGroupCalc;
    }
    
    //-------------------------------------------------------------------
    /**
        Returns a reference to a TaxGroupCalculation
        @return TaxGroupCalculation reference
    */
    //-------------------------------------------------------------------
    public TaxGroupCalculation getTaxGroupCalculation()
    {
        return this.taxGroupCalc;
    }
    
    //-------------------------------------------------------------------
    /**
        Sets the tax rate reference
        @param taxRate - reference to a tax rate
    */
    //-------------------------------------------------------------------
    public void setTaxRate(SalesTaxRateIfc taxRate)
    {
        this.taxRate = taxRate;
    }
    
    //-------------------------------------------------------------------
    /**
        Returns a reference to the tax rate
        @return tax rate
    */
    //-------------------------------------------------------------------
    public SalesTaxRateIfc getTaxRate()
    {
        return this.taxRate;
    }
    
    //-------------------------------------------------------------------
    /**
        Sets the action code for the valet
        @param action - one of the designated action constants
        @exception IllegalStateExeption is thrown if action is not one
                   of the defined constants
    */
    //-------------------------------------------------------------------
    public void setAction(int action)
    {
        switch (action)
        {
            case UNKNOWN:
            case NONE:
            case TAXGROUPBYID:
            case TAXGROUPBYNAME:
            case TAXRATEBYNAME:
            case TAXRATEBYID:
            {
                this.action = action;
                break;
            }
            default:
            {
                throw new IllegalStateException("TaxValet - Invalid value for action");
            }
        }
        
   }

    //-------------------------------------------------------------------
    /**
        Returns the action code
        @return action code
    */
    //-------------------------------------------------------------------
    
    public int getAction()
    {
        return this.action;
    }
        
    //-------------------------------------------------------------------
    /**
        Sets the id of the target object
        @param id - id of target object
    */
    //-------------------------------------------------------------------
    public void setId(int id)
    {
        this.id = id;
    }
        
    //-------------------------------------------------------------------
    /**
        Returns the id of the target object
        @return id of target object
    */
    //-------------------------------------------------------------------
    public int getId()
    {
        return this.id;
    }
        
    //-------------------------------------------------------------------
    /**
        Returns the name
        @return name of target object
    */
    //-------------------------------------------------------------------
    public String getName()
    {
        return this.name;
    }
        
    //-------------------------------------------------------------------
    /**
        Sets the name of the target object
        @param name - target object
    */
    //-------------------------------------------------------------------
    public void setName(String name)
    {
        this.name = name;
    }

    //----------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number.
        <p>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public static String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns a human readable string representation of this object.
        <p>
        @return A human readable string representation of this object.
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // The string to be returned
        String strResult = "Class:  " + getClass().getName() +
            "(Revision " + getRevisionNumber() + ")@" + hashCode();
        
        strResult +="\nId: " + id;
            strResult += "\nName:" + this.name;
            strResult += "\nAction: " + this.action;
            if (this.taxGroupCalc != null)
            {
                strResult += "\nTaxGroup:\n" + taxGroupCalc.toString();
            }
            if (this.taxRate != null)
            {
                strResult += "\nTaxRate:\n" + taxRate.toString();
            }
        return strResult;
    }
    
} // End TaxValet
