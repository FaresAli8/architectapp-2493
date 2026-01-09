package com.generated.procalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.generated.procalculator.data.Calculation
import com.generated.procalculator.ui.CalculatorAction
import com.generated.procalculator.ui.CalculatorViewModel
import com.generated.procalculator.ui.theme.ProCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    val viewModel = viewModel<CalculatorViewModel>()
    val displayState by viewModel.display.collectAsState()
    val resultState by viewModel.result.collectAsState()
    val history by viewModel.history.collectAsState()
    
    var showHistory by remember { mutableStateOf(false) }

    if (showHistory) {
        ModalBottomSheet(onDismissRequest = { showHistory = false }) {
            HistorySheet(
                history = history,
                onClear = { viewModel.onAction(CalculatorAction.ClearHistory) },
                onClose = { showHistory = false },
                onSelect = { 
                    viewModel.onAction(CalculatorAction.HistoryItemClick(it))
                    showHistory = false
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top Bar area with History
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { showHistory = true },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
        }

        // Display Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = displayState,
                fontSize = 48.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (resultState.isNotEmpty()) {
                Text(
                    text = resultState,
                    fontSize = 24.sp,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad
        val buttons = listOf(
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "√", "^",
            "DEL", "="
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(450.dp)
        ) {
            items(buttons) { btn ->
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.aspectRatio(if(btn == "=") 2f else 1f),
                    onClick = {
                        when (btn) {
                            "C" -> viewModel.onAction(CalculatorAction.Clear)
                            "DEL" -> viewModel.onAction(CalculatorAction.Delete)
                            "=" -> viewModel.onAction(CalculatorAction.Calculate)
                            "÷" -> viewModel.onAction(CalculatorAction.Operator("/"))
                            "×" -> viewModel.onAction(CalculatorAction.Operator("*"))
                            "+" -> viewModel.onAction(CalculatorAction.Operator("+"))
                            "-" -> viewModel.onAction(CalculatorAction.Operator("-"))
                            "^" -> viewModel.onAction(CalculatorAction.Operator("^"))
                            "√" -> viewModel.onAction(CalculatorAction.Operator("√")) // sqrt
                            "." -> viewModel.onAction(CalculatorAction.Decimal)
                            "(", ")" -> viewModel.onAction(CalculatorAction.Operator(btn))
                            else -> {
                                btn.toIntOrNull()?.let { 
                                    viewModel.onAction(CalculatorAction.Number(it)) 
                                }
                            }
                        }
                    },
                    isHighlight = btn == "=" || "C" == btn
                )
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isHighlight: Boolean = false
) {
    val backgroundColor = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isHighlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        if (symbol == "DEL") {
            Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = contentColor)
        } else {
            Text(text = symbol, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = contentColor)
        }
    }
}

@Composable
fun HistorySheet(
    history: List<Calculation>,
    onClear: () -> Unit,
    onClose: () -> Unit,
    onSelect: (Calculation) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.history_title), style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClear) {
                Text(stringResource(R.string.clear_history))
            }
        }
        
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, false)) {
            items(history) { calc ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onSelect(calc) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = calc.expression, style = MaterialTheme.typography.bodyMedium)
                        Text(text = "= ${calc.result}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(stringResource(R.string.close))
        }
    }
}