package com.example.crappycalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crappycalculator.ui.theme.CrappyCalculatorTheme
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrappyCalculatorTheme {
                val viewModel: CalculatorViewModel by viewModels()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        NumberDisplay(viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        Keypad(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NumberDisplay(vm: CalculatorViewModel) {
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
            .fillMaxWidth()
            .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp))
    ) {
        Box(
            modifier = Modifier
                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
}

@Composable
fun Keypad(vm: CalculatorViewModel) {
    val context = LocalContext.current

    Grid {
        item { KeypadButtonNew("<-") { vm.cursorBackward() } }
        item { KeypadButtonNew("->") { vm.cursorForward() } }
        item { KeypadButtonNew("|<-") { vm.cursorBack() } }
        item { KeypadButtonNew("->|") { vm.cursorFront() } }
        item { KeypadButtonNew("C") { vm.clear() } }
        item {
            KeypadButtonNew("BCK") {
                try {
                    vm.backward()
                } catch (e: IllegalStateException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        item {
            KeypadButtonNew("FWD") {
                try {
                    vm.forward()
                } catch (e: IllegalStateException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        item { KeypadButton("HIST") }
        item { KeypadButtonNew("BS") { vm.backspace() } }

        listOf(
            Token.E, Token.PI, Token.FACT, Token.MOD, Token.SIN, Token.COS, Token.TAN, Token.ASIN,
            Token.ACOS, Token.ATAN, Token.LN, Token.DIV, Token.MUL, Token.SUB, Token.ADD,
            Token.LBRA, Token.RBRA, Token.SQRT, Token.EXP, Token._7, Token._8, Token._9, Token._0,
            Token._4, Token._5, Token._6, Token.PERIOD, Token._1, Token._2, Token._3,
        ).forEach { btn ->
            item { KeypadButtonNew(btn.toString()) { vm.input(btn) } }
        }

        item {
            KeypadButtonNew("=") {
                try {
                    vm.eval()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun KeypadButtonNew(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        colors = ButtonDefaults.outlinedButtonColors(),
        modifier = Modifier.border(1.dp, Color.Gray)
    ) {
        Text(text = text)
    }
}

@Composable
fun KeypadButton(text: String) {
    val context = LocalContext.current

    Button(
        onClick = { Toast.makeText(context, text, Toast.LENGTH_SHORT).show() },
        shape = RectangleShape,
        colors = ButtonDefaults.outlinedButtonColors(),
        modifier = Modifier.border(1.dp, Color.Gray)
    ) {
        Text(text = text)
    }
}

@Composable
fun Grid(content: LazyGridScope.() -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy((-1).dp),
        horizontalArrangement = Arrangement.spacedBy((-1).dp),
        modifier = Modifier
            .padding(PaddingValues(32.dp, 0.dp, 32.dp, 0.dp))
            .fillMaxWidth(),
        content = content
    )
}
