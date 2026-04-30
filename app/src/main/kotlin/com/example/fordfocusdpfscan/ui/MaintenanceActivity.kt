package com.example.fordfocusdpfscan.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.DpfRepository
import com.example.fordfocusdpfscan.data.MaintenanceRepository
import com.example.fordfocusdpfscan.data.db.MaintenanceReminder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════════
// MaintenanceActivity.kt — 4th tab: promemoria manutenzione.
//
// Shows a RecyclerView of MaintenanceReminder cards.
// The auto-managed "Tagliando olio" entry (populated from ECU data) is pinned
// at the top and cannot be edited or deleted by the user.
// ═══════════════════════════════════════════════════════════════════════════════

class MaintenanceActivity : BaseTabActivity() {

    override val tabIndex = 3   // 0=Monitor, 1=Diagnostica, 2=Storico, 3=Manutenzione

    private lateinit var repo: MaintenanceRepository
    private lateinit var adapter: MaintenanceAdapter
    private lateinit var rvReminders: RecyclerView
    private lateinit var viewEmptyState: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)

        repo = MaintenanceRepository(this)

        // RecyclerView setup
        rvReminders   = findViewById(R.id.rvReminders)
        viewEmptyState = findViewById(R.id.viewEmptyState)

        adapter = MaintenanceAdapter(
            onDoneClick   = { reminder -> showDoneConfirmDialog(reminder) },
            onEditClick   = { reminder -> showAddDialog(reminder) },
            onDeleteClick = { reminder -> showDeleteConfirmDialog(reminder) }
        )
        rvReminders.layoutManager = LinearLayoutManager(this)
        rvReminders.adapter = adapter

        // Observe reminders — update list whenever DB changes or odometer changes
        observeReminders()

        // FAB
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showAddDialog()
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Data observation
    // ═════════════════════════════════════════════════════════════════════════

    private fun observeReminders() {
        lifecycleScope.launch {
            // Observe both the reminder list AND the odometer concurrently.
            // We combine them manually to avoid needing a ViewModel + combine().
            var latestOdometer = DpfRepository.dpfData.value.odometerKm
            var latestReminders = emptyList<MaintenanceReminder>()

            fun submitToAdapter() {
                val items = latestReminders.map {
                    MaintenanceAdapter.Item(it, latestOdometer)
                }
                adapter.submitList(items)
                viewEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }

            // Observe DPF data for odometer updates
            launch {
                DpfRepository.dpfData.collectLatest { data ->
                    val newOdo = data.odometerKm
                    if (newOdo != latestOdometer) {
                        latestOdometer = newOdo
                        submitToAdapter()
                    }
                }
            }

            // Observe reminder list from Room
            repo.reminders.collectLatest { list ->
                latestReminders = list
                submitToAdapter()
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Add reminder dialog
    // ═════════════════════════════════════════════════════════════════════════

    private fun showAddDialog(editing: MaintenanceReminder? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_maintenance, null)

        val tvTitle       = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etTitle       = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etInterval    = dialogView.findViewById<TextInputEditText>(R.id.etIntervalKm)
        val etLastDone    = dialogView.findViewById<TextInputEditText>(R.id.etLastDoneKm)
        val btnOggi       = dialogView.findViewById<Button>(R.id.btnOggi)
        val tvOdomNote    = dialogView.findViewById<TextView>(R.id.tvOdometerNote)
        val btnCancel     = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave       = dialogView.findViewById<Button>(R.id.btnSave)

        // Pre-fill if editing
        if (editing != null) {
            tvTitle.text         = "Modifica promemoria"
            etTitle.setText(editing.title)
            etInterval.setText(editing.intervalKm.toString())
            etLastDone.setText(editing.lastDoneKm.toString())
        }

        // "Oggi" — pre-fills with current ECU odometer (always enabled, editable)
        val currentOdometer = DpfRepository.dpfData.value.odometerKm
        btnOggi.setOnClickListener {
            if (currentOdometer > 0L) {
                etLastDone.setText(currentOdometer.toString())
                etLastDone.setSelection(etLastDone.text?.length ?: 0)
            } else {
                // No ECU data — show hint and let the user type manually
                tvOdomNote.visibility = View.VISIBLE
                etLastDone.requestFocus()
                Toast.makeText(
                    this,
                    "Dongle non connesso — inserisci i km manualmente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Rounded dialog background (styled)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val title    = etTitle.text?.toString()?.trim()
            val interval = etInterval.text?.toString()?.toLongOrNull()
            val lastDone = etLastDone.text?.toString()?.toLongOrNull()

            when {
                title.isNullOrBlank() ->
                    Toast.makeText(this, "Inserisci un titolo", Toast.LENGTH_SHORT).show()
                interval == null || interval <= 0 ->
                    Toast.makeText(this, "Inserisci un intervallo valido", Toast.LENGTH_SHORT).show()
                lastDone == null || lastDone < 0 ->
                    Toast.makeText(this, "Inserisci i km dell'ultima manutenzione", Toast.LENGTH_SHORT).show()
                else -> {
                    lifecycleScope.launch {
                        if (editing == null) {
                            repo.insert(MaintenanceReminder(
                                title      = title,
                                intervalKm = interval,
                                lastDoneKm = lastDone
                            ))
                        } else {
                            repo.update(editing.copy(
                                title      = title,
                                intervalKm = interval,
                                lastDoneKm = lastDone,
                                // Reset notification flags if lastDoneKm changed
                                notif1000Sent    = editing.lastDoneKm == lastDone && editing.notif1000Sent,
                                notif500Sent     = editing.lastDoneKm == lastDone && editing.notif500Sent,
                                notifOverdueSent = editing.lastDoneKm == lastDone && editing.notifOverdueSent
                            ))
                        }
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // "Fatto" confirmation dialog
    // ═════════════════════════════════════════════════════════════════════════

    private fun showDoneConfirmDialog(reminder: MaintenanceReminder) {
        val currentOdometer = DpfRepository.dpfData.value.odometerKm

        if (currentOdometer > 0L) {
            // ECU connected — show confirmation with live km
            AlertDialog.Builder(this)
                .setTitle("Conferma manutenzione")
                .setMessage(
                    "Hai effettuato \"${reminder.title}\"?\n\n" +
                    "Verrà registrato a %,d km.\n" +
                    "Il prossimo promemoria scatterà a %,d km."
                        .format(currentOdometer, currentOdometer + reminder.intervalKm)
                )
                .setPositiveButton("Sì, confermo") { _, _ ->
                    lifecycleScope.launch {
                        repo.markDone(reminder.id, currentOdometer)
                        Toast.makeText(
                            this@MaintenanceActivity,
                            "\"${reminder.title}\" registrato a %,d km".format(currentOdometer),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Annulla", null)
                .create()
                .also { d -> d.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded) }
                .show()
        } else {
            // No ECU — first confirm, then ask km
            AlertDialog.Builder(this)
                .setTitle("Conferma manutenzione")
                .setMessage("Hai effettuato \"${reminder.title}\"?")
                .setPositiveButton("Sì, confermo") { _, _ ->
                    showDoneManualKmDialog(reminder)
                }
                .setNegativeButton("Annulla", null)
                .create()
                .also { d -> d.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded) }
                .show()
        }
    }

    /**
     * Shown after confirmation when the OBD dongle is not connected.
     * Asks only for the current odometer reading to record the maintenance.
     */
    private fun showDoneManualKmDialog(reminder: MaintenanceReminder) {
        val input = com.google.android.material.textfield.TextInputEditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "es. 101500"
            setTextColor(getColor(android.R.color.white))
        }
        val container = android.widget.FrameLayout(this).apply {
            val pad = (20 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad / 2, pad, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Odometro attuale")
            .setMessage("Inserisci i km attuali per registrare \"${reminder.title}\".")
            .setView(container)
            .setPositiveButton("Registra") { _, _ ->
                val km = input.text?.toString()?.toLongOrNull()
                if (km != null && km > 0) {
                    lifecycleScope.launch {
                        repo.markDone(reminder.id, km)
                        Toast.makeText(
                            this@MaintenanceActivity,
                            "\"${reminder.title}\" registrato a %,d km".format(km),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Inserisci un valore valido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annulla", null)
            .create()
            .also { d -> d.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded) }
            .show()
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Delete confirmation dialog
    // ═════════════════════════════════════════════════════════════════════════

    private fun showDeleteConfirmDialog(reminder: MaintenanceReminder) {
        AlertDialog.Builder(this)
            .setTitle("Elimina promemoria")
            .setMessage("Eliminare \"${reminder.title}\"?")
            .setPositiveButton("Elimina") { _, _ ->
                lifecycleScope.launch { repo.delete(reminder) }
            }
            .setNegativeButton("Annulla", null)
            .create()
            .also { d -> d.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_rounded) }
            .show()
    }
}
