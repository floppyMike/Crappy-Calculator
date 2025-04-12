package com.example.crappycalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.example.crappycalculator.ui.theme.CrappyCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrappyCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        NumberDisplay()
                        Spacer(modifier = Modifier.height(16.dp))
                        Keypad()
                    }
                }
            }
        }
    }
}

@Composable
fun NumberDisplay() {
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
                    text = LoremIpsum().values.first(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .verticalScroll(rememberScrollState())
                )
                HorizontalDivider(thickness = 2.dp)
                Text(
                    text = "0",
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun Keypad() {
    Grid {
        item { KeypadButton("<-") }
        item { KeypadButton("->") }
        item { KeypadButton("|<-") }
        item { KeypadButton("->|") }
        item { KeypadButton("C") }
        item { KeypadButton("BCK") }
        item { KeypadButton("FWD") }
        item { KeypadButton("HIST") }
        item { KeypadButton("BS") }
        item { KeypadButton("e") }
        item { KeypadButton("pi") }
        item { KeypadButton("!") }
        item { KeypadButton("%") }
        item { KeypadButton("sin") }
        item { KeypadButton("cos") }
        item { KeypadButton("tan") }
        item { KeypadButton("asin") }
        item { KeypadButton("acos") }
        item { KeypadButton("atan") }
        item { KeypadButton("log") }
        item { KeypadButton("/") }
        item { KeypadButton("*") }
        item { KeypadButton("-") }
        item { KeypadButton("+") }
        item { KeypadButton("(") }
        item { KeypadButton(")") }
        item { KeypadButton("sqrt") }
        item { KeypadButton("^") }
        item { KeypadButton("7") }
        item { KeypadButton("8") }
        item { KeypadButton("9") }
        item { KeypadButton("0") }
        item { KeypadButton("4") }
        item { KeypadButton("5") }
        item { KeypadButton("6") }
        item { KeypadButton(".") }
        item { KeypadButton("1") }
        item { KeypadButton("2") }
        item { KeypadButton("3") }
        item { KeypadButton("=") }
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
