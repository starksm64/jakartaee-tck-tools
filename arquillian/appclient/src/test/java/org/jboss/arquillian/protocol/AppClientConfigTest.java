package org.jboss.arquillian.protocol;

import org.jboss.arquillian.config.descriptor.api.DefaultProtocolDef;
import org.jboss.arquillian.config.impl.extension.ConfigurationRegistrar;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.container.test.impl.MapObject;
import org.jboss.arquillian.protocol.appclient.AppClientProtocolConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AppClientConfigTest {
    @Test
    public void testFile() throws Exception {
        File dir = new File(".");
        System.out.println(dir.exists());
        System.out.println(dir.isDirectory());
        Process ls = Runtime.getRuntime().exec("/bin/ls", null, dir);
        int exit = ls.waitFor();
        System.out.println(exit);
    }
    @Test
    public void testConfig1() throws Exception {
        System.setProperty(ConfigurationRegistrar.ARQUILLIAN_XML_PROPERTY, "appclient1-arquillian.xml");
        ConfigurationRegistrar registrar = new ConfigurationRegistrar();
        ArquillianDescriptor descriptor = registrar.loadConfiguration();
        Assert.assertNotNull(descriptor);
        Assert.assertNotNull(descriptor.defaultProtocol("appclient"));
        String type = descriptor.defaultProtocol("appclient").getType();
        Assert.assertEquals("appclient", type);

        Map<String, String> props = descriptor.defaultProtocol("appclient").getProperties();

        AppClientProtocolConfiguration config = new AppClientProtocolConfiguration();
        MapObject.populate(config, props);

        // Raw strings
        Assert.assertEquals("-p;/home/jakartaeetck/bin/xml/../../tmp/tstest.jte", config.getClientCmdLineString());
        String expectedEnv = "JAVA_OPTS=-Djboss.modules.system.pkgs=com.sun.ts.lib,com.sun.javatest;CLASSPATH=${project.build.directory}/appclient/javatest.jar:${project.build.directory}/appclient/libutil.jar:${project.build.directory}/appclient/libcommon.jar";
        Assert.assertEquals(expectedEnv, config.getClientEnvString());
        Assert.assertTrue(config.isRunClient());
        Assert.assertEquals(".", config.getClientDir());

        // Parsed strings
        String[] args = config.clientCmdLineAsArray();
        Assert.assertEquals(2, args.length);
        Assert.assertEquals("-p", args[0]);
        Assert.assertEquals("/home/jakartaeetck/bin/xml/../../tmp/tstest.jte", args[1]);

        String[] envp = config.clientEnvAsArray();
        Assert.assertEquals(4, envp.length);
        Assert.assertEquals("JAVA_OPTS", envp[0]);
        Assert.assertEquals("-Djboss.modules.system.pkgs=com.sun.ts.lib,com.sun.javatest", envp[1]);
        Assert.assertEquals("CLASSPATH", envp[2]);
        String expectedCP = "${project.build.directory}/appclient/javatest.jar:${project.build.directory}/appclient/libutil.jar:${project.build.directory}/appclient/libcommon.jar";
        Assert.assertEquals(expectedCP, envp[3]);
        File expectedDir = new File(".");
        Assert.assertEquals(expectedDir, config.clientDirAsFile());
    }
}
