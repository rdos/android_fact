package ru.smartro.worknote.presentation.checklist

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.worknote.AFragment
import ru.smartro.worknote.R
import ru.smartro.worknote.awORKOLDs.extensions.hideProgress
import ru.smartro.worknote.awORKOLDs.extensions.showingProgress
import ru.smartro.worknote.awORKOLDs.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.awORKOLDs.util.MyUtil
import ru.smartro.worknote.log.AAct
import ru.smartro.worknote.toast
import ru.smartro.worknote.work.Status
import ru.smartro.worknote.work.ac.PERMISSIONS

class StartVehicleF: AFragment() {

    override fun onGetLayout(): Int = R.layout.f_start_vehicle

    private val viewModel: SingleViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MyUtil.hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 1)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Автомобиль"
            setDisplayHomeAsUpEnabled(true)
        }

        val adapter = StartVehicleAdapter { vehicle ->
            goToNextStep(vehicle)
        }

        val etVehicleFilter = view.findViewById<EditText>(R.id.et_act_start_vehicle__filter)
//        val textWatcher = TextWatcher()
        etVehicleFilter.addTextChangedListener { text: Editable? ->
            val filterText = text.toString()
            getAct().logSentry(filterText)
            adapter.updateList(filterText)
        }
        etVehicleFilter.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(etVehicleFilter.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        showingProgress(paramS().ownerName)

        val rv = view.findViewById<RecyclerView>(R.id.rv_act_start_vehicle)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        viewModel.getVehicleList(getArgumentID()).observe(viewLifecycleOwner) { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    val vehicles = data?.data
                    vehicles?.let {
                        adapter.setItems(it)
                        if (getAct().isDevelMode()) {
                            etVehicleFilter.setText("Тигуан")
                            val vehicle = it.find { el -> el.name == "Тигуан" }
                            if(vehicle != null) {
                                goToNextStep(vehicle)
                            } else {
                                toast("Не удаётся найти машину с именем \"Тигуан\"")
                            }
                        }
                    }
                    hideProgress()
                }
                Status.ERROR -> {
                    toast(result.msg)
                    hideProgress()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    hideProgress()
                }
            }
        }
    }

    private fun goToNextStep(vehicle: Vehicle) {
        paramS().vehicleId = vehicle.id
        paramS().vehicleName = vehicle.name
        // TODO!! will be changed to navigateMain
        val navHost = (getAct().supportFragmentManager.findFragmentById(R.id.checklist_nav_host) as NavHostFragment)
        val navController = navHost.navController
        val argSBundle = getArgSBundle(vehicle.id, null)
        navController.navigate(R.id.startWaybillF, argSBundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_logout_organisation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // TODO:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(requireContext(), item.itemId)
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}