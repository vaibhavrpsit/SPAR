/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/v21/Log.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:21 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:55  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/05/06 03:42:41  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.2.2.5  2004/04/28 00:46:11  mwright
 *   Created public SCHEMA_LOCATION - it is used in the unit testing subsystem
 *
 *   Revision 1.2.2.4  2004/04/26 22:02:33  mwright
 *   Changed schema location to refer to the 360 extension of the ixretail schema
 *
 *   Revision 1.2.2.3  2004/04/13 07:28:46  mwright
 *   Removed tabs
 *
 *   Revision 1.2.2.2  2004/03/28 10:36:06  mwright
 *   Added import of v2.1 constants interface, and changed implements line to use it instead of fully-specified name
 *
 *   Revision 1.2.2.1  2004/03/17 04:13:50  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.1  2004/03/15 09:40:34  mwright
 *   Initial revision for POSLog v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log.v21;
// XML imports
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.log.LogIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
//--------------------------------------------------------------------------
/**
    This class creates the POS log in IXRetail format. This construct is the
    container for the entire tlog and corresponds to the LogContainer
    schema.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class Log
implements LogIfc, IXRetailConstantsV21Ifc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8416865804512973889L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.ixretail.log.Log.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        schema location
    **/
    //protected String schemaLocation = POS_LOG_SCHEMA_LOCATION;  // this is 360-specific....contains 360 extensions
    protected String schemaLocation = SCHEMA_LOCATION;
    
    public static final String SCHEMA_LOCATION = "classpath://poslogv21/360POSLog.xsd"; 

    //----------------------------------------------------------------------------
    /**
        Constructs Log object. <P>
    **/
    //----------------------------------------------------------------------------
    public Log()
    {                                   // begin Log()
    }                                   // end Log()

    //---------------------------------------------------------------------
    /**
        Name of root element varies by version.
        @return XML root element name
    **/
    //---------------------------------------------------------------------
    public String getLogRootElement()
    {
        return ELEMENT_POS_LOG_ROOT;
    }
    
    
    //---------------------------------------------------------------------
    /**
        Sets schema location.
        @param value schema location
    **/
    //---------------------------------------------------------------------
    public void setSchemaLocation(String value)
    {
        schemaLocation = value;     // mgw: it appears that this is never called, so the default is always used
    }

    //---------------------------------------------------------------------
    /**
        Returns schema location.
        @return schema location
    **/
    //---------------------------------------------------------------------
    public String getSchemaLocation()
    {                                   // begin getSchemaLocation()
        return schemaLocation;
    }                                   // end getSchemaLocation()

    //---------------------------------------------------------------------
    /**
       Adds attributes to root element. <P>
       @param element root element
    **/
    //---------------------------------------------------------------------
    public void addAttributes(Element element)
    {
        element.setAttribute(ATTRIBUTE_NAMESPACE_TAG,           ATTRIBUTE_NAMESPACE_IXRETAIL);
        element.setAttribute(ATTRIBUTE_SCHEMA_NAMESPACE_TAG,    ATTRIBUTE_SCHEMA_NAMESPACE);
        element.setAttribute(ATTRIBUTE_SCHEMA_LOCATION_TAG,     
                             ATTRIBUTE_NAMESPACE_IXRETAIL + " " + getSchemaLocation());    // this is the 360-extended schema

        // define the 360 namespace prefix:
        element.setAttribute(ATTRIBUTE_NAMESPACE_TAG + ":" + EXTENDED_NAMESPACE_PREFIX,
                             ATTRIBUTE_EXTENDED_NAMESPACE);
        
    }


}
