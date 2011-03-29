package groovyx.gaelyk.cache

import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig
import com.google.appengine.api.memcache.MemcacheServiceFactory
import groovyx.gaelyk.GaelykCategory
import com.google.appengine.api.memcache.Expiration
import com.google.appengine.api.memcache.MemcacheService.SetPolicy

/**
 * Memcache enhancements tests
 *
 * @author Guillaume Laforge
 */
class MemcacheCategoryMethodsTest extends GroovyTestCase {
    private LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalMemcacheServiceTestConfig(),
    )

    protected void setUp() {
        super.setUp()
        helper.setUp()
    }

    protected void tearDown() {
        helper.tearDown()
        super.tearDown()
    }

    void testSubscriptNotationWorksWithGStringKeys() {
        def memcache = MemcacheServiceFactory.memcacheService

        use(GaelykCategory) {
            def number = "1234"
            memcache["key-${number}"] = 'some content'

            assert memcache["key-1234"] == 'some content'
            assert memcache["key-${number}"] == 'some content'
            assert memcache["key-${number}".toString()] == 'some content'
        }
    }

    void testOverrideGetPutWithGStrings() {
        def memcache = MemcacheServiceFactory.memcacheService

        def letter = 'A'
        memcache.put("letter-${letter}", 'hello')

        assert memcache.get("letter-${letter}") == 'hello'

        shouldFail {
            assert memcache.get("letter-A") == 'hello'
        }
    }

    void testOverridePutWithGStringKeysAndWithExpirationAndSetPolicy() {
        def memcache = MemcacheServiceFactory.memcacheService

        use(GaelykCategory) {
            def name = 'Guillaume'
            memcache.put("lastname-of-${name}-is", 'Laforge')

            assert memcache.get('lastname-of-Guillaume-is') == 'Laforge'
            assert memcache.get("lastname-of-${'Guillaume'}-is") == 'Laforge'

            memcache.put("age-of-${name}", 33, Expiration.byDeltaMillis(1000))
            assert memcache['age-of-Guillaume'] == 33

            assert 'age-of-Guillaume' in memcache

            memcache.put("sex-of-${name}", 'Male', Expiration.byDeltaMillis(1000), SetPolicy.SET_ALWAYS)
            assert memcache['sex-of-Guillaume'] == 'Male'
        }
    }

    void testPutAtObjectMethod() {
        def memcache = MemcacheServiceFactory.memcacheService

        use(GaelykCategory) {
            def now = new Date()
            memcache[now] = "aujourd'hui"

            assert now in memcache
            assert memcache[now] == "aujourd'hui"
        }
    }

    void testSetAndGetStringKeyMethod() {
        def memcache = MemcacheServiceFactory.memcacheService

        use(GaelykCategory) {
            memcache.number = 1234

            assert 'number' in memcache
            assert memcache.number == 1234
        }

    }
}
