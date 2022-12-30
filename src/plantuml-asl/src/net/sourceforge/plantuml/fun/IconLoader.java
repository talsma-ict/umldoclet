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
package net.sourceforge.plantuml.fun;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.security.SImageIO;

public class IconLoader {

	private static final int NUMBER_OF_ICONS = 31;

	private final static Map<String, BufferedImage> all = new ConcurrentHashMap<String, BufferedImage>();
	static private final List<String> tmp = new ArrayList<>();

	public static BufferedImage getRandom() {
		// return addTransparent(getIcon("sprite029.png"));
		return addTransparent(getIcon(getSomeQuote()));
	}

	private static String getSomeQuote() {
		synchronized (tmp) {
			if (tmp.size() == 0) {
				for (int i = 0; i < NUMBER_OF_ICONS; i++)
					tmp.add("sprite" + String.format("%03d", i) + ".png");

				Collections.shuffle(tmp);
			}
			final int size = tmp.size();
			final String result = tmp.get(size - 1);
			tmp.remove(size - 1);
			return result;
		}
	}

	private static BufferedImage getIcon(String name) {
		BufferedImage result = all.get(name);
		if (result == null) {
			result = getIconSlow(name);
			if (result != null)
				all.put(name, result);

		}
		return result;
	}

	private static BufferedImage getIconSlow(String name) {
		try {
			final InputStream is = IconLoader.class.getResourceAsStream(name);
			if (is == null)
				return null;

			final BufferedImage image = SImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			Logme.error(e);
		}
		return null;
	}

	private static BufferedImage addTransparent(BufferedImage ico) {
		if (ico == null)
			return null;

		final BufferedImage transparentIcon = new BufferedImage(ico.getWidth(), ico.getHeight(),
				BufferedImage.TYPE_INT_ARGB_PRE);
		for (int i = 0; i < ico.getWidth(); i++)
			for (int j = 0; j < ico.getHeight(); j++) {
				final int col = ico.getRGB(i, j);
				if (col != ico.getRGB(0, 0))
					transparentIcon.setRGB(i, j, col);
			}

		return transparentIcon;
	}

}
