package com.ems.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import com.ems.domain.model.Pcr
import com.ems.domain.model.VitalSigns
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PcrPrintAdapter(
    private val context: Context,
    private val pcr: Pcr,
    private val vitals: List<VitalSigns>
) : PrintDocumentAdapter() {

    private var printedDoc: PrintedPdfDocument? = null
    private val timeFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault())
    private val timeFmtShort = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }
        printedDoc = PrintedPdfDocument(context, newAttributes)
        val info = PrintDocumentInfo.Builder("PCR_${pcr.incidentNumber.ifBlank { pcr.id.take(8) }}.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()
        callback.onLayoutFinished(info, oldAttributes != newAttributes)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val doc = printedDoc ?: return
        val page = doc.startPage(0)
        drawPcr(page.canvas)
        doc.finishPage(page)

        try {
            doc.writeTo(FileOutputStream(destination.fileDescriptor))
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        } finally {
            doc.close()
        }
    }

    private fun drawPcr(canvas: Canvas) {
        val w = canvas.width.toFloat()
        var y = 60f
        val lm = 50f   // left margin
        val rm = w - 50f

        // ── Paints ──────────────────────────────────────────────────────────
        val headerPaint = Paint().apply { color = Color.parseColor("#1A1A1A"); textSize = 22f; isFakeBoldText = true; isAntiAlias = true }
        val titlePaint = Paint().apply { color = Color.parseColor("#0984E3"); textSize = 13f; isFakeBoldText = true; isAntiAlias = true }
        val bodyPaint = Paint().apply { color = Color.parseColor("#2D3436"); textSize = 11f; isAntiAlias = true }
        val mutedPaint = Paint().apply { color = Color.parseColor("#9CA3AF"); textSize = 10f; isAntiAlias = true }
        val linePaint = Paint().apply { color = Color.parseColor("#E5E7EB"); strokeWidth = 0.7f; isAntiAlias = true }
        val bgPaint = Paint().apply { color = Color.parseColor("#F7F7F7") }

        // ── Header bar ────────────────────────────────────────────────────
        canvas.drawRect(lm - 10f, y - 24f, rm + 10f, y + 24f, bgPaint)
        canvas.drawText("EMS PATIENT CARE REPORT", lm, y, headerPaint)
        canvas.drawText("Incident: ${pcr.incidentNumber.ifBlank { "—" }}   Unit: ${pcr.unitNumber.ifBlank { "—" }}", rm - 220f, y, mutedPaint)
        y += 40f

        fun section(title: String) {
            canvas.drawText(title, lm, y, titlePaint)
            y += 4f
            canvas.drawLine(lm, y, rm, y, linePaint)
            y += 14f
        }

        fun field(label: String, value: String, x: Float = lm, rightCol: Boolean = false) {
            val xPos = if (rightCol) w / 2f + 20f else x
            canvas.drawText("$label:", xPos, y, mutedPaint)
            canvas.drawText(value.ifBlank { "—" }, xPos + 90f, y, bodyPaint)
        }

        // ── Patient ────────────────────────────────────────────────────────
        section("PATIENT")
        field("Name", "${pcr.patientLastName}, ${pcr.patientFirstName}")
        field("DOB", pcr.patientDob, rightCol = true); y += 16f
        field("Age", pcr.patientAge?.toString() ?: "—")
        field("Gender", pcr.patientGender, rightCol = true); y += 16f
        field("Address", pcr.patientAddress); y += 16f
        field("Insurance", "${pcr.insuranceProvider} / ${pcr.insuranceId}"); y += 24f

        // ── Response ───────────────────────────────────────────────────────
        section("RESPONSE TIMES")
        pcr.dispatchTime?.let { field("Dispatch", timeFmt.format(it)) }
        pcr.onSceneTime?.let { field("On Scene", timeFmt.format(it), rightCol = true) }; y += 16f
        pcr.transportTime?.let { field("Transport", timeFmt.format(it)) }
        pcr.hospitalArrivalTime?.let { field("Hospital", timeFmt.format(it), rightCol = true) }; y += 24f

        // ── Clinical ───────────────────────────────────────────────────────
        section("CLINICAL")
        field("Complaint", pcr.chiefComplaint); y += 16f
        field("Mechanism", pcr.mechanismOfInjury); y += 16f
        field("LOC", pcr.levelOfConsciousness)
        field("Call Type", pcr.callType, rightCol = true); y += 24f

        // ── Vitals ────────────────────────────────────────────────────────
        if (vitals.isNotEmpty()) {
            section("VITAL SIGNS")
            // Table header
            val cols = listOf(lm, lm+60, lm+120, lm+180, lm+240, lm+300, lm+360)
            listOf("Time", "BP", "HR", "RR", "SpO₂", "GCS", "Pain").forEachIndexed { i, h ->
                canvas.drawText(h, cols[i], y, titlePaint)
            }
            y += 14f
            canvas.drawLine(lm, y, rm, y, linePaint)
            y += 10f
            vitals.forEach { v ->
                listOf(
                    timeFmtShort.format(v.timestamp),
                    v.bpDisplay,
                    v.heartRate?.toString() ?: "—",
                    v.respiratoryRate?.toString() ?: "—",
                    v.spo2?.let { "$it%" } ?: "—",
                    v.gcsTotal?.toString() ?: "—",
                    v.painScale?.toString() ?: "—"
                ).forEachIndexed { i, text ->
                    canvas.drawText(text, cols[i], y, bodyPaint)
                }
                y += 14f
            }
            y += 10f
        }

        // ── Treatment ────────────────────────────────────────────────────
        if (pcr.treatmentNotes.isNotBlank() || pcr.medicationsGiven.isNotBlank()) {
            section("TREATMENT")
            val interventions = pcr.treatmentNotes.split("|").filter { it.isNotBlank() }
            if (interventions.isNotEmpty()) {
                canvas.drawText("Interventions:", lm, y, mutedPaint)
                y += 13f
                interventions.chunked(3).forEach { row ->
                    canvas.drawText("• ${row.joinToString("   • ")}", lm + 8f, y, bodyPaint)
                    y += 13f
                }
            }
            val meds = pcr.medicationsGiven.split("||").filter { it.isNotBlank() }
            if (meds.isNotEmpty()) {
                y += 4f
                canvas.drawText("Medications:", lm, y, mutedPaint); y += 13f
                meds.forEach { entry ->
                    val p = entry.split("|")
                    val line = "${p.getOrNull(0) ?: ""} ${p.getOrNull(1) ?: ""} ${p.getOrNull(2) ?: ""}  ${p.getOrNull(3) ?: ""}".trim()
                    canvas.drawText("• $line", lm + 8f, y, bodyPaint); y += 13f
                }
            }
            y += 10f
        }

        // ── Narrative ────────────────────────────────────────────────────
        if (pcr.narrativeNotes.isNotBlank()) {
            section("NARRATIVE")
            pcr.narrativeNotes.lines().take(12).forEach { line ->
                canvas.drawText(line.take(90), lm, y, bodyPaint)
                y += 14f
            }
            y += 6f
        }

        // ── Disposition ──────────────────────────────────────────────────
        section("DISPOSITION")
        field("Destination", pcr.transportDestination); y += 16f
        field("Condition", pcr.patientConditionOnArrival)
        field("Refusal", if (pcr.refusalSigned) "AMA Signed" else "No", rightCol = true); y += 24f

        // Footer
        canvas.drawLine(lm, y, rm, y, linePaint); y += 12f
        canvas.drawText("Generated by EMS PCR App  ·  TWSP Batch 12  ·  ${timeFmt.format(java.time.Instant.now())}", lm, y, mutedPaint)
    }
}
