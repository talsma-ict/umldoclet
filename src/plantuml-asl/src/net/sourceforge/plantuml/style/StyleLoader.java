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
package net.sourceforge.plantuml.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.security.SFile;

public class StyleLoader {

	public static final int DELTA_PRIORITY_FOR_STEREOTYPE = 1000;
	private final SkinParam skinParam;

	public StyleLoader(SkinParam skinParam) {
		this.skinParam = skinParam;
	}

	private StyleBuilder styleBuilder;

	public StyleBuilder loadSkin(String filename) throws IOException {
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
	}

	private void loadSkinInternal(final BlocLines lines) {
		for (Style newStyle : getDeclaredStyles(lines, styleBuilder))
			this.styleBuilder.loadInternal(newStyle.getSignature(), newStyle);

	}

	private final static String KEYNAMES = "[-.\\w(), ]+?";
	private final static Pattern2 keyName = MyPattern.cmpile("^[:]?(" + KEYNAMES + ")([%s]+\\*)?[%s]*\\{$");
	private final static Pattern2 propertyAndValue = MyPattern.cmpile("^([\\w]+):?[%s]+(.*?);?$");
	private final static Pattern2 closeBracket = MyPattern.cmpile("^\\}$");

	public static Collection<Style> getDeclaredStyles(BlocLines lines, AutomaticCounter counter) {
		lines = lines.eventuallyMoveAllEmptyBracket();
		final List<Style> result = new ArrayList<>();
		final CssVariables variables = new CssVariables();
		StyleScheme scheme = StyleScheme.REGULAR;

		Context context = new Context();
		final List<Map<PName, Value>> maps = new ArrayList<Map<PName, Value>>();
		boolean inComment = false;
		for (StringLocated s : lines) {
			String trimmed = s.getTrimmed().getString();

			if (trimmed.startsWith("/*") || trimmed.endsWith("*/"))
				continue;
			if (trimmed.startsWith("/'") || trimmed.endsWith("'/"))
				continue;

			if (trimmed.startsWith("/*") || trimmed.startsWith("/'")) {
				inComment = true;
				continue;
			}
			if (trimmed.endsWith("*/") || trimmed.endsWith("'/")) {
				inComment = false;
				continue;
			}
			if (inComment)
				continue;

			if (trimmed.matches("@media.*dark.*\\{")) {
				scheme = StyleScheme.DARK;
				continue;
			}

			if (trimmed.startsWith("--")) {
				variables.learn(trimmed);
				continue;
			}

			final int x = trimmed.lastIndexOf("//");
			if (x != -1)
				trimmed = trimmed.substring(0, x).trim();

			final Matcher2 mKeyNames = keyName.matcher(trimmed);
			if (mKeyNames.find()) {
				String names = mKeyNames.group(1);
				final boolean isRecurse = mKeyNames.group(2) != null;
				if (isRecurse)
					names += "*";

				context = context.push(names);
				maps.add(new EnumMap<PName, Value>(PName.class));
				continue;
			}
			final Matcher2 mPropertyAndValue = propertyAndValue.matcher(trimmed);
			if (mPropertyAndValue.find()) {
				final PName key = PName.getFromName(mPropertyAndValue.group(1), scheme);
				final String value = variables.value(mPropertyAndValue.group(2));
				if (key != null && maps.size() > 0)
					maps.get(maps.size() - 1).put(key, //
							scheme == StyleScheme.REGULAR ? //
									ValueImpl.regular(value, counter) : ValueImpl.dark(value, counter));

				continue;
			}
			final Matcher2 mCloseBracket = closeBracket.matcher(trimmed);
			if (mCloseBracket.find()) {
				if (context.size() > 0) {
					final Collection<StyleSignatureBasic> signatures = context.toSignatures();
					for (StyleSignatureBasic signature : signatures) {
						Map<PName, Value> tmp = maps.get(maps.size() - 1);
						if (signature.isWithDot())
							tmp = addPriorityForStereotype(tmp);
						if (tmp.size() > 0) {
							final Style style = new Style(signature, tmp);
							result.add(style);
						}
					}
					context = context.pop();
					maps.remove(maps.size() - 1);
				} else {
					scheme = StyleScheme.REGULAR;
				}
			}
		}

		return Collections.unmodifiableList(result);

	}

	public static Map<PName, Value> addPriorityForStereotype(Map<PName, Value> tmp) {
		final Map<PName, Value> result = new EnumMap<>(PName.class);
		for (Entry<PName, Value> ent : tmp.entrySet())
			result.put(ent.getKey(), ((ValueImpl) ent.getValue()).addPriority(DELTA_PRIORITY_FOR_STEREOTYPE));

		return result;
	}

}
