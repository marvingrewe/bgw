@file:Suppress("unused")

package tools.aqua.bgw.visual

import tools.aqua.bgw.observable.ObjectProperty
import java.awt.Color

/**
 * A solid color visual.
 * Displays a rectangle filled with the given color.
 *
 * @param color color to use as filling.
 */
class ColorVisual(color: Color) : SingleLayerVisual() {
	
	/**
	 * Property for the displayed color of this visual.
	 * The alpha channel gets multiplied with the transparency property i.e. alpha = 128 (50%)
	 * and transparency = 0.5 (50%) leads to 25% visibility / 75% transparency.
	 */
	val colorProperty: ObjectProperty<Color> = ObjectProperty(color)
	
	/**
	 * The displayed color of this visual.
	 * The alpha channel gets multiplied with the transparency property i.e. alpha = 128 (50%)
	 * and transparency = 0.5 (50%) leads to 25% visibility / 75% transparency.
	 */
	var color: Color
		get() = colorProperty.value
		set(value) {
			colorProperty.value = value
		}
	
	/**
	 * A solid color visual.
	 * Displays a rectangle filled with the given color.
	 *
	 * @param r red channel.
	 * @param g green channel.
	 * @param b blue channel.
	 * @param a alpha channel. Default: 255.
	 */
	constructor(r: Int, g: Int, b: Int, a: Int = 255) : this(Color(r, g, b, a))
}