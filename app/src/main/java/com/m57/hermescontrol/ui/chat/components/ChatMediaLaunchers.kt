package com.m57.hermescontrol.ui.chat.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.m57.hermescontrol.ExternalActivityLifecycleGuard
import com.m57.hermescontrol.R
import com.m57.hermescontrol.data.model.Attachment
import com.m57.hermescontrol.ui.chat.VoiceNoteRecorder
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

class ChatMediaLaunchers(
    val isListening: Boolean,
    val isRecordingVoice: Boolean,
    val isVoiceNoteLocked: Boolean,
    val onMicTap: () -> Unit,
    val onCameraTap: () -> Unit,
    val onImageTap: () -> Unit,
    val onFileTap: () -> Unit,
    val onMicHoldStart: () -> Unit = {},
    val onMicHoldEnd: () -> Unit = {},
    val onMicHoldCancel: () -> Unit = {},
    val onMicLock: () -> Unit = {},
    /** Normalized 0–1 mic level while recording; read fresh at draw time. */
    val amplitudeProvider: () -> Float = { 0f },
)

@Composable
fun rememberChatMediaLaunchers(
    onAddAttachment: (uri: String, name: String, mimeType: String, size: Long) -> Unit,
    onAddAttachments: (List<Attachment>) -> Unit,
    onVoiceNoteRecorded: (file: File, insertIntoComposer: Boolean) -> Unit,
    onShowMessage: (String) -> Unit,
    launchExternalActivity: (() -> Unit) -> Unit,
    context: Context = LocalContext.current,
): ChatMediaLaunchers {
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val currentOnAddAttachment by rememberUpdatedState(onAddAttachment)
    val currentOnAddAttachments by rememberUpdatedState(onAddAttachments)
    val currentOnVoiceNoteRecorded by rememberUpdatedState(onVoiceNoteRecorded)
    val currentOnShowMessage by rememberUpdatedState(onShowMessage)
    val currentLaunchExternalActivity by rememberUpdatedState(launchExternalActivity)

    val sttPermissionDeniedMsg = stringResource(R.string.stt_permission_denied)
    val cameraErrorMsg = stringResource(R.string.chat_camera_error)
    val voiceRecordFailedMsg = stringResource(R.string.chat_voice_record_failed)
    val dictationHintMsg = stringResource(R.string.chat_voice_dictation_hint)
    val voiceQueuedHintMsg = stringResource(R.string.fourche_voice_queued_hint)

    // Fourche noronuke : les deux usages du vocal passent par le même enregistreur
    // et la même transcription côté serveur (le téléphone de Baptiste n'a pas de
    // reconnaissance vocale Google). Un appui long envoie le vocal directement ;
    // un tap ouvre une dictée mains libres dont le texte s'écrit dans le champ,
    // à compléter puis à re-dicter si besoin.
    val voiceNoteRecorder = remember { VoiceNoteRecorder(context) }
    var isRecordingVoice by remember { mutableStateOf(false) }

    // Locked = the recording continues after the finger lifts (slide up
    // during the hold); the action button then submits it. A tap-opened
    // dictation starts locked, so the same action button ends it.
    var isVoiceNoteLocked by remember { mutableStateOf(false) }

    // Ce que devient l'enregistrement en cours : envoyé, ou écrit dans le champ.
    var insertIntoComposer by remember { mutableStateOf(false) }

    // Ce qu'une demande de permission micro devra démarrer une fois accordée.
    var pendingPermissionIntent by remember { mutableStateOf<Boolean?>(null) }

    fun finishVoiceRecording() {
        val recordedFile = voiceNoteRecorder.stop()
        val insert = insertIntoComposer
        isRecordingVoice = false
        isVoiceNoteLocked = false
        insertIntoComposer = false
        if (recordedFile != null) {
            currentOnVoiceNoteRecorded(recordedFile, insert)
            if (!insert) currentOnShowMessage(voiceQueuedHintMsg)
        }
    }

    // Dictée mains libres : un tap sur le micro démarre l'enregistrement, le
    // bouton d'action l'arrête et la transcription s'écrit dans le champ.
    fun startDictation() {
        insertIntoComposer = true
        if (voiceNoteRecorder.start()) {
            isRecordingVoice = true
            isVoiceNoteLocked = true
            currentOnShowMessage(dictationHintMsg)
        } else {
            insertIntoComposer = false
            currentOnShowMessage(voiceRecordFailedMsg)
        }
    }

    // App-owned max-duration cap: the timer drives the same
    // finishVoiceRecording() path as the hold release, so exactly one code
    // path ever calls stop() — MediaRecorder's own auto-stop used to race
    // the release and discard a valid max-length clip (review, PR #1250).
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            delay(VoiceNoteRecorder.MAX_DURATION_MS.toLong())
            if (voiceNoteRecorder.isActive) {
                finishVoiceRecording()
            }
        }
    }

    DisposableEffect(voiceNoteRecorder) {
        onDispose {
            voiceNoteRecorder.cancel()
        }
    }

    // Permission micro. Accorder ne relance jamais un envoi direct (l'appui long
    // est déjà fini) mais démarre la dictée demandée par un tap.
    val voiceNotePermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            ExternalActivityLifecycleGuard.externalActivityReturned()
            val intent = pendingPermissionIntent
            pendingPermissionIntent = null
            if (!granted) {
                currentOnShowMessage(sttPermissionDeniedMsg)
            } else if (intent == true && !voiceNoteRecorder.isActive) {
                startDictation()
            }
        }

    // Multi-file picker for attachments (issue #195)
    val filePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetMultipleContents(),
        ) { uris: List<Uri> ->
            ExternalActivityLifecycleGuard.externalActivityReturned()
            val attachments =
                uris.mapNotNull { uri ->
                    runCatching {
                        var name = uri.lastPathSegment ?: "file"
                        var size = 0L
                        context.contentResolver
                            .query(
                                uri,
                                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                                null,
                                null,
                                null,
                            )?.use { cursor ->
                                if (cursor.moveToFirst()) {
                                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                                    if (nameIdx >= 0 && !cursor.isNull(nameIdx)) name = cursor.getString(nameIdx)
                                    if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) size = cursor.getLong(sizeIdx)
                                }
                            }
                        Attachment(
                            uri = uri.toString(),
                            name = name,
                            mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream",
                            size = size,
                        )
                    }.onFailure { error ->
                        Log.w("ChatScreen", "Skipping unreadable picked attachment", error)
                    }.getOrNull()
                }
            currentOnAddAttachments(attachments)
        }

    // Camera photo launcher (issue #195)
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture(),
        ) { success ->
            ExternalActivityLifecycleGuard.externalActivityReturned()
            val uri = pendingCameraUri
            pendingCameraUri = null
            if (success && uri != null) {
                try {
                    val fileName =
                        "photo_${
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(
                                Date(),
                            )
                        }.jpg"
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val size = inputStream?.use { it.available().toLong() } ?: 0L
                    currentOnAddAttachment(uri.toString(), fileName, "image/jpeg", size)
                } catch (e: Exception) {
                    Log.e("ChatScreen", "Camera capture failed", e)
                    currentOnShowMessage(cameraErrorMsg)
                }
            }
        }

    val onMicTap: () -> Unit = {
        if (voiceNoteRecorder.isActive) {
            // Le bouton d'action de la barre (ou un second tap) arrête
            // l'enregistrement en cours, dictée comme vocal direct.
            finishVoiceRecording()
        } else if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startDictation()
        } else {
            pendingPermissionIntent = true
            currentLaunchExternalActivity {
                voiceNotePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val onMicHoldStart: () -> Unit = {
        if (voiceNoteRecorder.isActive) {
            // Un enregistrement (vocal ou dictée) est déjà en cours.
        } else if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            insertIntoComposer = false
            if (voiceNoteRecorder.start()) {
                isRecordingVoice = true
            } else {
                currentOnShowMessage(voiceRecordFailedMsg)
            }
        } else {
            // The denial message comes from voiceNotePermissionLauncher's
            // result callback only — showing it here fired it before the
            // dialog even opened (review, PR #1250).
            pendingPermissionIntent = false
            currentLaunchExternalActivity {
                voiceNotePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val onMicHoldEnd: () -> Unit = {
        if (voiceNoteRecorder.isActive) {
            finishVoiceRecording()
        }
    }

    val onMicHoldCancel: () -> Unit = {
        if (voiceNoteRecorder.isActive) {
            voiceNoteRecorder.cancel()
        }
        isRecordingVoice = false
        isVoiceNoteLocked = false
        insertIntoComposer = false
    }

    // Locking keeps the recorder running once the finger lifts; only the
    // panel's delete action or the action button can end it then.
    val onMicLock: () -> Unit = {
        if (voiceNoteRecorder.isActive) {
            isVoiceNoteLocked = true
        }
    }

    // Mic-level feed for the recording blob (VoiceMicBlob). MediaRecorder's
    // maxAmplitude is the PEAK since the last read, so this poller must stay
    // the ONLY caller while recording. sqrt compression spreads speech
    // dynamics the way Telegram's RMS/1800 normalization does.
    val recordingAmplitude = remember { mutableStateOf(0f) }
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            while (isRecordingVoice) {
                recordingAmplitude.value =
                    sqrt(voiceNoteRecorder.currentAmplitude().coerceAtLeast(0) / 32767f)
                delay(AMPLITUDE_POLL_MS)
            }
        } else {
            recordingAmplitude.value = 0f
        }
    }

    val onCameraTap: () -> Unit = {
        try {
            val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val photoFile =
                File.createTempFile("camera_${timeStamp}_", ".jpg", context.cacheDir)
            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile,
                )
            pendingCameraUri = uri
            currentLaunchExternalActivity {
                cameraLauncher.launch(uri)
            }
        } catch (e: Exception) {
            Log.e("ChatScreen", "Camera launch failed", e)
        }
    }

    val onImageTap: () -> Unit = {
        currentLaunchExternalActivity {
            filePickerLauncher.launch("image/*")
        }
    }

    val onFileTap: () -> Unit = {
        currentLaunchExternalActivity {
            filePickerLauncher.launch("*/*")
        }
    }

    return remember(isRecordingVoice, isVoiceNoteLocked) {
        ChatMediaLaunchers(
            isListening = isRecordingVoice,
            isRecordingVoice = isRecordingVoice,
            isVoiceNoteLocked = isVoiceNoteLocked,
            onMicTap = onMicTap,
            onCameraTap = onCameraTap,
            onImageTap = onImageTap,
            onFileTap = onFileTap,
            onMicHoldStart = onMicHoldStart,
            onMicHoldEnd = onMicHoldEnd,
            onMicHoldCancel = onMicHoldCancel,
            onMicLock = onMicLock,
            amplitudeProvider = { recordingAmplitude.value },
        )
    }
}

/** Telegram samples their amplitude pipeline roughly this fast. */
private const val AMPLITUDE_POLL_MS = 60L
