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
package net.sourceforge.plantuml.tim;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.preproc.ReadLine;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc2.PreprocessorUtils;
import net.sourceforge.plantuml.security.SURL;
import net.sourceforge.plantuml.theme.ThemeUtils;

public class EaterTheme extends Eater {

	private String realName;
	private String name;
	private String from;
	private TContext context;

	public EaterTheme(StringLocated s) {
		super(s);
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException, EaterExceptionLocated {
		skipSpaces();
		checkAndEatChar("!theme");
		skipSpaces();
		this.name = this.eatAllToEnd();

		final int x = this.name.toLowerCase().indexOf(" from ");
		if (x != -1) {
			this.from = this.name.substring(x + " from ".length());
			this.name = this.name.substring(0, x);
			this.context = context;
		}

		this.realName = context.applyFunctionsAndVariables(memory, getLineLocation(), this.name);

	}

	public final ReadLine getTheme() throws EaterException {
		if (from == null) {
			final ReadLine reader = ThemeUtils.getReaderTheme(realName);
			return reader;
		}
		if (from.startsWith("http://") || from.startsWith("https://")) {
			final SURL url = SURL.create(ThemeUtils.getFullPath(from, realName));
			if (url == null) {
				throw EaterException.located("Cannot open URL");
			}
			try {
				return PreprocessorUtils.getReaderInclude(url, getLineLocation(), UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw EaterException.located("Cannot decode charset");
			}
		}

		try {
			final FileWithSuffix file = context.getFileWithSuffix(from, realName);
			return ReadLineReader.create(file.getReader(UTF_8), "theme " + realName);
		} catch (IOException e) {
			e.printStackTrace();
			throw EaterException.located("Cannot load " + realName);
		}

	}

	public String getName() {
		return name;
	}

}
