package com.ems.ui.screen.mnemonics

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ems.domain.model.Mnemonic
import com.ems.domain.model.MnemonicCategory
import com.ems.ui.components.EmsTopBar
import com.ems.ui.components.SectionHeader
import com.ems.ui.theme.*

@Composable
fun MnemonicsScreen(
    onBack: () -> Unit,
    viewModel: MnemonicsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = { EmsTopBar(title = "Clinical Mnemonics", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearch,
                placeholder = { Text("Search mnemonics…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Copied snackbar
            AnimatedVisibility(
                visible = state.copiedMnemonicId != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.12f))
                        .border(1.dp, SuccessGreen, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                    Text("Copied to clipboard!", color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Group by category
                val grouped = state.mnemonics.groupBy { it.category }
                MnemonicCategory.entries.forEach { category ->
                    val items = grouped[category] ?: return@forEach
                    item {
                        SectionHeader(category.displayName)
                    }
                    items(items, key = { it.acronym }) { mnemonic ->
                        MnemonicCard(
                            mnemonic = mnemonic,
                            onCopyToPcr = {
                                clipboard.setText(AnnotatedString(mnemonic.toPcrText()))
                                viewModel.onCopied(mnemonic.acronym)
                                // Auto-clear after 3s
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MnemonicCard(
    mnemonic: Mnemonic,
    onCopyToPcr: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categoryColor = when (mnemonic.category) {
        MnemonicCategory.HISTORY -> ClinicalBlue
        MnemonicCategory.ASSESSMENT -> WarningAmber
        MnemonicCategory.SCENE -> EmergencyRed
        MnemonicCategory.TRAUMA -> Color(0xFF7B1FA2)
        MnemonicCategory.AIRWAY -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Acronym badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(alpha = 0.12f))
                        .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = mnemonic.acronym,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mnemonic.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${mnemonic.items.size} items • ${mnemonic.category.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtleText
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = SubtleText
                )
            }

            // Expanded content
            AnimatedVisibility(visible = expanded) {
                Column {
                    Divider(color = DividerGray)
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mnemonic.items.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Letter badge
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(categoryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.letter,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = categoryColor
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.term,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SubtleText
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        // Copy to PCR button
                        OutlinedButton(
                            onClick = onCopyToPcr,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = categoryColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy to PCR Narrative", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
