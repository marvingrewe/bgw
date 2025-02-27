/*
 *    Copyright 2021 The BoardGameWork Authors
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package layoutelements.grid

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.uicomponents.UIComponent
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IteratorTest : GridPaneTestBase() {
	@Test
	@DisplayName("Iterator order test")
	fun testIteratorFullOrder() {
		val order = Stack<UIComponent>()
		for (i in 0..2) {
			for (j in 0..2) {
				order.push(contents[i][j])
			}
		}
		order.reverse()
		
		val iterator = grid.iterator()
		repeat(9) {
			assertTrue { iterator.hasNext() }
			
			val nextItem = iterator.next()
			
			assertEquals(it / 3, nextItem.columnIndex)
			assertEquals(it % 3, nextItem.rowIndex)
			assertEquals(order.pop(), nextItem.component)
		}
		
		assertFalse { iterator.hasNext() }
		assertFailsWith<NoSuchElementException> { iterator.next() }
	}
	
	@Test
	@DisplayName("Iterator test on empty grid")
	fun testIteratorOnEmptyGrid() {
		val emptyGrid = GridPane<UIComponent>(0, 0, 0, 0)
		
		val iterator = emptyGrid.iterator()
		
		assertFalse { iterator.hasNext() }
		assertFailsWith<NoSuchElementException> { iterator.next() }
	}
}