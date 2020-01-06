package com.algorigo.platform.algorigouilibraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialogTestBtn.setOnClickListener {
            startActivity(Intent(this, DialogTestActivity::class.java))
        }
        gaugeSeekbarTestBtn.setOnClickListener {
            startActivity(Intent(this, GaugeSeekBarTestAcitivity::class.java))
        }
    }
}
