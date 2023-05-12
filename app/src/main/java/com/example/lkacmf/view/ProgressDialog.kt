package com.example.lkacmf.view

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.lkacmf.R

class ProgressDialog(context: Context?) {
    private val progressDialog: AlertDialog

    /**
     * View to control the main layout of ProgressDialog
     */
    private val progressDialogLayout: LinearLayout

    /**
     * View to control the message of ProgressDialog
     */
    private val txtFeedBack: TextView

    /**
     * View to control the progressBar of ProgressDialog
     */
    private val progressBar: ProgressBar

    /**
     * View to control the percentage of ProgressDialog
     */
//    private val txtProgressbarPercent: TextView

    /**
     * Set the text of the FeedBack
     * @param text of the FeedBack
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setText(text: String?): ProgressDialog {
        txtFeedBack.text = text
        return this
    }

    /**
     * Set the size of the FeedBack text
     * @param size of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextSize(size: Float): ProgressDialog {
        txtFeedBack.textSize = size
        return this
    }

    /**
     * Set the dialog title visibility
     * @param visibility value of the feedback text (VISIBLE, INVISIBLE or GONE)
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextVisibility(visibility: Int): ProgressDialog {
        txtFeedBack.visibility = visibility
        return this
    }

    /**
     * Set the color of the FeedBack text
     * @param color of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextColor(color: Int): ProgressDialog {
        txtFeedBack.setTextColor(color)
        return this
    }

    /**
     * Set the color of the FeedBack text background
     * @param color of the FeedBack text background
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextBackground(color: Int): ProgressDialog {
        txtFeedBack.setBackgroundColor(color)
        return this
    }

    /**
     * Set the padding of the FeedBack text
     * @param  left padding of the FeedBack text
     * @param  top padding of the FeedBack text
     * @param  right padding of the FeedBack text
     * @param  bottom padding of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextPadding(left: Int, top: Int, right: Int, bottom: Int): ProgressDialog {
        txtFeedBack.setPadding(left, top, right, bottom)
        return this
    }

    /**
     * Set the padding of the FeedBack text
     * @param  padding of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextPadding(padding: Int): ProgressDialog {
        txtFeedBack.setPadding(padding, padding, padding, padding)
        return this
    }

    /**
     * Set the bottom padding of the FeedBack text
     * @param  bottom padding of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextPaddingBottom(bottom: Int): ProgressDialog {
        txtFeedBack.setPadding(
            txtFeedBack.paddingLeft,
            txtFeedBack.paddingTop,
            txtFeedBack.paddingRight,
            bottom
        )
        return this
    }

    /**
     * Set the custom shape of the FeedBack text
     * @param shape of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextShape(shape: Int): ProgressDialog {
        txtFeedBack.setBackgroundResource(shape)
        return this
    }

    /**
     * Set the font of the FeedBack text
     * @param font of the FeedBack text
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTextFont(font: Typeface?): ProgressDialog {
        txtFeedBack.typeface = font
        return this
    }

    /**
     * Show ProgressDialog
     */
    fun show() {
        progressDialog.show()
    }

    /**
     * Hide ProgressDialog
     */
    fun hide() {
        progressDialog.hide()
    }

    /**
     * Dismiss ProgressDialog
     */
    fun dismiss() {
        progressDialog.dismiss()
    }

    /**
     * Cancel ProgressDialog
     */
    fun cancel() {
        progressDialog.cancel()
    }

    /**
     * @return true if ProgressDialog is showing
     */
    val isShowing: Boolean
        get() = progressDialog.isShowing

    /**
     * Set the title of the ProgressDialog
     * @param title of the ProgressDialog
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setTitle(title: String?): ProgressDialog {
        progressDialog.setTitle(title)
        return this
    }

    /**
     * Set the custom title of the ProgressDialog
     * @param customTitle of the ProgressDialog
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setCustomTitle(customTitle: View?): ProgressDialog {
        progressDialog.setCustomTitle(customTitle)
        return this
    }

    /**
     * Set the message of the ProgressDialog
     * @param message of the ProgressDialog
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setMessage(message: String?): ProgressDialog {
        progressDialog.setMessage(message)
        return this
    }

    /**
     * Set the icon of the ProgressDialog
     * @param resID - the resourceId of the drawable to use as the icon or 0 if you don't want an icon
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setIcon(resID: Int): ProgressDialog {
        progressDialog.setIcon(resID)
        return this
    }

    /**
     * Set a listener to be invoked when the ProgressDialog is shown.
     * @param listener DialogInterface.OnShowListener listener to use.
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setOnShowListener(listener: OnShowListener?): ProgressDialog {
        progressDialog.setOnShowListener(listener)
        return this
    }

    /**
     * Set a listener to be invoked when the ProgressDialog is canceled.
     * @param listener DialogInterface.OnCancelListener listener to use.
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?): ProgressDialog {
        progressDialog.setOnCancelListener(listener)
        return this
    }

    /**
     * Set a listener to be invoked when the ProgressDialog is dismissed.
     * @param listener DialogInterface.OnDismissListener listener to use.
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): ProgressDialog {
        progressDialog.setOnDismissListener(listener)
        return this
    }

    /**
     * Set whether this ProgressDialog is cancelable
     * @param cancelable the dialog will not be cancelled by the end user if the value is false
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setCancelable(cancelable: Boolean): ProgressDialog {
        progressDialog.setCancelable(cancelable)
        return this
    }

    /**
     * Set dim amount for ProgressDialog
     * @param amount of the dim attribute
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogDimAmount(amount: Float): ProgressDialog {
        progressDialog.window!!.setDimAmount(amount)
        return this
    }

    /**
     * Set ProgressDialog to transparent
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogTransparent(): ProgressDialog {
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return this
    }

    /**
     * Set the Background color of the ProgressDialog
     * @param color of the ProgressDialog Background
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogBackground(color: Int): ProgressDialog {
        progressDialogLayout.setBackgroundColor(color)
        return this
    }

    /**
     * Set the Background color of the ProgressDialog
     * @param drawable - The Drawable to use as the background, or null to remove the background
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogBackgroundDrawable(drawable: Drawable?): ProgressDialog {
        progressDialogLayout.background = drawable
        return this
    }

    /**
     * Set the padding of the ProgressDialog
     * @param  padding of the ProgressDialog
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogPadding(padding: Int): ProgressDialog {
        progressDialogLayout.setPadding(padding, padding, padding, padding)
        return this
    }

    /**
     * Set the gravity of the ProgressDialog
     * @param  position – The position of ProgressDialog.
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogGravity(position: Int): ProgressDialog {
        progressDialog.window!!.setGravity(position)
        return this
    }

    /**
     * Set the height of the ProgressDialog
     * @param height - value of the ProgressDialog layout
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogHeight(height: Int): ProgressDialog {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window!!.attributes)
        layoutParams.height = height
        progressDialog.window!!.attributes = layoutParams
        return this
    }

    /**
     * Set the width of the ProgressDialog
     * @param width - value of the ProgressDialog layout
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setDialogWidth(width: Int): ProgressDialog {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog.window!!.attributes)
        layoutParams.width = width
        progressDialog.window!!.attributes = layoutParams
        return this
    }

    /**
     * Set the color of ProgressBar
     * @param color of the ProgressBar
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarColor(color: Int): ProgressDialog {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar.indeterminateDrawable.colorFilter =
                BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
        return this
    }

    /**
     * Set the background color of ProgressBar
     * @param color of the ProgressBar
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarBackGround(color: Int): ProgressDialog {
        progressBar.setBackgroundColor(color)
        return this
    }

    /**
     * Set the background color of ProgressBar
     * @param drawable – The Drawable to use as the background, or null to remove the background
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarBackGround(drawable: Drawable?): ProgressDialog {
        progressBar.background = drawable
        return this
    }

    /**
     * Define the drawable used to draw the progress bar in progress mode.
     * @param drawable – the new drawable
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressDrawable(drawable: Drawable?): ProgressDialog {
        progressBar.progressDrawable = drawable
        return this
    }

    /**
     * Set the visibility state of ProgressBar.
     * @param  visibility - value of ProgressBar (VISIBLE, INVISIBLE or GONE)
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarVisibility(visibility: Int): ProgressDialog {
        progressBar.visibility = View.INVISIBLE
        return this
    }

    /**
     * Set the current progress to the specified value.
     * @param progress – the new progress
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgress(progress: Int): ProgressDialog {
        progressBar.progress = progress
        return this
    }

    /**
     * Set the upper range of the progress bar max.
     * @param max – the upper range of this progress bar.
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressMax(max: Int): ProgressDialog {
        progressBar.max = max
        return this
    }

    /**
     * Define the drawable used to draw the progress bar in indeterminate mode.
     * @param drawable – the new drawable
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarShape(drawable: Drawable?): ProgressDialog {
        progressBar.indeterminateDrawable = drawable
        return this
    }

    /**
     * Set the padding of the ProgressDialog
     * @param  padding of the ProgressDialog
     * @return the instance of ProgressDialog to make a chain of function easily
     */
    fun setProgressBarPadding(padding: Int): ProgressDialog {
        progressBar.setPadding(padding, padding, padding, padding)
        return this
    }

    /**
     * Set the Percentage of Progressbar
     * @param text - percentage of Progressbar
     * @return the instance of ProgressDialog to make a chain of function easily
     */
//    fun setProgressbarPercent(text: String?): ProgressDialog {
//        txtProgressbarPercent.text = text
//        return this
//    }

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        progressDialog = AlertDialog.Builder(context!!).create()
        progressDialog.setView(view)

        progressDialogLayout = view.findViewById(R.id.progressDialogLayout)
        progressDialogLayout.minimumHeight = 350
        txtFeedBack = view.findViewById(R.id.txtFeedBack)
        progressBar = view.findViewById(R.id.progressBar)
//        txtProgressbarPercent = view.findViewById(R.id.txtProgressbarPercent)
    }
}
