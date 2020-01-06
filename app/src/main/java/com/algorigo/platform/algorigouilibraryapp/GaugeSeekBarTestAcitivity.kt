package com.algorigo.platform.algorigouilibraryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_gauge_seek_bar_test_acitivity.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GaugeSeekBarTestAcitivity : AppCompatActivity() {

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gauge_seek_bar_test_acitivity)

        seekbar2.isEnabled = true
        seekbar4.isEnabled = true

        disposable = Observable.interval(5000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                seekbar1.setProgress((Math.random() * 100).roundToInt())
                seekbar2.setProgress((Math.random() * 100).roundToInt())
                seekbar3.setProgress((Math.random() * 100).roundToInt())
                seekbar4.setProgress((Math.random() * 100).roundToInt())
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
