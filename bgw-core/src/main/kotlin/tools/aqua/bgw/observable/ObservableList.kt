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

@file:Suppress("unused", "MemberVisibilityCanBePrivate", "TooManyFunctions")

package tools.aqua.bgw.observable

import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.UnaryOperator
import kotlin.streams.toList

/**
 * An observable [List] implementation.
 *
 * @constructor Creates an [ObservableList].
 */
abstract class ObservableList<T> :ValueObservable<List<T>>(), Iterable<T> {
	
	/**
	 * Returns the number of elements in this list.
	 */
	val size: Int
		get() = list.size
	
	/**
	 * All available indices of this list as IntRange i.e. 0..size()-1.
	 */
	val indices: IntRange
		get() = 0 until size
	
	/**
	 * List field.
	 */
	abstract val list: MutableList<T>
	
	/**
	 * Returns `true` if this list contains no elements.
	 *
	 * @return `true` if this list contains no elements.
	 */
	fun isEmpty(): Boolean = list.size == 0
	
	/**
	 * Returns `true` if this list contains elements.
	 *
	 * @return `true` if this list contains elements.
	 */
	fun isNotEmpty(): Boolean = !isEmpty()
	
	/**
	 * Returns `true` if this list contains the specified element.
	 *
	 * More formally, returns `true` if and only if this list contains
	 * at least one element `e` such that
	 * `Objects.equals(o, e)`.
	 *
	 * @param o Element whose presence in this list is to be tested.
	 *
	 * @return `true` if this list contains the specified element, `false` otherwise.
	 */
	operator fun contains(o: Any?): Boolean = list.contains(o)
	
	/**
	 * Returns the index of the first occurrence of the specified element in this list,
	 * or -1 if this list does not contain the element.
	 *
	 * More formally, returns the lowest index `i` such that
	 * `Objects.equals(o, get(i))`, or -1 if there is no such index.
	 *
	 * @return First index of the element, or -1 if the list does not contain the element.
	 */
	fun indexOf(o: Any?): Int = list.indexOf(o)
	
	/**
	 * Returns the index of the last occurrence of the specified element in this list,
	 * or -1 if this list does not contain the element.
	 *
	 * More formally, returns the highest index `i` such that
	 * `Objects.equals(o, get(i))`,
	 * or -1 if there is no such index.
	 *
	 * @return Last index of the element, or -1 if the list does not contain the element.
	 */
	fun lastIndexOf(o: Any?): Int = list.lastIndexOf(o)
	
	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index Index of the element to return.
	 *
	 * @return The element at the specified position in this list.
	 *
	 * @throws IndexOutOfBoundsException If the index exceeds the list's bounds.
	 */
	operator fun get(index: Int): T = list[index]
	
	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 *
	 * @param index Index of the element to replace.
	 * @param element Element to be stored at the specified position.
	 *
	 * @return The element previously at the specified position.
	 *
	 * @throws IndexOutOfBoundsException If the index exceeds the list's bounds.
	 */
	operator fun set(index: Int, element: T): T {
		val snapshot = this.toList()
		val oldValue: T = list[index]
		list[index] = element
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return oldValue
	}
	
	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param element Element to be appended to this list.
	 */
	fun add(element: T): Boolean {
		val snapshot = this.toList()
		val result = list.add(element)
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return result
	}
	
	/**
	 * Inserts the specified element at the specified position in this list.
	 *
	 * Shifts the element currently at that position (if any) and any subsequent elements to the right
	 * (adds one to their indices).
	 *
	 * @param index Index at which the specified element is to be inserted.
	 * @param element Element to be inserted.
	 *
	 * @throws IndexOutOfBoundsException If the index exceeds the list's bounds.
	 */
	fun add(index: Int, element: T) {
		val snapshot = this.toList()
		list.add(index, element)
		notifyChange(oldValue = snapshot, newValue = this.toList())
	}
	
	/**
	 * Removes the first occurrence of the specified element from this list, if it is present.
	 *
	 * If the list does not contain the element, it is unchanged.
	 *
	 * More formally, removes the element with the lowest index `i` such that `Objects.equals(o, get(i))`
	 * (if such an element exists).
	 *
	 * Returns `true` if this list contained the specified element
	 * (or equivalently, if this list changed as a result of the call).
	 *
	 * @param o Element to be removed from this list, if present.
	 *
	 * @return `true` if this list contained the specified element.
	 */
	fun remove(o: T): Boolean {
		val snapshot = this.toList()
		val result = list.remove(o)
		
		if (result)
			notifyChange(oldValue = snapshot, newValue = this.toList())
		
		return result
	}
	
	/**
	 * Removes an element at the specified [index] from the list.
	 *
	 * @return The element that has been removed.
	 *
	 * @throws IndexOutOfBoundsException If the index exceeds the list's bounds.
	 */
	fun removeAt(index: Int): T {
		val snapshot = this.toList()
		val result = list.removeAt(index)
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return result
	}
	
	/**
	 * Removes the first element from this list and returns that removed element,
	 * or returns `null` if this list is empty.
	 */
	fun removeFirstOrNull(): T? {
		val snapshot = this.toList()
		val result = list.removeFirstOrNull()
		
		if (result != null)
			notifyChange(oldValue = snapshot, newValue = this.toList())
		
		return result
	}
	
	/**
	 * Removes the first element from this list and returns that removed element.
	 *
	 * @throws NoSuchElementException If the list was empty.
	 */
	fun removeFirst(): T = removeFirstOrNull() ?: throw NoSuchElementException("List is empty")
	
	/**
	 * Removes the last element from this list and returns that removed element,
	 * or returns `null` if this list is empty.
	 */
	fun removeLastOrNull(): T? {
		val snapshot = this.toList()
		val result = list.removeLastOrNull()
		
		if (result != null)
			notifyChange(oldValue = snapshot, newValue = this.toList())
		
		return result
	}
	
	/**
	 * Removes the last element from this list and returns that removed element.
	 *
	 * @throws NoSuchElementException If the list was empty.
	 */
	fun removeLast(): T = removeLastOrNull() ?: throw NoSuchElementException("List is empty")
	
	
	/**
	 * Removes all elements from this list. The list will be empty after this call returns.
	 */
	fun clear() {
		val snapshot = this.toList()
		if (list.isNotEmpty()) {
			list.clear()
			notifyChange(oldValue = snapshot, newValue = this.toList())
		}
	}
	
	/**
	 * Appends all elements in the specified collection to the end of this list,
	 * in the order that they are returned by the specified collection's Iterator.
	 *
	 * The behavior of this operation is undefined if the specified collection is modified
	 * while the operation is in progress (This implies that the behavior of this call is undefined
	 * if the specified collection is this list, and this list is nonempty).
	 *
	 * @param elements [Collection] containing elements to be added to this list.
	 *
	 * @return `true` if this list changed as a result of the call.
	 *
	 * @throws NullPointerException If the specified collection is null.
	 */
	fun addAll(elements: Collection<T>): Boolean {
		val snapshot = this.toList()
		val result = list.addAll(elements)
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return result
	}
	
	/**
	 * Inserts all elements in the specified collection into this list,
	 * starting at the specified position. Shifts the element currently at that position (if any)
	 * and any subsequent elements to the right (increases their indices). The new elements will appear
	 * in the list in the order that they are returned by the specified collection's iterator.
	 *
	 * @param index Index at which to insert the first element from the specified collection.
	 * @param elements [Collection] containing elements to be added to this list.
	 *
	 * @return `true` if this list changed as a result of the call.
	 *
	 * @throws IndexOutOfBoundsException If the index exceeds the list's bounds.
	 */
	fun addAll(index: Int, elements: Collection<T>): Boolean {
		val snapshot = this.toList()
		val result = list.addAll(index, elements)
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return result
	}
	
	/**
	 * Removes from this list all of its elements that are contained in the specified collection.
	 *
	 * @param elements [Collection] containing elements to be removed from this list.
	 *
	 * @return `true` if this list changed as a result of the call.
	 *
	 * @throws ClassCastException If the class of an element of this list is incompatible with the specified collection.
	 * @throws NullPointerException If this list contains a `null` element and the specified collection
	 * does not permit `null` elements.
	 *
	 * @see Collection.contains
	 */
	fun removeAll(elements: Collection<*>): Boolean {
		val snapshot = this.toList()
		val result = list.removeAll(elements)
		
		if (result)
			notifyChange(oldValue = snapshot, newValue = this.toList())
		
		return result
	}
	
	/**
	 * Retains only the elements in this list that are contained in the specified collection.
	 *
	 * In other words, removes from this list all of its elements that are not contained in the specified collection.
	 *
	 * @param elements Collection containing elements to be retained in this list.
	 *
	 * @return `true` if this list changed as a result of the call.
	 *
	 * @throws ClassCastException If the class of an element of this list is incompatible with the specified collection.
	 * @throws NullPointerException If this list contains a `null` element and the specified collection
	 * does not permit `null` elements.
	 *
	 * @see Collection.contains
	 */
	fun retainAll(elements: Collection<*>): Boolean {
		val snapshot = this.toList()
		val result = list.retainAll(elements)
		notifyChange(oldValue = snapshot, newValue = this.toList())
		return result
	}
	
	/**
	 * Returns a view of the portion of this list between the specified `fromIndex` inclusive and `toIndex` exclusive.
	 * (If `fromIndex` and `toIndex` are equal, the returned list is empty.)
	 *
	 * The returned list is backed by this list, so non-structural changes in the returned list
	 * are reflected in this list, and vice-versa.
	 *
	 * The returned list supports all optional list operations.
	 *
	 *
	 * This method eliminates the need for explicit range operations (of the sort that commonly exist for arrays).
	 *
	 * Any operation that expects a list can be used as a range operation
	 * by passing a subList view instead of a whole list.
	 *
	 * For example, the following idiom removes a range of elements from a list:
	 *
	 * <pre>
	 * list.subList(from, to).clear();
	 * </pre>
	 *
	 * Similar idioms may be constructed for [.indexOf] and [.lastIndexOf], and all algorithms in the
	 * [Collections] class can be applied to a subList.
	 *
	 * The semantics of the list returned by this method become undefined if the backing list (i.e., this list)
	 * is *structurally modified* in any way other than via the returned list (Structural modifications are those that
	 * change the size of this list, or otherwise perturb it in such a fashion that iterations in progress may yield
	 * incorrect results.).
	 *
	 * @throws IndexOutOfBoundsException If any index exceeds the list's bounds.
	 * @throws IllegalArgumentException If the endpoint indices are out of order (fromIndex > toIndex).
	 */
	fun subList(fromIndex: Int, toIndex: Int): List<T> = list.subList(fromIndex, toIndex)
	
	/**
	 * ForEach action.
	 */
	fun forEach(action: Consumer<in T>): Unit = list.forEach(action)
	
	/**
	 * Creates a *fail-fast* [Spliterator] over the elements in this list.
	 *
	 * @return A [Spliterator] over the elements in this list
	 */
	fun spliterator(): Spliterator<T> = list.spliterator()
	
	/**
	 * Removes all elements of this collection that satisfy the given predicate.
	 *
	 * Errors or runtime exceptions thrown during iteration or by the predicate are relayed to the caller.
	 *
	 * @param filter A predicate which returns `true` for elements to be removed.
	 *
	 * @return `true` if elements were removed.
	 * @throws NullPointerException If the specified filter is null
	 */
	fun removeIf(filter: Predicate<in T>): Boolean {
		val snapshot = this.toList()
		val result = list.removeIf(filter)
		
		if (result)
			notifyChange(oldValue = snapshot, newValue = this.toList())
		
		return result
	}
	
	/**
	 * Replaces each element of this list with the result of applying the operator to that element.
	 *
	 * @param operator The operator to apply to each element.
	 */
	fun replaceAll(operator: UnaryOperator<T>) {
		val snapshot = this.toList()
		list.replaceAll(operator)
		notifyChange(oldValue = snapshot, newValue = this.toList())
	}
	
	/**
	 * Sorts this list by given comparator.
	 *
	 * @param comparator [Comparator] to be applied. If the elements contained in this list implement the comparable
	 * interface, pass null.
	 */
	fun sort(comparator: Comparator<in T>?) {
		val sorted = list.stream().sorted(comparator).toList()
		list.clear()
		list.addAll(sorted)
		//notifyChange() not necessary since addAll() already notified.
	}
	
	/**
	 * Returns an [Iterator] over the elements in this [ObservableList].
	 *
	 * @return [Iterator] over the elements in this list.
	 */
	override fun iterator(): Iterator<T> = list.iterator()
	
	/**
	 * Sets [list] silently.
	 */
	internal fun setSilent(elements: List<T>) {
		list.clear()
		list.addAll(elements)
	}
}