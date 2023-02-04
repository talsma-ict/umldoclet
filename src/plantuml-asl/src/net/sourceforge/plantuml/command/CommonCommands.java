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
package net.sourceforge.plantuml.command;

import java.util.List;

import net.sourceforge.plantuml.classdiagram.command.CommandHideShowByGender;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShowByVisibility;
import net.sourceforge.plantuml.sequencediagram.command.CommandSkin;
import net.sourceforge.plantuml.statediagram.command.CommandHideEmptyDescription;
import net.sourceforge.plantuml.style.CommandStyleImport;
import net.sourceforge.plantuml.style.CommandStyleMultilinesCSS;

public final class CommonCommands {

	private CommonCommands() {
	}

	static public void addCommonCommands1(List<Command> cmds) {
		addTitleCommands(cmds);
		addCommonCommands2(cmds);
		addCommonHides(cmds);
	}

	static public void addCommonCommands2(List<Command> cmds) {
		cmds.add(CommandNope.ME);
		cmds.add(CommandPragma.ME);
		cmds.add(CommandAssumeTransparent.ME);

		cmds.add(CommandSkinParam.ME);
		cmds.add(CommandSkinParamMultilines.ME);
		cmds.add(CommandSkin.ME);
		cmds.add(CommandMinwidth.ME);
		cmds.add(CommandPage.ME);
		cmds.add(CommandRotate.ME);
		cmds.add(CommandScale.ME);
		cmds.add(CommandScaleWidthAndHeight.ME);
		cmds.add(CommandScaleWidthOrHeight.ME);
		cmds.add(CommandScaleMaxWidth.ME);
		cmds.add(CommandScaleMaxHeight.ME);
		cmds.add(CommandScaleMaxWidthAndHeight.ME);
		cmds.add(CommandAffineTransform.ME);
		cmds.add(CommandAffineTransformMultiline.ME);
		final CommandFactorySprite factorySpriteCommand = new CommandFactorySprite();
		cmds.add(factorySpriteCommand.createMultiLine(false));
		cmds.add(factorySpriteCommand.createSingleLine());
		cmds.add(CommandSpriteSvg.ME);
		cmds.add(CommandSpriteFile.ME);
		cmds.add(CommandSpriteSvgMultiline.ME);

		cmds.add(CommandStyleMultilinesCSS.ME);
		cmds.add(CommandStyleImport.ME);

	}

	static public void addCommonHides(List<Command> cmds) {
		cmds.add(CommandHideEmptyDescription.ME);
		cmds.add(CommandHideShowByVisibility.ME);
		cmds.add(CommandHideShowByGender.ME);
	}

	static public void addTitleCommands(List<Command> cmds) {
		cmds.add(CommandTitle.ME);
		cmds.add(CommandMainframe.ME);
		cmds.add(CommandCaption.ME);
		cmds.add(CommandMultilinesCaption.ME);
		cmds.add(CommandMultilinesTitle.ME);
		cmds.add(CommandMultilinesLegend.ME);
		cmds.add(CommandLegend.ME);

		cmds.add(CommandFooter.ME);
		cmds.add(CommandMultilinesFooter.ME);

		cmds.add(CommandHeader.ME);
		cmds.add(CommandMultilinesHeader.ME);
	}

}
