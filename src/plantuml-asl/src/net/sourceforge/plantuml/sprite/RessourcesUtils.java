/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.sprite;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.version.Version;

public class RessourcesUtils {

	public static Set<String> getJarFile(String path, boolean folder) throws IOException {
		if (path.startsWith("/") || path.endsWith("/")) {
			throw new IllegalArgumentException();
		}
		final String protocol = getProtocol();
		if ("file".equals(protocol)) {
			final URL local = Version.class.getClassLoader().getResource(path);
			try {
				return listEntry(new SFile(local.toURI()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;
			}
		}
		if ("jar".equals(protocol)) {
			final String classFile = Version.class.getName().replace(".", "/") + ".class";
			final URL versionURL = Version.class.getClassLoader().getResource(classFile);
			final String jarPath = versionURL.getPath().substring(5, versionURL.getPath().indexOf("!"));
			if (folder) {
				return listFolders(new JarFile(URLDecoder.decode(jarPath, UTF_8.name())), path + "/");
			} else {
				return listFiles(new JarFile(URLDecoder.decode(jarPath, UTF_8.name())), path + "/");

			}
		}
		return Collections.<String>emptySet();
	}

	private static String getProtocol() {
		final URL resource = Version.class.getClassLoader().getResource("net/sourceforge/plantuml/version/logo.png");
		return resource.getProtocol();
	}

	private static Set<String> listFiles(JarFile jarFile, String path) {
		final Enumeration<JarEntry> entries = jarFile.entries();
		final Set<String> result = new TreeSet<>();
		while (entries.hasMoreElements()) {
			final String name = entries.nextElement().getName();
			if (name.startsWith(path)) {
				result.add(name.substring(path.length()));
			}
		}
		return result;
	}

	private static Set<String> listFolders(JarFile jarFile, String path) {
		final Enumeration<JarEntry> entries = jarFile.entries();
		final Set<String> result = new TreeSet<>();
		while (entries.hasMoreElements()) {
			final String name = entries.nextElement().getName();
			if (name.startsWith(path)) {
				final String folder = name.substring(path.length());
				final int x = folder.indexOf('/');
				if (x != -1) {
					result.add(folder.substring(0, x));
				}
			}
		}
		return result;
	}

	private static Set<String> listEntry(SFile dir) {
		final Set<String> result = new TreeSet<>();
		for (String n : dir.list()) {
			result.add(n);
		}
		return result;
	}

}
