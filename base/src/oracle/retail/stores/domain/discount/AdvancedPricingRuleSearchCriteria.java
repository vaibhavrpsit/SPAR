/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AdvancedPricingRuleSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:49:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:57:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:17:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   08 Dec 2001 10:42:00   mia
 * Added the StoreID and the RuleID specifically, formatted id does not work for store "CORP"
 * Resolution for Backoffice SCR-61: EYSPOS5.0.0 - Store Coupon Enhancements
 *
 *    Rev 1.0   Sep 20 2001 16:12:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

// foundation imports
import oracle.retail.stores.domain.utility.AbstractRoutable;
import oracle.retail.stores.domain.utility.EYSDate;

//----------------------------------------------------------------------------
/**
    Used to define the search criteria for Advanced Pricing Rules. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class AdvancedPricingRuleSearchCriteria
extends AbstractRoutable implements AdvancedPricingRuleSearchCriteriaIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6074245322809932986L;

  /**
        Revision number supplied by source-code-control system.
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Rule description
    **/
    protected String description = null;
    /**
        Rule name.
    **/
    protected String name = null;
    /**
        Store ID.
    **/
    protected String storeID = null;
    /**
        Rule ID.
    **/
    protected String ruleID = null;
    /**
        Formatted Rule ID.
    **/
    protected String id = null;

    /**
        Start date is later than this value.
        If startDateAfter.equals(startDateBefore) then we're looking for this
        specific date.
    **/
    protected EYSDate startDateAfter = null;

    /**
        Start date is before this value.
    **/
    protected EYSDate startDateBefore = null;

    /**
        End date is later than this value.
    **/
    protected EYSDate endDateAfter = null;

    /**
        End date is before this value.
    **/
    protected EYSDate endDateBefore = null;

    /**
        Source for the rule
    **/
    protected String source = null;

    /**
        Target for the rule
    **/
    protected String target = null;

    /**
        Sort by criteria
    **/
    protected String sortBy = null;

    /**
        Check for date range for active status
    **/
    protected boolean checkActiveStatus = false;

    //------------------------------------------------------------------------
    /**
        Retrieves the rule description. <P>
        @return description
    **/
    //------------------------------------------------------------------------
    public String getDescription()
    {
        return(description);
    }

    //------------------------------------------------------------------------
    /**
        Sets the rule description. <P>
        @param value description
    **/
    //------------------------------------------------------------------------
    public void setDescription(String value)
    {
        description = value;
    }
    //------------------------------------------------------------------------
    /**
        Retrieves the rule name. <P>
        @return description
    **/
    //------------------------------------------------------------------------
    public String getName()
    {
        return(name);
    }

    //------------------------------------------------------------------------
    /**
        Sets the rule name. <P>
        @param value name
    **/
    //------------------------------------------------------------------------
    public void setName(String value)
    {
        name = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the Store ID . <P>
        @return Store id
    **/
    //------------------------------------------------------------------------
    public String getStoreID()
    {
        return(storeID);
    }

    //------------------------------------------------------------------------
    /**
        Sets the rule id. <P>
        @param value id
    **/
    //------------------------------------------------------------------------
    public void setStoreID(String value)
    {
        storeID = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the rule ID . <P>
        @return rule id
    **/
    //------------------------------------------------------------------------
    public String getRuleID()
    {
        return(ruleID);
    }

    //------------------------------------------------------------------------
    /**
        Sets the rule id. <P>
        @param value id
    **/
    //------------------------------------------------------------------------
    public void setRuleID(String value)
    {
        ruleID = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the Formatted rule ID. see AdvancedPricingRule for Format <P>
        @return rule id
    **/
    //------------------------------------------------------------------------
    public String getId()
    {
        return(id);
    }

    //------------------------------------------------------------------------
    /**
        Sets the rule Formatted rule ID. see AdvancedPricingRule for Format <P>
        @param value id
    **/
    //------------------------------------------------------------------------
    public void setId(String value)
    {
        id = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the start date after argument. <P>
        @return start date after
    **/
    //------------------------------------------------------------------------
    public EYSDate getStartDateAfter()
    {
        return(startDateAfter);
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the start date after argument. <P>
        @return String start date after
    **/
    //------------------------------------------------------------------------
    public String getStartDateAfterString()
    {
        return(startDateAfter.toFormattedString("MM/dd/yyyy"));
    }

    //------------------------------------------------------------------------
    /**
        Sets the start date after argument. <P>
        @param value start date after
    **/
    //------------------------------------------------------------------------
    public void setStartDateAfter(EYSDate value)
    {
        startDateAfter = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the start date before argument. <P>
        @return start date before
    **/
    //------------------------------------------------------------------------
    public EYSDate getStartDateBefore()
    {
        return(startDateBefore);
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the start date before argument. <P>
        @return String start date before
    **/
    //------------------------------------------------------------------------
    public String getStartDateBeforeString()
    {
        return(startDateBefore.toFormattedString("MM/dd/yyyy"));
    }
    //------------------------------------------------------------------------
    /**
        Sets the start date before argument. <P>
        @param value start date before
    **/
    //------------------------------------------------------------------------
    public void setStartDateBefore(EYSDate value)
    {
        startDateBefore = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the end date after argument. <P>
        @return start date after
    **/
    //------------------------------------------------------------------------
    public EYSDate getEndDateAfter()
    {
        return(endDateAfter);
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the end date after argument. <P>
        @return start date after
    **/
    //------------------------------------------------------------------------
    public String getEndDateAfterString()
    {
        return endDateAfter.toFormattedString("MM/dd/yyyy");
    }

    //------------------------------------------------------------------------
    /**
        Sets the end date after argument. <P>
        @param value end date after
    **/
    //------------------------------------------------------------------------
    public void setEndDateAfter(EYSDate value)
    {
        endDateAfter = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the end date before argument. <P>
        @return end date before
    **/
    //------------------------------------------------------------------------
    public EYSDate getEndDateBefore()
    {
        return(endDateBefore);
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the end date before argument. <P>
        @return String end date before
    **/
    //------------------------------------------------------------------------
    public String getEndDateBeforeString()
    {
        return endDateBefore.toFormattedString("MM/dd/yyyy");
    }


    //------------------------------------------------------------------------
    /**
        Sets the endDate before argument. <P>
        @param value start date before
    **/
    //------------------------------------------------------------------------
    public void setEndDateBefore(EYSDate value)
    {
        endDateBefore = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the source . <P>
        @return source
    **/
    //------------------------------------------------------------------------
    public String getSource()
    {
        return(source);
    }

    //------------------------------------------------------------------------
    /**
        Sets the criteria for the source. <P>
        @param value source
    **/
    //------------------------------------------------------------------------
    public void setSource(String value)
    {
        source = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the target. <P>
        @return target
    **/
    //------------------------------------------------------------------------
    public String getTarget()
    {
        return(target);
    }

    //------------------------------------------------------------------------
    /**
        Sets the criteria for the target. <P>
        @param value target
    **/
    //------------------------------------------------------------------------
    public void setTarget(String value)
    {
        target = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the sortBy criteria. <P>
        @return sortBy
    **/
    //------------------------------------------------------------------------
    public String getSortBy()
    {
        return(sortBy);
    }

    //------------------------------------------------------------------------
    /**
        Sets the criteria for the sortBy. <P>
        @param value sortBy
    **/
    //------------------------------------------------------------------------
    public void setSortBy(String value)
    {
        sortBy = value;
    }

    //------------------------------------------------------------------------
    /**
        Retrieves the ActiveStatus criteria. <P>
        @return sortBy
    **/
    //------------------------------------------------------------------------
    public boolean getCheckActiveStatus()
        {
            return checkActiveStatus;
        }

    //------------------------------------------------------------------------
    /**
        Retrieves the ActiveStatus criteria. <P>
        @return sortBy
    **/
    //------------------------------------------------------------------------
    public void setCheckActiveStatus(boolean value)
        {
            checkActiveStatus = value;
        }

}
