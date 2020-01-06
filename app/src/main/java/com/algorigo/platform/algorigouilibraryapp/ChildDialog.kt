package com.algorigo.platform.algorigouilibraryapp

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.algorigo.platform.algorigouilibrary.dialog.AlgorigoDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.include_dialog_child.*
import java.util.concurrent.TimeUnit

class ChildDialog : AlgorigoDialog() {

    private var disposable: Disposable? = null

    init {
        initTitle(R.string.child_dialog_title)
        initCustomView(R.layout.include_dialog_child)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childDialogText.setOnClickListener {
            doIt()
        }
        doIt()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        disposable?.dispose()
    }

    private fun doIt() {
        if (disposable == null) {
            disposable = Observable.interval(100, TimeUnit.MILLISECONDS)
                .doFinally { disposable = null }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    childDialogText.setTextColor(Color.rgb((Math.random()*255).toInt(), (Math.random()*255).toInt(), (Math.random()*255).toInt()))
                }, {
                    Log.e(LOG_TAG, "", it)
                })
        } else {
            disposable?.dispose()
        }
    }

    companion object {
        private val LOG_TAG = ChildDialog::class.java.simpleName
    }
}