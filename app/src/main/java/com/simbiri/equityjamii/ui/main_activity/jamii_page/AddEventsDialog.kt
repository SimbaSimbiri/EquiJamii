package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ozcanalasalvar.datepicker.view.datepicker.DatePicker
import com.ozcanalasalvar.datepicker.view.timepicker.TimePicker
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.constants.POST_STORAGE_REF
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.databinding.DialogAddEventsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEventsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAddEventsBinding? = null
    private var eventDate: Calendar = Calendar.getInstance()
    private lateinit var openImagePicker: ActivityResultLauncher<CropImageContractOptions>
    private val eventStorageRef = FirebaseStorage.getInstance().getReference()

    private var imageUri: Uri? = null
    private val binding get() = _binding!!
    private var event: Event? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val currentId = AuthUtils.getCurrentUserId()

    companion object {
        private const val ARGS_EVENT = "event"
        fun newInstance(event: Event?): AddEventsDialog {
            val args = Bundle()
            args.putParcelable(ARGS_EVENT, event)
            val fragment = AddEventsDialog()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        openImagePicker = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                binding.eventImage.visibility = View.VISIBLE
                Glide.with(requireContext()).load(result.uriContent).centerCrop()
                    .into(binding.eventImage)
                imageUri = result.uriContent
            } else {
                val error = result.error
                Toast.makeText(context, "Error: ${error?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEventsBinding.inflate(inflater, container, false)
        event = arguments?.getParcelable(ARGS_EVENT)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            postButton.setOnClickListener { postEvent() }
            cancelButton.setOnClickListener { dismiss() }
            addImageButton.setOnClickListener {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        true,
                        false,
                        CropImageView.CropShape.RECTANGLE,
                        cropCornerRadius = 8.0F,
                        cropMenuCropButtonTitle = "Done",
                        showCropLabel = true,
                        activityTitle = "Crop event poster",
                        activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                        toolbarColor = requireContext().resources.getColor(R.color.black),
                        progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                        guidelines = CropImageView.Guidelines.OFF,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = false
                    )
                )

                openImagePicker.launch(options)
            }

            event?.let {
                titleInput.setText(it.title)
                descriptionInput.setText(it.description)
                typeInput.setText(it.eventType)

                if (it.imageUrl != null) {
                    binding.eventImage.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(event?.imageUrl).fitCenter()
                        .into(this.eventImage)
                }

                val eventDateTime = it.dateTime?.toDate() ?: Calendar.getInstance().time

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(eventDateTime).toString()
                val formattedTime = DateFormat.format("HHmm", eventDateTime).toString()

                binding.selectedDateDisplayTv.text = formattedDate
                binding.selectedTimeDisplayTv.text = formattedTime + " hrs"

                locationInput.setText(it.location)
            }



            setUpEventDateTimeDialogs()

        }

    }

    private fun setUpEventDateTimeDialogs() {

        if (event?.dateTime != null) {
            eventDate.time = event?.dateTime?.toDate() ?: Calendar.getInstance().time
        } else {
            eventDate = Calendar.getInstance()
        }

        binding.selectDateTv.setOnClickListener {
            DatePickerDialog(
                requireContext(), R.style.CustomDatePickerTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    eventDate.set(Calendar.YEAR, year)
                    eventDate.set(Calendar.MONTH, monthOfYear)
                    eventDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(eventDate.time).toString()
                    binding.selectedDateDisplayTv.text = formattedDate
                },
                eventDate.get(Calendar.YEAR),
                eventDate.get(Calendar.MONTH),
                eventDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.selectTimeTv.setOnClickListener {
            TimePickerDialog(
                requireContext(), R.style.CustomTimePickerTheme,
                { _, hourOfDay, minute ->
                    eventDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    eventDate.set(Calendar.MINUTE, minute)

                    val formattedTime = DateFormat.format("HHmm", eventDate).toString()
                    binding.selectedTimeDisplayTv.text = formattedTime + " hrs"
                },
                eventDate.get(Calendar.HOUR_OF_DAY),
                eventDate.get(Calendar.MINUTE),
                true
            ).show()
        }

    }


    private fun postEvent() {
        val eventItemRef = eventStorageRef.child(POST_STORAGE_REF)
            .child(FieldValue.serverTimestamp().toString() + ".jpg")

        Toast.makeText(
            requireContext(),
            "Uploading event to cloud",
            Toast.LENGTH_LONG
        ).show()

        if (imageUri != null) {
            eventItemRef.putFile(Uri.parse(imageUri.toString()))
                .addOnCompleteListener { taskUpload ->
                    if (taskUpload.isSuccessful) {
                        eventItemRef.downloadUrl.addOnSuccessListener { eventImageUri ->
                            val newEventMap: HashMap<String, Any?> = hashMapOf(
                                "title" to binding.titleInput.text.toString().trim(),
                                "description" to binding.descriptionInput.text.toString().trim(),
                                "location" to binding.locationInput.text.toString().trim(),
                                "dateTime" to Timestamp(eventDate.time),
                                "imageUrl" to eventImageUri.toString(),
                                "userId" to currentId!!,
                                "eventType" to binding.typeInput.text.toString().trim()
                            )

                            if (event == null) {
                                firestore.collection(EVENTS_C0LLECTION).add(newEventMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Event added successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        updateWithDocumentId(it.id)
                                        dismiss()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to add event",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                firestore.collection(EVENTS_C0LLECTION)
                                    .document(event!!.documentId!!).update(newEventMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Event updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dismiss()

                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to update event",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }
        } else {
            val newEventMap: HashMap<String, Any?> = hashMapOf(
                "title" to binding.titleInput.text.toString().trim(),
                "description" to binding.descriptionInput.text.toString().trim(),
                "location" to binding.locationInput.text.toString().trim(),
                "dateTime" to Timestamp(eventDate.time),
                "imageUrl" to event?.imageUrl,
                "userId" to currentId!!,
                "eventType" to binding.typeInput.text.toString().trim()
            )

            if (event == null) {
                firestore.collection(EVENTS_C0LLECTION).add(newEventMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Event added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateWithDocumentId(it.id)
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to add event",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                firestore.collection(EVENTS_C0LLECTION)
                    .document(event!!.documentId!!).update(newEventMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Event updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Failed to update event",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_add_events)
        dialog.setCanceledOnTouchOutside(false)


        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet!!.layoutParams.height = metrics.heightPixels
            bottomSheet.requestLayout()

            bottomSheet.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
                behavior.isHideable = false
                behavior.peekHeight = (metrics.heightPixels * 0.9).toInt()
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }


    private fun updateWithDocumentId(documentId: String) {
        firestore.collection(EVENTS_C0LLECTION).document(documentId)
            .update("documentId", documentId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


