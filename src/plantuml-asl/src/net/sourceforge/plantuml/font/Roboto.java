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
package net.sourceforge.plantuml.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class Roboto {
    // ::remove folder when __HAXE__

	private final static String[] names = new String[] { "Roboto-BlackItalic.ttf", "Roboto-Black.ttf",
			"Roboto-BoldItalic.ttf", "Roboto-Bold.ttf", "RobotoCondensed-BoldItalic.ttf", "RobotoCondensed-Bold.ttf",
			"RobotoCondensed-Italic.ttf", "RobotoCondensed-LightItalic.ttf", "RobotoCondensed-Light.ttf",
			"RobotoCondensed-MediumItalic.ttf", "RobotoCondensed-Medium.ttf", "RobotoCondensed-Regular.ttf",
			"Roboto-Italic.ttf", "Roboto-LightItalic.ttf", "Roboto-Light.ttf", "Roboto-MediumItalic.ttf",
			"Roboto-Medium.ttf", "Roboto-Regular.ttf", "Roboto-ThinItalic.ttf", "Roboto-Thin.ttf" };

	public static void registerFonts() throws FontFormatException, IOException {
		for (String n : names) {
			final InputStream stream = Roboto.class.getResourceAsStream(n);
			final Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
		}
	}
}
