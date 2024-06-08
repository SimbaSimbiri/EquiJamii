package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.databinding.DialogAddEventsBinding
import java.util.Calendar

class AddEventsDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAddEventsBinding? = null
    private var eventDate: Calendar = Calendar.getInstance()
    private lateinit var openImagePicker: ActivityResultLauncher<CropImageContractOptions>
    private var imageUri : Uri? = null
    private val binding get() = _binding!!
    private var event: Event? = null
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        fun newInstance(event: Event?): AddEventsDialog {
            val args = Bundle()
            args.putParcelable("event", event)
            val fragment = AddEventsDialog()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        openImagePicker = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // Display the selected image using Glide
                Glide.with(requireContext()).load(result.uriContent).into(binding.eventImage)
                // Save the URI to use when posting to Firestore
                imageUri = result.uriContent
            } else {
                val error = result.error
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = DialogAddEventsBinding.inflate(inflater, container, false)
        event = arguments?.getParcelable("event")
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            postButton.setOnClickListener { postEvent() }
            cancelButton.setOnClickListener { dismiss() }
            addImageButton.setOnClickListener {
                val options = CropImageContractOptions(null, CropImageOptions(
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
                    fixAspectRatio = false))

                openImagePicker.launch(options)
            }

            // Populate the views if editing an existing event
            event?.let {
                titleInput.setText(it.title)
                descriptionInput.setText(it.description)
                typeInput.setText(it.eventType)
                // Initialize date and time pickers with the event's existing date and time
            }
        }
    }

    private fun setupDateTimePickers() {
        // Initialize the date picker with the current date or specific date if editing
        event?.dateTime?.toDate()?.let { date ->
            eventDate.time = date
        }

    }

    private fun postEvent() {
        val newEvent = Event(
            title = binding.titleInput.text.toString(),
            description = binding.descriptionInput.text.toString(),
            location = binding.locationInput.text.toString(),
            dateTime = Timestamp(eventDate.time), // Use the updated eventDate from pickers
            imageUrl = imageUri?.toString() ?: event?.imageUrl,
            userId = "currentUserId", // Example user ID, fetch this dynamically as needed
            eventType = binding.typeInput.text.toString(),
            documentId = event?.documentId // Existing ID if editing
        )

        if (event == null) {
            // Adding a new event
            firestore.collection("events").add(newEvent)
                .addOnSuccessListener {
                    Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Updating an existing event
            firestore.collection("events").document(newEvent.documentId!!)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


