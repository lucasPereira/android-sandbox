package br.dev.lucas.sandbox

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CameraActivity : AppCompatActivity() {

    private lateinit var permissionRequest: ActivityResultLauncher<String>

    private var hasPermission: Boolean = false
    private var openCamera: Camera? = null
    private var openCameraId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        title = "Camera"
        showCameras()
        permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasPermission = granted
            showCamera()
        }
    }

    private fun showCameras() {
        val cameras = mutableListOf<String>()
        val numberOfCameras = Camera.getNumberOfCameras()
        SandboxLogger.debug("Number of cameras: $numberOfCameras")
        for (index in 0 until numberOfCameras) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(index, cameraInfo)
            SandboxLogger.debug("Camera $index: <facing ${cameraInfo.facing}> <orientation ${cameraInfo.orientation}> <canDisableShutterSound ${cameraInfo.canDisableShutterSound}>")
            cameras.add("Camera $index: <facing ${cameraInfo.facing}> <orientation ${cameraInfo.orientation}> <canDisableShutterSound ${cameraInfo.canDisableShutterSound}>")
        }
        val list = findViewById<ListView>(R.id.cameras_list)
        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cameras)
        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            checkPermissionAndShowCamera(position)
        }

    }

    private fun checkPermissionAndShowCamera(position: Int) {
        openCameraId = position
        when {
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                hasPermission = true
                showCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)-> {
                hasPermission = false
                showPermissionInfo()
            }
            else -> {
                hasPermission = false
                permissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showCamera() {
        if (hasPermission && openCameraId != null) {
            openCamera?.stopPreview()
            openCamera?.release()
            openCamera = Camera.open(openCameraId!!)
            val cameraSurface = findViewById<SurfaceView>(R.id.camera_surface)
            val cameraHolder = cameraSurface.holder
            openCamera?.setPreviewDisplay(cameraHolder)
            openCamera?.startPreview()
        }
    }

    private fun showPermissionInfo() {
        SandboxLogger.debug("Education permission information dialog")
    }

}
