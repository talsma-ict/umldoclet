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
package net.sourceforge.plantuml.security.authentication.token;

import net.sourceforge.plantuml.security.authentication.SecurityAccessInterceptor;
import net.sourceforge.plantuml.security.authentication.SecurityAuthentication;

import java.net.URLConnection;
import java.util.Map;

/**
 * Applies from {@link SecurityAuthentication} data plain token authentication access headers. This is a raw header
 * injection with static data.
 *
 * @author Aljoscha Rittner
 */
public class TokenAuthAccessInterceptor implements SecurityAccessInterceptor {

	/**
	 * Applies from {@link SecurityAuthentication} data plain token authentication access headers.
	 * <p>
	 * Expects headers.* key value pairs to pass it directly to the connection.
	 *
	 * @param authentication the determined authentication data to authorize for the endpoint access
	 * @param connection     the connection to the endpoint
	 */
	@Override
	public void apply(SecurityAuthentication authentication, URLConnection connection) {

		for (Map.Entry<String, Object> header : authentication.getTokens().entrySet() ) {
			if (!header.getKey().startsWith("headers.") || header.getValue() == null) {
				continue;
			}
			String key = header.getKey().substring(8);
			String value = header.getValue().toString();
			connection.setRequestProperty(key, value);
		}
	}
}
