/*
 * $Id: ObjectClassSpec.java,v 1.4 2003/03/13 18:28:03 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */
package org.apache.eve.schema ;


import java.util.Collection ;
import java.util.ArrayList ;


/**
 * Objectclass specification bean used to store the schema information for an
 * objectclass definition.  This class is generated by the schema parser and
 * populated in part by it during the schema file load phase.  Other properties
 * of the bean are filled in afterwords in other phases during the
 * initialization of the schema manager.
 */
public class ObjectClassSpec
{
	public static final int ABSTRACT = 0 ;
	public static final int AUXILIARY = 1 ;
	public static final int STRUCTURAL = 2 ;

    /////////////////////////////
	// Specification Variables //
    /////////////////////////////

    int type = ABSTRACT ;
    String oid ;
    String desc ;
    ArrayList mayList = new ArrayList() ;
    ArrayList mustList = new ArrayList() ;
    ArrayList nameList = new ArrayList() ;
    ArrayList superClasses = new ArrayList() ;

	//////////////////////////////
    // Implementation Variables //
	//////////////////////////////

    /** Directly references other child ObjectClassSpecs */
	ArrayList m_children = new ArrayList() ;


    public String getDesc()
    {
        return desc ;
    }


    public Collection getSuperClasses()
    {
        return superClasses ;
    }


    public int getType()
    {
		return type ;
    }


    public Collection getMustList()
    {
        return mustList ;
    }


    public Collection getMayList()
    {
        return mayList ;
    }


    public String getOid()
    {
        return oid ;
    }


    public Collection getNameList()
    {
        return nameList ;
    }


    public Collection getAllNames()
    {
        return nameList ;
    }


    public String toString()
    {
        return (String) nameList.get(0) ;
    }
}
