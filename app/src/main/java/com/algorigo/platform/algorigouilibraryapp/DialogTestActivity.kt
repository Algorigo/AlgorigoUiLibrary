package com.algorigo.platform.algorigouilibraryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.algorigo.platform.algorigouilibrary.dialog.AlgorigoDialog
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_dialog_test.*
import java.util.concurrent.TimeUnit

class DialogTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_test)

        test1.setOnClickListener {
            AlgorigoDialog.Builder()
                .setTitle(R.string.dialog_title)
                .setDescription(R.string.dialog_description)
                .build()
                .show(supportFragmentManager, "test1")
        }
        test2.setOnClickListener {
            ChildDialog()
                .show(supportFragmentManager, "test2")
        }
        AlgorigoDialog.Builder()
            .setTitle("")
            .setCustomView(R.layout.dialog_algorigo, { view, bundle ->
                // onViewCreated 동작
            })
            .setConfirmBtn("", { dialog, observer ->
                Completable.timer(5, TimeUnit.SECONDS)
                    .subscribe(observer)
            })
    }
}
