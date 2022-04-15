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
package net.sourceforge.plantuml.security.authentication.basicauth;

import net.sourceforge.plantuml.security.authentication.SecurityAuthentication;
import net.sourceforge.plantuml.security.authentication.SecurityAuthorizeManager;
import net.sourceforge.plantuml.security.authentication.SecurityCredentials;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link BasicAuthAuthorizeManager} creates the authentication on the fly from the credentials without
 * any access to other services.
 *
 * @author Aljoscha Rittner
 */
public class BasicAuthAuthorizeManager implements SecurityAuthorizeManager {
	@Override
	public SecurityAuthentication create(SecurityCredentials credentials) {
		String type = credentials.getType();
		String identifier = credentials.getIdentifier();
		char[] secret = credentials.getSecret();
		Map<String, Object> tokens = new HashMap<String, Object>();
		tokens.put("identifier", identifier);
		if (secret != null) {
			tokens.put("secret", secret.clone());
		}
		return new SecurityAuthentication(type, tokens);
	}
}
