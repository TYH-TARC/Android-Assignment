package com.twoinc.twopay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BankFragment : Fragment() {

    public fun BankFragment(){
        //something
    }

    override public fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card,container,false)
    }

}