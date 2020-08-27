/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.version;

import java.util.Date;

import net.sourceforge.plantuml.security.SURL;

public class Version {

	private static final int MAJOR_SEPARATOR = 1000000;

	public static int version() {
		return 1202016;
	}

	public static int versionPatched() {
		if (beta() != 0) {
			return version() + 1;
		}
		return version();
	}

	public static String versionString() {
		if (beta() != 0) {
			return dotted(version() + 1) + "beta" + beta();
		}
		return dotted(version());
	}

	public static String fullDescription() {
		return "PlantUML version " + Version.versionString() + " (" + Version.compileTimeString() + ")";
	}

	private static String dotted(int nb) {
		final String minor = "" + nb % MAJOR_SEPARATOR;
		final String major = "" + nb / MAJOR_SEPARATOR;
		return major + "." + minor.substring(0, 4) + "." + minor.substring(4);
	}

	public static String versionString(int size) {
		final StringBuilder sb = new StringBuilder(versionString());
		while (sb.length() < size) {
			sb.append(' ');
		}
		return sb.toString();
	}

	public static int beta() {
		final int beta = 0;
		return beta;
	}

	public static String etag() {
		return Integer.toString(version() % MAJOR_SEPARATOR - 201670, 36) + Integer.toString(beta(), 36);
	}

	public static String turningId() {
		return etag();
	}

	public static long compileTime() {
		return 1598214043932L;
	}

	public static String compileTimeString() {
		if (beta() != 0) {
			return "Unknown compile time";
		}
		return new Date(Version.compileTime()).toString();
	}

	public static String getJarPath() {
		try {
			final ClassLoader loader = Version.class.getClassLoader();
			if (loader == null) {
				return "No ClassLoader?";
			}
			final SURL url = SURL.create(loader.getResource("net/sourceforge/plantuml/version/Version.class"));
			if (url == null) {
				return "No URL?";
			}
			String fullpath = url.toString();
			fullpath = fullpath.replaceAll("net/sourceforge/plantuml/version/Version\\.class", "");
			return fullpath;
		} catch (Throwable t) {
			t.printStackTrace();
			return t.toString();
		}
	}

}
