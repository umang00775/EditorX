package com.elementalideas.editorx.edit

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.elementalideas.editorx.Keys
import com.elementalideas.editorx.R
import com.elementalideas.editorx.masks.Adjust
import com.elementalideas.editorx.masks.Filters
import com.elementalideas.editorx.masks.Rotate
import com.elementalideas.editorx.masks.Static
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.Toast
import com.elementalideas.editorx.Dashboard
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

/*
* Constant values for previous operation
* NULL
* FILTER
* BRIGHTNESS
* CONTRAST
* SATURATION
* EXPOSURE
* SHADOW
* SHARPNESS
* TEMPERATURE
* TONE
* BRILLIANCE
* VIGNETTE
*   */

/*
* Seekbar-1 --> -100 to 100
* Seekbar-2 --> 0 to 100
*  */

class Edit: Fragment(R.layout.edit) {
    /**********************************/

    private lateinit var mainRotate: RelativeLayout
    private lateinit var mainFilters: RelativeLayout
    private lateinit var mainAdjust: RelativeLayout

    private lateinit var level1: RelativeLayout
    private lateinit var seekbar1: SeekBar
    private lateinit var seekbar2: SeekBar

    private lateinit var level2: RelativeLayout
    private lateinit var rotateOptions: RelativeLayout
    private lateinit var adjustOptions: HorizontalScrollView
    private lateinit var filtersOptions: HorizontalScrollView

    private lateinit var leftRotate: RelativeLayout
    private lateinit var rightRotate: RelativeLayout

    private lateinit var showImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var seekValue: TextView

    private lateinit var originalImage: Bitmap
    private lateinit var editedImage: Bitmap

    private lateinit var prevOperation: String
    private var seek1Value: Int = 100
    private var seek2Value: Int = 50

    private val FILTER_TIME: Long = 1500
    private val ADJUST_TIME: Long = 1500

    private val FOLDER_NAME = "EditorX"

    private lateinit var storage: FirebaseStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize fire store
        storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // previous operation
        prevOperation = "NULL"

        // Main three options
        mainRotate = view.findViewById(R.id.main_rotate)
        mainFilters = view.findViewById(R.id.main_filters)
        mainAdjust = view.findViewById(R.id.main_adjust)

        // Level - 1 options
        level1 = view.findViewById(R.id.level1_container)
        seekbar1 = view.findViewById(R.id.slider1)
        seekbar2 = view.findViewById(R.id.slider2)
        seekValue = view.findViewById(R.id.seek_value)

        // Level - 2 options
        level2 = view.findViewById(R.id.level2_container)
        rotateOptions = view.findViewById(R.id.rotate_options)
        adjustOptions = view.findViewById(R.id.adjust_options)
        filtersOptions = view.findViewById(R.id.filter_options)

        // Rotate options
        leftRotate = view.findViewById(R.id.left_rotate)
        rightRotate = view.findViewById(R.id.right_rotate)

        // Image & progress bar
        showImage = view.findViewById(R.id.show_image)
        progressBar = view.findViewById(R.id.progressbar)


        // Large progress bar
        val largeProgressBar = view.findViewById<ProgressBar>(R.id.large_progressbar)
        largeProgressBar.visibility = View.VISIBLE

        // get the initial data from previous screen
        val sharedPrefs = requireContext().getSharedPreferences(Keys.SHARED_PREF, Context.MODE_PRIVATE)
        val selectedImageUriString = sharedPrefs.getString(Keys.SELECTED_IMG_URI, "")
        val selectedImageUri = Uri.parse(selectedImageUriString)
        showImage.setImageURI(selectedImageUri)

        /****************************/
        val hudda = showImage.drawable as BitmapDrawable
        val bitmap = hudda.bitmap
        editedImage = bitmap
        originalImage = bitmap
        /****************************/


//        GlobalScope.launch(Dispatchers.Main) {
//            val bitmapDeferred = async(Dispatchers.Default) {
//                getBitmapFromUri(selectedImageUri)
//            }
//            val tempImage = bitmapDeferred.await()
//            if (tempImage != null) {
//                originalImage = tempImage
//            }
//            editedImage = originalImage
//            largeProgressBar.visibility = View.INVISIBLE
//        }


        // main options on clicks
        mainOpts()

        // Handle rotate
        handleRotate()

        // Handle filters
        handleFilters(view)

        // Handle adjustments
        handleAdjustment(view)

        // Initial visibility
        hideSeekBars()
        handleAdjustFontColor(view, "NULL")
        rotateOptions.visibility = View.INVISIBLE
        filtersOptions.visibility = View.INVISIBLE
        adjustOptions. visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        seekValue.visibility = View.INVISIBLE

        // Handle top bars
        val discard = view.findViewById<ImageView>(R.id.discard_edit)
        val save = view.findViewById<ImageView>(R.id.save_edit)
        discard.setOnClickListener {
            showDiscardChangesDialog()
        }

        save.setOnClickListener {
            val pngData: ByteArray = bitmapToPng(editedImage)

            val fileOutputStream = FileOutputStream("path/to/save/image.png")
            fileOutputStream.write(pngData)
            fileOutputStream.close()
        }
    }





    // HandleAdjustment
    private fun handleAdjustment(view: View){
        val adjustBrightness = view.findViewById<RelativeLayout>(R.id.adjust_brightness)
        val adjustContrast = view.findViewById<RelativeLayout>(R.id.adjust_contrast)
        val adjustSaturation = view.findViewById<RelativeLayout>(R.id.adjust_saturation)
        val adjustExposure = view.findViewById<RelativeLayout>(R.id.adjust_exposure)
        val adjustShadow = view.findViewById<RelativeLayout>(R.id.adjust_shadow)
        val adjustSharpness = view.findViewById<RelativeLayout>(R.id.adjust_sharpness)
        val adjustTemperature = view.findViewById<RelativeLayout>(R.id.adjust_temperature)
        val adjustTone = view.findViewById<RelativeLayout>(R.id.adjust_tone)
        val adjustBrilliance = view.findViewById<RelativeLayout>(R.id.adjust_brilliance)
        val adjustVignette = view.findViewById<RelativeLayout>(R.id.adjust_vignette)
        val adjustBlur = view.findViewById<RelativeLayout>(R.id.adjust_blur)

        adjustBrightness.setOnClickListener {
            handleAdjustFontColor(view, "BRIGHTNESS")
            showSeekBar1("BRIGHTNESS")
        }
        adjustContrast.setOnClickListener {
            handleAdjustFontColor(view, "CONTRAST")
            showSeekBar1("CONTRAST")
        }
        adjustSaturation.setOnClickListener {
            handleAdjustFontColor(view, "SATURATION")
            showSeekBar1("SATURATION")
        }
        adjustExposure.setOnClickListener {
            handleAdjustFontColor(view, "EXPOSURE")
            showSeekBar1("EXPOSURE")
        }
        adjustShadow.setOnClickListener {
            handleAdjustFontColor(view, "SHADOW")
            showSeekBar1("SHADOW")
        }
        adjustSharpness.setOnClickListener {
            handleAdjustFontColor(view, "SHARPNESS")
            showSeekBar2("SHARPNESS")
        }
        adjustTemperature.setOnClickListener {
            handleAdjustFontColor(view, "TEMPERATURE")
            showSeekBar1("TEMPERATURE")
        }
        adjustTone.setOnClickListener {
            handleAdjustFontColor(view, "TONE")
            showSeekBar1("TONE")
        }
        adjustBrilliance.setOnClickListener {
            handleAdjustFontColor(view, "BRILLIANCE")
            showSeekBar2("BRILLIANCE")
        }
        adjustVignette.setOnClickListener {
            handleAdjustFontColor(view, "VIGNETTE")
            showSeekBar1("VIGNETTE")
        }
        adjustBlur.setOnClickListener {
            handleAdjustFontColor(view, "BLUR")
            showSeekBar2("BLUR")
        }

    }
    private fun showSeekBar1(operation: String){
        val adjust = Adjust()
        seekbar1.visibility = View.VISIBLE
        seekbar2.visibility = View.INVISIBLE
        seekbar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seek1Value = progress - 100
                seekValue.visibility = View.VISIBLE
                seekValue.text = seek1Value.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekValue.visibility = View.INVISIBLE
                if (operation=="BRIGHTNESS"){
                    editedImage = if (prevOperation == operation) adjust.adjustBrightness(originalImage, seek1Value)
                    else adjust.adjustBrightness(editedImage, seek1Value)
                }
                else if (operation=="CONTRAST"){
                    editedImage = if (prevOperation == operation) adjust.adjustContrast(originalImage, seek1Value)
                    else adjust.adjustContrast(editedImage, seek1Value)
                }
                else if (operation=="SATURATION"){
                    editedImage = if (prevOperation == operation) adjust.adjustSaturation(originalImage, seek1Value)
                    else adjust.adjustSaturation(editedImage, seek1Value)
                }
                else if (operation=="EXPOSURE"){
                    editedImage = if (prevOperation == operation) adjust.adjustExposure(originalImage, seek1Value)
                    else adjust.adjustExposure(editedImage, seek1Value)
                }
                else if (operation=="SHADOW"){
                    editedImage = if (prevOperation == operation) adjust.adjustShadows(originalImage, seek1Value)
                    else adjust.adjustShadows(editedImage, seek1Value)
                }
                else if (operation=="TEMPERATURE"){
                    editedImage = if (prevOperation == operation) adjust.adjustColorTemperature(originalImage, seek1Value)
                    else adjust.adjustColorTemperature(editedImage, seek1Value)
                }
                else if (operation=="TONE"){
                    editedImage =
                        if (prevOperation == operation) adjust.adjustTone(originalImage, seek1Value)
                        else adjust.adjustTone(editedImage, seek1Value)
                }
                else if (operation=="VIGNETTE"){
                    editedImage = if (prevOperation == operation) adjust.adjustVignette(originalImage, seek1Value)
                    else adjust.adjustVignette(editedImage, seek1Value)
                }

                prevOperation = operation
                progressBar.visibility = View.VISIBLE
                Handler().postDelayed({
                    setImage()
                    progressBar.visibility = View.INVISIBLE
                }, ADJUST_TIME)
            }
        })

    }
    private fun showSeekBar2(operation: String){
        val adjust = Adjust()
        seekbar1.visibility = View.INVISIBLE
        seekbar2.visibility = View.VISIBLE
        seekbar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seek2Value = progress
                seekValue.visibility = View.VISIBLE
                seekValue.text = seek2Value.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekValue.visibility = View.INVISIBLE
                if (operation=="SHARPNESS"){
                    editedImage = if (prevOperation == operation) adjust.adjustSharpness(originalImage, seek2Value, requireContext() as Activity)
                    else adjust.adjustSharpness(editedImage, seek2Value, requireContext() as Activity)
                }
                else if (operation=="BRILLIANCE"){
                    editedImage = if (prevOperation == operation) adjust.adjustBrilliance(originalImage, seek2Value)
                    else adjust.adjustBrilliance(editedImage, seek2Value)
                }
                else if(operation=="BLUR"){
                    editedImage = if (prevOperation == operation) adjust.adjustBlur(originalImage, seek2Value, requireContext() as Activity)
                    else adjust.adjustBlur(editedImage, seek2Value, requireContext() as Activity)
                }

                prevOperation = operation
                progressBar.visibility = View.VISIBLE
                Handler().postDelayed({
                    setImage()
                    progressBar.visibility = View.INVISIBLE
                }, ADJUST_TIME)
            }
        })
    }
    private fun hideSeekBars(){
        seekbar1.visibility = View.INVISIBLE
        seekbar2.visibility = View.INVISIBLE
        seekValue.visibility = View.INVISIBLE
    }
    private fun handleAdjustFontColor(view: View, active: String){
        val opt1 = view.findViewById<TextView>(R.id.brightness)
        val opt2 = view.findViewById<TextView>(R.id.contrast)
        val opt3 = view.findViewById<TextView>(R.id.saturation)
        val opt4 = view.findViewById<TextView>(R.id.exposure)
        val opt5 = view.findViewById<TextView>(R.id.shadow)
        val opt6 = view.findViewById<TextView>(R.id.sharpness)
        val opt7 = view.findViewById<TextView>(R.id.temperature)
        val opt8 = view.findViewById<TextView>(R.id.tone)
        val opt9 = view.findViewById<TextView>(R.id.brilliance)
        val opt10 = view.findViewById<TextView>(R.id.vignette)
        val opt11 = view.findViewById<TextView>(R.id.blur)

        opt1.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt2.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt3.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt4.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt5.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt6.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt7.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt8.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt9.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt10.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))
        opt11.setTextColor(ContextCompat.getColor(requireContext(), R.color.adjust_opt_secondary))

        if (active == "BRIGHTNESS") opt1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "CONTRAST") opt2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "SATURATION") opt3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "EXPOSURE") opt4.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "SHADOW") opt5.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "SHARPNESS") opt6.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "TEMPERATURE") opt7.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "TONE") opt8.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "BRILLIANCE") opt9.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "VIGNETTE") opt10.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        else if (active == "BLUR") opt11.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    // Handle filters
    private fun handleFilters(view: View){
        val filters = Filters()

        val noFilter = view.findViewById<RelativeLayout>(R.id.no_filter)
        val redFilter = view.findViewById<RelativeLayout>(R.id.red_filter)
        val pinkFilter = view.findViewById<RelativeLayout>(R.id.pink_filter)
        val orangeFilter = view.findViewById<RelativeLayout>(R.id.orange_filter)
        val yellowFilter = view.findViewById<RelativeLayout>(R.id.yellow_filter)
        val greenFilter = view.findViewById<RelativeLayout>(R.id.green_filter)
        val blueFilter = view.findViewById<RelativeLayout>(R.id.blue_filter)
        val purpleFilter = view.findViewById<RelativeLayout>(R.id.purple_filter)
        val hotPinkFilter = view.findViewById<RelativeLayout>(R.id.hot_pink_filter)
        val cyanFilter = view.findViewById<RelativeLayout>(R.id.cyan_filter)
        val oRedFilter = view.findViewById<RelativeLayout>(R.id.orange_red_filter)
//        val oiledFilter = view.findViewById<RelativeLayout>(R.id.oiled_filter)
        val bwFilter = view.findViewById<RelativeLayout>(R.id.bw_filter)
        val randomiseFilter = view.findViewById<RelativeLayout>(R.id.randomise_filter)

        // Applying 14 filters
        noFilter.setOnClickListener {
            editedImage = originalImage

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },1500)
        }
        redFilter.setOnClickListener {
            editedImage = if(prevOperation == "FILTER") filters.colorizeImage(originalImage, Static.RED_HEX_CODE)
            else filters.colorizeImage(editedImage, Static.RED_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            }, FILTER_TIME)
        }
        pinkFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.PINK_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.PINK_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        orangeFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.ORANGE_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.ORANGE_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        yellowFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.YELLOW_HEX_CODE)
            editedImage = filters.colorizeImage(editedImage, Static.YELLOW_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },1500)
        }
        greenFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.GREEN_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.GREEN_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        blueFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.BLUE_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.BLUE_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        purpleFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.PURPLE_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.PURPLE_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        hotPinkFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.HOT_PINK_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.HOT_PINK_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        cyanFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.CYAN_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.CYAN_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        oRedFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.colorizeImage(originalImage, Static.ORANGE_RED_HEX_CODE)
            else editedImage = filters.colorizeImage(editedImage, Static.ORANGE_RED_HEX_CODE)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
//        oiledFilter.setOnClickListener {
//            if (prevOperation == "FILTER") editedImage = filters.applyOilEffect(originalImage)
//            else editedImage = filters.applyOilEffect(editedImage)
//
//            prevOperation = "FILTER"
//
//            progressBar.visibility = View.VISIBLE
//            Handler().postDelayed({
//                setImage()
//                progressBar.visibility = View.INVISIBLE
//            },FILTER_TIME)
//        }
        bwFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.convertToBlackAndWhite(originalImage)
            else editedImage = filters.convertToBlackAndWhite(editedImage)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }
        randomiseFilter.setOnClickListener {
            if (prevOperation == "FILTER") editedImage = filters.randomColorization(originalImage, Static.COLOR_LIST_COLORIZATION)
            else editedImage = filters.randomColorization(editedImage, Static.COLOR_LIST_COLORIZATION)

            prevOperation = "FILTER"

            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                setImage()
                progressBar.visibility = View.INVISIBLE
            },FILTER_TIME)
        }

    }

    // Handle rotation
    private fun handleRotate(){
        val rotate = Rotate()
        leftRotate.setOnClickListener {
            editedImage = rotate.rotateBitmap(editedImage, -90)
            setImage()
        }
        rightRotate.setOnClickListener {
            editedImage = rotate.rotateBitmap(editedImage, 90)
            setImage()
        }
    }

    // Image set in to view
    private fun setImage(){
        showImage.setImageBitmap(editedImage)
    }

    // Bitmap from URI
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }


    // Main three options on clicks
    private fun mainOpts(){
        mainRotate.setOnClickListener {
            rotateViewSetUp()
            hideSeekBars()
        }
        mainFilters.setOnClickListener {
            filterViewSetUp()
            hideSeekBars()
        }
        mainAdjust.setOnClickListener {
            adjustViewSetUp()
            hideSeekBars()
        }
    }
    private fun rotateViewSetUp(){
        rotateOptions.visibility = View.VISIBLE
        filtersOptions.visibility = View.INVISIBLE
        adjustOptions. visibility = View.INVISIBLE
        originalImage = editedImage
    }
    private fun filterViewSetUp(){
        rotateOptions.visibility = View.INVISIBLE
        filtersOptions.visibility = View.VISIBLE
        adjustOptions. visibility = View.INVISIBLE
        originalImage = editedImage
    }
    private fun adjustViewSetUp(){
        rotateOptions.visibility = View.INVISIBLE
        filtersOptions.visibility = View.INVISIBLE
        adjustOptions. visibility = View.VISIBLE
        originalImage = editedImage
    }

    // Show alert box for discarding
    private fun showDiscardChangesDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Discard changes?")
        builder.setMessage("Are you sure you want to discard the changes?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            startActivity(Intent(requireContext(), Dashboard::class.java))
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->
            Toast.makeText(requireContext(), "Continue editing...", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }


    // Bitmap to png
    fun bitmapToPng(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }





    /***********************************/




}