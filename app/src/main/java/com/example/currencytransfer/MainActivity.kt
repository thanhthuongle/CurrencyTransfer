package com.example.currencytransfer

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currency: Array<String> = arrayOf("Vietnam - Dong", "United States - Dollar", "Russia - Rouble", "China - Yuan", "Laos - Kip")
        val currencySymbol: Array<String> = arrayOf("đ", "$", "₽", "¥", "₭")
        val currencyStands: Array<String> = arrayOf("VND", "USD", "RUB", "CNY", "LAK")
        //Vietnam - Dong | United States - Dollar | Russia - Rouble | China - Yuan | Laos - Kip => Vietnam - Dong
        val rates: Array<Double> = arrayOf(1.0, 25355.0, 260.7319, 3561.1455, 1.1601)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            currency)

        // status of system: 0: chieu thuan| 1: chieu nghich| default Value: 0
        var status: Int = 0
        var currency1: Int = 0
        var currency2: Int = 0
        var isUpdating = false

        val unitFromCurrency: TextView = findViewById(R.id.fromCurrency)
        val unitToCurrency: TextView = findViewById(R.id.toCurrency)
        val unitToTarget: TextView = findViewById(R.id.unitToTarget)
        val updateTime: TextView = findViewById(R.id.updateTime)
        val amount1: EditText = findViewById(R.id.fromAmount)
        val amount2: EditText = findViewById(R.id.toAmount)
        val buttonReset: Button = findViewById(R.id.buttonReset)

        updateTime.text = "Updated 10/27/2024 3:26 PM"
        unitToTarget.text = "1 ${currencyStands[currency1]} = ${String.format("%.5f", rates[currency1] / rates[currency2]).trimEnd('0').trimEnd(',').trimEnd('.')} ${currencyStands[currency2]}"

        fun updateUnitToTarget(status: Int) {
            if (status == 0)
                unitToTarget.text = "1 ${currencyStands[currency1]} = ${String.format("%.5f", rates[currency1] / rates[currency2]).trimEnd('0').trimEnd(',').trimEnd('.')} ${currencyStands[currency2]}"
            else unitToTarget.text = "1 ${currencyStands[currency2]} = ${String.format("%.5f", rates[currency2] / rates[currency1]).trimEnd('0').trimEnd(',').trimEnd('.')} ${currencyStands[currency1]}"
        }

        fun updateAmount(amount: String, status: Int) {
            if(status == 0) {
                val rate = rates[currency1]/rates[currency2]
                val newAmount2 = amount.toDouble() * rate
                val newAmount2Content = String.format("%.5f", newAmount2).trimEnd('0').trimEnd(',').trimEnd('.')
                amount2.setText(newAmount2Content)
            } else{
                val rate = rates[currency2]/rates[currency1]
                val newAmount1 = amount.toDouble() * rate
                val newAmount1Content = String.format("%.5f", newAmount1).trimEnd('0').trimEnd(',').trimEnd('.')
                amount1.setText(newAmount1Content)
            }
        }
        findViewById<Spinner>(R.id.fromCurrencySpinner).run {
            adapter = arrayAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitFromCurrency.text = currencySymbol[p2]
                    currency1 = p2
                    updateUnitToTarget(status)
                    isUpdating = true // Đánh dấu là đang cập nhật
                    if(status == 0)
                        updateAmount(amount1.text.toString(), status)
                    else
                        updateAmount(amount2.text.toString(), status)
                    isUpdating = false // Đánh dấu là không còn cập nhật
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
        findViewById<Spinner>(R.id.toCurrencySpinner).run {
            adapter = arrayAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    unitToCurrency.text = currencySymbol[p2]
                    currency2 = p2
                    updateUnitToTarget(status)
                    isUpdating = true // Đánh dấu là đang cập nhật
                    if(status == 0)
                        updateAmount(amount1.text.toString(), status)
                    else
                        updateAmount(amount2.text.toString(), status)
                    isUpdating = false // Đánh dấu là không còn cập nhật
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }


        amount1.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP ) {
                updateUnitToTarget(0)
                amount1.postDelayed({
                    amount1.setSelection(amount1.text.length) // Đặt con trỏ ở cuối văn bản
                }, 1)
            }
            false
        }
        amount2.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP ) {
                updateUnitToTarget(1)
                amount2.postDelayed({
                    amount2.setSelection(amount2.text.length) // Đặt con trỏ ở cuối văn bản
                }, 1)
            }
            false
        }

        amount1.setOnEditorActionListener { _, actionId, event ->
            // Kiểm tra xem hành động có phải là "Next" không
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                amount2.postDelayed({
                    amount2.setSelection(amount2.text.length) // Đặt con trỏ ở cuối văn bản
                }, 1)
            }
            false // Không xử lý sự kiện
        }


        amount1.addTextChangedListener(object : TextWatcher {
            lateinit var oldValue: String
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldValue = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return // Nếu đang cập nhật, không làm gì cả

                if(s.toString().isEmpty()) {
                    amount1.setText("0")
                    amount1.setSelection(amount1.text.length)
                } else {
                    if(oldValue == "0" && s.toString().length > 1) {
                        val newAmount1Content = s.toString().substring(1)
                        amount1.setText(newAmount1Content)
                        amount1.setSelection(amount1.text.length)
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return // Nếu đang cập nhật, không làm gì cả
                isUpdating = true // Đánh dấu là đang cập nhật

                if(status == 1){
                    val len =  s.toString().length
                    if(len > oldValue.length || len == 1) {
                        val newAmount1Content: String = s.toString()[len - 1].toString()
                        amount1.setText(newAmount1Content)
                        amount1.setSelection(amount1.text.length)
                        status = 0
                        updateAmount(newAmount1Content, status)
                    } else{
                        amount1.setText("0")
                        amount1.setSelection(amount1.text.length)
                        status = 0
                        updateAmount("0", status)
                    }
                }

                Log.d("newdsd1", "$s")
                val newAmount: String = if(s.toString().isEmpty()) "0" else s.toString()
                amount1.setSelection(amount1.text.length)
                if(newAmount != oldValue)
                    updateAmount(newAmount, status)
                status = 0
                isUpdating = false // Đánh dấu là không còn cập nhật
            }
        })

        amount2.addTextChangedListener(object : TextWatcher {
            lateinit var oldValue: String
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldValue = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return // Nếu đang cập nhật, không làm gì cả
                if(s.toString().isEmpty()) {
                    amount2.setText("0")
                    amount2.setSelection(amount2.text.length)
                } else {
                    if(oldValue == "0" && s.toString().length > 1) {
                        val newAmount2Content = s.toString().substring(1)
                        amount2.setText(newAmount2Content)
                        amount2.setSelection(amount2.text.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return // Nếu đang cập nhật, không làm gì cả
                isUpdating = true // Đánh dấu là đang cập nhật

                if(status == 0){
                    val len =  s.toString().length
                    if(len > oldValue.length || len == 1){
                        val newAmount2Content: String = s.toString()[len-1].toString()
                        amount2.setText(newAmount2Content)
                        amount2.setSelection(amount2.text.length)
                        status = 1
                        updateAmount(newAmount2Content, status)
                    } else {
                        amount2.setText("0")
                        amount2.setSelection(amount2.text.length)
                        status = 1
                        updateAmount("0", status)
                    }

                }

                Log.d("newdsd2", "$s")
                Log.d("old2", "$oldValue")
                val newAmount: String = if(s.toString().isEmpty()) "0" else s.toString()
                amount2.setSelection(amount2.text.length)
                if(newAmount != oldValue)
                    updateAmount(newAmount, status)

                status = 1

                isUpdating = false // Đánh dấu là không còn cập nhật
            }
        })

        buttonReset.setOnTouchListener { v, event ->
            isUpdating = true
            amount1.setText("0")
            amount1.setSelection(amount1.text.length)
            amount2.setText("0")
            amount2.setSelection(amount2.text.length)
            isUpdating = false
            true
        }
    }
}