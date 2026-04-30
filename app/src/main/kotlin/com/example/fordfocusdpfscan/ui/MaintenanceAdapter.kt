package com.example.fordfocusdpfscan.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fordfocusdpfscan.R
import com.example.fordfocusdpfscan.data.db.MaintenanceReminder

// ═══════════════════════════════════════════════════════════════════════════════
// MaintenanceAdapter.kt — RecyclerView adapter for maintenance reminder cards.
//
// Each card shows:
//   • Title + status badge (OK / IN SCADENZA / URGENTE / SCADUTO)
//   • Colored left accent bar (green / amber / red)
//   • Progress bar (km used vs interval)
//   • "Scade tra X km" or "Scaduto da X km"
//   • "Fatto a X km  ·  Prossimo a Y km"
//   • [Fatto oggi] button (hidden for auto-managed ECU entries)
//   • [Elimina] button (hidden for auto-managed ECU entries)
// ═══════════════════════════════════════════════════════════════════════════════

class MaintenanceAdapter(
    private val onDoneClick: (MaintenanceReminder) -> Unit,
    private val onEditClick: (MaintenanceReminder) -> Unit,
    private val onDeleteClick: (MaintenanceReminder) -> Unit
) : ListAdapter<MaintenanceAdapter.Item, MaintenanceAdapter.ViewHolder>(ItemDiffCallback()) {

    /**
     * Wraps a reminder with the current odometer so the adapter can compute
     * km remaining without holding a reference to a live data source.
     */
    data class Item(val reminder: MaintenanceReminder, val currentOdometer: Long)

    private class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(old: Item, new: Item) = old.reminder.id == new.reminder.id
        override fun areContentsTheSame(old: Item, new: Item) = old == new
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusBar:   View        = view.findViewById(R.id.viewStatusBar)
        val tvTitle:     TextView    = view.findViewById(R.id.tvTitle)
        val tvBadge:     TextView    = view.findViewById(R.id.tvStatusBadge)
        val tvAutoBadge: TextView    = view.findViewById(R.id.tvAutoBadge)
        val progress:    ProgressBar = view.findViewById(R.id.progressBar)
        val tvRemaining: TextView    = view.findViewById(R.id.tvKmRemaining)
        val tvDetail:    TextView    = view.findViewById(R.id.tvDetail)
        val btnDone:     Button      = view.findViewById(R.id.btnDone)
        val btnEdit:     Button      = view.findViewById(R.id.btnEdit)
        val btnDelete:   Button      = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_maintenance_card, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item     = getItem(position)
        val reminder = item.reminder
        val odometer = item.currentOdometer
        val ctx      = holder.itemView.context

        val status    = reminder.status(odometer)
        val kmLeft    = reminder.kmRemaining(odometer)
        val progress  = reminder.progressPercent(odometer)

        // ── Status colors ─────────────────────────────────────────────────────
        val (barColor, badgeColor, badgeText) = when (status) {
            MaintenanceReminder.Status.OK      -> Triple(0xFF00C853.toInt(), 0xFF00C853.toInt(), "OK")
            MaintenanceReminder.Status.WARNING -> Triple(0xFFFF9500.toInt(), 0xFFFF9500.toInt(), "IN SCADENZA")
            MaintenanceReminder.Status.DANGER  -> Triple(0xFFFF3B30.toInt(), 0xFFFF3B30.toInt(), "URGENTE")
            MaintenanceReminder.Status.OVERDUE -> Triple(0xFFFF3B30.toInt(), 0xFFFF3B30.toInt(), "SCADUTO")
        }

        val progressDrawable = when (status) {
            MaintenanceReminder.Status.OK      -> R.drawable.bg_progress_ok
            MaintenanceReminder.Status.WARNING -> R.drawable.bg_progress_warn
            MaintenanceReminder.Status.DANGER,
            MaintenanceReminder.Status.OVERDUE -> R.drawable.bg_progress_danger
        }

        // ── Bind views ────────────────────────────────────────────────────────
        holder.statusBar.setBackgroundColor(barColor)
        holder.tvTitle.text = reminder.title

        holder.tvBadge.text = badgeText
        holder.tvBadge.setTextColor(badgeColor)

        // ECU badge — only for auto-managed tagliando
        holder.tvAutoBadge.visibility = if (reminder.isAutoManaged) View.VISIBLE else View.GONE

        holder.progress.progressDrawable = ctx.getDrawable(progressDrawable)
        holder.progress.max      = 100
        holder.progress.progress = progress.coerceIn(0, 100)

        // km remaining text
        holder.tvRemaining.text = when (status) {
            MaintenanceReminder.Status.OVERDUE ->
                "Scaduto da %,d km".format(-kmLeft)
            else ->
                "Scade tra %,d km".format(kmLeft)
        }
        holder.tvRemaining.setTextColor(barColor)

        // Detail line
        val dueAt = reminder.dueAtKm()
        holder.tvDetail.text = if (reminder.lastDoneKm > 0)
            "Fatto a %,d km  ·  Prossimo a %,d km".format(reminder.lastDoneKm, dueAt)
        else
            "Prossimo a %,d km".format(dueAt)

        // ── Buttons — hidden for auto-managed entries ─────────────────────────
        if (reminder.isAutoManaged) {
            holder.btnDone.visibility   = View.GONE
            holder.btnEdit.visibility   = View.GONE
            holder.btnDelete.visibility = View.GONE
        } else {
            holder.btnDone.visibility   = View.VISIBLE
            holder.btnEdit.visibility   = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDone.setOnClickListener   { onDoneClick(reminder) }
            holder.btnEdit.setOnClickListener   { onEditClick(reminder) }
            holder.btnDelete.setOnClickListener { onDeleteClick(reminder) }
        }
    }
}
