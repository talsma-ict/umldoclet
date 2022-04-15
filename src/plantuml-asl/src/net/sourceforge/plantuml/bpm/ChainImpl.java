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
package net.sourceforge.plantuml.bpm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

public class ChainImpl<O> implements Chain<O> {

	private final List<O> positive = new ArrayList<>();
	private final List<O> negative = new ArrayList<>();
	private int currentVersion;

	public boolean remove(O data) {
		updateStructuralVersion();
		boolean result = positive.remove(data);
		if (result == false) {
			result = negative.remove(data);
		}
		return result;
	}

	public ChainImpl<O> cloneMe() {
		final ChainImpl<O> result = new ChainImpl<>();
		result.currentVersion = this.currentVersion;
		result.positive.addAll(this.positive);
		result.negative.addAll(this.negative);
		return result;
	}

	public int compare(O a, O b) {
		if (a.equals(b)) {
			return 0;
		}
		for (int i = negative.size() - 1; i >= 0; i--) {
			if (a.equals(negative.get(i))) {
				return -1;
			}
			if (b.equals(negative.get(i))) {
				return 1;
			}
		}
		for (O cur : positive) {
			if (a.equals(cur)) {
				return -1;
			}
			if (b.equals(cur)) {
				return 1;
			}
		}
		throw new UnsupportedOperationException();
	}

	public List<O> toList() {
		final List<O> result = new ArrayList<>();
		for (O element : negative) {
			if (element != null) {
				result.add(0, element);
			}
		}
		for (O element : positive) {
			if (element != null) {
				result.add(element);
			}
		}
		return Collections.unmodifiableList(result);
	}

	private ChainImpl() {
	}

	public ChainImpl(O root) {
		this.positive.add(Objects.requireNonNull(root));
	}

	private int updateStructuralVersion() {
		currentVersion++;
		return currentVersion;
	}

	public boolean contains(O data) {
		Objects.requireNonNull(data);
		for (int i = 0; i < Math.max(positive.size(), negative.size()); i++) {
			if (i < positive.size() && data == positive.get(i)) {
				return true;
			}
			if (i < negative.size() && data == negative.get(i)) {
				return true;
			}
		}
		return false;
	}

	public Navigator<O> navigator(O data) {
		Objects.requireNonNull(data);
		for (int i = 0; i < Math.max(positive.size(), negative.size()); i++) {
			if (i < positive.size() && data == positive.get(i)) {
				final InternalNavigator result = new InternalNavigator(i, currentVersion);
				assert result.get() == data;
				return result;
			}
			if (i < negative.size() && data == negative.get(i)) {
				final InternalNavigator result = new InternalNavigator(-i - 1, currentVersion);
				assert result.get() == data;
				return result;
			}
		}
		throw new IllegalArgumentException();
	}

	private O getInternal(int position) {
		ensure(position);
		if (position >= 0) {
			return positive.get(position);
		} else {
			return negative.get(-position - 1);
		}
	}

	private void setInternal(int position, O data) {
		Objects.requireNonNull(data);
		ensure(position);
		if (position >= 0) {
			positive.set(position, data);
		} else {
			negative.set(-position - 1, data);
		}
	}

	private void insertInternal(int position, O data) {
		Objects.requireNonNull(data);
		ensure(position);
		if (position >= 0) {
			positive.add(position, data);
		} else {
			negative.add(-position - 1, data);
		}
	}

	private void ensure(int position) {
		if (position >= 0) {
			ensureInternal(position, positive);
		} else {
			ensureInternal(-position - 1, negative);
		}
	}

	private void ensureInternal(int position, List<O> list) {
		assert position >= 0 : "position=" + position;
		while (list.size() <= position) {
			list.add(null);
		}
		assert list.size() > position;
		// Just check that list.get(position) does not throw Exception
		assert list.get(position) != this;
	}

	class InternalNavigator implements Navigator<O> {

		private int position = 0;
		private int version;

		private InternalNavigator(int position, int version) {
			this.position = position;
			this.version = version;
		}

		private void checkConsistency() {
			if (version != currentVersion) {
				throw new ConcurrentModificationException();
			}
		}

		public O next() {
			checkConsistency();
			position++;
			return get();
		}

		public O previous() {
			checkConsistency();
			position--;
			return get();
		}

		public O get() {
			checkConsistency();
			return getInternal(position);
		}

		public void set(O data) {
			checkConsistency();
			setInternal(position, data);
		}

		public void insertBefore(O data) {
			version = updateStructuralVersion();
			insertInternal(position, data);
		}

		public void insertAfter(O data) {
			version = updateStructuralVersion();
			insertInternal(position + 1, data);
		}
	}

}
