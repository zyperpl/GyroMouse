package pl.zyper.gyroscopemouse

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_add_ip.view.*

class IPDialogFragment : DialogFragment() {
    interface Listener
    {
        fun onAddIP(dialog:DialogFragment, ip:String)
        fun onCancelIP(dialog:DialogFragment)
    }

    companion object {
        const val tag:String = "IPDialogFragment"
    }

    var listener:Listener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        listener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_ip, null)
        builder.setView(dialogView)

        builder.setPositiveButton(R.string.add, { _, _ ->
            listener?.onAddIP(this, dialogView.ipInput.text.toString())
        })

        builder.setNegativeButton(R.string.cancel, { _, _ ->
            listener?.onCancelIP(this)
        })

        return builder.create()
    }
}