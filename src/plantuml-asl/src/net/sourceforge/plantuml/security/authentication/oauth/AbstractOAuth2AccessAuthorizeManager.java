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
package net.sourceforge.plantuml.security.authentication.oauth;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.security.authentication.SecurityAuthentication;
import net.sourceforge.plantuml.security.authentication.SecurityAuthorizeManager;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Default abstract OAuth2 AccessAuthorizeManager for OAuth2 managers.
 *
 * @author Aljoscha Rittner
 */
public abstract class AbstractOAuth2AccessAuthorizeManager implements SecurityAuthorizeManager {

	/**
	 * Default headers for token service access.<p>
	 * Initialize with:
	 * <pre>
	 * "Content-Type"="application/x-www-form-urlencoded; charset=UTF-8"
	 * "Accept"="application/json"
	 * </pre>
	 *
	 * @return headers
	 */
	protected Map<String, Object> headers() {
		Map<String, Object> map = new HashMap<>();
		map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		map.put("Accept", "application/json");
		return map;
	}

	/**
	 * Builds the access parameter map.
	 *
	 * @param tokenResponse the JSOn object with the response data
	 * @param tokenType     token type to use instead of token_type from response
	 * @return data-map
	 */
	protected Map<String, Object> buildAccessDataFromResponse(JsonObject tokenResponse, String tokenType) {
		Map<String, Object> map = new HashMap<>();

		toMap(map, tokenResponse, OAuth2Tokens.ACCESS_TOKEN);
		toMap(map, tokenResponse, OAuth2Tokens.SCOPE);
		toMap(map, tokenResponse, OAuth2Tokens.EXPIRES_IN);

		if (tokenType == null) {
			toMap(map, tokenResponse, OAuth2Tokens.TOKEN_TYPE);
			if (!map.isEmpty() && !map.containsKey(OAuth2Tokens.TOKEN_TYPE.key())) {
				// default token type is bearer
				map.put(OAuth2Tokens.TOKEN_TYPE.key(), "bearer");
			}
		} else {
			// Caller don't belief in the token_type response
			if (!map.isEmpty()) {
				map.put(OAuth2Tokens.TOKEN_TYPE.key(), tokenType);
			}
		}

		return map;
	}

	/**
	 * Translates the JSON value to a map key/value.
	 *
	 * @param map      collection to store
	 * @param response values from response
	 * @param name     name of the value
	 */
	private void toMap(Map<String, Object> map, JsonObject response, OAuth2Tokens name) {
		JsonValue jsonValue = response.get(name.key());
		if (jsonValue != null && !jsonValue.isNull()) {
			if (jsonValue.isString()) {
				map.put(name.key(), jsonValue.asString());
			} else if (jsonValue.isNumber()) {
				map.put(name.key(), jsonValue.asInt());
			} else if (jsonValue.isBoolean()) {
				map.put(name.key(), jsonValue.asBoolean());
			}
		}
	}

	/**
	 * Encodes the data to UTF-8 into {@code application/x-www-form-urlencoded}.
	 *
	 * @param data data to encode
	 * @return the encoded data
	 */
	protected String urlEncode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * Calls the endpoint to load the token response and create a SecurityAuthentication.
	 *
	 * @param proxy        Proxy for the access
	 * @param grantType    grant type
	 * @param tokenType    token type to use instead of token_type from response
	 * @param tokenService URL to token service
	 * @param content      body content
	 * @param basicAuth    principal basicAuth
	 * @return the authentication object to access resources (or null)
	 */
	protected SecurityAuthentication requestAndCreateAuthFromResponse(
			Proxy proxy, String grantType, String tokenType,
			SURL tokenService, String content, SecurityAuthentication basicAuth) {
		byte[] bytes = tokenService.getBytesOnPost(proxy, basicAuth, content, headers());
		if (bytes != null) {
			JsonValue tokenResponse = Json.parse(new String(bytes, StandardCharsets.UTF_8));
			if (tokenResponse != null && !tokenResponse.isNull()) {
				return new SecurityAuthentication("oauth2", null, grantType,
						buildAccessDataFromResponse(tokenResponse.asObject(), tokenType));
			}
		}
		return null;
	}
}
