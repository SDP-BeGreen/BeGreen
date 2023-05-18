package com.github.sdp_begreen.begreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.sdp_begreen.begreen.R
import com.github.sdp_begreen.begreen.viewModels.ContestCreationViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.hbb20.CountryPickerView
import com.hbb20.countrypicker.models.CPCountry
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
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

        setupTitle(view)
        setupPrivateCheckbox(view)
        setupExpandButton(view)
        setupCity(view)
        setupPostalCode(view)
        setupCountryPicker(view)
        setupRadius(view)
        populateAndUpdateTimeZone(view)
        setupDateButton(view)
        setupStartHoursButton(view)
        setupEndHoursButton(view)
        setupCancelCreationButton(view)
        setupConfirmCreationButton(view)
        setupDateEditText(view)
        setupHourEditText(view)
        return view
    }

    //------------------------------------Title/Private--------------------------------------------

    /**
     * Setup the title selection input
     * @param view the view of the fragment
     */
    private fun setupTitle(view: View) {
        val contestTitle = view.findViewById<TextInputEditText>(R.id.contest_creation_title)
        contestTitle.setText(contestCreationViewModel.contestTitle)
        contestTitle.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                contestCreationViewModel.contestTitle = contestTitle.text.toString()
            }
        }
    }


    /**
     * Setup the private checkbox
     * @param view the view of the fragment
     */
    private fun setupPrivateCheckbox(view: View) {
        val privateCheckbox = view.findViewById<CheckBox>(R.id.private_contest_checkbox)
        privateCheckbox.isChecked = contestCreationViewModel.isPrivate
        privateCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            contestCreationViewModel.isPrivate = isChecked
        }
    }


    //------------------------------------Location--------------------------------------------

    /**
     * Setup the Map image button
     * @param view the view of the fragment
     */
    private fun setupMapButton(view: View) {
        val mapButton = view.findViewById<ImageView>(R.id.contest_creation_location_map)
        mapButton.setOnClickListener {

        }
    }

    /**
     * Setup the expand button for the location details
     * @param view the view of the fragment
     */
    private fun setupExpandButton(view: View) {
        val expandButton = view.findViewById<ImageView>(R.id.contest_creation_location_expand)
        val locationDetailsContainer =
            view.findViewById<View>(R.id.contest_location_details_container)

        locationDetailsContainer.visibility = View.GONE
        expandButton.setImageResource(R.drawable.ic_chevron_up)

        expandButton.setOnClickListener {
            if (locationDetailsContainer.visibility == View.GONE) {
                locationDetailsContainer.visibility = View.VISIBLE
                expandButton.setImageResource(R.drawable.ic_chevron_down)
            } else {
                locationDetailsContainer.visibility = View.GONE
                expandButton.setImageResource(R.drawable.ic_chevron_up)
            }
        }
    }

    /**
     * Setup the city input of the contest
     * @param view the view of the fragment
     */
    private fun setupCity(view: View) {
        val cityText = view.findViewById<TextInputEditText>(R.id.city_contest_creation)
        cityText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (contestCreationViewModel.editCity(cityText.text.toString())) {
                    cityText.setText(contestCreationViewModel.city.value.toString())
                }
            }
        }
        lifecycleScope.launch {
            contestCreationViewModel.city.flowWithLifecycle(lifecycle).collect {
                cityText.setText(it)
            }
        }
    }


    /**
     * Setup the postal code input of the contest
     * @param view the view of the fragment
     */
    private fun setupPostalCode(view: View) {
        val postalCodeText = view.findViewById<TextInputEditText>(R.id.postal_code_contest_creation)
        postalCodeText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (contestCreationViewModel.editPostalCode(postalCodeText.text.toString())) {
                    postalCodeText.setText(contestCreationViewModel.postalCode.value.toString())
                }
            }
        }
        lifecycleScope.launch {
            contestCreationViewModel.postalCode.flowWithLifecycle(lifecycle).collect {
                postalCodeText.setText(it)
            }
        }
    }

    /**
     * Setup the country picker
     * @param view the view of the fragment
     */
    private fun setupCountryPicker(view: View) {
        val countryPicker =
            view.findViewById<CountryPickerView>(R.id.contest_creation_country_picker)

        countryPicker.cpViewHelper.onCountryChangedListener = { selectedCountry: CPCountry? ->
            if (!contestCreationViewModel.editCountry(selectedCountry?.name)) {
                Toast.makeText(context, "Country not valid", Toast.LENGTH_SHORT).show()
            }
        }
        lifecycleScope.launch {
            contestCreationViewModel.country.flowWithLifecycle(lifecycle).collect {
                countryPicker.cpViewHelper.cpDataStore.countryList.forEach { country ->
                    if (country.name == it) {
                        countryPicker.cpViewHelper.setCountry(country)
                    }
                }
            }
        }

    }

    /**
     * Setup the radius of the contest input
     * @param view the view of the fragment
     */
    private fun setupRadius(view: View) {
        val radiusText = view.findViewById<TextInputEditText>(R.id.radius_contest_creation)
        radiusText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (contestCreationViewModel.editRadius(Integer.parseInt(radiusText.text.toString()))) {
                    radiusText.setText(contestCreationViewModel.radius.value.toString())
                }
            }
        }
        lifecycleScope.launch {
            contestCreationViewModel.radius.flowWithLifecycle(lifecycle).collect {
                radiusText.setText(it.toString() ?: "0")
            }
        }
    }

    //------------------------------------Timezone/Date--------------------------------------------

    /**
     * Populate the time zone spinner with the available time zones and set it to the default
     * @param view the view of the fragment
     */
    private fun populateAndUpdateTimeZone(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.contest_timezone_spinner)
        val idArray = TimeZone.getAvailableIDs()
        val idAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            idArray
        )
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = idAdapter;

        // now set the spinner to default timezone from the time zone settings
        for (i in idArray.indices) {
            if (idArray[i] == TimeZone.getDefault().id) {
                spinner.setSelection(i)
                break
            }
        }
    }


    /**
     * Convert a formatted date to a timestamp
     * @param date the formatted date
     */
    private fun fromFormattedDateToLong(date: String): Long {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        if (date.contains(Regex("[a-zA-Z]"))) return 0
        val datetmp = formatter.parse(date)
        return datetmp?.time ?: 0
    }

    /**
     * Convert a timestamp to a formatted date
     * @param date the timestamp
     */
    private fun fromLongToFormattedDate(date: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date)
    }

    /**
     * Add a listener to the date edit text
     * @param text the edit text
     * @param viewModelTimeValue the time value in the view model
     * @param editFun the function to edit the time value in the view model
     */
    private fun addDateListener(
        text: EditText,
        viewModelTimeValue: StateFlow<Long?>,
        editFun: (input: Long) -> Unit
    ) {
        text.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val timestamp = fromFormattedDateToLong(text.text.toString())
                editFun(timestamp)
                text.setText(viewModelTimeValue.value?.let {
                    fromLongToFormattedDate(
                        it
                    )
                })
            }
        }
    }

    /**
     * Setup the start/end date edit button
     * @param view the view of the fragment
     */
    private fun setupDateEditText(view: View) {
        val startDateText = view.findViewById<EditText>(R.id.start_date_contest_text)
        val endDateText = view.findViewById<EditText>(R.id.end_date_contest_text)

        addDateListener(
            startDateText,
            contestCreationViewModel.startDate,
            contestCreationViewModel::editStartDate
        )
        addDateListener(
            endDateText,
            contestCreationViewModel.endDate,
            contestCreationViewModel::editEndDate
        )
    }

    /**
     * Create a new date picker with the current start and end date
     * @return the new date picker
     */
    private fun getNewDatePicker(): MaterialDatePicker<Pair<Long, Long>> {
        if (contestCreationViewModel.startDate.value == null || contestCreationViewModel.endDate.value == null) {
            contestCreationViewModel.editStartDate(System.currentTimeMillis())
            contestCreationViewModel.editEndDate(System.currentTimeMillis())
        }
        if (contestCreationViewModel.startDate.value!! > contestCreationViewModel.endDate.value!!) {
            contestCreationViewModel.editStartDate(contestCreationViewModel.endDate.value!!)
        }
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select dates")
        builder.setSelection(
            Pair(
                contestCreationViewModel.startDate.value,
                contestCreationViewModel.endDate.value
            )
        )
        return builder.build()
    }

    /**
     * Add a listener to the date picker
     * @param datePicker the date picker to setup
     */
    private fun addListenerOnDatePicker(datePicker: MaterialDatePicker<Pair<Long, Long>>) {
        datePicker.addOnPositiveButtonClickListener {
            val pair: Pair<Long, Long> =
                datePicker.selection as Pair<Long, Long>
            if (!contestCreationViewModel.editStartDate(pair.first)) {
                Toast.makeText(context, "Start date not valid", Toast.LENGTH_SHORT).show()
            }
            if (!contestCreationViewModel.editEndDate(pair.second)) {
                Toast.makeText(context, "End date not valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Setup the date button listener
     * @param button the button to setup
     */
    private fun setDateButtonNewListener(button: Button) {
        val datePicker = getNewDatePicker()
        addListenerOnDatePicker(datePicker)

        button.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }
    }

    /**
     * Setup the date button and the date picker
     * @param view the view of the fragment
     */
    private fun setupDateButton(view: View) {
        val startDateButton = view.findViewById<Button>(R.id.date_period_contest)
        val startDateText = view.findViewById<EditText>(R.id.start_date_contest_text)
        val endDateText = view.findViewById<EditText>(R.id.end_date_contest_text)
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .build()


        lifecycleScope.launch {
            contestCreationViewModel.startDate.combine(contestCreationViewModel.endDate) { startDate, endDate ->
                if (startDate != null && endDate != null) {
                    startDate to endDate
                } else {
                    null
                }
            }.flowWithLifecycle(lifecycle).collect {
                it?.let { (startDate, endDate) ->
                    startDateText.setText(formatter.format(startDate))
                    endDateText.setText(formatter.format(endDate))
                    setDateButtonNewListener(startDateButton)
                }
            }
        }

        startDateButton.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }

        addListenerOnDatePicker(datePicker)
    }

    //----------------------------------Hours--------------------------------------------
    /**
     * Create a new time picker with the current start and end hour and set the button listener
     * @param button the button to setup
     * @param hour the hour flow to setup
     * @param minute the minute flow to setup
     */
    private fun setTimeButtonNewListener(
        button: Button,
        hour: StateFlow<Int?>,
        minute: StateFlow<Int?>
    ) {
        val timePicker = getNewTimePicker(hour, minute)
        addListenerOnTimePicker(timePicker)

        button.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "hourPicker")
        }
    }

    /**
     * Add the listener to the time picker
     * @param timePicker the time picker to setup
     * @param hour the hour state flow
     * @param minute the minute state flow
     * @param editHour the function to edit the hour value in the view model
     * @param editMinute the function to edit the minute value in the view model
     */
    private fun addListenerOnTimePicker(
        timePicker: MaterialTimePicker,
        hour: StateFlow<Int?>,
        minute: StateFlow<Int?>,
        editHour: (input: Int) -> Unit,
        editMinute: (input: Int) -> Unit
    ) {
        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            if (!contestCreationViewModel.editStartHour(hour)) {
                Toast.makeText(context, "Start hours not valid!", Toast.LENGTH_SHORT).show()
            }
            if (!contestCreationViewModel.editStartMinute(minute)) {
                Toast.makeText(context, "Start minutes not valid!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Add a listener to the hour edit text
     * @param text the edit text to setup
     * @param hour the hour state flow
     * @param minute the minute state flow
     * @param editHour the function to edit the hour state flow
     * @param editMinute the function to edit the minute state flow
     */
    private fun addHourListener(
        text: EditText,
        hour: StateFlow<Int?>,
        minute: StateFlow<Int?>,
        editHour: (input: Int) -> Boolean,
        editMinute: (input: Int) -> Boolean
    ) {
        text.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val hourtmp = text.text.toString()
                if (hourtmp.contains(Regex("[a-zA-Z]")) || !hourtmp.contains(":")) {
                    if (hour.value == null || minute.value == null) {
                        editHour(12)
                        editMinute(0)
                    }
                    val hourstr = hour.value
                    val minutestr = minute.value
                    text.setText("$hourstr:$minutestr")
                    return@setOnFocusChangeListener
                }
                val hourNext = hourtmp.split(":")[0].toInt()
                val minuteNext = hourtmp.split(":")[1].toInt()
                editHour(hourNext)
                editMinute(minuteNext)
            }
        }
    }

    /**
     * Setup the hour edit text
     * @param view the view of the fragment
     */
    private fun setupHourEditText(view: View) {
        val startHourText = view.findViewById<EditText>(R.id.start_hour_contest_text)
        val endHourText = view.findViewById<EditText>(R.id.end_hour_contest_text)

        addHourListener(
            startHourText,
            contestCreationViewModel.startHour,
            contestCreationViewModel.startMinute,
            contestCreationViewModel::editStartHour,
            contestCreationViewModel::editStartMinute
        )
        addHourListener(
            endHourText,
            contestCreationViewModel.endHour,
            contestCreationViewModel.endMinute,
            contestCreationViewModel::editEndHour,
            contestCreationViewModel::editEndMinute
        )
    }

    /**
     * Get a hour picker with flow hours
     * @param hour the hour state flow
     * @param minute the minute state flow
     */
    private fun getNewTimePicker(
        hour: StateFlow<Int?>,
        minute: StateFlow<Int?>
    ): MaterialTimePicker {
        if (contestCreationViewModel.startHour.value == null || contestCreationViewModel.endHour.value == null) {
            contestCreationViewModel.editStartHour(12)
            contestCreationViewModel.editStartMinute(0)
            contestCreationViewModel.editEndHour(12)
            contestCreationViewModel.editEndMinute(0)
        }
        val builder = MaterialTimePicker.Builder()
        builder.setTimeFormat(TimeFormat.CLOCK_24H)
        builder.setTitleText("Select hours")
        builder.setHour(hour.value!!)
        builder.setMinute(minute.value!!)
        return builder.build()
    }

    /**
     * Setup the start hour button
     * @param view the view of the fragment
     */
    private fun setupStartHoursButton(view: View) {
        val startHourButton = view.findViewById<Button>(R.id.start_hour_contest)
        val startHourText = view.findViewById<EditText>(R.id.start_hour_contest_text)
        val hourPicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                //.setTitle("Select Appointment time")
                .build()
        lifecycleScope.launch {
            contestCreationViewModel.startHour.combine(contestCreationViewModel.startMinute) { startHour, startMinute ->
                if (startHour != null && startMinute != null) {
                    startHour to startMinute
                } else {
                    null
                }
            }.flowWithLifecycle(lifecycle).collect {
                it?.let { (startHour, startMinute) ->
                    startHourText.setText("$startHour:$startMinute")
                    setTimeButtonNewListener(startHourButton)
                }
            }
        }

        startHourButton.setOnClickListener {
            hourPicker.show(requireActivity().supportFragmentManager, "hourPicker")
        }

        hourPicker.addOnPositiveButtonClickListener {
            val hour = hourPicker.hour
            val minute = hourPicker.minute
            startHourText.setText("$hour:$minute")
        }
    }

    /**
     * Setup the end hour button
     * @param view the view of the fragment
     */
    private fun setupEndHoursButton(view: View) {
        val endHoursButton = view.findViewById<Button>(R.id.end_hour_contest)
        val endHourText = view.findViewById<EditText>(R.id.end_hour_contest_text)

        val hourPicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                //.setTitle("Select Appointment time")
                .build()

        endHoursButton.setOnClickListener {
            hourPicker.show(requireActivity().supportFragmentManager, "hourPicker")
        }
        hourPicker.addOnPositiveButtonClickListener {
            val hour = hourPicker.hour
            val minute = hourPicker.minute
            endHourText.setText("$hour:$minute")
        }
    }

    //------------------------------------Cancel/Confirm button------------------------------------

    /**
     * Setup the confirm creation button
     * @param view the view of the fragment
     */
    private fun setupConfirmCreationButton(view: View) {
        val confirmCreationButton = view.findViewById<Button>(R.id.contest_confirm_button)
        confirmCreationButton.setOnClickListener {
            if (contestCreationViewModel.isContestCreationValid()) {
                //TODO: Call firebase to create the contest
                lifecycleScope.launch {
                    requireActivity().supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace(R.id.mainFragmentContainer, ContestsFragment())
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Please fill all the fields with correct values",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Setup the cancel creation button
     * @param view the view of the fragment
     */
    private fun setupCancelCreationButton(view: View) {
        val cancelCreationButton = view.findViewById<Button>(R.id.contest_cancel_button)
        cancelCreationButton.setOnClickListener {
            lifecycleScope.launch {
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.mainFragmentContainer, ContestsFragment())
                }
            }
        }
    }


    /**
     * Companion object to create the fragment
     */
    companion object {

        @JvmStatic
        fun newInstance() =
            ContestCreationFragment()
    }
}