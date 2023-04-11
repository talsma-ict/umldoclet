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
package net.sourceforge.plantuml.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.skin.SkinParam;
import net.sourceforge.plantuml.style.parser.StyleParser;
import net.sourceforge.plantuml.style.parser.StyleParsingException;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.LineLocationImpl;
import net.sourceforge.plantuml.utils.Log;

public class StyleLoader {
    // ::remove file when __HAXE__

	private final SkinParam skinParam;

	public StyleLoader(SkinParam skinParam) {
		this.skinParam = skinParam;
	}

	private StyleBuilder styleBuilder;

	public StyleBuilder loadSkin(String filename) throws IOException, StyleParsingException {
		this.styleBuilder = new StyleBuilder(skinParam);

		final InputStream internalIs = getInputStreamForStyle(filename);
		if (internalIs == null) {
			Log.error("No .skin file seems to be available");
			throw new NoStyleAvailableException();
		}
		final BlocLines lines2 = BlocLines.load(internalIs, new LineLocationImpl(filename, null));
		loadSkinInternal(lines2);
		if (this.styleBuilder == null) {
			Log.error("No .skin file seems to be available");
			throw new NoStyleAvailableException();
		}
		return this.styleBuilder;
	}

	public static InputStream getInputStreamForStyle(String filename) throws IOException {
		// ::uncomment when __CORE__
//		final String res = "/skin/" + filename;
//		final InputStream is = StyleLoader.class.getResourceAsStream(res);
//		return is;
		// ::done

		// ::comment when __CORE__
		InputStream internalIs = null;
		SFile localFile = new SFile(filename);
		Log.info("Trying to load style " + filename);
		try {
			if (localFile.exists() == false)
				localFile = FileSystem.getInstance().getFile(filename);
		} catch (IOException e) {
			Log.info("Cannot open file. " + e);
		}

		if (localFile.exists()) {
			Log.info("File found : " + localFile.getPrintablePath());
			internalIs = localFile.openFile();
		} else {
			Log.info("File not found : " + localFile.getPrintablePath());
			final String res = "/skin/" + filename;
			internalIs = StyleLoader.class.getResourceAsStream(res);
			if (internalIs != null)
				Log.info("... but " + filename + " found inside the .jar");

		}
		return internalIs;
		// ::done
	}

	private void loadSkinInternal(final BlocLines lines) throws StyleParsingException {
		for (Style newStyle : StyleParser.parse(lines, styleBuilder))
			this.styleBuilder.loadInternal(newStyle.getSignature(), newStyle);
	}

	public static final int DELTA_PRIORITY_FOR_STEREOTYPE = 1000;

	public static Map<PName, Value> addPriorityForStereotype(Map<PName, Value> tmp) {
		final Map<PName, Value> result = new EnumMap<>(PName.class);
		for (Entry<PName, Value> ent : tmp.entrySet())
			result.put(ent.getKey(), ((ValueImpl) ent.getValue()).addPriority(DELTA_PRIORITY_FOR_STEREOTYPE));

		return result;
	}

}
