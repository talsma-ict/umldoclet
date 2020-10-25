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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.png.PngTitler;
import net.sourceforge.plantuml.utils.MathUtils;

public class SequenceDiagramArea {

	private final double sequenceWidth;
	private final double sequenceHeight;

	private double headerWidth;
	private double headerHeight;
	private double headerMargin;

	private double titleWidth;
	private double titleHeight;

	private double captionWidth;
	private double captionHeight;

	private double footerWidth;
	private double footerHeight;
	private double footerMargin;

	private double legendWidth;
	private double legendHeight;
	private boolean isLegendTop;
	private HorizontalAlignment legendHorizontalAlignment;

	public void setLegend(Dimension2D dimLegend, boolean isLegendTop, HorizontalAlignment horizontalAlignment) {
		this.legendHorizontalAlignment = horizontalAlignment;
		this.legendWidth = dimLegend.getWidth();
		this.legendHeight = dimLegend.getHeight();
		this.isLegendTop = isLegendTop;
	}

	public double getLegendWidth() {
		return legendWidth;
	}

	public boolean hasLegend() {
		return legendHeight > 0 && legendWidth > 0;
	}

	public double getLegendX() {
		if (legendHorizontalAlignment == HorizontalAlignment.LEFT) {
			return 0;
		} else if (legendHorizontalAlignment == HorizontalAlignment.RIGHT) {
			return Math.max(0, getWidth() - legendWidth);
		} else {
			return Math.max(0, getWidth() - legendWidth) / 2;
		}
	}

	public SequenceDiagramArea(double width, double height) {
		this.sequenceWidth = width;
		this.sequenceHeight = height;
	}

	public void setTitleArea(double width, double height) {
		this.titleWidth = width;
		this.titleHeight = height;
	}

	private void setCaptionArea(double width, double height) {
		this.captionWidth = width;
		this.captionHeight = height;
	}

	public void setCaptionArea(Dimension2D dim) {
		setCaptionArea(dim.getWidth(), dim.getHeight());
	}

	public void setHeaderArea(double headerWidth, double headerHeight, double headerMargin) {
		this.headerWidth = headerWidth;
		this.headerHeight = headerHeight;
		this.headerMargin = headerMargin;
	}

	public void setFooterArea(double footerWidth, double footerHeight, double footerMargin) {
		this.footerWidth = footerWidth;
		this.footerHeight = footerHeight;
		this.footerMargin = footerMargin;
	}

	public double getWidth() {
		return MathUtils.max(sequenceWidth, headerWidth, titleWidth, footerWidth, captionWidth);
	}

	public double getHeight() {
		return sequenceHeight + headerHeight + headerMargin + titleHeight + footerMargin + footerHeight + captionHeight
				+ legendHeight;
	}

	public double getFooterY() {
		return sequenceHeight + headerHeight + headerMargin + titleHeight + footerMargin + captionHeight + legendHeight;
	}

	public double getCaptionY() {
		return sequenceHeight + headerHeight + headerMargin + titleHeight + legendHeight;
	}

	public double getLegendY() {
		if (isLegendTop) {
			return titleHeight + headerHeight + headerMargin;
		}
		return sequenceHeight + headerHeight + headerMargin + titleHeight;

	}

	public double getTitleX() {
		return (getWidth() - titleWidth) / 2;
	}

	public double getTitleY() {
		return headerHeight + headerMargin;
	}

	public double getHeaderHeightMargin() {
		return headerHeight + headerMargin;
	}

	public double getCaptionX() {
		return (getWidth() - captionWidth) / 2;
	}

	public double getSequenceAreaX() {
		return (getWidth() - sequenceWidth) / 2;
	}

	public double getSequenceAreaY() {
		if (isLegendTop) {
			return getTitleY() + titleHeight + legendHeight;
		}
		return getTitleY() + titleHeight;
	}

	public double getHeaderY() {
		return 0;
	}

	public double getFooterX(HorizontalAlignment align) {
		if (align == HorizontalAlignment.LEFT) {
			return 0;
		}
		if (align == HorizontalAlignment.RIGHT) {
			return getWidth() - footerWidth;
		}
		if (align == HorizontalAlignment.CENTER) {
			return (getWidth() - footerWidth) / 2;
		}
		throw new IllegalStateException();
	}

	public double getHeaderX(HorizontalAlignment align) {
		if (align == HorizontalAlignment.LEFT) {
			return 0;
		}
		if (align == HorizontalAlignment.RIGHT) {
			return getWidth() - headerWidth;
		}
		if (align == HorizontalAlignment.CENTER) {
			return (getWidth() - headerWidth) / 2;
		}
		throw new IllegalStateException();
	}

	public void initFooter(PngTitler pngTitler, StringBounder stringBounder) {
		final Dimension2D dim = pngTitler.getTextDimension(stringBounder);
		if (dim != null) {
			if (SkinParam.USE_STYLES())
				setFooterArea(dim.getWidth(), dim.getHeight(), 0);
			else
				setFooterArea(dim.getWidth(), dim.getHeight(), 3);
		}
	}

	public void initHeader(PngTitler pngTitler, StringBounder stringBounder) {
		final Dimension2D dim = pngTitler.getTextDimension(stringBounder);
		if (dim != null) {
			if (SkinParam.USE_STYLES())
				setHeaderArea(dim.getWidth(), dim.getHeight(), 0);
			else
				setHeaderArea(dim.getWidth(), dim.getHeight(), 3);
		}
	}

}
