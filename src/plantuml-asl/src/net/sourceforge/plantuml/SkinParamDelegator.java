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
package net.sourceforge.plantuml;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotSplines;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.Padder;
import net.sourceforge.plantuml.sprite.Sprite;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleBuilder;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.svg.LengthAdjust;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class SkinParamDelegator implements ISkinParam {

	final private ISkinParam skinParam;

	public SkinParamDelegator(ISkinParam skinParam) {
		this.skinParam = skinParam;
	}

	@Override
	public HColor getHyperlinkColor() {
		return skinParam.getHyperlinkColor();
	}

	@Override
	public HColor getBackgroundColor() {
		return skinParam.getBackgroundColor();
	}

	@Override
	public int getCircledCharacterRadius() {
		return skinParam.getCircledCharacterRadius();
	}

	@Override
	public UFont getFont(Stereotype stereotype, boolean inPackageTitle, FontParam... fontParam) {
		return skinParam.getFont(stereotype, false, fontParam);
	}

	@Override
	public HColor getFontHtmlColor(Stereotype stereotype, FontParam... param) {
		return skinParam.getFontHtmlColor(stereotype, param);
	}

	@Override
	public HColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable) {
		return skinParam.getHtmlColor(param, stereotype, clickable);
	}

	@Override
	public String getValue(String key) {
		return skinParam.getValue(key);
	}

	@Override
	public int classAttributeIconSize() {
		return skinParam.classAttributeIconSize();
	}

	@Override
	public int getDpi() {
		return skinParam.getDpi();
	}

	@Override
	public DotSplines getDotSplines() {
		return skinParam.getDotSplines();
	}

	@Override
	public HorizontalAlignment getHorizontalAlignment(AlignmentParam param, ArrowDirection arrowDirection,
			boolean isReverseDefine, HorizontalAlignment overrideDefault) {
		return skinParam.getHorizontalAlignment(param, arrowDirection, isReverseDefine, overrideDefault);
	}

	@Override
	public ColorMapper getColorMapper() {
		return skinParam.getColorMapper();
	}

	@Override
	public boolean shadowing(Stereotype stereotype) {
		return skinParam.shadowing(stereotype);
	}

	@Override
	public PackageStyle packageStyle() {
		return skinParam.packageStyle();
	}

	@Override
	public Sprite getSprite(String name) {
		return skinParam.getSprite(name);
	}

	@Override
	public ComponentStyle componentStyle() {
		return skinParam.componentStyle();
	}

	@Override
	public boolean stereotypePositionTop() {
		return skinParam.stereotypePositionTop();
	}

	@Override
	public boolean useSwimlanes(UmlDiagramType type) {
		return skinParam.useSwimlanes(type);
	}

	@Override
	public double getNodesep() {
		return skinParam.getNodesep();
	}

	@Override
	public double getRanksep() {
		return skinParam.getRanksep();
	}

	@Override
	public double getRoundCorner(CornerParam param, Stereotype stereotype) {
		return skinParam.getRoundCorner(param, stereotype);
	}

	@Override
	public double getDiagonalCorner(CornerParam param, Stereotype stereotype) {
		return skinParam.getDiagonalCorner(param, stereotype);
	}

	@Override
	public UStroke getThickness(LineParam param, Stereotype stereotype) {
		return skinParam.getThickness(param, stereotype);
	}

	@Override
	public LineBreakStrategy maxMessageSize() {
		return skinParam.maxMessageSize();
	}

	@Override
	public LineBreakStrategy wrapWidth() {
		return skinParam.wrapWidth();
	}

	@Override
	public boolean strictUmlStyle() {
		return skinParam.strictUmlStyle();
	}

	@Override
	public boolean forceSequenceParticipantUnderlined() {
		return skinParam.forceSequenceParticipantUnderlined();
	}

	@Override
	public ConditionStyle getConditionStyle() {
		return skinParam.getConditionStyle();
	}

	@Override
	public ConditionEndStyle getConditionEndStyle() {
		return skinParam.getConditionEndStyle();
	}

	@Override
	public double minClassWidth() {
		return skinParam.minClassWidth();
	}

	@Override
	public boolean sameClassWidth() {
		return skinParam.sameClassWidth();
	}

	@Override
	public Rankdir getRankdir() {
		return skinParam.getRankdir();
	}

	@Override
	public boolean useOctagonForActivity(Stereotype stereotype) {
		return skinParam.useOctagonForActivity(stereotype);
	}

	@Override
	public HColorSet getIHtmlColorSet() {
		return skinParam.getIHtmlColorSet();
	}

	@Override
	public boolean useUnderlineForHyperlink() {
		return skinParam.useUnderlineForHyperlink();
	}

	@Override
	public HorizontalAlignment getDefaultTextAlignment(HorizontalAlignment defaultValue) {
		return skinParam.getDefaultTextAlignment(defaultValue);
	}

	@Override
	public double getPadding() {
		return skinParam.getPadding();
	}

	@Override
	public int groupInheritance() {
		return skinParam.groupInheritance();
	}

	@Override
	public Guillemet guillemet() {
		return skinParam.guillemet();
	}

	@Override
	public boolean handwritten() {
		return skinParam.handwritten();
	}

	@Override
	public String getSvgLinkTarget() {
		return skinParam.getSvgLinkTarget();
	}

	@Override
	public String getPreserveAspectRatio() {
		return skinParam.getPreserveAspectRatio();
	}

	@Override
	public String getMonospacedFamily() {
		return skinParam.getMonospacedFamily();
	}

	@Override
	public Colors getColors(ColorParam param, Stereotype stereotype) throws NoSuchColorException {
		return skinParam.getColors(param, stereotype);
	}

	@Override
	public int getTabSize() {
		return skinParam.getTabSize();
	}

	@Override
	public boolean shadowingForNote(Stereotype stereotype) {
		return shadowingForNote(stereotype);
	}

	@Override
	public int maxAsciiMessageLength() {
		return skinParam.maxAsciiMessageLength();
	}

	@Override
	public int colorArrowSeparationSpace() {
		return skinParam.colorArrowSeparationSpace();
	}

	@Override
	public SplitParam getSplitParam() {
		return skinParam.getSplitParam();
	}

	@Override
	public int swimlaneWidth() {
		return skinParam.swimlaneWidth();
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return skinParam.getUmlDiagramType();
	}

	@Override
	public HColor hoverPathColor() {
		return skinParam.hoverPathColor();
	}

	@Override
	public double getPadding(PaddingParam param) {
		return skinParam.getPadding(param);
	}

	@Override
	public boolean useRankSame() {
		return skinParam.useRankSame();
	}

	@Override
	public boolean displayGenericWithOldFashion() {
		return skinParam.displayGenericWithOldFashion();
	}

	@Override
	public TikzFontDistortion getTikzFontDistortion() {
		return skinParam.getTikzFontDistortion();
	}

	@Override
	public boolean responseMessageBelowArrow() {
		return skinParam.responseMessageBelowArrow();
	}

	@Override
	public boolean svgDimensionStyle() {
		return skinParam.svgDimensionStyle();
	}

	@Override
	public char getCircledCharacter(Stereotype stereotype) {
		return skinParam.getCircledCharacter(stereotype);
	}

	@Override
	public LineBreakStrategy swimlaneWrapTitleWidth() {
		return skinParam.swimlaneWrapTitleWidth();
	}

	@Override
	public boolean fixCircleLabelOverlapping() {
		return skinParam.fixCircleLabelOverlapping();
	}

	@Override
	public void setUseVizJs(boolean useVizJs) {
		skinParam.setUseVizJs(useVizJs);
	}

	@Override
	public boolean isUseVizJs() {
		return skinParam.isUseVizJs();
	}

	@Override
	public void copyAllFrom(ISkinSimple other) {
		skinParam.copyAllFrom(other);
	}

	@Override
	public Map<String, String> values() {
		return skinParam.values();
	}

	@Override
	public HorizontalAlignment getStereotypeAlignment() {
		return skinParam.getStereotypeAlignment();
	}

	@Override
	public Padder sequenceDiagramPadder() {
		return skinParam.sequenceDiagramPadder();
	}

	@Override
	public StyleBuilder getCurrentStyleBuilder() {
		return skinParam.getCurrentStyleBuilder();
	}

	@Override
	public void muteStyle(Style modifiedStyle) {
		skinParam.muteStyle(modifiedStyle);
	}

	@Override
	public Collection<String> getAllSpriteNames() {
		return skinParam.getAllSpriteNames();
	}

	@Override
	public String getDefaultSkin() {
		return skinParam.getDefaultSkin();
	}

	@Override
	public void setDefaultSkin(String newFileName) {
		skinParam.setDefaultSkin(newFileName);
	}

	@Override
	public ActorStyle actorStyle() {
		return skinParam.actorStyle();
	}

	@Override
	public void setSvgSize(String origin, String sizeToUse) {
		skinParam.setSvgSize(origin, sizeToUse);
	}

	@Override
	public String transformStringForSizeHack(String s) {
		return skinParam.transformStringForSizeHack(s);
	}

	@Override
	public LengthAdjust getlengthAdjust() {
		return skinParam.getlengthAdjust();
	}

//	@Override
//	public void assumeTransparent(ThemeStyle style) {
//		skinParam.assumeTransparent(style);
//	}

	@Override
	public ThemeStyle getThemeStyle() {
		return skinParam.getThemeStyle();
	}

}
