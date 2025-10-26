package com.example.robot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.robot.ui.theme.NeonBlue
import com.example.robot.ui.theme.SpaceGray
import com.example.robot.ui.theme.TextPrimary
import com.example.robot.viewmodel.MaterialViewModel
import com.example.robot.viewmodel.MetalFilterState

@Composable
fun FilterButton(
    viewModel: MaterialViewModel,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    val activeFilterCount = countActiveFilters(viewModel)

    Box(modifier = modifier) {
        BadgedBox(
            badge = {
                if (activeFilterCount > 0) {
                    Badge(
                        containerColor = NeonBlue,
                        contentColor = TextPrimary
                    ) {
                        Text(text = activeFilterCount.toString())
                    }
                }
            }
        ) {
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Abrir menú de filtros",
                    tint = TextPrimary
                )
            }
        }


        FilterMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false },
            viewModel = viewModel
        )
    }
}

@Composable
private fun countActiveFilters(viewModel: MaterialViewModel): Int {
    val colorFilter by viewModel.colorFilter.collectAsStateWithLifecycle()
    val isMetalFilter by viewModel.isMetalFilter.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()

    var count = 0
    if (colorFilter.isNotEmpty()) count++
    if (isMetalFilter != MetalFilterState.ALL) count++
    if (categoryFilter.isNotEmpty()) count++
    return count
}


@Composable
fun FilterMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: MaterialViewModel
) {
    val availableColors by viewModel.availableColors.collectAsStateWithLifecycle()
    val selectedColors by viewModel.colorFilter.collectAsStateWithLifecycle()

    val metalFilterState by viewModel.isMetalFilter.collectAsStateWithLifecycle()

    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.categoryFilter.collectAsStateWithLifecycle()

    val maxHeight = 300.dp

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(SpaceGray)
            .widthIn(min = 250.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { viewModel.clearFilters() },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.ClearAll, contentDescription = "Limpiar Filtros", tint = NeonBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Limpiar Filtros", color = NeonBlue, style = MaterialTheme.typography.labelMedium)
            }
        }
        HorizontalDivider(color = TextPrimary.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 8.dp))

        Column(
            Modifier
                .heightIn(max = maxHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {

            FilterSectionTitle("Color")
            availableColors.forEach { color ->
                FilterCheckboxItem(
                    text = color,
                    checked = selectedColors.contains(color),
                    onCheckedChange = { viewModel.toggleColorFilter(color) }
                )
            }
            HorizontalDivider(color = TextPrimary.copy(alpha = 0.3f))

            FilterSectionTitle("¿Es Metal?")
            Column(Modifier.selectableGroup()) {
                FilterRadioButtonItem(
                    text = "Todos",
                    selected = metalFilterState == MetalFilterState.ALL,
                    onClick = { viewModel.setMetalFilter(MetalFilterState.ALL) }
                )
                FilterRadioButtonItem(
                    text = "Sí",
                    selected = metalFilterState == MetalFilterState.METAL,
                    onClick = { viewModel.setMetalFilter(MetalFilterState.METAL) }
                )
                FilterRadioButtonItem(
                    text = "No",
                    selected = metalFilterState == MetalFilterState.NON_METAL,
                    onClick = { viewModel.setMetalFilter(MetalFilterState.NON_METAL) }
                )
            }
            HorizontalDivider(color = TextPrimary.copy(alpha = 0.3f))

            FilterSectionTitle("Categoría")
            availableCategories.forEach { category ->
                FilterCheckboxItem(
                    text = category,
                    checked = selectedCategories.contains(category),
                    onCheckedChange = { viewModel.toggleCategoryFilter(category) }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = NeonBlue,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
    )
}

@Composable
fun FilterCheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Checkbox
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = NeonBlue,
                uncheckedColor = TextPrimary.copy(alpha = 0.7f)
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun FilterRadioButtonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = NeonBlue,
                unselectedColor = TextPrimary.copy(alpha = 0.7f)
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}