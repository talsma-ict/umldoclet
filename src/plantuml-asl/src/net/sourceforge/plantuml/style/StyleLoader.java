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
package net.sourceforge.plantuml.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	private final SkinParam skinParam;

	public StyleLoader(SkinParam skinParam) {
		this.skinParam = skinParam;
	}

	private StyleBuilder styleBuilder;

	public StyleBuilder loadSkin(String filename) throws IOException {
		this.styleBuilder = new StyleBuilder(skinParam);

		InputStream internalIs = null;
		SFile localFile = new SFile(filename);
		Log.info("Trying to load style " + filename);
		if (localFile.exists() == false) {
			localFile = FileSystem.getInstance().getFile(filename);
		}
		if (localFile.exists()) {
			Log.info("File found : " + localFile.getPrintablePath());
			internalIs = localFile.openFile();
		} else {
			Log.info("File not found : " + localFile.getPrintablePath());
			final String res = "/skin/" + filename;
			internalIs = StyleLoader.class.getResourceAsStream(res);
			if (internalIs != null) {
				Log.info("... but " + filename + " found inside the .jar");
			}
		}
		if (internalIs == null) {
			return null;
		}
		final BlocLines lines2 = BlocLines.load(internalIs, new LineLocationImpl(filename, null));
		loadSkinInternal(lines2);
		return styleBuilder;
	}

	private void loadSkinInternal(final BlocLines lines) {
		for (Style newStyle : getDeclaredStyles(lines, styleBuilder)) {
			this.styleBuilder.put(newStyle.getSignature(), newStyle);
		}
	}

	private static final String NAME_USER = "[\\w()]+?";
	private final static Pattern2 userName = MyPattern.cmpile("^[.:]?(" + NAME_USER + ")([%s]+\\*)?[%s]*\\{$");
	private final static Pattern2 propertyAndValue = MyPattern.cmpile("^([\\w]+):?[%s]+(.*?);?$");
	private final static Pattern2 closeBracket = MyPattern.cmpile("^\\}$");

	public static Collection<Style> getDeclaredStyles(BlocLines lines, AutomaticCounter counter) {
		lines = lines.eventuallyMoveAllEmptyBracket();
		final List<Style> result = new ArrayList<Style>();

		final List<String> context = new ArrayList<String>();
		final List<Map<PName, Value>> maps = new ArrayList<Map<PName, Value>>();
		boolean inComment = false;
		for (StringLocated s : lines) {
			String trimmed = s.getTrimmed().getString();
			if (trimmed.startsWith("/*") || trimmed.startsWith("/'")) {
				inComment = true;
				continue;
			}
			if (trimmed.endsWith("*/") || trimmed.endsWith("'/")) {
				inComment = false;
				continue;
			}
			if (inComment) {
				continue;
			}
			final int x = trimmed.lastIndexOf("//");
			if (x != -1) {
				trimmed = trimmed.substring(0, x).trim();
			}
			final Matcher2 mUserName = userName.matcher(trimmed);
			if (mUserName.find()) {
				String n = mUserName.group(1);
				final boolean isRecurse = mUserName.group(2) != null;
				if (isRecurse) {
					n += "*";
				}
				context.add(n);
				maps.add(new EnumMap<PName, Value>(PName.class));
				continue;
			}
			final Matcher2 mPropertyAndValue = propertyAndValue.matcher(trimmed);
			if (mPropertyAndValue.find()) {
				final PName key = PName.getFromName(mPropertyAndValue.group(1));
				final String value = mPropertyAndValue.group(2);
				if (key != null && maps.size() > 0) {
					maps.get(maps.size() - 1).put(key, new ValueImpl(value, counter));
				}
				continue;
			}
			final Matcher2 mCloseBracket = closeBracket.matcher(trimmed);
			if (mCloseBracket.find()) {
				if (context.size() > 0) {
					final StyleSignature signature = contextToSignature(context);
					final Style style = new Style(signature, maps.get(maps.size() - 1));
					result.add(style);
					context.remove(context.size() - 1);
					maps.remove(maps.size() - 1);
				}
			}
		}

		return Collections.unmodifiableList(result);

	}

	private static StyleSignature contextToSignature(List<String> context) {
		StyleSignature result = StyleSignature.empty();
		boolean star = false;
		for (Iterator<String> it = context.iterator(); it.hasNext();) {
			String s = it.next();
			if (s.endsWith("*")) {
				star = true;
				s = s.substring(0, s.length() - 1);
			}
			result = result.add(s);
		}
		if (star) {
			result = result.addStar();
		}
		return result;
	}

}
