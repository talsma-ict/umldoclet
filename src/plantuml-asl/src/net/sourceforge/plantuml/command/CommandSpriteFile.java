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
package net.sourceforge.plantuml.command;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.sprite.SpriteImage;
import net.sourceforge.plantuml.sprite.SpriteSvg;

public class CommandSpriteFile extends SingleLineCommand2<TitledDiagram> {

	public CommandSpriteFile() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandSpriteFile.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("sprite"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("\\$?"), //
				new RegexLeaf("NAME", "([-%pLN_]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("FILE", "(.*)"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram system, LineLocation location, RegexResult arg) {
		final String src = arg.get("FILE", 0);
		final Sprite sprite;
		try {
			if (src.startsWith("jar:")) {
				final String inner = src.substring(4) + ".png";
				final InputStream is = SpriteImage.getInternalSprite(inner);
				if (is == null) {
					return CommandExecutionResult.error("No such internal sprite: " + inner);
				}
				sprite = new SpriteImage(SImageIO.read(is));
			} else if (src.contains("~")) {
				final int idx = src.lastIndexOf("~");
				final SFile f = FileSystem.getInstance().getFile(src.substring(0, idx));
				if (f.exists() == false) {
					return CommandExecutionResult.error("Cannot read: " + src);
				}
				final String name = src.substring(idx + 1);
				sprite = getImageFromZip(f, name);
				if (sprite == null) {
					return CommandExecutionResult.error("Cannot read: " + src);
				}
			} else {
				final SFile f = FileSystem.getInstance().getFile(src);
				if (f.exists() == false) {
					return CommandExecutionResult.error("Cannot read: " + src);
				}
				if (isSvg(f.getName())) {
					final String tmp = FileUtils.readSvg(f);
					if (tmp == null) {
						return CommandExecutionResult.error("Cannot read: " + src);
					}
					sprite = new SpriteSvg(tmp);
				} else {
					final BufferedImage tmp = f.readRasterImageFromFile();
					if (tmp == null) {
						return CommandExecutionResult.error("Cannot read: " + src);
					}
					sprite = new SpriteImage(tmp);
				}
			}
		} catch (IOException e) {
			Log.error("Error reading " + src + " " + e);
			return CommandExecutionResult.error("Cannot read: " + src);
		}
		system.addSprite(arg.get("NAME", 0), sprite);
		return CommandExecutionResult.ok();
	}

	private Sprite getImageFromZip(SFile f, String name) throws IOException {
		final InputStream tmp = f.openFile();
		if (tmp == null) {
			return null;
		}
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(tmp);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				final String fileName = ze.getName();
				if (ze.isDirectory()) {
				} else if (fileName.equals(name)) {
					if (isSvg(name)) {
						return new SpriteSvg(FileUtils.readSvg(zis));
					} else {
						return new SpriteImage(SImageIO.read(zis));
					}
				}
				ze = zis.getNextEntry();
			}
		} finally {
			if (zis != null) {
				zis.closeEntry();
				zis.close();
			}
		}
		return null;
	}

	private boolean isSvg(String name) {
		return name.toLowerCase().endsWith(".svg");
	}
}
