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

import java.util.Collection;

import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotSplines;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.Padder;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public interface ISkinParam extends ISkinSimple {

	public static final int SWIMLANE_WIDTH_SAME = -1;

	public HColor getHyperlinkColor();

	public boolean useUnderlineForHyperlink();

	public HColor getBackgroundColor();

	public HColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable);

	public Colors getColors(ColorParam param, Stereotype stereotype);

	public HColor getFontHtmlColor(Stereotype stereotype, FontParam... param);

	public UStroke getThickness(LineParam param, Stereotype stereotype);

	public UFont getFont(Stereotype stereotype, boolean inPackageTitle, FontParam... fontParam);

	public HorizontalAlignment getHorizontalAlignment(AlignmentParam param, ArrowDirection arrowDirection,
			boolean isReverseDefine);

	public HorizontalAlignment getDefaultTextAlignment(HorizontalAlignment defaultValue);

	public HorizontalAlignment getStereotypeAlignment();

	public int getCircledCharacterRadius();

	public char getCircledCharacter(Stereotype stereotype);

	public int classAttributeIconSize();

	public DotSplines getDotSplines();

	public boolean shadowing(Stereotype stereotype);

	public boolean shadowingForNote(Stereotype stereotype);

	public boolean shadowing2(Stereotype stereotype, SkinParameter skinParameter);

	public PackageStyle getPackageStyle();

	public boolean useUml2ForComponent();

	public boolean stereotypePositionTop();

	public boolean useSwimlanes(UmlDiagramType type);

	public double getNodesep();

	public double getRanksep();

	public double getRoundCorner(CornerParam param, Stereotype stereotype);

	public double getDiagonalCorner(CornerParam param, Stereotype stereotype);

	public LineBreakStrategy maxMessageSize();

	public LineBreakStrategy wrapWidth();

	public LineBreakStrategy swimlaneWrapTitleWidth();

	public boolean strictUmlStyle();

	public boolean forceSequenceParticipantUnderlined();

	public ConditionStyle getConditionStyle();

	public ConditionEndStyle getConditionEndStyle();

	public double minClassWidth();

	public boolean sameClassWidth();

	public Rankdir getRankdir();

	public boolean useOctagonForActivity(Stereotype stereotype);

	public int groupInheritance();

	public Guillemet guillemet();

	public boolean handwritten();

	public String getSvgLinkTarget();

	public String getPreserveAspectRatio();

	public int getTabSize();

	public int maxAsciiMessageLength();

	public int colorArrowSeparationSpace();

	public SplitParam getSplitParam();

	public int swimlaneWidth();

	public UmlDiagramType getUmlDiagramType();

	public HColor getHoverPathColor();

	public TikzFontDistortion getTikzFontDistortion();

	public double getPadding(PaddingParam param);

	public boolean useRankSame();

	public boolean displayGenericWithOldFashion();

	public boolean responseMessageBelowArrow();

	public boolean svgDimensionStyle();

	public boolean fixCircleLabelOverlapping();

	public void setUseVizJs(boolean useVizJs);

	public boolean isUseVizJs();

	public Padder getSequenceDiagramPadder();

	public StyleBuilder getCurrentStyleBuilder();

	public void muteStyle(Style modifiedStyle);

	public Collection<String> getAllSpriteNames();

	public String getDefaultSkin();

	public void setDefaultSkin(String newSkin);

	public ActorStyle getActorStyle();

}
