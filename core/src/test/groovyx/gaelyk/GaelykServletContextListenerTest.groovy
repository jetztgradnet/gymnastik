package groovyx.gaelyk

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContext
import groovyx.gaelyk.plugins.PluginsHandler

/**
 * @author Guillaume Laforge
 */
class GaelykServletContextListenerTest extends GroovyTestCase {

    protected void setUp() {
        super.setUp()

        // sets the environment to "Development"
        System.setProperty("environment", "Development")
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testContextListener() {
        boolean called = false
        PluginsHandler.instance.scriptContent = { called = true }

        def context = [:] as ServletContext
        def event = new ServletContextEvent(context)

        def listener = new GaelykServletContextListener()

        listener.contextInitialized event

        assert called
    }
}
