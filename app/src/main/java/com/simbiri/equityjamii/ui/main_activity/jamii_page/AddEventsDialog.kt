package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
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
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.databinding.DialogAddEventsBinding
import java.util.Calendar

class AddEventsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAddEventsBinding? = null
    private var eventDate: Calendar = Calendar.getInstance()
    private lateinit var openImagePicker: ActivityResultLauncher<CropImageContractOptions>
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
                Glide.with(requireContext()).load(result.uriContent).into(binding.eventImage)
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
    ): View? {
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
                        activityTitle = "Crop post image",
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

            // Populate the views if editing an existing event
            event?.let {
                titleInput.setText(it.title)
                descriptionInput.setText(it.description)
                typeInput.setText(it.eventType)
                Glide.with(requireActivity()).load(event?.imageUrl).into(this.eventImage)

                setupDateTimePickers()
            }
        }
    }

    private fun setupDateTimePickers() {

        val initialDateMillis = event?.dateTime?.toDate()?.time ?: System.currentTimeMillis()

        binding.datePicker.apply {
            this.setDate(initialDateMillis)

            setDateChangeListener(object : DatePicker.OnDateChangedListener,
                com.ozcanalasalvar.datepicker.view.datepicker.DatePicker.DateChangeListener {
                override fun onDateChanged(date: Long, day: Int, month: Int, year: Int) {
                    eventDate.set(Calendar.DATE, date.toInt())
                    eventDate.set(Calendar.YEAR, year)
                    eventDate.set(Calendar.MONTH, month)
                    eventDate.set(Calendar.DAY_OF_MONTH, day)
                }

                override fun onDateChanged(
                    view: DatePicker?,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {

                }

            })
        }

        binding.timePicker.apply {
            this.setTime(eventDate.get(Calendar.HOUR_OF_DAY), eventDate.get(Calendar.MINUTE))

            setTimeChangeListener(object : TimePicker.OnTimeChangedListener,
                com.ozcanalasalvar.datepicker.view.timepicker.TimePicker.TimeChangeListener {
                override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {

                }

                override fun onTimeChanged(hour: Int, minute: Int, timeFormat: String?) {
                    eventDate.set(Calendar.HOUR_OF_DAY, hour)
                    eventDate.set(Calendar.MINUTE, minute)
                }

            })
        }

    }

    private fun postEvent() {
        val newEvent = Event(
            title = binding.titleInput.text.toString().trim(),
            description = binding.descriptionInput.text.toString().trim(),
            location = binding.locationInput.text.toString().trim(),
            dateTime = Timestamp(eventDate.time),
            imageUrl = imageUri?.toString() ?: event?.imageUrl,
            userId = currentId!!,
            eventType = binding.typeInput.text.toString().trim(),
            documentId = event?.documentId
        )

        if (event == null) {
            firestore.collection(EVENTS_C0LLECTION).add(newEvent)
                .addOnSuccessListener {
                    Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show()
                    updateWithDocumentId(it.id)
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show()
                }
        } else {
            firestore.collection(EVENTS_C0LLECTION).document(newEvent.documentId!!)
                .set(newEvent)
                .addOnSuccessListener {
                    Toast.makeText(context, "Event updated successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update event", Toast.LENGTH_SHORT).show()
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
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = (metrics.heightPixels * 0.9).toInt()
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


