/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import java.net.URLConnection;

/**
 * The security access interceptor applies the authentication information to a
 * HTTP connection. This can be a user/password combination for BasicAuth or a
 * bearer token for OAuth2.
 *
 * @author Aljoscha Rittner
 */
public interface SecurityAccessInterceptor {
    // ::remove folder when __HAXE__
    // ::remove folder when __CORE__
	/**
	 * Applies to a connection the authentication information.
	 *
	 * @param authentication the determined authentication data to authorize for the
	 *                       endpoint access
	 * @param connection     the connection to the endpoint
	 */
	void apply(SecurityAuthentication authentication, URLConnection connection);
}
