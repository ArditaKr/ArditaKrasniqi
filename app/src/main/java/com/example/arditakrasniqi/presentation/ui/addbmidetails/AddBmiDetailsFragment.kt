package com.example.arditakrasniqi.presentation.ui.addbmidetails


import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.arditakrasniqi.R
import com.example.arditakrasniqi.databinding.FragmentAddBmiDetailsBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


@RequiresApi(Build.VERSION_CODES.Q)
class AddBmiDetailsFragment : Fragment() {

    private lateinit var viewModel: AddBmiDetailsViewModel
    private lateinit var name: String
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var gender: String
    private var bmi: Float = 0f
    private lateinit var binding: FragmentAddBmiDetailsBinding
    private lateinit var weightNumberPicker: NumberPicker
    private lateinit var heightNumberPicker: NumberPicker
    private lateinit var genderNumberPicker: NumberPicker
    private lateinit var nameEditText: EditText
    var prevStarted = "yes"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBmiDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        splashScreen()
        initView()
        initWeight()
        initGender()

       MobileAds.initialize(requireContext()){status ->
           Log.d("TAG", "Mobile Ad Status $status")
       }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("")).build()
        )
        navigateToBmiDetails()
    }

    private fun splashScreen() {
        object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                binding.splashLinearLayout.visibility = View.GONE
                binding.afterSplash.visibility = VISIBLE
                (activity as AppCompatActivity?)!!.supportActionBar!!.show()
            }
            override fun onTick(p0: Long) {}
        }.start()
    }

    private fun initView() {
        weightNumberPicker = binding.weightNumberPicker
        heightNumberPicker = binding.heightNumberPicker
        genderNumberPicker = binding.genderNumberPicker
        nameEditText = binding.userName
    }

    private fun initWeight() {
        weightNumberPicker.minValue = 0
        weightNumberPicker.maxValue = 200
        weightNumberPicker.value = 43
        weightNumberPicker.textColor = resources.getColor(R.color.title_color)
        weightNumberPicker.wrapSelectorWheel = true
        weightNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            calculateBMI()
        }

        heightNumberPicker.minValue = 0
        heightNumberPicker.maxValue = 250
        heightNumberPicker.value = 127
        heightNumberPicker.textColor = resources.getColor(R.color.title_color)
        heightNumberPicker.wrapSelectorWheel = true
           calculateBMI()

    }

    private fun calculateBMI() {
        val weight = weightNumberPicker.value.toString().toDouble()
        val height = heightNumberPicker.value.toString().toDouble()
        bmi = (weight / ((height / 100) * (height / 100))).toFloat()
        Log.d("TAG", "calculateBMI: $bmi")

    }


    private fun initGender() {
        val genderValues = arrayOf("Male", "Female")
        genderNumberPicker.displayedValues = genderValues
        genderNumberPicker.minValue = 0
        genderNumberPicker.maxValue = genderValues.size - 1
        genderNumberPicker.value = genderValues.size - 1
        gender = "Female"
        genderNumberPicker.textColor = resources.getColor(R.color.title_color)
        genderNumberPicker.wrapSelectorWheel = true
        genderNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            gender = newVal.toString()
        }
    }

    private fun navigateToBmiDetails() {
        loadAd()
        binding.calculateBtn.setOnClickListener {
            name = binding.userName.text.toString()
            showAd()
        }
    }


    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/8691691433", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d("TAG", "errorrrrrrr"+it) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("TAG", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    findNavController().navigate(AddBmiDetailsFragmentDirections.actionAddBmiDetailsFragmentToBmiDetailsFragment(name, bmi))
                }
            }
            mInterstitialAd?.show(this@AddBmiDetailsFragment.requireActivity())
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            findNavController().navigate(AddBmiDetailsFragmentDirections.actionAddBmiDetailsFragmentToBmiDetailsFragment(name, bmi))
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


}