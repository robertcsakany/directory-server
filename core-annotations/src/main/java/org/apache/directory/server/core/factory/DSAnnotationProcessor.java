/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.server.core.factory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.ApplyLdifs;
import org.apache.directory.server.core.annotations.ContextEntry;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreateIndex;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.entry.DefaultServerEntry;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Helper class used to create a DS from the annotations
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DSAnnotationProcessor
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( DSAnnotationProcessor.class );

    /**
     * Create a DirectoryService from a Unit test annotation
     *
     * @param description The annotations containing the info from which we will create the DS
     * @return A valid DS
     */
    public static DirectoryService getDirectoryService( Description description )
    {
        try
        {
            CreateDS dsBuilder = description.getAnnotation( CreateDS.class );
            
            if ( dsBuilder != null )
            {
                LOG.debug( "Starting DS {}...", dsBuilder.name() );
                Class<?> factory = dsBuilder.factory();
                DirectoryServiceFactory dsf = ( DirectoryServiceFactory ) factory.newInstance();
                
                DirectoryService service = dsf.getDirectoryService();
                service.setAccessControlEnabled( dsBuilder.enableAccessControl() );
                service.setAllowAnonymousAccess( dsBuilder.allowAnonAccess() );
                
                dsf.init( dsBuilder.name() );
                
                return service;
            }
            else
            {
                LOG.debug( "No {} DS.", description.getDisplayName() );
                return null;
            }
        }
        catch ( Exception e )
        {
            return null;
        }
    }
    
    
    /**
     * Create a DirectoryService from an annotation. The @CreateDS annotation must
     * be associated with either the method or the encapsulating class. We will first
     * try to get the annotation from the method, and if there is none, then we try
     * at the class level. 
     *
     * @return A valid DS
     */
    public static DirectoryService getDirectoryService() throws Exception
    {
        CreateDS dsBuilder = null;
        
        // Get the caller by inspecting the stackTrace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // Get the enclosing class
        Class<?> classCaller = Class.forName( stackTrace[2].getClassName() );
        
        // Get the current method
        String methodCaller = stackTrace[2].getMethodName();
        
        // Check if we have any annotation associated with the method
        Method[] methods = classCaller.getMethods();
        
        for ( Method method : methods )
        {
            if ( methodCaller.equals( method.getName() ) )
            {
                dsBuilder = method.getAnnotation( CreateDS.class );
                
                if ( dsBuilder != null )
                {
                    break;
                }
            }
        }

        // No : look at the class level
        if ( dsBuilder == null )
        {
            dsBuilder = classCaller.getAnnotation( CreateDS.class );
        }
        
        // Ok, we have found a CreateDS annotation. Process it now.
        try
        {
            LOG.debug( "Starting DS {}...", dsBuilder.name() );
            Class<?> factory = dsBuilder.factory();
            DirectoryServiceFactory dsf = ( DirectoryServiceFactory ) factory.newInstance();
            
            DirectoryService service = dsf.getDirectoryService();
            service.setAccessControlEnabled( dsBuilder.enableAccessControl() );
            service.setAllowAnonymousAccess( dsBuilder.allowAnonAccess() );
            
            dsf.init( dsBuilder.name() );
            
            // Process the Partition, if any.
            for ( CreatePartition createPartition : dsBuilder.partitions() )
            {
                // Create the partition
                Partition partition = new JdbmPartition();
                partition.setId( createPartition.name() );
                partition.setSuffix( createPartition.suffix() );
                partition.setSchemaManager( service.getSchemaManager() );
                ( ( JdbmPartition ) partition ).setCacheSize( createPartition.cacheSize() );
                ( ( JdbmPartition ) partition ).setPartitionDir( 
                    new File( service.getWorkingDirectory(), createPartition.name() ) );
                
                // Process the indexes if any
                CreateIndex[] indexes = createPartition.indexes();

                for ( CreateIndex createIndex : indexes )
                {
                    Index<String, ServerEntry> index = new JdbmIndex<String, ServerEntry>( createIndex.attribute() );
                    index.setCacheSize( createIndex.cacheSize() );
                    
                    ( ( JdbmPartition ) partition ).addIndexedAttributes( index );
                }
                
                partition.setSchemaManager( service.getSchemaManager() );
                
                // Inject the partition into the DirectoryService
                service.addPartition( partition );
                
                // Last, process the context entry
                ContextEntry contextEntry = createPartition.contextEntry();
                
                if ( contextEntry != null )
                {
                    injectEntries( service, contextEntry.entryLdif() );
                }
            }
            
            return service;
        }
        catch ( Exception e )
        {
            return null;
        }
    }
    
    
    /**
     * Create a DirectoryService using the class annotation
     * TODO getClassDirectoryService.
     *
     * @param clazz
     * @return
     */
    public static DirectoryService getClassDirectoryService( Class<?> clazz )
    {
        try
        {
            CreateDS dsBuilder = clazz.getAnnotation( CreateDS.class );
            
            if ( dsBuilder != null )
            {
                LOG.debug( "Starting the {} DS...", clazz.getName() );
                Class<?> factory = dsBuilder.factory();
                DirectoryServiceFactory dsf = ( DirectoryServiceFactory ) factory.newInstance();
                dsf.init( dsBuilder.name() );
                
                DirectoryService service = dsf.getDirectoryService();
                
                return service;
            }
            else
            {
                LOG.debug( "No {} DS.", clazz.getName() );
                return null;
            }
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    
    /**
     * injects an LDIF entry in the given DirectoryService
     * 
     * @param entry the LdifEntry to be injected
     * @param service the DirectoryService
     * @throws Exception
     */
    private static void injectEntry( LdifEntry entry, DirectoryService service ) throws Exception
    {
        if ( entry.isChangeAdd() )
        {
            service.getAdminSession().add( new DefaultServerEntry( service.getSchemaManager(), entry.getEntry() ) );
        }
        else if ( entry.isChangeModify() )
        {
            service.getAdminSession().modify( entry.getDn(), entry.getModificationItems() );
        }
        else
        {
            String message = "Unsupported changetype found in LDIF: " + entry.getChangeType();
            throw new NamingException( message );
        }
    }

    
    /**
     * injects the LDIF entries present in a LDIF file
     * 
     * @param service the DirectoryService 
     * @param ldifFiles the array of LDIF file names (only )
     * @throws Exception
     */
    public static void injectLdifFiles( Class<?> clazz, DirectoryService service, String[] ldifFiles ) throws Exception
    {
        if ( ( ldifFiles != null ) && ( ldifFiles.length > 0 ) )
        {
            for ( String ldifFile : ldifFiles )
            {
                try
                {
                    LdifReader ldifReader = new LdifReader( clazz.getClassLoader().getResourceAsStream( ldifFile ) ); 
    
                    for ( LdifEntry entry : ldifReader )
                    {
                        injectEntry( entry, service );
                    }
                    
                    ldifReader.close();
                }
                catch ( Exception e )
                {
                    LOG.error( "Cannot inject the following entry : {}. Error : {}.", ldifFile, e.getMessage() );
                }
            }
        }
    }
    
    
    /**
     * Inject an ldif String into the server. DN must be relative to the
     * root.
     *
     * @param service the directory service to use 
     * @param ldif the ldif containing entries to add to the server.
     * @throws NamingException if there is a problem adding the entries from the LDIF
     */
    public static void injectEntries( DirectoryService service, String ldif ) throws Exception
    {
        LdifReader reader = new LdifReader();
        List<LdifEntry> entries = reader.parseLdif( ldif );

        for ( LdifEntry entry : entries )
        {
            injectEntry( entry, service );
        }

        // And close the reader
        reader.close();
    }

    
    /**
     * Apply the LDIF entries to the given service
     */
    public static void applyLdifs( Description desc, DirectoryService service ) throws Exception
    {
        if( desc == null )
        {
            return;
        }
        
        ApplyLdifFiles applyLdifFiles = desc.getAnnotation( ApplyLdifFiles.class );

        if ( applyLdifFiles != null )
        {
            LOG.debug( "Applying {} to {}", applyLdifFiles.value(), desc.getDisplayName() );
            injectLdifFiles( desc.getClass(), service, applyLdifFiles.value() );
        }
        
        ApplyLdifs applyLdifs = desc.getAnnotation( ApplyLdifs.class ); 
        
        if ( ( applyLdifs != null ) && ( applyLdifs.value() != null ) )
        {
            String[] ldifs = applyLdifs.value();

            for ( String s : ldifs )
            {
                LOG.debug( "Applying {} to {}", ldifs, desc.getDisplayName() );
                injectEntries( service, s );
            }
        }
    }

}
