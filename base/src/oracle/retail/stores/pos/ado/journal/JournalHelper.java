package oracle.retail.stores.pos.ado.journal;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
import java.util.ArrayList;
import java.util.List;


/**
 * Base product implementation of the JournalHelperIfc interface. Supports adding
 * extended Journal Actions via configuration from the ApplicationContext.xml file.
 * The class and interface can be extended by implementors to provide additional
 * functionality in the ADO base Journal classes.
 * 
 * @author rhaight
 *
 */
public class JournalHelper implements JournalHelperIfc {

	/** Collection of extended Journal action enumeration names */
	protected List<String> extJrnlEnums = new ArrayList<String>();
	
	@Override
	public List<String> getExtendedJournalActionEnums() {
		return extJrnlEnums;
	}
	
	
	/**
	 * Accessor used by Spring to load the configured extended enum names
	 * @param extEnums
	 */
	public void setExtendedJournalActionEnums(List<String> extEnums)
	{
		extJrnlEnums = extEnums;
	}

}
