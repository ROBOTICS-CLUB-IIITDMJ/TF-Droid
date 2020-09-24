package com.priyanshnama.tf_droid

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var preview : ImageView? = null
    private var modelList : Spinner? = null
    private var mInputSize = 224
    private lateinit var classifier: Classifier
    private var model = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        modelList = findViewById(R.id.model_list)
        preview = findViewById(R.id.preview)
        populateList()
        modelList?.onItemSelectedListener = this
    }

    private fun populateList() {
        ArrayAdapter.createFromResource(
            this,
            R.array.models,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            this.modelList?.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if(pos==0) {
            model = "cat_vs_dog"
            mInputSize = 224
        }
        else if(pos==1) {
            model = "identify_number"
            mInputSize = 64
        }
        classifier = Classifier(assets, model+ "/model.tflite", model+"/label.txt", mInputSize)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun upload(view : View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            preview?.setImageURI(data?.data) // handle chosen image
        }
    }

    fun predict(view : View) {
        try {
            val bitmap = ((preview as ImageView).drawable as BitmapDrawable).bitmap
            val result = classifier.recognizeImage(bitmap)
            runOnUiThread { Toast.makeText(this, result[0].title, Toast.LENGTH_SHORT).show() }
        }catch (e : ClassCastException){
            runOnUiThread { Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show() }
        }
    }
}