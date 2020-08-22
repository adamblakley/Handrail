package com.orienteering.handrail.dialogs
/**
 * Dialog listener, apply obeject text to logic on all user input types for name and note
 *
 */
interface StandardDialogListener {
    fun applyText(username : String, note : String)
}