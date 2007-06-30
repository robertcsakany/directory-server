/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

/**
 * This option specifies the maximum size datagram that the client
 * should be prepared to reassemble.  The size is specified as a 16-bit
 * unsigned integer.  The minimum value legal value is 576.
 * 
 * The code for this option is 22, and its length is 2.
 */
package org.apache.directory.server.dhcp.options.perhost;


import java.nio.ByteBuffer;

import org.apache.directory.server.dhcp.options.DhcpOption;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MaximumDatagramSize extends DhcpOption
{
    private byte[] maximumDatagramSize;


    /**
     * Creates a new instance of MaximumDatagramSize.
     *
     * @param maximumDatagramSize
     */
    public MaximumDatagramSize( byte[] maximumDatagramSize )
    {
        super( 22, 2 );
        this.maximumDatagramSize = maximumDatagramSize;
    }


    protected void valueToByteBuffer( ByteBuffer out )
    {
        out.put( maximumDatagramSize );
    }
}
