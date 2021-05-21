package fr.projetl3.deltapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projetl3.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    //---------------------OCR-------------------------

    private lateinit var cameraHolder: SurfaceHolder
    /*
    private lateinit var cameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer
     */
    //--------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        //--------------------------------------------------

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener {
            // Get a stable reference of the modifiable image capture use case
            takePhoto()
        }


        // Set up the listener for button login page
        login_button.setOnClickListener { switchToLoginPage() }

        /*COM*/outputDirectory = getOutputDirectory()

       /*COM*/cameraExecutor = Executors.newSingleThreadExecutor()


    }

    private fun switchToLoginPage(){
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if(user == null){
                val otherActivity = Intent(this, LoginApp::class.java)
                startActivity(otherActivity)
            } else {
                val otherActivity = Intent(this, Account::class.java)
                startActivity(otherActivity)
            }
            finish()
        } catch (e: Exception){
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".bmp"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        var savedUri: Uri
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { Log.e(TAG, "Photo capture failed: ${exc.message}", exc) }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    var msg = "Photo enregistrée( $savedUri )"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    try {
                        val fichier = File(savedUri.path.toString())
                        ////fi.toString()) //"/storage/emulated/0/Android/media/com.example.projetl3/ProjetL3/2021-05-14-23-59-35-805.bmp"
                        Thread.sleep(1000)
                        msg = fichier.absolutePath.toString()
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        if(fichier.exists()){
                            val myBitmap : Bitmap = BitmapFactory.decodeFile(fichier.getAbsolutePath())
                            msg = "Bytmap = $myBitmap"
                            pictureTaken.setImageBitmap(myBitmap)
                            pictureTaken.visibility = View.VISIBLE
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception){
                        val msgError = "Erreur [$e]"
                        Toast.makeText(baseContext, msgError, Toast.LENGTH_SHORT).show()
                    }
                    Log.d(TAG, msg)
                }
            })
    /*
        savedUri = Uri.fromFile(photoFile)
        val uri = Uri.parse(savedUri.toString())
        val fi = File(uri.path)
        var read : Boolean = false
        while(!read){
            try {
                val dt = DetectText(fi.toString())//fi.toString()) //"/storage/emulated/0/Android/media/com.example.projetl3/ProjetL3/2021-05-14-23-59-35-805.bmp"
                read = true
            } catch (e: Exception){
                val msgError = "Erreur [$e]"
                Toast.makeText(baseContext, msgError, Toast.LENGTH_SHORT).show()
            }
        }
        val msgPass = "Le fichier à bien été lu."
        Toast.makeText(baseContext, msgPass, Toast.LENGTH_SHORT).show()
*/


    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                    ,imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        //cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}