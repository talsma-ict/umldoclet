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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ComponentStyle;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.skin.ActorStyle;
import net.sourceforge.plantuml.svek.PackageStyle;

public abstract class USymbol {

	private static final Map<String, USymbol> all = new HashMap<String, USymbol>();

	public final static USymbol STORAGE = record("STORAGE", SkinParameter.STORAGE, new USymbolStorage());
	public final static USymbol DATABASE = record("DATABASE", SkinParameter.DATABASE, new USymbolDatabase());
	public final static USymbol CLOUD = record("CLOUD", SkinParameter.CLOUD, new USymbolCloud());
	public final static USymbol CARD = record("CARD", SkinParameter.CARD, new USymbolCard(SkinParameter.CARD));
	public final static USymbol FRAME = record("FRAME", SkinParameter.FRAME, new USymbolFrame());
	public final static USymbol NODE = record("NODE", SkinParameter.NODE, new USymbolNode());
	public final static USymbol ARTIFACT = record("ARTIFACT", SkinParameter.ARTIFACT, new USymbolArtifact());
	public final static USymbol PACKAGE = record("PACKAGE", SkinParameter.PACKAGE,
			new USymbolFolder(SkinParameter.PACKAGE, true));
	public final static USymbol FOLDER = record("FOLDER", SkinParameter.FOLDER,
			new USymbolFolder(SkinParameter.FOLDER, false));
	public final static USymbol FILE = record("FILE", SkinParameter.FILE, new USymbolFile());
	public final static USymbol RECTANGLE = record("RECTANGLE", SkinParameter.RECTANGLE,
			new USymbolRectangle(SkinParameter.RECTANGLE));
	public final static USymbol HEXAGON = record("HEXAGON", SkinParameter.HEXAGON, new USymbolHexagon());
	public final static USymbol PERSON = record("PERSON", SkinParameter.PERSON, new USymbolPerson());
	public final static USymbol LABEL = record("LABEL", SkinParameter.LABEL,
			new USymbolLabel(SkinParameter.LABEL));
	public final static USymbol ARCHIMATE = record("ARCHIMATE", SkinParameter.ARCHIMATE,
			new USymbolRectangle(SkinParameter.ARCHIMATE));
	public final static USymbol COLLECTIONS = record("COLLECTIONS", SkinParameter.COLLECTIONS,
			new USymbolCollections(SkinParameter.COLLECTIONS));
	public final static USymbol AGENT = record("AGENT", SkinParameter.AGENT, new USymbolRectangle(SkinParameter.AGENT));
	public final static USymbol ACTOR_STICKMAN = record("ACTOR_STICKMAN", SkinParameter.ACTOR,
			new USymbolActor(ActorStyle.STICKMAN));
	public final static USymbol ACTOR_STICKMAN_BUSINESS = record("ACTOR_STICKMAN_BUSINESS", SkinParameter.ACTOR,
			new USymbolActor(ActorStyle.STICKMAN_BUSINESS));
	public final static USymbol ACTOR_AWESOME = record("ACTOR_AWESOME", SkinParameter.ACTOR,
			new USymbolActor(ActorStyle.AWESOME));
	public final static USymbol ACTOR_HOLLOW = record("ACTOR_HOLLOW", SkinParameter.ACTOR,
			new USymbolActor(ActorStyle.HOLLOW));
	public final static USymbol USECASE = null;
	public final static USymbol COMPONENT1 = record("COMPONENT1", SkinParameter.COMPONENT1, new USymbolComponent1());
	public final static USymbol COMPONENT2 = record("COMPONENT2", SkinParameter.COMPONENT2, new USymbolComponent2());
	public final static USymbol BOUNDARY = record("BOUNDARY", SkinParameter.BOUNDARY, new USymbolBoundary());
	public final static USymbol ENTITY_DOMAIN = record("ENTITY_DOMAIN", SkinParameter.ENTITY,
			new USymbolEntityDomain());
	public final static USymbol CONTROL = record("CONTROL", SkinParameter.CONTROL, new USymbolControl());
	public final static USymbol INTERFACE = record("INTERFACE", SkinParameter.INTERFACE, new USymbolInterface());
	public final static USymbol QUEUE = record("QUEUE", SkinParameter.QUEUE, new USymbolQueue());
	public final static USymbol STACK = record("STACK", SkinParameter.STACK, new USymbolStack());
	public final static USymbol TOGETHER = record("TOGETHER", SkinParameter.QUEUE, new USymbolTogether());

	abstract public SkinParameter getSkinParameter();

	// public USymbol withStereoAlignment(HorizontalAlignment alignment) {
	// return this;
	// }

	public FontParam getFontParam() {
		return getSkinParameter().getFontParam();
	}

	public FontParam getFontParamStereotype() {
		return getSkinParameter().getFontParamStereotype();

	}

	public ColorParam getColorParamBack() {
		return getSkinParameter().getColorParamBack();
	}

	public ColorParam getColorParamBorder() {
		return getSkinParameter().getColorParamBorder();
	}

	private static USymbol record(String code, SkinParameter skinParameter, USymbol symbol) {
		all.put(StringUtils.goUpperCase(code), symbol);
		return symbol;
	}

	public abstract TextBlock asSmall(TextBlock name, TextBlock label, TextBlock stereotype,
			SymbolContext symbolContext, HorizontalAlignment stereoAlignment);

	public abstract TextBlock asBig(TextBlock label, HorizontalAlignment labelAlignment, TextBlock stereotype,
			double width, double height, SymbolContext symbolContext, HorizontalAlignment stereoAlignment);

	static class Margin {
		private final double x1;
		private final double x2;
		private final double y1;
		private final double y2;

		Margin(double x1, double x2, double y1, double y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}

		double getWidth() {
			return x1 + x2;
		}

		double getHeight() {
			return y1 + y2;
		}

		public Dimension2D addDimension(Dimension2D dim) {
			return new Dimension2DDouble(dim.getWidth() + x1 + x2, dim.getHeight() + y1 + y2);
		}

		public double getX1() {
			return x1;
		}

		public double getY1() {
			return y1;
		}
	}

	public int suppHeightBecauseOfShape() {
		return 0;
	}

	public int suppWidthBecauseOfShape() {
		return 0;
	}

//	final Stencil getRectangleStencil(final Dimension2D dim) {
//		return new Stencil() {
//			public double getStartingX(StringBounder stringBounder, double y) {
//				return 0;
//			}
//
//			public double getEndingX(StringBounder stringBounder, double y) {
//				return dim.getWidth();
//			}
//		};
//	}

	public static USymbol fromString(String s, ActorStyle actorStyle, ComponentStyle componentStyle,
			PackageStyle packageStyle) {
		if (s == null) {
			return null;
		}
		if (s.equalsIgnoreCase("package")) {
			return packageStyle.toUSymbol();
		}
		if (s.equalsIgnoreCase("actor")) {
			return actorStyle.toUSymbol();
		}
		if (s.equalsIgnoreCase("component")) {
			return componentStyle.toUSymbol();
		}
		if (s.equalsIgnoreCase("entity")) {
			return ENTITY_DOMAIN;
		}
		if (s.equalsIgnoreCase("circle")) {
			return INTERFACE;
		}
		final USymbol result = all.get(StringUtils.goUpperCase(s.replaceAll("\\W", "")));
		return result;
	}

	public static USymbol fromString(String symbol, ISkinParam skinParam) {
		USymbol usymbol = null;
		if (symbol.equalsIgnoreCase("artifact")) {
			usymbol = USymbol.ARTIFACT;
		} else if (symbol.equalsIgnoreCase("folder")) {
			usymbol = USymbol.FOLDER;
		} else if (symbol.equalsIgnoreCase("file")) {
			usymbol = USymbol.FILE;
		} else if (symbol.equalsIgnoreCase("package")) {
			usymbol = USymbol.PACKAGE;
		} else if (symbol.equalsIgnoreCase("rectangle")) {
			usymbol = USymbol.RECTANGLE;
		} else if (symbol.equalsIgnoreCase("person")) {
			usymbol = USymbol.PERSON;
		} else if (symbol.equalsIgnoreCase("hexagon")) {
			usymbol = USymbol.HEXAGON;
		} else if (symbol.equalsIgnoreCase("label")) {
			usymbol = USymbol.LABEL;
		} else if (symbol.equalsIgnoreCase("collections")) {
			usymbol = USymbol.COLLECTIONS;
		} else if (symbol.equalsIgnoreCase("node")) {
			usymbol = USymbol.NODE;
		} else if (symbol.equalsIgnoreCase("frame")) {
			usymbol = USymbol.FRAME;
		} else if (symbol.equalsIgnoreCase("cloud")) {
			usymbol = USymbol.CLOUD;
		} else if (symbol.equalsIgnoreCase("database")) {
			usymbol = USymbol.DATABASE;
		} else if (symbol.equalsIgnoreCase("queue")) {
			usymbol = USymbol.QUEUE;
		} else if (symbol.equalsIgnoreCase("stack")) {
			usymbol = USymbol.STACK;
		} else if (symbol.equalsIgnoreCase("storage")) {
			usymbol = USymbol.STORAGE;
		} else if (symbol.equalsIgnoreCase("agent")) {
			usymbol = USymbol.AGENT;
		} else if (symbol.equalsIgnoreCase("actor/")) {
			usymbol = USymbol.ACTOR_STICKMAN_BUSINESS;
		} else if (symbol.equalsIgnoreCase("actor")) {
			usymbol = skinParam.actorStyle().toUSymbol();
		} else if (symbol.equalsIgnoreCase("component")) {
			usymbol = skinParam.componentStyle().toUSymbol();
		} else if (symbol.equalsIgnoreCase("boundary")) {
			usymbol = USymbol.BOUNDARY;
		} else if (symbol.equalsIgnoreCase("control")) {
			usymbol = USymbol.CONTROL;
		} else if (symbol.equalsIgnoreCase("entity")) {
			usymbol = USymbol.ENTITY_DOMAIN;
		} else if (symbol.equalsIgnoreCase("card")) {
			usymbol = USymbol.CARD;
		} else if (symbol.equalsIgnoreCase("interface")) {
			usymbol = USymbol.INTERFACE;
		} else if (symbol.equalsIgnoreCase("()")) {
			usymbol = USymbol.INTERFACE;
		}
		return usymbol;
	}
}
