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
package net.sourceforge.plantuml.security.authentication;

import java.util.Arrays;
import java.util.Map;

/**
 * The authentication to access an endpoint. This information will be generated by a SecurityAuthenticationInterceptor.
 *
 * @author Aljoscha Rittner
 */
public class SecurityAuthentication implements SecurityCredentialsContainer {

	/**
	 * Type of authentication (e.g. basicauth, oauth2)
	 */
	private final String type;

	/**
	 * Characteristic of an authentication (e.g. openId). Can be null.<p>
	 * This kind of information is typically not needed. Useful for debugging purpose.
	 */
	private final String shape;

	/**
	 * Origin authorization process (e.g. client_credentials.<p>
	 * This kind of information is typically not needed. Useful for debugging purpose.
	 */
	private final String grantType;

	/**
	 * A map of needed data tokens to authenticate access to an endpoint.
	 */
	private final Map<String, Object> tokens;

	public SecurityAuthentication(String type, Map<String, Object> tokens) {
		this(type, null, null, tokens);
	}

	public SecurityAuthentication(String type, String shape, String grantType, Map<String, Object> tokens) {
		this.type = type;
		this.shape = shape;
		this.grantType = grantType;
		this.tokens = tokens;
	}

	public String getType() {
		return type;
	}

	public String getShape() {
		return shape;
	}

	public String getGrantType() {
		return grantType;
	}

	/**
	 * Requests the state of this authentication.
	 *
	 * @return true, if we have no authentication.
	 */
	public boolean isPublic() {
		return "public".equalsIgnoreCase(type) && (tokens == null || tokens.isEmpty());
	}

	public Map<String, Object> getTokens() {
		return tokens;
	}

	@Override
	public void eraseCredentials() {
		if (tokens != null && !tokens.isEmpty()) {
			for (Object tokenVal : tokens.values()) {
				if (tokenVal instanceof char[]) {
					Arrays.fill((char[]) tokenVal, '*');
				}
			}
			tokens.clear();
		}
	}
}
