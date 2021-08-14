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
package net.sourceforge.plantuml;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.code.AsciiEncoder;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.error.PSystemErrorPreprocessor;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc2.PreprocessorModeSet;
import net.sourceforge.plantuml.tim.TimLoader;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.Version;

public class BlockUml {

	private final List<StringLocated> rawSource;
	private final List<StringLocated> data;
	private List<StringLocated> debug;
	private Diagram system;
	private final Defines localDefines;
	private final ISkinSimple skinParam;
	private final Set<FileWithSuffix> included = new HashSet<>();

	public Set<FileWithSuffix> getIncluded() {
		return Collections.unmodifiableSet(included);
	}

	BlockUml(String... strings) {
		this(convert(strings), Defines.createEmpty(), null, null);
	}

	public String getEncodedUrl() throws IOException {
		final Transcoder transcoder = TranscoderUtil.getDefaultTranscoder();
		final String source = getDiagram().getSource().getPlainString();
		final String encoded = transcoder.encode(source);
		return encoded;
	}

	public String getFlashData() {
		final StringBuilder sb = new StringBuilder();
		for (StringLocated line : data) {
			sb.append(line.getString());
			sb.append('\r');
			sb.append(BackSlash.CHAR_NEWLINE);
		}
		return sb.toString();
	}

	public static List<StringLocated> convert(String... strings) {
		return convert(Arrays.asList(strings));
	}

	public static List<StringLocated> convert(List<String> strings) {
		final List<StringLocated> result = new ArrayList<>();
		LineLocationImpl location = new LineLocationImpl("block", null);
		for (String s : strings) {
			location = location.oneLineRead();
			result.add(new StringLocated(s, location));
		}
		return result;
	}

	private boolean preprocessorError;

	public BlockUml(List<StringLocated> strings, Defines defines, ISkinSimple skinParam, PreprocessorModeSet mode) {
		this.rawSource = new ArrayList<>(strings);
		this.localDefines = defines;
		this.skinParam = skinParam;
		final String s0 = strings.get(0).getTrimmed().getString();
		if (StartUtils.startsWithSymbolAnd("start", s0) == false) {
			throw new IllegalArgumentException();
		}
		if (mode == null) {
			this.data = new ArrayList<>(strings);
		} else {
			final TimLoader timLoader = new TimLoader(mode.getImportedFiles(), defines, mode.getCharset(),
					(DefinitionsContainer) mode);
			this.included.addAll(timLoader.load(strings));
			this.data = timLoader.getResultList();
			this.debug = timLoader.getDebug();
			this.preprocessorError = timLoader.isPreprocessorError();
		}
	}

	public String getFileOrDirname() {
		if (OptionFlags.getInstance().isWord()) {
			return null;
		}
		final Matcher2 m = StartUtils.patternFilename.matcher(StringUtils.trin(data.get(0).getString()));
		final boolean ok = m.find();
		if (ok == false) {
			return null;
		}
		String result = m.group(1);
		final int x = result.indexOf(',');
		if (x != -1) {
			result = result.substring(0, x);
		}
		for (int i = 0; i < result.length(); i++) {
			final char c = result.charAt(i);
			if ("<>|".indexOf(c) != -1) {
				return null;
			}
		}
		if (result.startsWith("file://")) {
			result = result.substring("file://".length());
		}
		result = result.replaceAll("\\.\\w\\w\\w$", "");
		return result;
	}

	public Diagram getDiagram() {
		if (system == null) {
			if (preprocessorError) {
				system = new PSystemErrorPreprocessor(data, debug);
			} else {
				system = new PSystemBuilder().createPSystem(skinParam, data, rawSource);
			}
		}
		return system;
	}

	public final List<StringLocated> getData() {
		return data;
	}

	private String internalEtag() {
		try {
			final AsciiEncoder coder = new AsciiEncoder();
			final MessageDigest msgDigest = MessageDigest.getInstance("MD5");
			for (StringLocated s : data) {
				msgDigest.update(s.getString().getBytes("UTF-8"));
			}
			final byte[] digest = msgDigest.digest();
			return coder.encode(digest);
		} catch (Exception e) {
			e.printStackTrace();
			return "NOETAG";
		}
	}

	public String etag() {
		return Version.etag() + internalEtag();
	}

	public long lastModified() {
		return (Version.compileTime() / 1000L / 60) * 1000L * 60 + Version.beta() * 1000L * 3600;
	}

	public boolean isStartDef(String name) {
		final String signature = "@startdef(id=" + name + ")";
		return data.get(0).getString().equalsIgnoreCase(signature);
	}

	public List<String> getDefinition(boolean withHeader) {
		final List<String> result = new ArrayList<>();
		for (StringLocated s : data) {
			result.add(s.getString());
		}
		if (withHeader) {
			return Collections.unmodifiableList(result);
		}
		return Collections.unmodifiableList(result.subList(1, result.size() - 1));
	}

	public Defines getLocalDefines() {
		return localDefines;
	}

}
