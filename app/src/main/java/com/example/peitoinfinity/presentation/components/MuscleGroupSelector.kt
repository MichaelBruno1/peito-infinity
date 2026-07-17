package com.example.peitoinfinity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.peitoinfinity.domain.model.MuscleGroup
import com.example.peitoinfinity.ui.theme.PeitoDimens
import com.example.peitoinfinity.ui.theme.toColor

@Composable
fun MuscleGroupSelector(
    selectedGroup: MuscleGroup?,
    onGroupSelected: (MuscleGroup?) -> Unit,
    modifier: Modifier = Modifier,
    groups: List<MuscleGroup> = listOf(
        MuscleGroup.CHEST,
        MuscleGroup.BACK,
        MuscleGroup.SHOULDERS,
        MuscleGroup.BICEPS,
        MuscleGroup.TRICEPS,
        MuscleGroup.QUADRICEPS,
        MuscleGroup.HAMSTRINGS,
        MuscleGroup.GLUTES,
        MuscleGroup.CALVES,
        MuscleGroup.ABS,
        MuscleGroup.FOREARMS,
        MuscleGroup.CARDIO_SYSTEM,
        MuscleGroup.TRAPS
    )
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = PeitoDimens.paddingMd)
    ) {
        // Opção "Todos"
        item {
            val isSelected = selectedGroup == null
            FilterChip(
                selected = isSelected,
                onClick = { onGroupSelected(null) },
                label = { Text("Todos") }
            )
        }

        items(groups) { group ->
            val isSelected = selectedGroup == group
            val muscleColor = group.toColor()
            FilterChip(
                selected = isSelected,
                onClick = { onGroupSelected(group) },
                label = { Text(group.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = muscleColor.copy(alpha = 0.25f),
                    selectedLabelColor = muscleColor,
                    selectedLeadingIconColor = muscleColor
                )
            )
        }
    }
}
