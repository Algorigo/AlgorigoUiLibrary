package com.algorigo.platform.algorigouilibrary.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.algorigo.platform.algorigouilibrary.R
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_algorigo.*
import kotlinx.android.synthetic.main.dialog_algorigo.view.*


open class AlgorigoDialog : DialogFragment() {

    class Builder {
        private var title: String? = null
        private var titleResourceId: Int? = null
        private var description: String? = null
        private var descriptionResourceId: Int? = null
        private var customResourceId: Int? = null
        private var viewCreatedDelegate: ((View, Bundle?) -> Unit)? = null
        private var confirmString: String? = null
        private var confirmResourceId: Int? = null
        private var confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null
        private var cancelString: String? = null
        private var cancelResourceId: Int? = null
        private var cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null

        fun setTitle(title: String): Builder {
            this.title = title
            this.titleResourceId = null
            return this
        }

        fun setTitle(@StringRes titleResourceId: Int): Builder {
            this.titleResourceId = titleResourceId
            this.title = null
            return this
        }

        fun setDescription(description: String): Builder {
            this.description = description
            this.descriptionResourceId = null
            this.customResourceId = null
            this.viewCreatedDelegate = null
            return this
        }

        fun setDescription(@StringRes descriptionResourceId: Int): Builder {
            this.descriptionResourceId = descriptionResourceId
            this.description = null
            this.customResourceId = null
            this.viewCreatedDelegate = null
            return this
        }

        fun setCustomView(@LayoutRes customResourceId: Int, viewCreatedDelegate: (View, Bundle?) -> Unit): Builder {
            this.customResourceId = customResourceId
            this.viewCreatedDelegate = viewCreatedDelegate
            this.description = null
            return this
        }

        fun setConfirmBtn(confirmString: String, confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null): Builder {
            this.confirmString = confirmString
            this.confirmResourceId = null
            this.confirmCallback = confirmCallback
            return this
        }

        fun setConfirmBtn(@StringRes confirmResourceId: Int, confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null): Builder {
            this.confirmResourceId = confirmResourceId
            this.confirmString = null
            this.confirmCallback = confirmCallback
            return this
        }

        fun setConfirmBtn(confirmCallback: (AlgorigoDialog, CompletableObserver) -> Unit): Builder {
            this.confirmString = null
            this.confirmResourceId = null
            this.confirmCallback = confirmCallback
            return this
        }

        fun setCancelBtn(cancelString: String, cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null): Builder {
            this.cancelString = cancelString
            this.cancelResourceId = null
            this.cancelCallback = cancelCallback
            return this
        }

        fun setCancelBtn(@StringRes cancelResourceId: Int, cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null): Builder {
            this.cancelResourceId = cancelResourceId
            this.cancelString = null
            this.cancelCallback = cancelCallback
            return this
        }

        fun setCancelBtn(cancelCallback: (AlgorigoDialog, CompletableObserver) -> Unit): Builder {
            this.cancelString = null
            this.cancelResourceId = null
            this.cancelCallback = cancelCallback
            return this
        }

        fun build(): AlgorigoDialog {
            return AlgorigoDialog()
                .also {
                it.title = title
                it.titleResourceId = titleResourceId
                it.description = description
                it.descriptionResourceId = descriptionResourceId
                it.customResourceId = customResourceId
                it.viewCreatedDelegate = viewCreatedDelegate
                it.confirmString = confirmString
                it.confirmResourceId = confirmResourceId
                it.confirmCallback = confirmCallback
                it.cancelString = cancelString
                it.cancelResourceId = cancelResourceId
                it.cancelCallback = cancelCallback
            }
        }
    }

    private var title: String? = null
    private var titleResourceId: Int? = null
    private var description: String? = null
    private var descriptionResourceId: Int? = null
    private var customResourceId: Int? = null
    private var viewCreatedDelegate: ((View, Bundle?) -> Unit)? = null
    private var confirmString: String? = null
    private var confirmResourceId: Int? = null
    private var confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null
    private var cancelString: String? = null
    private var cancelResourceId: Int? = null
    private var cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null
    private var disposable: Disposable? = null

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                when {
                    cancelCallback != null -> {
                        cancelBtn.performClick()
                        true
                    }
                    confirmCallback != null -> {
                        confirmBtn.performClick()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }

        return dialog
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_algorigo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when {
            title != null -> {
                view.dialogTitleView.text = title!!
                view.dialogTitleView.visibility = View.VISIBLE
            }
            titleResourceId != null -> {
                view.dialogTitleView.setText(titleResourceId!!)
                view.dialogTitleView.visibility = View.VISIBLE
            }
            else -> view.dialogTitleView.visibility = View.GONE
        }

        when {
            description != null -> {
                view.dialogDescriptionView.text = description!!
                view.dialogDescriptionView.visibility = View.VISIBLE
            }
            descriptionResourceId != null -> {
                view.dialogDescriptionView.setText(descriptionResourceId!!)
                view.dialogDescriptionView.visibility = View.VISIBLE
            }
            else -> view.dialogDescriptionView.visibility = View.GONE
        }

        if (customResourceId != null) {
            view.customViewLayout.visibility = View.VISIBLE
            val customView = LayoutInflater.from(view.context).inflate(customResourceId!!, view.customViewLayout, true)
            viewCreatedDelegate?.let {
                it(customView, savedInstanceState)
            }
        } else {
            view.customViewLayout.visibility = View.GONE
        }

        if (confirmString != null) {
            view.confirmBtn.text = confirmString!!
        } else if (confirmResourceId != null) {
            view.confirmBtn.setText(confirmResourceId!!)
        }
        if (confirmCallback != null) {
            view.confirmBtn.setOnClickListener {
                if (disposable == null) {
                    PublishSubject.create<Any>().also {
                        disposable = it
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe {
                                progressLayout.visibility = View.VISIBLE
                                confirmBtn.isEnabled = false
                                cancelBtn.isEnabled = false
                            }
                            .doFinally { disposable = null }
                            .subscribe({}, {
                                progressLayout.visibility = View.GONE
                                confirmBtn.isEnabled = true
                                cancelBtn.isEnabled = true
                            }, {
                                dismiss()
                            })
                        confirmCallback!!(this, it.toCompletableObserver())
                    }
                }
            }
        } else {
            view.confirmBtn.setOnClickListener {
                dismiss()
            }
        }

        when {
            cancelString != null -> {
                view.cancelBtn.text = cancelString!!
                view.cancelBtn.visibility = View.VISIBLE
            }
            cancelResourceId != null -> {
                view.cancelBtn.setText(cancelResourceId!!)
                view.cancelBtn.visibility = View.VISIBLE
            }
            cancelCallback != null -> view.cancelBtn.visibility = View.VISIBLE
            else -> view.cancelBtn.visibility = View.GONE
        }
        if (cancelCallback != null) {
            view.cancelBtn.setOnClickListener {
                if (disposable == null) {
                    PublishSubject.create<Any>().also {
                        disposable = it
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe {
                                progressLayout.visibility = View.VISIBLE
                                confirmBtn.isEnabled = false
                                cancelBtn.isEnabled = false
                            }
                            .doFinally { disposable = null }
                            .subscribe({}, {
                                progressLayout.visibility = View.GONE
                                confirmBtn.isEnabled = true
                                cancelBtn.isEnabled = true
                            }, {
                                dismiss()
                            })
                        cancelCallback!!(this, it.toCompletableObserver())
                    }
                }
            }
        } else {
            view.cancelBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        disposable?.dispose()
    }

    protected fun initTitle(title: String) {
        this.title = title
        this.titleResourceId = null
    }

    protected fun initTitle(@StringRes titleResourceId: Int) {
        this.titleResourceId = titleResourceId
        this.title = null
    }

    protected fun initCustomView(@LayoutRes customResourceId: Int) {
        this.customResourceId = customResourceId
        this.description = null
    }

    protected fun initConfirmBtn(confirmString: String, confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null) {
        this.confirmString = confirmString
        this.confirmResourceId = null
        this.confirmCallback = confirmCallback
    }

    protected fun initConfirmBtn(@StringRes confirmResourceId: Int, confirmCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null) {
        this.confirmResourceId = confirmResourceId
        this.confirmString = null
        this.confirmCallback = confirmCallback
    }

    protected fun initConfirmBtn(confirmCallback: (AlgorigoDialog, CompletableObserver) -> Unit) {
        this.confirmString = null
        this.confirmResourceId = null
        this.confirmCallback = confirmCallback
    }

    protected fun initCancelBtn(cancelString: String, cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null) {
        this.cancelString = cancelString
        this.cancelResourceId = null
        this.cancelCallback = cancelCallback
    }

    protected fun initCancelBtn(@StringRes cancelResourceId: Int, cancelCallback: ((AlgorigoDialog, CompletableObserver) -> Unit)? = null) {
        this.cancelResourceId = cancelResourceId
        this.cancelString = null
        this.cancelCallback = cancelCallback
    }

    protected fun initCancelBtn(cancelCallback: (AlgorigoDialog, CompletableObserver) -> Unit) {
        this.cancelString = null
        this.cancelResourceId = null
        this.cancelCallback = cancelCallback
    }
}