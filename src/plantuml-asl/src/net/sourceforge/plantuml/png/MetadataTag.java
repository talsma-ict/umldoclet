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
package net.sourceforge.plantuml.png;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SImageIO;

public class MetadataTag {

	private final Object source;
	private final String tag;

	public MetadataTag(SFile file, String tag) throws FileNotFoundException {
		this.source = file.conv();
		this.tag = tag;
	}

	public MetadataTag(java.io.File file, String tag) {
		this.source = file;
		this.tag = tag;
	}

	public MetadataTag(InputStream is, String tag) {
		this.source = is;
		this.tag = tag;
	}

	public String getData() throws IOException {
		final ImageInputStream iis = SImageIO.createImageInputStream(source);
		final Iterator<ImageReader> readers = SImageIO.getImageReaders(iis);

		if (readers.hasNext()) {
			// pick the first available ImageReader
			final ImageReader reader = readers.next();

			// attach source to the reader
			reader.setInput(iis, true);

			// read metadata of first image
			final IIOMetadata metadata = reader.getImageMetadata(0);

			final String[] names = metadata.getMetadataFormatNames();
			final int length = names.length;
			for (int i = 0; i < length; i++) {
				final String result = displayMetadata(metadata.getAsTree(names[i]));
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	private String displayMetadata(Node root) {
		return displayMetadata(root, 0);
	}

	private String displayMetadata(Node node, int level) {
		final NamedNodeMap map = node.getAttributes();
		if (map != null) {
			final Node keyword = map.getNamedItem("keyword");
			if (keyword != null && tag.equals(keyword.getNodeValue())) {
				final Node text = map.getNamedItem("value");
				if (text != null) {
					return text.getNodeValue();
				}
			}
		}

		Node child = node.getFirstChild();

		// children, so close current tag
		while (child != null) {
			// print children recursively
			final String result = displayMetadata(child, level + 1);
			if (result != null) {
				return result;
			}
			child = child.getNextSibling();
		}

		return null;

	}

}
