package com.phonetaxx.ui.navigationdrawer.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.phonetaxx.BuildConfig
import com.phonetaxx.R
import com.phonetaxx.extension.showToast
import com.phonetaxx.firebase.model.CallLogsDbModel
import com.phonetaxx.utils.Const
import com.phonetaxx.utils.DateTimeHelper
import com.phonetaxx.utils.MediaHelper
import com.phonetaxx.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_report_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException


private const val DATA_LIST = "param1"
private const val FILTER_TYPE = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddNumberDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddNumberDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReportDialogFragment : DialogFragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var calllLogList: ArrayList<CallLogsDbModel?> = ArrayList()
    private var selectedFilterType: String = ""
    private val header_no = "No"
    private val header_Name = "Name"
    private val header_PhoneNumber = "PhoneNumber"
    private val header_Category = "Category"
    private val header_Call_Duration = "Call Duration"
    private val header_Date = "Date"
    private val CSV_HEADER =
        header_no + "," + header_Name + "," + header_PhoneNumber + "," + header_Category + "," + header_Call_Duration + "," + header_Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            calllLogList = it.getParcelableArrayList<CallLogsDbModel?>(DATA_LIST)!!
            selectedFilterType = it.getString(FILTER_TYPE)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        ivClose.setOnClickListener(this)
        btnSendEmail.setOnClickListener(this)

        cardPdf?.setOnClickListener(this)
        cardCvs?.setOnClickListener(this)
        cardXls?.setOnClickListener(this)

        tvEmail.text = PreferenceHelper.getInstance().getProfileData()?.email
        countCallAndOtherInformation()
    }

    var businessCall = 0.0
    var personCall = 0.0
    var uncategorizedCall = 0.0

    var businessUses = 0.0
    var personalUses = 0.0
    var totalCallDurationInSecond = 0.0
    var totalCallDurationBusinessCallInSecond = 0.0
    var totalCallDurationPersonalCallInSecond = 0.0
    var totalCallDurationPersonalCallInMinute = 0.0
    var totalCallDurationBusinessCallInMinute = 0.0

    val TAG: String = "ReportDialogFragment"
    private fun countCallAndOtherInformation() {

        businessCall = 0.0
        personCall = 0.0
        uncategorizedCall = 0.0

        businessUses = 0.0
        personalUses = 0.0
        totalCallDurationInSecond = 0.0
        totalCallDurationBusinessCallInSecond = 0.0
        totalCallDurationPersonalCallInSecond = 0.0
        for (i in 0..calllLogList.size - 1) {
            if (calllLogList.get(i)?.callCategory!!.equals(Const.business)) {
                businessCall++
                totalCallDurationBusinessCallInSecond =
                    totalCallDurationBusinessCallInSecond + calllLogList.get(i)?.callDurationInSecond.toString()
                        .toInt()

            } else if (calllLogList.get(i)?.callCategory!!.equals(Const.personal)) {
                personCall++
                totalCallDurationPersonalCallInSecond =
                    totalCallDurationPersonalCallInSecond + calllLogList.get(i)?.callDurationInSecond.toString()
                        .toInt()

            } else if (calllLogList.get(i)?.callCategory!!.equals(Const.uncategorized)) {
                uncategorizedCall++
            }

            totalCallDurationInSecond =
                totalCallDurationInSecond + calllLogList.get(i)?.callDurationInSecond.toString()
                    .toInt()
        }
        if (businessCall > 0) {
//            businessUses = ((100 * businessCall) / (businessCall + personCall))
            businessUses =
                (totalCallDurationBusinessCallInSecond / 60) / (totalCallDurationInSecond / 60) * 100

        }
        if (personCall > 0) {
//            personalUses = ((100 * personCall) / (businessCall + personCall))
            personalUses =
                (totalCallDurationPersonalCallInMinute / 60) / (totalCallDurationInSecond / 60) * 100

        }

//        tvCalls.setText(calllLogList.size.toString())

        if (totalCallDurationPersonalCallInSecond > 0) {
            totalCallDurationPersonalCallInMinute = totalCallDurationPersonalCallInSecond / 60
        } else {
            totalCallDurationPersonalCallInMinute = 0.0
        }

        if (totalCallDurationBusinessCallInSecond > 0) {
            totalCallDurationBusinessCallInMinute = totalCallDurationBusinessCallInSecond / 60
        } else {
            totalCallDurationBusinessCallInMinute = 0.0
        }

        Log.v(TAG, "setOverViewData : Calls : " + calllLogList.size)
        Log.v(TAG, "setOverViewData : Minutes Talked : " + (totalCallDurationInSecond / 60))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawableResource(R.color.dialog_transparent_bg);

    }


    companion object {
        @JvmStatic
        fun newInstance(
            param1: ArrayList<CallLogsDbModel?>,
            selectedFilterType: String
        ) =
            ReportDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(DATA_LIST, param1)
                    putString(FILTER_TYPE, selectedFilterType)
                }
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivClose -> {
                dialog?.dismiss()
            }
            R.id.btnSendEmail -> {
                exportAsPDF(true)
                exportCSV()
            }
            R.id.cardPdf -> {
                exportAsPDF(false)

            }
            R.id.cardCvs -> {
                exportCSV()

            }
            R.id.cardXls -> {

            }
        }
    }

    private fun exportXls() {

    }

    private fun exportAsPDF(shareWithEmail: Boolean) {

        var filePath = MediaHelper.getInstance().getOutputPDFFile(getInitialName() + "_")

        PrintDocument(filePath.absolutePath, shareWithEmail)

    }

    private fun exportCSV(dest: String) {

        var filePath = MediaHelper.getInstance().getOutputCSVFile(getInitialName() + "_")

        var fileWriter = FileWriter(filePath)

        try {
            fileWriter.append(CSV_HEADER).append('\n')

            var count = 1
            for (callLogModel in calllLogList) {
                fileWriter.append(count.toString())
                fileWriter.append(',')
                fileWriter.append(callLogModel!!.name)
                fileWriter.append(',')
                fileWriter.append(callLogModel.phoneNumber)
                fileWriter.append(',')
                fileWriter.append(getCategoryName(callLogModel.callCategory))
                fileWriter.append(',')
                fileWriter.append(
                    DateTimeHelper.getInstance().getTimeFormateFromSeconds(
                        callLogModel.callDurationInSecond
                    )
                )
                fileWriter.append(',')
                fileWriter.append(
                    DateTimeHelper.getInstance()
                        .getAppDateTimeFormat(callLogModel.callDateTimeUTC)
                )
                fileWriter.append('\n')
                count++
            }
            fileWriter.append('\n')
            fileWriter.append('\n')

            fileWriter.append("Total Call")
            fileWriter.append(",")
            fileWriter.append((count - 1).toString())
            fileWriter.append('\n')

            fileWriter.append("Total Call Min")
            fileWriter.append(",")
            fileWriter.append(((totalCallDurationInSecond / 60)).toString())
            fileWriter.append('\n')


            showToast("Export CSV successfully!")
        } catch (e: Exception) {
            showToast("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter.flush()
                fileWriter.close()
            } catch (e: Exception) {
                showToast("Flushing/closing error!")
            }
        }

        sharePdfWithEmail(dest, filePath.toString())

    }

    private fun exportCSV() {

        var filePath = MediaHelper.getInstance().getOutputCSVFile(getInitialName() + "_")

        var fileWriter = FileWriter(filePath)

        try {
            fileWriter.append(CSV_HEADER).append('\n')

            var count = 1
            for (callLogModel in calllLogList) {
                fileWriter.append(count.toString())
                fileWriter.append(',')
                fileWriter.append(callLogModel!!.name)
                fileWriter.append(',')
                fileWriter.append(callLogModel.phoneNumber)
                fileWriter.append(',')
                fileWriter.append(getCategoryName(callLogModel.callCategory))
                fileWriter.append(',')
                fileWriter.append(
                    DateTimeHelper.getInstance().getTimeFormateFromSeconds(
                        callLogModel.callDurationInSecond
                    )
                )
                fileWriter.append(',')
                fileWriter.append(
                    DateTimeHelper.getInstance()
                        .getAppDateTimeFormat(callLogModel.callDateTimeUTC)
                )
                fileWriter.append('\n')
                count++
            }
            fileWriter.append('\n')
            fileWriter.append('\n')

            fileWriter.append("Total Call")
            fileWriter.append(",")
            fileWriter.append((count - 1).toString())
            fileWriter.append('\n')

            fileWriter.append("Total Call Min")
            fileWriter.append(",")
            fileWriter.append(((totalCallDurationInSecond / 60)).toString())
            fileWriter.append('\n')


            showToast("Export CSV successfully!")
        } catch (e: Exception) {
            showToast("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter.flush()
                fileWriter.close()
            } catch (e: Exception) {
                showToast("Flushing/closing error!")
            }
        }
    }

    private fun getInitialName(): String? {
        if (selectedFilterType.equals(Const.TODAY)) {
            return DateTimeHelper.getInstance().getDisplayDateFormat(System.currentTimeMillis())
        } else if (selectedFilterType.equals(Const.WEEKLY)) {
            return DateTimeHelper.getInstance().getWeekDays(System.currentTimeMillis())
        } else {
            return DateTimeHelper.getInstance().getDisplayMonthFormat(System.currentTimeMillis())
        }
    }

    private fun getCategoryName(callCategory: String): String {
        if (callCategory.equals(Const.business)) {
            return getString(R.string.business)
        } else if (callCategory.equals(Const.personal)) {
            return getString(R.string.personal)
        } else {
            return getString(R.string.uncategorized)
        }
    }


    @Throws(IOException::class, java.io.IOException::class)
    fun PrintDocument(dest: String, shareWithEmail: Boolean) {
        try {

            //            Document document = new Document();
            val document = Document(
                PageSize.LETTER_LANDSCAPE,
                0f,
                0f,
                40f,
                0f
            )//PENGUIN_SMALL_PAPERBACK used to set the paper size
            PdfWriter.getInstance(document, FileOutputStream(dest))
            document.open()
            createTable(document)
            document.close()

            if (shareWithEmail) {
                exportCSV(dest)

            }

            showToast("Pdf Create sucessfully")
        } catch (e: Exception) {
            showToast(e.message.toString())
            e.printStackTrace()
        }

    }

    private fun sharePdfWithEmail(destPDF: String, destCSV: String) {

        var shareUrl =
            FileProvider.getUriForFile(
                requireActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", File(
                    destPDF
                )
            );

        var shareCSV = FileProvider.getUriForFile(
            requireActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", File(
                destCSV
            )
        );
//
//        var emailIntent = Intent(Intent.ACTION_SEND)
//        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        emailIntent.setType("application/pdf")
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PhoneTaxx Report");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, shareUrl)
//        startActivity(Intent.createChooser(emailIntent, "Send mail..."));


        //need to "send multiple" to get more than one attachment
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "text/plain"
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(emailTo))
//        emailIntent.putExtra(Intent.EXTRA_CC, arrayOf<String>(emailCC))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PhoneTaxx Report")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        //has to be an ArrayList
        val uris: ArrayList<Uri> = ArrayList<Uri>()

        uris.add(shareUrl)


        uris.add(shareCSV)

        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))

    }

    private val catFont = Font(
        Font.FontFamily.TIMES_ROMAN, 12f,
        Font.BOLD
    ) // Set of font family alrady present with itextPdf library.
    private val catFontSize12 = Font(
        Font.FontFamily.TIMES_ROMAN, 12f,
        Font.NORMAL
    ) // Set of font family alrady present with itextPdf library.


    @Throws(BadElementException::class)
    private fun createTable(document: Document) {
        val table = PdfPTable(6)
        var c1 = PdfPCell(Phrase(header_no, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        c1.setPadding(10f)
        table.addCell(c1)

        c1 = PdfPCell(Phrase(header_Name, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        c1.setPadding(10f)
        table.addCell(c1)

        c1 = PdfPCell(Phrase(header_PhoneNumber, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        c1.setPadding(10f)
        table.addCell(c1)


        c1 = PdfPCell(Phrase(header_Category, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        c1.setPadding(10f)
        table.addCell(c1)

        c1 = PdfPCell(Phrase(header_Call_Duration, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        c1.setPadding(10f)
        table.addCell(c1)

        c1 = PdfPCell(Phrase(header_Date, catFont))
        c1.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(c1)

        table.headerRows = 1
        var count = 1
        for (callLogModel in calllLogList) {

            c1 = PdfPCell(Phrase(count.toString(), catFontSize12))
            c1.horizontalAlignment = Element.ALIGN_CENTER
            c1.setPadding(10f)
            table.addCell(c1)

            c1 = PdfPCell(Phrase(callLogModel!!.name, catFontSize12))
            c1.horizontalAlignment = Element.ALIGN_CENTER
            c1.setPadding(10f)
            table.addCell(c1)

            c1 = PdfPCell(Phrase(callLogModel.phoneNumber, catFontSize12))
            c1.horizontalAlignment = Element.ALIGN_CENTER
            c1.setPadding(10f)
            table.addCell(c1)

            c1 = PdfPCell(Phrase(getCategoryName(callLogModel.callCategory), catFontSize12))
            c1.horizontalAlignment = Element.ALIGN_CENTER
            c1.setPadding(10f)
            table.addCell(c1)

            c1 = PdfPCell(
                Phrase(
                    DateTimeHelper.getInstance()
                        .getTimeFormateFromSeconds(callLogModel.callDurationInSecond),
                    catFontSize12
                )
            )
            c1.horizontalAlignment = Element.ALIGN_CENTER
            c1.setPadding(10f)
            table.addCell(c1)

            c1 = PdfPCell(
                Phrase(
                    DateTimeHelper.getInstance()
                        .getAppDateTimeFormat(callLogModel.callDateTimeUTC),
                    catFontSize12
                )
            )
            c1.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(c1)

            count++
        }

        document.add(Paragraph("     Total No of calls : " + (count - 1), catFont))
        document.add(
            Paragraph(
                "     Total Minutes of Talk : " + (String.format(
                    "%.0f",
                    totalCallDurationInSecond / 60
                ) + " Min"), catFont
            )
        )
        document.add(
            Paragraph(
                "     Buisnees Usage : " + (String.format(
                    "%.0f",
                    businessUses
                ) + "%"), catFont
            )
        )
        document.add(Paragraph(" ", catFont))
        document.add(Paragraph(" ", catFont))
        document.add(table)

    }


}
