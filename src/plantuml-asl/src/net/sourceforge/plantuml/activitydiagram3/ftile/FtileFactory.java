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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ForkStyle;
import net.sourceforge.plantuml.activitydiagram3.Instruction;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.url.Url;

public interface FtileFactory {

	public StringBounder getStringBounder();

	public ISkinParam skinParam();

	public Ftile start(Swimlane swimlane);

	public Ftile stop(Swimlane swimlane);

	public Ftile end(Swimlane swimlane);

	public Ftile spot(Swimlane swimlane, String spot, HColor color);

	public Ftile activity(Display label, Swimlane swimlane, BoxStyle style, Colors colors, Stereotype stereotype);

	public Ftile addNote(Ftile ftile, Swimlane swimlane, Collection<PositionedNote> notes,
			VerticalAlignment verticalAlignment);

	public Ftile addUrl(Ftile ftile, Url url);

	public Ftile decorateIn(Ftile ftile, LinkRendering linkRendering);

	public Ftile decorateOut(Ftile ftile, LinkRendering linkRendering);

	public Ftile assembly(Ftile tile1, Ftile tile2);

	public Ftile repeat(BoxStyle boxStyleIn, Swimlane swimlane, Swimlane swimlaneOut, Display startLabel, Ftile repeat,
			Display test, Display yes, Display out, Colors colors, Ftile backward, boolean noOut,
			LinkRendering incoming1, LinkRendering incoming2);

	public Ftile createWhile(LinkRendering outColor, Swimlane swimlane, Ftile whileBlock, Display test, Display yes,
			HColor color, Instruction specialOut, Ftile backward, LinkRendering incoming1, LinkRendering incoming2);

	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch, LinkRendering outColor,
			LinkRendering topInlinkRendering, Url url);

	public Ftile createSwitch(Swimlane swimlane, List<Branch> branches, LinkRendering afterEndwhile,
			LinkRendering topInlinkRendering, Display labelTest);

	public Ftile createParallel(List<Ftile> all, ForkStyle style, String label, Swimlane in, Swimlane out);

	public Ftile createGroup(Ftile list, Display name, HColor backColor, PositionedNote note, USymbol type,
			Style style);

}
