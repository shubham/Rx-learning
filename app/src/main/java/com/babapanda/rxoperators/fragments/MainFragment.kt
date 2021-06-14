package com.babapanda.rxoperators.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.babapanda.rxoperators.BaseFragment
import com.babapanda.rxoperators.databinding.FragmentMainBinding

class MainFragment : BaseFragment() {
    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding1 = FragmentMainBinding.inflate(inflater, container, false)
        binding = binding1
        setUpClickListeners()
        return binding?.root
    }

    private fun setUpClickListeners() {

        binding?.btnDemoDebounce?.setOnClickListener {

        }
        binding?.btnDemoPolling?.setOnClickListener {
            clickedOn(PollingFragment())
        }
        binding?.btnDemoNetworkDetector?.setOnClickListener {
            clickedOn(NetworkDetectorFragment())
        }

        binding?.btnDemoPseudoCache?.setOnClickListener {
            clickedOn( PseudoCacheFragment())
        }

        binding?.btnDemoUsing?.setOnClickListener {
            clickedOn( UsingFragment())
        }
        binding?.btnDemoTimeout?.setOnClickListener {
            clickedOn(TimeOutDemoFragment())
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun clickedOn(fragment: Fragment) {
        val tag = fragment.javaClass.toString()
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(tag)
            ?.replace(android.R.id.content, fragment, tag)
            ?.commit()
    }
}