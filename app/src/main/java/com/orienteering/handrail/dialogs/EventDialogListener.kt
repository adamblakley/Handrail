package com.orienteering.handrail.dialogs

/**
 * Dialog listener, apply event text on event edit or creation
 *
 */
interface EventDialogListener {
    fun applyEventText(name : String, note : String)
}