package com.example.caps.ui.helper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.example.caps.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class PredictionHelper(
    private val context: Context,
    private val modelName: String = "Xception_V3_frozen.tflite",
    private val onResult: (String, Float) -> Unit,
    private val onError: (String) -> Unit,
    private val onDownloadSuccess: () -> Unit
) {
    private var interpreter: InterpreterApi? = null
    private val inputSize = 224
    private var isGPUSupported: Boolean = false
    private var isInterpreterInitialized = false

    init {
        val startTime = System.currentTimeMillis()
        Log.d("PredictionHelper", "TensorFlow Lite initialization started.")
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(false)
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            Log.d("PredictionHelper", "TensorFlow Lite initialized successfully.")
            loadModel()
        }.addOnFailureListener {
            onError("TensorFlow Lite failed to initialize.")
        }.addOnCompleteListener {
            val endTime = System.currentTimeMillis()
            Log.d("PredictionHelper", "Initialization completed in ${endTime - startTime} ms.")
        }
    }

    @Synchronized
    private fun downloadModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("cancer_haa", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                if (model.file != null) {
                    Log.d("PredictionHelper", "Model berhasil diunduh.")
                    onDownloadSuccess()
                    initializeInterpreter(model)
                } else {
                    onError("Model berhasil diunduh tetapi file tidak ditemukan.")
                }
            }
            .addOnFailureListener { e: Exception ->
                onError(context.getString(R.string.model_gagal))
                Log.e("PredictionHelper", "Gagal mengunduh model: ${e.message}")
            }
    }


    private fun loadModel() {
        try {
            val buffer = loadModelFile(context.assets, modelName)
            Log.d("PredictionHelper", "Model loaded successfully from assets.")
            onDownloadSuccess()
            initializeInterpreter(buffer)
        } catch (e: IOException) {
            onError("Failed to load model: ${e.message}")
            Log.e("PredictionHelper", "Error loading model: ${e.message}")
        }
    }

    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)

            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            } else if (model is CustomModel) {
                model.file?.let {
                    interpreter = InterpreterApi.create(it, options)
                }
            }

            val inputShape = interpreter?.getInputTensor(0)?.shape()
            Log.d("PredictionHelper", "Model input shape: ${inputShape?.contentToString()}")

            isInterpreterInitialized = true
            Log.d("PredictionHelper", "Interpreter initialized successfully.")
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e("InitializeInterpreter", e.message.toString())
        }
    }


    fun predict(bitmap: Bitmap) {
        if (!isInterpreterInitialized) {
            onError("Interpreter is not initialized yet.")
            return
        }
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(12) }
        try {
            interpreter?.run(input, output)
            val predictions = output[0]
            val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1
            val confidence = predictions[maxIndex] * 100
            val predictedClass = getClassLabel(maxIndex)
            onResult(predictedClass, confidence)
        } catch (e: Exception) {
            onError("Prediction failed: ${e.message}")
            Log.e("Tes", e.message.toString())
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224
        val byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        scaledBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        if (byteBuffer.position() != byteBuffer.capacity()) {
            Log.e("PredictionHelper", "Error: ByteBuffer position does not match capacity!")
        }

        return byteBuffer
    }





//    private fun preprocessImage(bitmap: Bitmap): Array<FloatArray> {
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//        val intArray = IntArray(inputSize * inputSize)
//        scaledBitmap.getPixels(intArray, 0, inputSize, 0, 0, inputSize, inputSize)
//
//        val inputArray = Array(1) { FloatArray(inputSize * inputSize * 3) }
//
//        for (i in intArray.indices) {
//            val pixel = intArray[i]
//            val r = (pixel shr 16 and 0xFF) / 255.0f
//            val g = (pixel shr 8 and 0xFF) / 255.0f
//            val b = (pixel and 0xFF) / 255.0f
//
//            inputArray[0][i * 3] = r
//            inputArray[0][i * 3 + 1] = g
//            inputArray[0][i * 3 + 2] = b
//        }
//
//        return inputArray
//    }

//    private fun preprocessImage(bitmap: Bitmap): Array<Array<FloatArray>> {
//        // Tentukan inputSize
//        val inputSize = 224 // Ukuran gambar yang diinginkan
//
//        // Membuat bitmap dengan ukuran yang sesuai
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        // Mengambil data pixel dalam array Int
//        val intArray = IntArray(inputSize * inputSize)
//        scaledBitmap.getPixels(intArray, 0, inputSize, 0, 0, inputSize, inputSize)
//
//        // Membuat array input dengan dimensi (1, 224, 224, 3)
//        val inputArray = Array(1) { Array(inputSize) { FloatArray(inputSize * 3) } }
//
//        // Menyusun pixel menjadi array yang memiliki 3 kanal (RGB)
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixel = intArray[i * inputSize + j]
//                val r = (pixel shr 16 and 0xFF) / 255.0f
//                val g = (pixel shr 8 and 0xFF) / 255.0f
//                val b = (pixel and 0xFF) / 255.0f
//
//                // Mengatur nilai RGB untuk posisi yang sesuai di array
//                inputArray[0][i][j * 3] = r
//                inputArray[0][i][j * 3 + 1] = g
//                inputArray[0][i][j * 3 + 2] = b
//            }
//        }
//
//        println("Dimensi array: [${inputArray.size}, ${inputArray[0].size}, ${inputArray[0][0].size}]")
//
//        return inputArray
//    }

//    private fun preprocessImage(bitmap: Bitmap): Array<Array<FloatArray>> {
//        val inputSize = 224
//
//        // Membuat bitmap dengan ukuran yang sesuai
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        // Mengambil data pixel dalam array Int
//        val intArray = IntArray(inputSize * inputSize)
//        scaledBitmap.getPixels(intArray, 0, inputSize, 0, 0, inputSize, inputSize)
//
//        // Membuat array input dengan dimensi (1, 224, 224, 3)
//        val inputArray = Array(1) { Array(inputSize) { FloatArray(inputSize * 3) } }
//
//        // Menyusun pixel menjadi array yang memiliki 3 kanal (RGB)
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixel = intArray[i * inputSize + j]
//                val r = (pixel shr 16 and 0xFF) / 255.0f
//                val g = (pixel shr 8 and 0xFF) / 255.0f
//                val b = (pixel and 0xFF) / 255.0f
//
//                inputArray[0][i][j * 3] = r
//                inputArray[0][i][j * 3 + 1] = g
//                inputArray[0][i][j * 3 + 2] = b
//            }
//        }
//
//        println("Dimensi array: [${inputArray.size}, ${inputArray[0].size}, ${inputArray[0][0].size}]")
//
//        return inputArray
//    }

//    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
//        val inputSize = 224
//
//        // Membuat bitmap dengan ukuran yang sesuai
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
//
//        // Mengambil data pixel dalam array Int
//        val intArray = IntArray(inputSize * inputSize)
//        scaledBitmap.getPixels(intArray, 0, inputSize, 0, 0, inputSize, inputSize)
//
//        // Membuat array input dengan dimensi (1, 224, 224, 3)
//        val inputArray = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }
//
//        // Menyusun pixel menjadi array yang memiliki 3 kanal (RGB)
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixel = intArray[i * inputSize + j]
//                val r = (pixel shr 16 and 0xFF) / 255.0f
//                val g = (pixel shr 8 and 0xFF) / 255.0f
//                val b = (pixel and 0xFF) / 255.0f
//
//                inputArray[0][i][j][0] = r // Red channel
//                inputArray[0][i][j][1] = g // Green channel
//                inputArray[0][i][j][2] = b // Blue channel
//            }
//        }
//
//        Log.d("PredictionHelper", "Dimensi array input: [${inputArray.size}, ${inputArray[0].size}, ${inputArray[0][0].size}, ${inputArray[0][0][0].size}]")
//
//        return inputArray
//    }

    private fun getClassLabel(index: Int): String {
        val labels = listOf(
            "Monumen Ikada",
            "Monumen Nasional",
            "Monumen Perjuangan",
            "Monumen Selamat Datang",
            "Patung Bung Karno",
            "Patung Diponegoro",
            "Patung Kartini",
            "Patung Kuda Arjuna Wijaya",
            "Patung M.H. Thamrin",
            "Patung Pembebasan Irian Barat",
            "Patung Persahabatan",
            "Tugu Tani"
        )
        return labels.getOrElse(index) { "Unknown" }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): ByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    fun close() {
        interpreter?.close()
    }
}