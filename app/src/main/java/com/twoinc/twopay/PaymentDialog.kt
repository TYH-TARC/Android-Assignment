package com.twoinc.twopay

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class PaymentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_payment_popout, null))
                    .setMessage("Confirm Payment ? ")
                    .setTitle("Payment")
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }



//    fun newInstance(): PaymentDialog {
//        return PaymentDialog()
//    }

}