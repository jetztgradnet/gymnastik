/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package groovyx.gaelyk

import groovyx.gaelyk.logging.LoggerAccessor

/**
 * Class responsible for adding adding Google App Engine related services into the binding of Groovlets and Templates.
 *
 * @author Marcel Overdijk
 * @author Guillaume Laforge
 * @author Benjamin Muschko
 */
class GaelykBindingEnhancer {

    /**
     * Bind the various Google App Engine services and variables
     *
     * @param binding Binding in which to bind the GAE services and variables
     */
    static void bind(Binding binding) {
        // Tells whether the application is running in local development mode
        // or is deployed on Google's cloud
        binding.setVariable("localMode", System.getProperty("environment")?.equalsIgnoreCase("Development"))

        // Since GAE SDK 1.3.3.1: special system properties
        binding.setVariable("app", [
                env: [
                        name: System.getProperty("environment"),
                        version: System.getProperty("version"),
                ],
                gaelyk: [
                        version: '0.6.1'
                ],
                id: System.getProperty("applicationId"),
                version: System.getProperty("applicationVersion")
        ])

        // Add a logger variable to easily access any logger
        binding.setVariable("logger", new LoggerAccessor())
    }
}
