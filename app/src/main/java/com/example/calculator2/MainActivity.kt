package com.example.calculator2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var editText: TextInputEditText
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false
    private val operations = ArrayList<Char>() // Moved operations list declaration here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)

        // Set click listeners for numeric buttons
        val numericButtons = listOf<Button>(
            findViewById(R.id.button_one),
            findViewById(R.id.button_two),
            findViewById(R.id.button_three),
            findViewById(R.id.button_four),
            findViewById(R.id.button_five),
            findViewById(R.id.button_six),
            findViewById(R.id.button_seven),
            findViewById(R.id.button_eight),
            findViewById(R.id.button_nine),
            findViewById(R.id.button_zero)
        )

        for (button in numericButtons) {
            button.setOnClickListener { onDigitClick(button) }
        }

        // Set click listeners for operator buttons
        val operatorButtons = listOf<Button>(
            findViewById(R.id.button_add),
            findViewById(R.id.button_subtract),
            findViewById(R.id.button_multiply),
            findViewById(R.id.button_divide),
            findViewById(R.id.button_sqrt),
            findViewById(R.id.button_decimal)
        )

        for (button in operatorButtons) {
            button.setOnClickListener { onOperatorClick(button) }
        }

        // Set click listener for the equals button
        findViewById<Button>(R.id.button_equals).setOnClickListener { onEqualsClick() }

        // Set click listener for the clear button
        findViewById<Button>(R.id.button_clear).setOnClickListener { onClearClick() }
    }

    private fun onDigitClick(view: View) {
        editText.append((view as Button).text)
        lastNumeric = true
    }

    private fun onOperatorClick(view: View) {
        if (lastNumeric && !lastDot) {
            editText.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun onEqualsClick() {
        if (lastNumeric) {
            val expression = editText.text.toString()
            try {
                val result = evaluateExpression(expression)
                if (result == -99999.9) {
                    editText.setText("Can't divide by zero")
                } else if (result == -99999.99) {
                    editText.setText("Can't sqrt neg. number")
                } else {
                    editText.setText(result.toString())
                }
                lastDot = result.toString().contains(".")
            } catch (e: ArithmeticException) {
                editText.setText("Error")
                lastNumeric = false
                lastDot = false
            }
        }
    }

    private fun onClearClick() {
        editText.setText("")
        operations.clear() // Clear operations list here
        lastNumeric = false
        lastDot = false
    }

    private fun evaluateExpression(expression: String): Double {
        var result: Double = 0.0

        for (char in expression) {
            if (char == '+' || char == '-' || char == '*' || char == '/' || char == '√') {
                operations.add(char)
            }
        }

        val numbers = expression.split(Regex("[+\\-*/√\\\\]"))
        result = numbers[0].toDouble()

        for(i in 1 until numbers.size) {
            when(operations[i-1]) {
                '+' -> result += numbers[i].toDouble()
                '-' -> result -= numbers[i].toDouble()
                '*' -> result *= numbers[i].toDouble()
                '/' -> {
                    val divisor = numbers[i].toDouble()
                    if (divisor == 0.0) {
                        // Handle the error, for example, return NaN
                        result = -99999.9
                        editText.setText("Can't divide by zero")
                        break // Exit the loop or handle the error accordingly
                    } else {
                        result /= divisor
                    }
                }
                '√' -> {
                    val operand = numbers[i].toDouble()
                    if (operand < 0) {
                        result = -99999.99
                        editText.setText("Can't sqrt neg. number")
                        break
                    } else {
                        val sqrtResult = sqrt(operand)
                        result = sqrtResult // Assign the square root result to a separate variable
                    }
                }
            }
        }

        return result
    }
}
