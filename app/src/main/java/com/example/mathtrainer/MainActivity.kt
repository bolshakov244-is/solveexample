package com.example.mathtrainer

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private companion object {
        const val STATE_IDLE = 0
        const val STATE_ANSWER = 1
        const val STATE_CHECKED = 2

        const val KEY_STATE = "state"
        const val KEY_OP1 = "op1"
        const val KEY_OP2 = "op2"
        const val KEY_OPERATOR = "operator"
        const val KEY_CORRECT = "correct"
        const val KEY_WRONG = "wrong"
        const val KEY_ANSWER = "answer"
        const val KEY_IS_RIGHT = "isRight"
    }

    private lateinit var tvTotal: TextView
    private lateinit var tvCorrect: TextView
    private lateinit var tvWrong: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvOp1: TextView
    private lateinit var tvOp2: TextView
    private lateinit var tvOperator: TextView
    private lateinit var etAnswer: EditText
    private lateinit var container: LinearLayout
    private lateinit var btnStart: Button
    private lateinit var btnCheck: Button

    private var state = STATE_IDLE
    private var op1 = 0
    private var op2 = 0
    private var operator = '+'
    private var correctCount = 0
    private var wrongCount = 0
    private var isRight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTotal = findViewById(R.id.tvTotal)
        tvCorrect = findViewById(R.id.tvCorrect)
        tvWrong = findViewById(R.id.tvWrong)
        tvPercent = findViewById(R.id.tvPercent)
        tvOp1 = findViewById(R.id.tvOp1)
        tvOp2 = findViewById(R.id.tvOp2)
        tvOperator = findViewById(R.id.tvOperator)
        etAnswer = findViewById(R.id.etAnswer)
        container = findViewById(R.id.exampleContainer)
        btnStart = findViewById(R.id.btnStart)
        btnCheck = findViewById(R.id.btnCheck)

        savedInstanceState?.let {
            state = it.getInt(KEY_STATE)
            op1 = it.getInt(KEY_OP1)
            op2 = it.getInt(KEY_OP2)
            operator = it.getChar(KEY_OPERATOR)
            correctCount = it.getInt(KEY_CORRECT)
            wrongCount = it.getInt(KEY_WRONG)
            isRight = it.getBoolean(KEY_IS_RIGHT)
            etAnswer.setText(it.getString(KEY_ANSWER, ""))
        }

        btnStart.setOnClickListener { generateExample() }
        btnCheck.setOnClickListener { checkAnswer() }

        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STATE, state)
        outState.putInt(KEY_OP1, op1)
        outState.putInt(KEY_OP2, op2)
        outState.putChar(KEY_OPERATOR, operator)
        outState.putInt(KEY_CORRECT, correctCount)
        outState.putInt(KEY_WRONG, wrongCount)
        outState.putBoolean(KEY_IS_RIGHT, isRight)
        outState.putString(KEY_ANSWER, etAnswer.text.toString())
    }

    private fun generateExample() {
        operator = listOf('*', '/', '-', '+').random()
        if (operator == '/') {
            op1 = Random.nextInt(10, 100)
            op2 = (10..99).filter { op1 % it == 0 }.random()
        } else {
            op1 = Random.nextInt(10, 100)
            op2 = Random.nextInt(10, 100)
        }
        etAnswer.setText("")
        state = STATE_ANSWER
        render()
    }

    private fun checkAnswer() {
        val answer = etAnswer.text.toString().toIntOrNull()
        if (answer == null) {
            Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show()
            return
        }
        val expected = when (operator) {
            '*' -> op1 * op2
            '/' -> op1 / op2
            '-' -> op1 - op2
            else -> op1 + op2
        }
        isRight = answer == expected
        if (isRight) correctCount++ else wrongCount++
        state = STATE_CHECKED
        render()
    }


    private fun render() {
        val started = state != STATE_IDLE
        tvOp1.text = if (started) op1.toString() else "00"
        tvOp2.text = if (started) op2.toString() else "00"
        tvOperator.text = if (started) operator.toString() else "+"

        btnStart.isEnabled = state != STATE_ANSWER
        btnCheck.isEnabled = state == STATE_ANSWER
        etAnswer.isEnabled = state == STATE_ANSWER

        container.setBackgroundColor(
            when {
                state != STATE_CHECKED -> Color.WHITE
                isRight -> Color.GREEN
                else -> Color.RED
            }
        )

        val total = correctCount + wrongCount
        tvTotal.text = total.toString()
        tvCorrect.text = correctCount.toString()
        tvWrong.text = wrongCount.toString()
        val percent = if (total == 0) 0.0 else correctCount * 100.0 / total
        tvPercent.text = String.format("%.2f%%", percent)
    }
}