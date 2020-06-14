/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.preproc.FileWithSuffix;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;
import net.sourceforge.plantuml.ugraphic.sprite.SpriteImage;
import net.sourceforge.plantuml.ugraphic.sprite.SpriteSvg;

public class CommandSpriteFile extends SingleLineCommand2<UmlDiagram> {

	public CommandSpriteFile() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandSpriteFile.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("sprite"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("\\$?"), //
				new RegexLeaf("NAME", "([\\p{L}0-9_]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("FILE", "(.*)"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(UmlDiagram system, LineLocation location, RegexResult arg) {
		final String src = arg.get("FILE", 0);
		final Sprite sprite;
		try {
			if (src.startsWith("jar:")) {
				final String inner = src.substring(4) + ".png";
				final InputStream is = SpriteImage.getInternalSprite(inner);
				if (is == null) {
					return CommandExecutionResult.error("No such internal sprite: " + inner);
				}
				sprite = new SpriteImage(ImageIO.read(is));
			} else if (src.contains("~")) {
				final int idx = src.lastIndexOf("~");
				final File f = FileSystem.getInstance().getFile(src.substring(0, idx));
				if (f.exists() == false) {
					return CommandExecutionResult.error("File does not exist: " + src);
				}
				final String name = src.substring(idx + 1);
				sprite = getImageFromZip(f, name);
				if (sprite == null) {
					return CommandExecutionResult.error("No image " + name + " in " + FileWithSuffix.getFileName(f));
				}
			} else {
				final File f = FileSystem.getInstance().getFile(src);
				if (f.exists() == false) {
					return CommandExecutionResult.error("File does not exist: " + src);
				}
				if (isSvg(f.getName())) {
					sprite = new SpriteSvg(f);
				} else {
					sprite = new SpriteImage(FileUtils.ImageIO_read(f));
				}
			}
		} catch (IOException e) {
			Log.error("Error reading " + src + " " + e);
			return CommandExecutionResult.error("Cannot read: " + src);
		}
		system.addSprite(arg.get("NAME", 0), sprite);
		return CommandExecutionResult.ok();
	}

	private Sprite getImageFromZip(File f, String name) throws IOException {
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(f));
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				final String fileName = ze.getName();
				if (ze.isDirectory()) {
				} else if (fileName.equals(name)) {
					if (isSvg(name)) {
						return new SpriteSvg(FileUtils.readSvg(zis));
					} else {
						return new SpriteImage(ImageIO.read(zis));
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
