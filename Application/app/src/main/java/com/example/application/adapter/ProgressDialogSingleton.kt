import android.app.ProgressDialog
import android.content.Context

object ProgressDialogSingleton {
    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog(context: Context, message: String) {
        progressDialog?.dismiss() // Dismiss any existing dialog
        progressDialog = ProgressDialog(context)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null // Reset progressDialog instance
    }
}
