package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinanceScreen(
    viewModel: MinistryViewModel,
    transactions: List<FinancialTransactionEntity>,
    currentLang: AppLanguage
) {
    var showAddTxDialog by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf("All") }

    val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val filteredTransactions = transactions.filter {
        selectedTypeFilter == "All" || it.type.equals(selectedTypeFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leader Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(GoldContainer, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = OnGoldContainer, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ministry Treasury & Finance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Accountant: EBENEZER MUGISHA • Clean Stewardship",
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                    }
                }
            }
        }

        // Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Net Ministry Balance",
                        value = "${String.format("%,.0f", balance)} RWF",
                        subtitle = "Current treasury",
                        icon = Icons.Default.AccountBalance,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Income",
                        value = "${String.format("%,.0f", totalIncome)} RWF",
                        subtitle = "Gifts & fundraising",
                        icon = Icons.Default.ArrowDownward,
                        iconColor = DeepForestGreen,
                        iconBgColor = SageContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Expenses",
                        value = "${String.format("%,.0f", totalExpense)} RWF",
                        subtitle = "Operations & welfare",
                        icon = Icons.Default.ArrowUpward,
                        iconColor = MaterialTheme.colorScheme.error,
                        iconBgColor = Color(0xFFFDE8E8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Financial Journal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                Button(
                    onClick = { showAddTxDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Entry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Filter chips
        item {
            val types = listOf("All", "Income", "Expense")
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(types) { t ->
                    FilterChip(
                        selected = selectedTypeFilter == t,
                        onClick = { selectedTypeFilter = t },
                        label = { Text(t, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredTransactions, key = { it.id }) { tx ->
            val isIncome = tx.type == "Income"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (isIncome) SageContainer else Color(0xFFFDE8E8),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIncome) DeepForestGreen else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.description,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "${tx.category} • ${tx.dateStr}",
                            fontSize = 11.sp,
                            color = CoolGrey
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${if (isIncome) "+" else "-"}${String.format("%,.0f", tx.amount)} RWF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isIncome) DeepForestGreen else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = tx.type,
                            fontSize = 10.sp,
                            color = CoolGrey
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showAddTxDialog) {
        var description by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("Income") }
        var category by remember { mutableStateOf("Donations") }
        var dateStr by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }

        AlertDialog(
            onDismissRequest = { showAddTxDialog = false },
            title = { Text("Record Financial Entry") },
            text = {
                Column {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount in RWF *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Type:", fontSize = 12.sp, color = CoolGrey)
                    val types = listOf("Income", "Expense")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(types) { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(t, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (e.g. Donations, Equipment, Welfare)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull()
                        if (description.isNotBlank() && amount != null && amount > 0) {
                            viewModel.addTransaction(
                                FinancialTransactionEntity(
                                    type = type,
                                    category = category,
                                    amount = amount,
                                    description = description,
                                    dateStr = dateStr,
                                    recordedBy = "Ebenezer Mugisha"
                                )
                            )
                            showAddTxDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Save Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTxDialog = false }) { Text("Cancel") }
            }
        )
    }
}
