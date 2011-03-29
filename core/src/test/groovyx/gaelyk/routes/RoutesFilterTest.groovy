package groovyx.gaelyk.routes

import javax.servlet.FilterConfig
import javax.servlet.ServletContext
import groovyx.gaelyk.GaelykBindingEnhancer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.FilterChain
import javax.servlet.RequestDispatcher
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * 
 * @author Guillaume Laforge
 */
class RoutesFilterTest extends GroovyTestCase {

    Binding binding

    def filter = new RoutesFilter()

    protected void setUp() {
        super.setUp()

        // sets the environment to "Development"
        System.setProperty("environment", "Development")

        binding = new Binding()
        GaelykBindingEnhancer.bind(binding)


        filter.init(new FilterConfig() {
            String getFilterName() { "RoutesFilter" }

            String getInitParameter(String s) {
                if (s == 'routes.location') {
                    'src/test/groovyx/gaelyk/routes/routes.sample'
                }
            }

            Enumeration getInitParameterNames() {
                ['routes.location'] as Enumeration
            }

            ServletContext getServletContext() {
                [:] as ServletContext
            }

        })

        filter.loadRoutes()

        assert filter.routes
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testNoRouteFound() {
        def request = [
                getRequestURI: { -> "/nowhere" },
                getMethod: { -> "GET" }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chained = false
        def chain = [
                doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }
        ] as FilterChain

        filter.doFilter(request, response, chain)

        assert chained
    }

    void testIgnoredRoute() {
        def request = [
                getRequestURI: { -> "/ignore" },
                getMethod: { -> "GET" }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chained = false
        def chain = [
                doFilter: { HttpServletRequest req, HttpServletResponse resp -> chained = true }
        ] as FilterChain

        filter.doFilter(request, response, chain)

        assert chained
    }

    void testRedirectRoute() {
        def request = [
                getRequestURI: { -> "/redirect" },
                getMethod: { -> "GET" }
        ] as HttpServletRequest

        def redirected = ""

        def response = [
                sendRedirect: { String where -> redirected = where }
        ] as HttpServletResponse

        def chain = [:] as FilterChain

        filter.doFilter(request, response, chain)

        assert redirected == "/elsewhere.gtpl"
    }

    void testRouteWithANamespace() {
        def forwarded = false
        def dispatched = ""

        def dispatcher = [
                forward: { ServletRequest req, ServletResponse resp -> forwarded = true }
        ] as RequestDispatcher

        def request = [
                getRequestURI: { -> "/acme/home" },
                getQueryString: { -> "" },
                getMethod: { -> "GET" },
                getRequestDispatcher: { String s -> dispatched = s; return dispatcher }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chain = [:] as FilterChain

        filter.doFilter(request, response, chain)

        assert forwarded
        assert dispatched == "/customer.groovy?cust=acme"
    }

    void testNormalRoute() {
        def forwarded = false
        def dispatched = ""

        def dispatcher = [
                forward: { ServletRequest req, ServletResponse resp -> forwarded = true }
        ] as RequestDispatcher

        def request = [
                getRequestURI: { -> "/somewhere" },
                getQueryString: { -> "" },
                getMethod: { -> "GET" },
                getRequestDispatcher: { String s -> dispatched = s; return dispatcher }
        ] as HttpServletRequest

        def response = [:] as HttpServletResponse

        def chain = [:] as FilterChain

        filter.doFilter(request, response, chain)

        assert forwarded
        assert dispatched == "/somewhere.groovy"
    }

}
