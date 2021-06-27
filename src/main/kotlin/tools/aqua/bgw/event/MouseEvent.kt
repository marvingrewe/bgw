@file:Suppress("unused")

package tools.aqua.bgw.event

/**
 * Event that gets raised for mouse inputs.
 *
 * @param button corresponding mouse button enum value.
 */
class MouseEvent(val button: MouseButtonType) : InputEvent()