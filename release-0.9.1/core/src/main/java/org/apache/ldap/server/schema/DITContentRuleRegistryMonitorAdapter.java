/*
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.server.schema;


import org.apache.ldap.common.schema.DITContentRule;


/**
 * A simple do nothing monitor adapter for DITContentRuleRegistries.  Note for
 * safty exception based callback print the stack tract to stderr.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DITContentRuleRegistryMonitorAdapter
    implements DITContentRuleRegistryMonitor
{
    public void registered( DITContentRule dITContentRule )
    {
    }


    public void lookedUp( DITContentRule dITContentRule )
    {
    }


    public void lookupFailed( String oid, Throwable fault )
    {
        if ( fault != null )
        {
            fault.printStackTrace();
        }
    }


    public void registerFailed( DITContentRule dITContentRule, Throwable fault )
    {
        if ( fault != null )
        {
            fault.printStackTrace();
        }
    }
}
