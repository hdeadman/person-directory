/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apereo.services.persondir.support;

import org.apereo.services.persondir.AbstractPersonAttributeDaoTest;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RunWith(JUnit4.class)
public class TomlLdapPersonAttributeDaoTest extends AbstractPersonAttributeDaoTest {

    private final TomlLdapPersonAttributeDao dao;


    public TomlLdapPersonAttributeDaoTest() throws Exception {
        final Resource tomlConfigFile = new FileSystemResource("src/test/resources/ldap.toml");
        this.dao = new TomlLdapPersonAttributeDao(tomlConfigFile);
    }

    @Test
    public void testTomlConfigFileStuffedIntoDao() throws Exception {


        assertNotNull(dao.getBaseDN());
        assertNotNull(dao.getQueryAttributeMapping());
        assertNotNull(dao.getQueryType());
        assertNotNull(dao.getResultAttributeMapping());
        assertNotNull(dao.getContextSource());
    }


    @Override
    protected IPersonAttributeDao getPersonAttributeDaoInstance() {
        return this.dao;
    }
}