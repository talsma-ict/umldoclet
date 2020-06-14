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
package net.sourceforge.plantuml;

import java.util.Map;

import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotSplines;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.IHtmlColorSet;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.Padder;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;

public class SkinParamDelegator implements ISkinParam {

	final private ISkinParam skinParam;

	public SkinParamDelegator(ISkinParam skinParam) {
		this.skinParam = skinParam;
	}

	public HtmlColor getHyperlinkColor() {
		return skinParam.getHyperlinkColor();
	}

	public HtmlColor getBackgroundColor() {
		return skinParam.getBackgroundColor();
	}

	public int getCircledCharacterRadius() {
		return skinParam.getCircledCharacterRadius();
	}

	public UFont getFont(Stereotype stereotype, boolean inPackageTitle, FontParam... fontParam) {
		return skinParam.getFont(stereotype, false, fontParam);
	}

	public HtmlColor getFontHtmlColor(Stereotype stereotype, FontParam... param) {
		return skinParam.getFontHtmlColor(stereotype, param);
	}

	public HtmlColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable) {
		return skinParam.getHtmlColor(param, stereotype, clickable);
	}

	public String getValue(String key) {
		return skinParam.getValue(key);
	}

	public int classAttributeIconSize() {
		return skinParam.classAttributeIconSize();
	}

	public int getDpi() {
		return skinParam.getDpi();
	}

	public DotSplines getDotSplines() {
		return skinParam.getDotSplines();
	}

	public HorizontalAlignment getHorizontalAlignment(AlignmentParam param, ArrowDirection arrowDirection,
			boolean isReverseDefine) {
		return skinParam.getHorizontalAlignment(param, arrowDirection, isReverseDefine);
	}

	public ColorMapper getColorMapper() {
		return skinParam.getColorMapper();
	}

	public boolean shadowing(Stereotype stereotype) {
		return skinParam.shadowing(stereotype);
	}

	public boolean shadowing2(Stereotype stereotype, SkinParameter skinParameter) {
		return skinParam.shadowing2(stereotype, skinParameter);
	}

	public PackageStyle getPackageStyle() {
		return skinParam.getPackageStyle();
	}

	public Sprite getSprite(String name) {
		return skinParam.getSprite(name);
	}

	public boolean useUml2ForComponent() {
		return skinParam.useUml2ForComponent();
	}

	public boolean stereotypePositionTop() {
		return skinParam.stereotypePositionTop();
	}

	public boolean useSwimlanes(UmlDiagramType type) {
		return skinParam.useSwimlanes(type);
	}

	public double getNodesep() {
		return skinParam.getNodesep();
	}

	public double getRanksep() {
		return skinParam.getRanksep();
	}

	public double getRoundCorner(CornerParam param, Stereotype stereotype) {
		return skinParam.getRoundCorner(param, stereotype);
	}

	public double getDiagonalCorner(CornerParam param, Stereotype stereotype) {
		return skinParam.getDiagonalCorner(param, stereotype);
	}

	public UStroke getThickness(LineParam param, Stereotype stereotype) {
		return skinParam.getThickness(param, stereotype);
	}

	public LineBreakStrategy maxMessageSize() {
		return skinParam.maxMessageSize();
	}

	public LineBreakStrategy wrapWidth() {
		return skinParam.wrapWidth();
	}

	public boolean strictUmlStyle() {
		return skinParam.strictUmlStyle();
	}

	public boolean forceSequenceParticipantUnderlined() {
		return skinParam.forceSequenceParticipantUnderlined();
	}

	public ConditionStyle getConditionStyle() {
		return skinParam.getConditionStyle();
	}

	public ConditionEndStyle getConditionEndStyle() {
		return skinParam.getConditionEndStyle();
	}

	public double minClassWidth() {
		return skinParam.minClassWidth();
	}

	public boolean sameClassWidth() {
		return skinParam.sameClassWidth();
	}

	public Rankdir getRankdir() {
		return skinParam.getRankdir();
	}

	public boolean useOctagonForActivity(Stereotype stereotype) {
		return skinParam.useOctagonForActivity(stereotype);
	}

	public IHtmlColorSet getIHtmlColorSet() {
		return skinParam.getIHtmlColorSet();
	}

	public boolean useUnderlineForHyperlink() {
		return skinParam.useUnderlineForHyperlink();
	}

	public HorizontalAlignment getDefaultTextAlignment(HorizontalAlignment defaultValue) {
		return skinParam.getDefaultTextAlignment(defaultValue);
	}

	public double getPadding() {
		return skinParam.getPadding();
	}

	public int groupInheritance() {
		return skinParam.groupInheritance();
	}

	public Guillemet guillemet() {
		return skinParam.guillemet();
	}

	public boolean handwritten() {
		return skinParam.handwritten();
	}

	public String getSvgLinkTarget() {
		return skinParam.getSvgLinkTarget();
	}

	public String getMonospacedFamily() {
		return skinParam.getMonospacedFamily();
	}

	public Colors getColors(ColorParam param, Stereotype stereotype) {
		return skinParam.getColors(param, stereotype);
	}

	public int getTabSize() {
		return skinParam.getTabSize();
	}

	public boolean shadowingForNote(Stereotype stereotype) {
		return shadowingForNote(stereotype);
	}

	public int maxAsciiMessageLength() {
		return skinParam.maxAsciiMessageLength();
	}

	public int colorArrowSeparationSpace() {
		return skinParam.colorArrowSeparationSpace();
	}

	public SplitParam getSplitParam() {
		return skinParam.getSplitParam();
	}

	public int swimlaneWidth() {
		return skinParam.swimlaneWidth();
	}

	public UmlDiagramType getUmlDiagramType() {
		return skinParam.getUmlDiagramType();
	}

	public HtmlColor getHoverPathColor() {
		return skinParam.getHoverPathColor();
	}

	public double getPadding(PaddingParam param) {
		return skinParam.getPadding(param);
	}

	public boolean useRankSame() {
		return skinParam.useRankSame();
	}

	public boolean displayGenericWithOldFashion() {
		return skinParam.displayGenericWithOldFashion();
	}

	public TikzFontDistortion getTikzFontDistortion() {
		return skinParam.getTikzFontDistortion();
	}

	public boolean responseMessageBelowArrow() {
		return skinParam.responseMessageBelowArrow();
	}

	public boolean svgDimensionStyle() {
		return skinParam.svgDimensionStyle();
	}

	public char getCircledCharacter(Stereotype stereotype) {
		return skinParam.getCircledCharacter(stereotype);
	}

	public LineBreakStrategy swimlaneWrapTitleWidth() {
		return skinParam.swimlaneWrapTitleWidth();
	}

	public boolean fixCircleLabelOverlapping() {
		return skinParam.fixCircleLabelOverlapping();
	}

	public void setUseVizJs(boolean useVizJs) {
		skinParam.setUseVizJs(useVizJs);
	}

	public boolean isUseVizJs() {
		return skinParam.isUseVizJs();
	}

	public void copyAllFrom(ISkinSimple other) {
		skinParam.copyAllFrom(other);
	}

	public Map<String, String> values() {
		return skinParam.values();
	}

	public HorizontalAlignment getStereotypeAlignment() {
		return skinParam.getStereotypeAlignment();
	}

	public Padder getSequenceDiagramPadder() {
		return skinParam.getSequenceDiagramPadder();
	}

	public StyleBuilder getCurrentStyleBuilder() {
		return skinParam.getCurrentStyleBuilder();
	}

	public void muteStyle(Style modifiedStyle) {
		skinParam.muteStyle(modifiedStyle);
	}

}
