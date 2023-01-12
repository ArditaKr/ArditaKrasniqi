package com.example.arditakrasniqi.presentation.ui.bmidetails

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.arditakrasniqi.databinding.FragmentBmiDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class BmiDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBmiDetailsBinding
    private lateinit var viewModel: BmiDetailsViewModel
    private lateinit var name: String
    val args: BmiDetailsFragmentArgs by navArgs<BmiDetailsFragmentArgs>()
    private lateinit var main: View
    private lateinit var result: String
    private lateinit var helloUserNameTxt: TextView
    private lateinit var resultBeforeDotTxt: TextView
    private lateinit var resultAfterDotTxt: TextView
    private lateinit var state: String
    private lateinit var rateBtn: AppCompatButton
    private lateinit var shareBtn: AppCompatButton
    private val REQUEST_EXTERNAL_STORAGe = 1
    private lateinit var imagePath: File
    private val permissionstorage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBmiDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        initView()
        showResult()
        onClicks()
    }

    private fun getArgs() {
        name = args.name
        result = args.result.toString()
    }

    private fun initView() {
        helloUserNameTxt = binding.helloUsernameTxt
        resultBeforeDotTxt = binding.numberBeforeDot
        resultAfterDotTxt = binding.numberAfterDot
        rateBtn = binding.rateBtn
        shareBtn = binding.shareBtn
    }

    private fun showResult() {
        state = if (result < 18.5.toString()) {
            "Underweight"
        } else if (result >= 18.5.toString() && result < 24.9.toString()) {
            "Normal"
        } else if (result > 24.9.toString() && result < 30.toString()) {
            "Overweight"
        } else {
            "Obese"
        }

        helloUserNameTxt.text = "Hello $name, YOU ARE $state"
        Log.d("TAG", "showResult: $result")
        resultBeforeDotTxt.text = result.substring(0, result.indexOf("."))
        resultAfterDotTxt.text = result.substring(result.lastIndexOf("."), result.length)

    }

    private fun onClicks(){
        rateBtn.setOnClickListener {
          openAppRating(requireContext())
        }
        shareBtn.setOnClickListener {
            verifyStoragePermissions(this@BmiDetailsFragment.requireActivity())
//            val bitmap = takeScreenshot()
//            saveBitmap(bitmap!!)
//            shareIt()

        }
    }

    private fun shareIt() {
        val uri = Uri.fromFile(imagePath)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        val shareBody = "My highest score with screen shot"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Catch score")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun takeScreenshot(): Bitmap? {
        val rootView: View = binding.root
        rootView.isDrawingCacheEnabled = true
        return rootView.drawingCache
    }

    private fun saveBitmap(bitmap: Bitmap) {
        imagePath = File(
            Environment.getExternalStorageDirectory().toString() + "/scrnshot.png"
        ) ////File imagePath
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }
    }


    // verifying if storage permission is given or not
    private fun verifyStoragePermissions(activity: Activity?) {
        val permissions = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe)
            }
        }
    }

    private fun openAppRating(context: Context) {
        val appId: String = context.packageName
        val rateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$appId")
        )
        var marketFound = false
        val otherApps: List<ResolveInfo> = context.packageManager.queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) {
            if (otherApp.activityInfo.applicationInfo.packageName == "com.android.vending") {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                    otherAppActivity.applicationInfo.packageName,
                    otherAppActivity.name
                )
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                rateIntent.component = componentName
                context.startActivity(rateIntent)
                marketFound = true
                break
            }
        }
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appId")
            )
            context.startActivity(webIntent)
        }
    }
}