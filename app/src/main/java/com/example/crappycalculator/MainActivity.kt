package com.example.crappycalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.crappycalculator.ui.theme.CrappyCalculatorTheme

enum class Screen {
    Calculator,
    History,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrappyCalculatorTheme {
                val viewModel: CalculatorViewModel by viewModels()
                val navController = rememberNavController()

                Scaffold(
                    topBar = { TopBarDisplay(viewModel, navController) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Calculator.name,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()
                    ) {
                        composable(route = Screen.Calculator.name) {
                            Column(verticalArrangement = Arrangement.SpaceBetween) {
                                InputDisplay(viewModel)
                                Spacer(modifier = Modifier.height(16.dp))
                                Keypad(viewModel)
                            }
                        }
                        composable(route = Screen.History.name) {
                            HistoryDisplay(viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}

//
// Top Bar
//

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarDisplay(vm: CalculatorViewModel, nav: NavHostController) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("text/plain")) {
            it?.let {
                context.contentResolver.openOutputStream(it)
                    ?.use { out -> out.write(state.history.joinToString("\n").toByteArray()) }
            }
        }

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(backStackEntry?.destination?.route ?: Screen.Calculator.name)

    TopAppBar(
        title = { Text(text = currentScreen.name) },
        actions = {
            OutlinedButton(onClick = {
                launcher.launch("calculator_export.txt")
            }) {
                Text(text = "Export")
            }
            OutlinedButton(onClick = {
                when (currentScreen) {
                    Screen.History -> {
                        nav.popBackStack(route = Screen.Calculator.name, inclusive = false)
                    }

                    Screen.Calculator -> {
                        nav.navigate(Screen.History.name)
                    }
                }
            }) {
                Text(text = "History")
            }
        }
    )
}

//
// Calculator
//

@Composable
fun InputDisplay(vm: CalculatorViewModel) {
    val state by vm.state.collectAsState()

    val (input, cursorPos, result) = if (state.viewIdx == state.history.size) Triple(
        state.cur.input,
        state.cur.cursorPos,
        state.cur.result ?: "---"
    ) else {
        val hist = state.history[state.viewIdx]
        Triple(hist.bsHistory, null, hist.result)
    }

    Box(
        modifier = Modifier
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = getExpression(input, cursorPos),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .verticalScroll(rememberScrollState())
            )
            HorizontalDivider(thickness = 2.dp)
            Text(
                text = result,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Keypad(vm: CalculatorViewModel) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy((-1).dp),
        horizontalArrangement = Arrangement.spacedBy((-1).dp),
        modifier = Modifier
            .padding(PaddingValues(32.dp, 0.dp, 32.dp, 0.dp))
            .fillMaxWidth(),
    ) {
        item { KeypadButton("<-") { vm.cursorBackward() } }
        item { KeypadButton("->") { vm.cursorForward() } }
        item { KeypadButton("|<-") { vm.cursorBack() } }
        item { KeypadButton("->|") { vm.cursorFront() } }
        item { KeypadButton("C") { vm.clear() } }
        item {
            KeypadButton("BCK") {
                try {
                    vm.backward()
                } catch (e: IllegalStateException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        item {
            KeypadButton("FWD") {
                try {
                    vm.forward()
                } catch (e: IllegalStateException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        item { KeypadButton("BS") { vm.backspace() } }

        listOf(
            Token.E,
            Token.PI,
            Token.FACT,
            Token.MOD,
            Token.SIN,
            Token.COS,
            Token.TAN,
            Token.ASIN,
            Token.ACOS,
            Token.ATAN,
            Token.ABS,
            Token.LOG,
            Token.DIV,
            Token.MUL,
            Token.SUB,
            Token.ADD,
            Token.LBRA,
            Token.RBRA,
            Token.SQRT,
            Token.EXP,
            Token._7,
            Token._8,
            Token._9,
            Token._0,
            Token._4,
            Token._5,
            Token._6,
            Token.PERIOD,
            Token._1,
            Token._2,
            Token._3,
        ).forEach { btn ->
            item { KeypadButton(btn.toString()) { vm.input(btn) } }
        }

        item {
            KeypadButton("=") {
                try {
                    vm.eval()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message ?: "Something is wrong with the expression"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.outlinedButtonColors(),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.border(1.dp, Color.Gray)
    ) {
        Text(text = text, maxLines = 1)
    }
}

//
// History
//

@Composable
fun HistoryDisplay(vm: CalculatorViewModel, nav: NavHostController) {
    val state by vm.state.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(state.history) { i, hist ->
            OutlinedCard(
                onClick = {
                    vm.goto(i)
                    nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = getExpression(hist.bsHistory, null),
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                    Text(
                        text = hist.result,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
