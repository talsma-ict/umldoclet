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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositionned;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.style.StyleBuilder;

public abstract class TitledDiagram extends AbstractPSystem implements Diagram, Annotated {

	public static final boolean FORCE_SMETANA = false;

	private DisplayPositionned title = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.TOP);

	private DisplayPositionned caption = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	private DisplayPositionned legend = DisplayPositionned.none(HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	private final DisplaySection header = DisplaySection.none();
	private final DisplaySection footer = DisplaySection.none();
	private Display mainFrame;
	private final UmlDiagramType type;

	private final SkinParam skinParam;

	private final Pragma pragma = new Pragma();

	public Pragma getPragma() {
		return pragma;
	}

	public TitledDiagram(UmlDiagramType type) {
		this.type = type;
		this.skinParam = SkinParam.create(type);
	}

	public final StyleBuilder getCurrentStyleBuilder() {
		return skinParam.getCurrentStyleBuilder();
	}

	public TitledDiagram(UmlDiagramType type, ISkinSimple orig) {
		this(type);
		if (orig != null) {
			this.skinParam.copyAllFrom(orig);
		}
	}

	final public UmlDiagramType getUmlDiagramType() {
		return type;
	}

	public final ISkinParam getSkinParam() {
		return skinParam;
	}

	public void setParam(String key, String value) {
		skinParam.setParam(StringUtils.goLowerCase(key), value);
	}

	public void addSprite(String name, Sprite sprite) {
		skinParam.addSprite(name, sprite);
	}

	public CommandExecutionResult loadSkin(String newSkin) throws IOException {
		getSkinParam().setDefaultSkin(newSkin + ".skin");
		return CommandExecutionResult.ok();
		// final String res = "/skin/" + filename + ".skin";
		// final InputStream internalIs = UmlDiagram.class.getResourceAsStream(res);
		// if (internalIs != null) {
		// final BlocLines lines2 = BlocLines.load(internalIs, new
		// LineLocationImpl(filename, null));
		// return loadSkinInternal(lines2);
		// }
		// if (OptionFlags.ALLOW_INCLUDE == false) {
		// return CommandExecutionResult.ok();
		// }
		// final File f = FileSystem.getInstance().getFile(filename + ".skin");
		// if (f == null || f.exists() == false || f.canRead() == false) {
		// return CommandExecutionResult.error("Cannot load skin from " + filename);
		// }
		// final BlocLines lines = BlocLines.load(f, new LineLocationImpl(f.getName(),
		// null));
		// return loadSkinInternal(lines);
	}

	// private CommandExecutionResult loadSkinInternal(final BlocLines lines) {
	// final CommandSkinParam cmd1 = new CommandSkinParam();
	// final CommandSkinParamMultilines cmd2 = new CommandSkinParamMultilines();
	// for (int i = 0; i < lines.size(); i++) {
	// final BlocLines ext1 = lines.subList(i, i + 1);
	// if (cmd1.isValid(ext1) == CommandControl.OK) {
	// cmd1.execute(this, ext1);
	// } else if (cmd2.isValid(ext1) == CommandControl.OK_PARTIAL) {
	// i = tryMultilines(cmd2, i, lines);
	// }
	// }
	// return CommandExecutionResult.ok();
	// }

//	private int tryMultilines(CommandSkinParamMultilines cmd2, int i, BlocLines lines) {
//		for (int j = i + 1; j <= lines.size(); j++) {
//			final BlocLines ext1 = lines.subList(i, j);
//			if (cmd2.isValid(ext1) == CommandControl.OK) {
//				cmd2.execute(this, ext1);
//				return j;
//			} else if (cmd2.isValid(ext1) == CommandControl.NOT_OK) {
//				return j;
//			}
//		}
//		return i;
//	}

	final public void setTitle(DisplayPositionned title) {
		if (title.isNull() || title.getDisplay().isWhite()) {
			return;
		}
		this.title = title;
	}

	@Override
	final public DisplayPositionned getTitle() {
		return title;
	}

	final public void setMainFrame(Display mainFrame) {
		this.mainFrame = mainFrame;
	}

	final public void setCaption(DisplayPositionned caption) {
		this.caption = caption;
	}

	final public DisplayPositionned getCaption() {
		return caption;
	}

	final public DisplaySection getHeader() {
		return header;
	}

	final public DisplaySection getFooter() {
		return footer;
	}

	final public DisplayPositionned getLegend() {
		return legend;
	}

	public void setLegend(DisplayPositionned legend) {
		this.legend = legend;
	}

	final public Display getMainFrame() {
		return mainFrame;
	}

	private boolean useSmetana;

	public void setUseSmetana(boolean useSmetana) {
		this.useSmetana = useSmetana;
	}

	public boolean isUseSmetana() {
		if (FORCE_SMETANA)
			return true;
		return useSmetana;
	}

	public final double getScaleCoef(FileFormatOption fileFormatOption) {
		if (getSkinParam().getDpi() == 96) {
			return fileFormatOption.getScaleCoef();
		}
		return getSkinParam().getDpi() * fileFormatOption.getScaleCoef() / 96.0;
	}

}
