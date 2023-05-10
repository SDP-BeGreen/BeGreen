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
import com.github.sdp_begreen.begreen.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryPickerView
import java.util.TimeZone


/**
 * A simple [Fragment] subclass.
 * Use the [ContestCreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContestCreationFragment : Fragment() {


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
        val expandButton = view.findViewById<ImageView>(R.id.contest_creation_location_expand)
        val mapButton = view.findViewById<ImageView>(R.id.contest_creation_location_map)
        val cityText = view.findViewById<TextInputEditText>(R.id.city_contest_creation)
        val postalCodeText = view.findViewById<TextInputEditText>(R.id.postal_code_contest_creation)
        val countryPicker = view.findViewById<CountryPickerView>(R.id.contest_creation_country_picker)
        val radiusText = view.findViewById<TextInputEditText>(R.id.radius_contest_creation)
        val timezoneSpinner = view.findViewById<Spinner>(R.id.contest_timezone_spinner)
        val startDateButton = view.findViewById<Button>(R.id.start_date_contest)
        val startDateText = view.findViewById<TextClock>(R.id.start_date_contest_text)
        val endDateButton = view.findViewById<Button>(R.id.end_date_contest)
        val endDateText = view.findViewById<TextClock>(R.id.end_date_contest_text)
        val startHourButton = view.findViewById<Button>(R.id.start_hour_contest)
        val startHourText = view.findViewById<TextClock>(R.id.start_hour_contest_text)
        val endHourButton = view.findViewById<Button>(R.id.end_hour_contest)
        val endHourText = view.findViewById<TextClock>(R.id.end_hour_contest_text)
        val cancelCreationButton = view.findViewById<Button>(R.id.contest_cancel_button)
        val confirmCreationButton = view.findViewById<Button>(R.id.contest_confirm_button)
        val locationDetailsContainer = view.findViewById<View>(R.id.contest_location_details_container)
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
        val rangeDatePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .build()

        setupExpandButton(expandButton, locationDetailsContainer)
        populateAndUpdateTimeZone(timezoneSpinner)
        setupStartDateButton(startDateButton, startDateText, rangeDatePicker)
        return view
    }

    fun setupMapButton(mapButton: ImageView) {
        mapButton.setOnClickListener {

        }
    }

    fun setupStartDateButton(startDateButton : Button, startDateText : TextClock, datePicker : MaterialDatePicker<*>) {
        startDateButton.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }
    }


    fun setupExpandButton(expandButton: ImageView, locationDetailsContainer: View){
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

    fun populateAndUpdateTimeZone(spinner: Spinner) {
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