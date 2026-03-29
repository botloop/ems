package com.ems.domain.model

data class Mnemonic(
    val acronym: String,
    val title: String,
    val category: MnemonicCategory,
    val items: List<MnemonicItem>
) {
    fun toPcrText(): String = buildString {
        appendLine("=== $acronym - $title ===")
        items.forEach { item ->
            appendLine("${item.letter} - ${item.term}: ___________________")
        }
    }
}

data class MnemonicItem(
    val letter: String,
    val term: String,
    val description: String
)

enum class MnemonicCategory(val displayName: String) {
    HISTORY("History Taking"),
    ASSESSMENT("Physical Assessment"),
    SCENE("Scene Management"),
    TRAUMA("Trauma"),
    AIRWAY("Airway")
}
