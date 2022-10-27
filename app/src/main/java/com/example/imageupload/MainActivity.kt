package com.example.imageupload

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.imageupload.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog : AlertDialog
    private var uriImagem :Uri? = null
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        binding.imageView.setImageURI(null)
        binding.imageView.setImageURI(uriImagem)
    }


    companion object {
        private const val PERMISSAO_GALERIA = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private val requestGaleria =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissao ->
        if(permissao) {
            resultGaleria.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        } else {
            showDialogPermissao()
        }
    }

    private val resultGaleria =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.data?.data != null) {
                val bitmap: Bitmap = if(Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        baseContext.contentResolver,
                        result.data?.data
                    )
                } else {
                    val source = ImageDecoder.createSource(
                        this.contentResolver,
                        result.data?.data!!)
                    ImageDecoder.decodeBitmap(source)
                }
                binding.imageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uriImagem = obterImagemCamera()
        binding.ButtonNav.setOnClickListener { navigateToRecycler() }
        binding.ButtonGallery.setOnClickListener { verificarPermissaoGaleria() }
        binding.CloseButton.setOnClickListener {
            binding.imageView.setImageURI(null)
            binding.imageView.setImageBitmap(null)
        }
        binding.ButtonCamera.setOnClickListener { contract.launch(uriImagem) }
    }

    private fun verificarPermissaoGaleria() {
        val permissaoGaleriaAceita = verificaPermissao(PERMISSAO_GALERIA)

        when {
            permissaoGaleriaAceita -> {
                resultGaleria.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ))
            }

            shouldShowRequestPermissionRationale(PERMISSAO_GALERIA) -> showDialogPermissao()

             else -> requestGaleria.launch(PERMISSAO_GALERIA)
        }
    }

    private fun showDialogPermissao() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Precisamos do acesso a galeria do dispositivo, deseja permitir agora ?")
            .setNegativeButton("Não") {_, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") {_, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
        dialog = builder.create()
        dialog.show()
    }

    private fun verificaPermissao(permissao: String) =
        ContextCompat.checkSelfPermission(this, permissao) == PackageManager.PERMISSION_GRANTED


    private fun obterImagemCamera() : Uri? {
        val image = File(applicationContext.filesDir, "my_images.png")
        return FileProvider.getUriForFile(applicationContext,
            "com.example.imageupload",image)
    }

    private fun navigateToRecycler() {
        startActivity(Intent(this, RecyclerActivity::class.java))
    }
}
