/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/StringSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    cgreen 05/27/10 - convert to oracle packaging
*    cgreen 05/27/10 - convert to oracle packaging
*    cgreen 05/26/10 - convert to oracle packaging
*    abonda 01/03/10 - update header date
*    ddbake 10/04/08 - POS Persistence Infrastructure Work
*    ddbake 10/04/08 - New class to use for general cases where a string is the
*                      criteria to search and we need to return results for
*                      multiple locales.
*
*
*
* ===========================================================================
*/
package oracle.retail.stores.domain.arts;

import java.io.Serializable;

import oracle.retail.stores.common.utility.LocaleRequestor;

//-------------------------------------------------------------------------
/**
    A container class that contains a string to search and the locales
    to retrieve.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class StringSearchCriteria implements Serializable
{
    /**
       This id is used to tell the compiler not to generate a
       new serialVersionUID.
     **/
    static final long serialVersionUID = -1L;

    /**
        The identifying string
    **/
    protected String identifier = null;


    /**
        The Locale Requestor
    **/
    protected LocaleRequestor localeRequestor = null;


    //---------------------------------------------------------------------
    /**
        Class constructor.
        @param  localeRequestor The locales to search
        @param  identifier      Identifier to search
    **/
    //---------------------------------------------------------------------
    public StringSearchCriteria(LocaleRequestor localeRequestor, String identifier)
    {
        this.localeRequestor = localeRequestor;
        this.identifier = identifier;
    }

    //---------------------------------------------------------------------
    /**
        Returns the Locale Requestor
        <p>
        @return  the Locale Requestor
    **/
    //---------------------------------------------------------------------
    public LocaleRequestor getLocaleRequestor()
    {
        return(localeRequestor);
    }

    //---------------------------------------------------------------------
    /**
        Sets the Locale Requestor
        <p>
        @param  localeRequestor   Locale Requestor
    **/
    //---------------------------------------------------------------------
    public void setLocaleRequestor(LocaleRequestor localeRequestor)
    {
        this.localeRequestor = localeRequestor;
    }

    //---------------------------------------------------------------------
    /**
        Returns the Identifier
        <p>
        @return  the Identifier
    **/
    //---------------------------------------------------------------------
    public String getIdentifier()
    {
        return(identifier);
    }

    //---------------------------------------------------------------------
    /**
        Sets the Identifier. <p>
        @param  identifier   The Identifier
    **/
    //---------------------------------------------------------------------
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
}
