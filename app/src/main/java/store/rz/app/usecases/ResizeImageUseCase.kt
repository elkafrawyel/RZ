package store.rz.app.usecases

import com.facebook.spectrum.Spectrum
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.JPEG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import java.io.InputStream


class ResizeImageUseCase(
    private val mSpectrum: Spectrum
) {

    fun resize(inputStream: InputStream) {
        val transcodeOptions = TranscodeOptions.Builder(EncodeRequirement(JPEG, 80))
            .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, ImageSize(1080, 1080))
            .build()

        val result = mSpectrum.transcode(
            EncodedImageSource.from(inputStream),
            EncodedImageSink.from("my/output/file/path/upload.jpeg"),
            transcodeOptions,
            "upload_flow_callsite_identifier"
        )
    }
}