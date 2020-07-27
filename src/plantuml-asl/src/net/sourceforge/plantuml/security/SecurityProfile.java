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
package net.sourceforge.plantuml.security;

/**
 * There are 4 different security profile defined.
 * <p>
 * The security profile to be used is set at the launch of PlantUML and cannot
 * be changed by users. The security profile defines what an instance of
 * PlantUML is allowed to do:<br>
 * - access some local file <br>
 * - connection to some remote URL <br>
 * - print some technical information to the users.
 * <p>
 * <p>
 * The security profile is defined: <br>
 * - either by an environment variable<br>
 * - or an option at command line
 * <p>
 * There is also a default value, which is LEGACY in this current
 * implementation.
 * 
 */
public enum SecurityProfile {

	/**
	 * Running in SANDBOX mode is completely secure. No local file can be read
	 * (except dot executable) No remote URL access can be used No technical
	 * information are print to users.
	 * <p>
	 * This mode is defined for test and debug, since it's not very useful for
	 * users. However, you can use it if you need to.
	 */
	SANDBOX,

	/**
	 * 
	 */
	ALLOWLIST,

	/**
	 * This mode is designed for PlantUML running in a web server.
	 * 
	 */
	INTERNET,

	/**
	 * This mode reproduce old PlantUML version behaviour.
	 * <p>
	 * Right now, this is the default Security Profile but this will be removed from
	 * future version because it is now full secure, especially on Internet server.
	 */
	LEGACY,

	/**
	 * Running in UNSECURE mode means that PlantUML can access to any local file and
	 * can connect to any URL.
	 * <p>
	 * Some technical information (file path, Java version) are also printed in some
	 * error messages. This is not an issue if you are running PlantUML locally. But
	 * you should not use this mode if PlantUML is running on some server,
	 * especially if the server is accessible from Internet.
	 */
	UNSECURE;

	/**
	 * Initialize the default value.
	 * <p>
	 * It search in some config variable if the user has defined a some default
	 * value.
	 * 
	 * @return the value
	 */
	static SecurityProfile init() {
		final String env = SecurityUtils.getenv("PLANTUML_SECURITY_PROFILE");
		if ("SANDBOX".equalsIgnoreCase(env)) {
			return SANDBOX;
		} else if ("ALLOWLIST".equalsIgnoreCase(env)) {
			return ALLOWLIST;
		} else if ("INTERNET".equalsIgnoreCase(env)) {
			return INTERNET;
		} else if ("UNSECURE".equalsIgnoreCase(env)) {
			return UNSECURE;
		}
		return LEGACY;
	}

	/**
	 * A Human understandable description.
	 */
	public String longDescription() {
		switch (this) {
		case SANDBOX:
			return "This is completely safe: no access to local files or to distant URL.";
		case ALLOWLIST:
			return "Some local ressource may be accessible.";
		case INTERNET:
			return "<i>Mode designed for server connected to Internet.";
		case LEGACY:
			return "<b>Warning: this mode will be removed in future version";
		case UNSECURE:
			return "<b>Make sure that this server is not accessible from Internet";
		}
		return "<i>This is completely safe: no access on local files or on distant URL.";
	}

	/**
	 * Retrieve the timeout for URL.
	 */
	public long getTimeout() {
		switch (this) {
		case SANDBOX:
			return 1000L;
		case ALLOWLIST:
			return 1000L * 60 * 5;
		case INTERNET:
			return 1000L * 10;
		case LEGACY:
			return 1000L * 60;
		case UNSECURE:
			return 1000L * 60 * 5;
		}
		throw new AssertionError();
	}

}
