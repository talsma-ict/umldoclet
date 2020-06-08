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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import net.sourceforge.plantuml.StringUtils;

/**
 * Secure replacement for java.net.URL.
 * <p>
 * This class should be used instead of java.net.URL.
 * <p>
 * This class does some control access.
 *
 */
public class SURL {

	private final URL internal;

	private SURL(String src) throws MalformedURLException {
		this(new URL(src));
	}

	private SURL(URL url) {
		this.internal = url;
	}

	public static SURL create(String url) {
		if (url == null) {
			return null;
		}
		if (url.startsWith("http://") || url.startsWith("https://"))
			try {
				return new SURL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		return null;
	}

	public static SURL create(URL url) {
		if (url == null) {
			return null;
		}
		return new SURL(url);
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	/**
	 * Check SecurityProfile to see if this URL can be open.
	 */
	private boolean isUrlOk() {
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.SANDBOX) {
			// In SANDBOX, we cannot read any URL
			return false;
		}
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.LEGACY) {
			return true;
		}
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE) {
			// We are UNSECURE anyway
			return true;
		}
		if (isInWhiteList()) {
			return true;
		}
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.INTERNET) {
			final int port = internal.getPort();
			// Using INTERNET profile, port 80 and 443 are ok
			if (port == 80 || port == 443) {
				return true;
			}
		}
		return false;
	}

	private boolean isInWhiteList() {
		final String full = cleanPath(internal.toString());
		for (String white : getWhiteList()) {
			if (full.startsWith(cleanPath(white))) {
				return true;
			}
		}
		return false;
	}

	private String cleanPath(String path) {
		path = path.trim().toLowerCase(Locale.US);
		// We simplify/normalize the url, removing default ports
		path = path.replace(":80/", "");
		path = path.replace(":443/", "");
		return path;
	}

	private List<String> getWhiteList() {
		final String env = SecurityUtils.getenv("plantuml.whitelist.url");
		if (env == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env).split(";"));
	}

	public URLConnection openConnection() {
		if (isUrlOk())
			try {
				return internal.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	public InputStream openStream() {
		if (isUrlOk())
			try {
				return internal.openStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	public BufferedImage readRasterImageFromURL() {
		if (isUrlOk())
			try {
				final ImageIcon tmp = new ImageIcon(internal);
				return SecurityUtils.readRasterImage(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

}
