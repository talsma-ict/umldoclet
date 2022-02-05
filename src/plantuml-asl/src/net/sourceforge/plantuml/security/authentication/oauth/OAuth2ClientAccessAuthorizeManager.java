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
package net.sourceforge.plantuml.security.authentication.oauth;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.security.authentication.SecurityAuthentication;
import net.sourceforge.plantuml.security.authentication.SecurityCredentials;
import net.sourceforge.plantuml.security.authentication.basicauth.BasicAuthAuthorizeManager;

import java.util.Arrays;

/**
 * Authorize the principal (from {@link SecurityCredentials} and creates a {@link SecurityAuthentication} object with a
 * bearer token secret.
 *
 * @author Aljoscha Rittner
 */
public class OAuth2ClientAccessAuthorizeManager extends AbstractOAuth2AccessAuthorizeManager {

	/**
	 * Basic Auth manager to access the token service with authorization.
	 */
	private final BasicAuthAuthorizeManager basicAuthManager = new BasicAuthAuthorizeManager();

	@Override
	public SecurityAuthentication create(SecurityCredentials credentials) {
		String grantType = credentials.getPropertyStr("grantType");
		String requestScope = credentials.getPropertyStr("scope");
		String accessTokenUri = credentials.getPropertyStr("accessTokenUri");
		String tokenType = credentials.getPropertyStr("tokenType");

		// Extra BasicAuth data to access the token service endpoint (if needed)
		String identifier = credentials.getPropertyStr("credentials.identifier");
		char[] secret = credentials.getPropertyChars("credentials.secret");

		try {
			SURL tokenService = SURL.create(accessTokenUri);

			StringBuilder content = new StringBuilder()
					.append("grant_type=")
					.append(urlEncode(grantType));
			if (StringUtils.isNotEmpty(requestScope)) {
				content.append("&scope=").append(urlEncode(requestScope));
			}

			SecurityAuthentication basicAuth;
			if (identifier != null) {
				// OAuth2 with extra Endpoint BasicAuth credentials
				basicAuth = basicAuthManager.create(
						SecurityCredentials.basicAuth(identifier, secret));
				// We need to add the principal to the form
				content.append("&client_id").append(urlEncode(credentials.getIdentifier()))
						.append("&client_secret").append(urlEncode(new String(credentials.getSecret())));
			} else {
				// OAuth2 with BasicAuth via principal (standard)
				basicAuth = basicAuthManager.create(
						SecurityCredentials.basicAuth(credentials.getIdentifier(), credentials.getSecret()));
			}

			return requestAndCreateAuthFromResponse(
					credentials.getProxy(), grantType, tokenType, tokenService, content.toString(), basicAuth);
		} finally {
			if (secret != null && secret.length > 0) {
				Arrays.fill(secret, '*');
			}
		}
	}

}
