/*
 * Copyright 2013 The Sculptor Project Team, including the original 
 * author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sculptor.generator.mwe2;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.eclipse.xtext.mwe.RuntimeResourceSetInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * This variation of {@link RuntimeResourceSetInitializer} overrides
 * {@link #getClassPathEntries()} to return a list of classpath entries which is
 * retrieved from the current threads context classloader.
 * <p>
 * <b>Only {@link URLClassLoader} is supported!</b>
 */
public class ContextClassLoaderAwareRuntimeResourceSetInitializer extends RuntimeResourceSetInitializer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContextClassLoaderAwareRuntimeResourceSetInitializer.class);

	@Override
	public List<String> getClassPathEntries() {
		List<String> classPathEntries;
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader instanceof URLClassLoader) {

			// adds class path entries from current threads context classloader.
			classPathEntries = Lists.newArrayList();
			addClassPathEntries(contextClassLoader, classPathEntries);
			LOGGER.debug("Classpath entries from thread context loader: {}", classPathEntries);
		} else {

			// Create a list of class path entries from the Java system property
			// "java.class.path"
			classPathEntries = super.getClassPathEntries();
			LOGGER.debug("Classpath entries from system property 'java.class.path': {}", classPathEntries);
		}
		return classPathEntries;
	}

	/**
	 * Adds class path entries from given classloader hierarchy (recursive).
	 */
	private void addClassPathEntries(ClassLoader contextClassLoader, List<String> classPathEntries) {
		for (URL url : ((URLClassLoader) contextClassLoader).getURLs()) {
			String path = url.getPath().trim().toLowerCase(); 
			if (path.endsWith("/") || path.endsWith(".jar")) {
				classPathEntries.add(url.getFile());
			}
		}

		// Add class path entries from parent classloader
		if (contextClassLoader.getParent() instanceof URLClassLoader) {
			addClassPathEntries(contextClassLoader.getParent(), classPathEntries);
		}
	}

}
