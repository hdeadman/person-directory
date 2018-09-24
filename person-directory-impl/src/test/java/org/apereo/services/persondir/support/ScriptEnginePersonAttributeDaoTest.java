package org.apereo.services.persondir.support;

import org.apache.commons.io.IOUtils;
import org.apereo.services.persondir.AbstractPersonAttributeDaoTest;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.IPersonAttributes;
import org.apereo.services.persondir.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/annotationContext.xml"})
public class ScriptEnginePersonAttributeDaoTest extends AbstractPersonAttributeDaoTest {

    @Autowired
    private ApplicationContext applicationContext;

    private ScriptEnginePersonAttributeDao dao;

    public ScriptEnginePersonAttributeDaoTest() {
        this.dao = new ScriptEnginePersonAttributeDao();
    }

    @Override
    protected IPersonAttributeDao getPersonAttributeDaoInstance() {
        return this.dao;
    }

    @Test
    public void testGetAttributesWithJavascript() {
        this.dao.setScriptFile("SampleScriptedJavascriptPersonAttributeDao.js");
        final IPersonAttributes person = this.dao.getPerson("testuser");
        assertNotNull(person);
        assertEquals(person.getName(), "testuser");
        assertEquals(person.getAttributes().size(), 4);
    }

    @Test
    public void testGetAttributesWithGroovy() {
        this.dao.setScriptFile("SampleScriptedGroovyPersonAttributeDao.groovy");
        final IPersonAttributes person = this.dao.getPerson("testuser");
        assertNotNull(person);
        assertEquals(person.getName(), "testuser");
        assertEquals(person.getAttributes().size(), 4);
    }

    @Test
    public void testGetAttributesWithGroovyString() throws IOException {
        this.dao.setScriptFile(IOUtils.toString(applicationContext.getResource("file:./src/test/resources/SampleScriptedGroovyPersonAttributeDao.groovy").getInputStream(), StandardCharsets.UTF_8));
        this.dao.setEngineName("groovy");
        final IPersonAttributes person = this.dao.getPerson("testuser");
        assertNotNull(person);
        assertEquals(person.getName(), "testuser");
        assertEquals(person.getAttributes().size(), 4);
    }

    @Test
    public void testGetMultiValuedAttributesWithGroovy() {
        this.dao.setScriptFile("SampleScriptedGroovyPersonAttributeDao.groovy");
        final Map<String, List<Object>> query = new LinkedHashMap<>();
        query.put("username", Util.list("testuser"));
        final Set<IPersonAttributes> personSet = this.dao.getPeopleWithMultivaluedAttributes(query);
        assertNotNull(personSet);
        assertEquals(personSet.size(),1);
        assertEquals(((IPersonAttributes)personSet.iterator().next()).getName(),"testuser");
    }
}
