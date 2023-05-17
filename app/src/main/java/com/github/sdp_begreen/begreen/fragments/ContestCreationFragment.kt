package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextClock
import androidx.fragment.app.viewModels
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.viewModels.ContestCreationViewModel
import com.github.sdp_begreen.begreen.viewModels.ProfileEditedValuesViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hbb20.CountryPickerView
import java.text.SimpleDateFormat
import java.util.TimeZone


/**
 * A simple [Fragment] subclass.
 * Use the [ContestCreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContestCreationFragment : Fragment() {

    private val contestCreationViewModel by viewModels<ContestCreationViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contest_creation, container, false)
        val contestTitle = view.findViewById<TextInputEditText>(R.id.contest_creation_title)
        val privateCheckbox = view.findViewById<CheckBox>(R.id.private_contest_checkbox)
        val mapButton = view.findViewById<ImageView>(R.id.contest_creation_location_map)
        val cityText = view.findViewById<TextInputEditText>(R.id.city_contest_creation)
        val postalCodeText = view.findViewById<TextInputEditText>(R.id.postal_code_contest_creation)
        val countryPicker = view.findViewById<CountryPickerView>(R.id.contest_creation_country_picker)
        val radiusText = view.findViewById<TextInputEditText>(R.id.radius_contest_creation)
        val cancelCreationButton = view.findViewById<Button>(R.id.contest_cancel_button)
        val confirmCreationButton = view.findViewById<Button>(R.id.contest_confirm_button)


        setupExpandButton(view)
        populateAndUpdateTimeZone(view)
        setupDateButton(view)
        setupStartHoursButton(view)
        setupEndHoursButton(view)
        return view
    }

    fun setupMapButton(mapButton: ImageView) {
        mapButton.setOnClickListener {

        }
    }

    fun setupDateButton(view : View) {
        val startDateButton = view.findViewById<Button>(R.id.date_period_contest)
        val startDateText = view.findViewById<TextClock>(R.id.start_date_contest_text)
        val endDateText = view.findViewById<TextClock>(R.id.end_date_contest_text)

        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .build()

        startDateButton.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }
        datePicker.addOnPositiveButtonClickListener {
            startDateText.text = datePicker.headerText
            val pair : androidx.core.util.Pair<Long,Long> = datePicker.selection as androidx.core.util.Pair<Long, Long>
            startDateText.text = pair.first.toString()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val begin = formatter.format(pair.first)
            val end = formatter.format(pair.second)
            startDateText.text = begin
            endDateText.text = end
        }
    }

    fun setupStartHoursButton(view : View) {
        val startHourButton = view.findViewById<Button>(R.id.start_hour_contest)
        val startHourText = view.findViewById<TextClock>(R.id.start_hour_contest_text)
        val hourPicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                //.setTitle("Select Appointment time")
                .build()

        startHourButton.setOnClickListener{
            hourPicker.show(requireActivity().supportFragmentManager, "hourPicker")
        }
        hourPicker.addOnPositiveButtonClickListener {
            val hour = hourPicker.hour
            val minute = hourPicker.minute
            startHourText.text = "$hour:$minute"
        }
    }

    fun setupEndHoursButton(view : View) {
        val endHoursButton = view.findViewById<Button>(R.id.end_hour_contest)
        val endHourText = view.findViewById<TextClock>(R.id.end_hour_contest_text)

        val hourPicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                //.setTitle("Select Appointment time")
                .build()

        endHoursButton.setOnClickListener{
            hourPicker.show(requireActivity().supportFragmentManager, "hourPicker")
        }
        hourPicker.addOnPositiveButtonClickListener {
            val hour = hourPicker.hour
            val minute = hourPicker.minute
            endHourText.text = "$hour:$minute"
        }
    }

    fun setupExpandButton(view: View){
        val expandButton = view.findViewById<ImageView>(R.id.contest_creation_location_expand)
        val locationDetailsContainer = view.findViewById<View>(R.id.contest_location_details_container)
        expandButton.setOnClickListener {
            if (locationDetailsContainer.visibility == View.GONE) {
                locationDetailsContainer.visibility = View.VISIBLE
                expandButton.setImageResource(R.drawable.ic_chevron_down)
            }
            else {
                locationDetailsContainer.visibility = View.GONE
                expandButton.setImageResource(R.drawable.ic_chevron_up)
            }
        }
    }

    fun populateAndUpdateTimeZone(view : View) {
        val spinner = view.findViewById<Spinner>(R.id.contest_timezone_spinner)
        val idArray = TimeZone.getAvailableIDs()
        val idAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            idArray
        )
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(idAdapter);

        // now set the spinner to default timezone from the time zone settings
        for (i in idArray.indices) {
            if (idArray[i] == TimeZone.getDefault().id) {
                spinner.setSelection(i)
                break
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ContestCreationFragment()
    }
}