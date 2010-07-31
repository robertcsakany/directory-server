/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.server.core.administrative;

import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.subtree.AdministrativeRole;

/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SpecificAdministrativePoint extends AbstractAdministrativePoint
{
    /**
     * Create an instance of SpecificAdministrativePoint
     *
     * @param dn The AdministrativePoint DN
     * @param uuid The AdministrativePoint UUID
     * @param role The AdministrativePoint role
     */
    protected SpecificAdministrativePoint( DN dn, String uuid, AdministrativeRole role )
    {
        super( dn, uuid, role );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSpecific()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public AdministrativeRole getRole()
    {
        return role;
    }


    /**
     * {@inheritDoc}
     */
    public AdministrativePoint getParent()
    {
        return parent;
    }
}
