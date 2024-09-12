package ca.sheridancollege.chowdrah.assignment2

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionReference = firestore.collection("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_display_results, container, false)
        val view = inflater.inflate(R.layout.fragment_display_results, container, false)

        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout2)
        val radioButtonGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        // Fetch data from Firestore and populate the table
        fetchDataAndPopulateTable(tableLayout)

        // Listen for radio button changes
        radioButtonGroup.setOnCheckedChangeListener { _, checkedId ->
            // Handle radio button changes here
            when (checkedId) {
                R.id.radioButton -> {
                    // Sort by name
                    fetchDataAndPopulateTableSortedByCountryName(tableLayout)
                }

                R.id.radioButton2 -> {
                    // Sort by continent
                    fetchDataAndPopulateTableSortedByContinent(tableLayout)
                }

                R.id.radioButton3 -> {
                    // Sort by points
                    fetchDataAndPopulateTableSortedByPoints(tableLayout)
                }
            }
        }

        return view

    }


    private fun fetchDataAndPopulateTable(tableLayout: TableLayout) {
        // Clear existing rows from the table
        tableLayout.removeAllViews()

        // Add table header row
        val headerRow = TableRow(requireContext())
        val headerTexts =
            arrayOf("Team  ", "Continent", "Played ", "Won ", "Drawn ", "Lost ", "Points")
        for (text in headerTexts) {
            val textView = createTextView(text)
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        // Fetch data from Firestore
        collectionReference.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val teamName = document.getString("countryName") ?: ""
                    val continentName = document.getString("continentName") ?: ""
                    val gamesPlayed = document.getLong("gamesPlayed") ?: 0
                    val gamesWon = document.getLong("gamesWon") ?: 0
                    val gamesDrawn = document.getLong("gamesDrawn") ?: 0
                    val gamesLost = document.getLong("gamesLost") ?: 0
                    val points = calculatePoints(gamesWon, gamesDrawn)

                    // Add data row to the table
                    val dataRow = TableRow(requireContext())
                    val dataTexts = arrayOf(
                        teamName,
                        continentName,
                        gamesPlayed.toString(),
                        gamesWon.toString(),
                        gamesDrawn.toString(),
                        gamesLost.toString(),
                        points.toString()
                    )
                    for (text in dataTexts) {
                        val textView = createTextView(text)
                        dataRow.addView(textView)
                    }
                    tableLayout.addView(dataRow)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FetchData", "Error fetching data: $exception")
            }
    }

    private fun createTextView(text: String): TextView {
        return TextView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            this.text = text
            textSize = 16f // Text size in sp
            setTextColor(Color.WHITE)
        }
    }

    private fun fetchDataAndPopulateTableSortedByCountryName(tableLayout: TableLayout) {
        collectionReference.orderBy("countryName").get()
            .addOnSuccessListener { documents ->
                populateTableFromDocuments(documents, tableLayout)
            }
            .addOnFailureListener { exception ->
                Log.e("FetchData", "Error fetching sorted data: $exception")
            }
    }


    private fun fetchDataAndPopulateTableSortedByContinent(tableLayout: TableLayout) {
        collectionReference.orderBy("continentName").get()
            .addOnSuccessListener { documents ->
                populateTableFromDocuments(documents, tableLayout)
            }
            .addOnFailureListener { exception ->
                Log.e("FetchData", "Error fetching sorted data: $exception")
            }
    }

    private fun fetchDataAndPopulateTableSortedByPoints(tableLayout: TableLayout) {
        collectionReference.get()
            .addOnSuccessListener { documents ->
                val sortedDocuments = documents.sortedByDescending { document ->
                    val gamesWon = document.getLong("gamesWon") ?: 0
                    val gamesDrawn = document.getLong("gamesDrawn") ?: 0
                    calculatePoints(gamesWon, gamesDrawn)
                }
                populateTableFromDocuments2(sortedDocuments, tableLayout)
            }
            .addOnFailureListener { exception ->
                Log.e("FetchData", "Error fetching sorted data: $exception")
            }
    }

    private fun populateTableFromDocuments(documents: QuerySnapshot, tableLayout: TableLayout) {
        tableLayout.removeAllViews() // Clear existing rows from the table

        // Add table header row
        val headerRow = TableRow(requireContext())
        val headerTexts =
            arrayOf("Team  ", "Continent", "Played ", "Won ", "Drawn ", "Lost ", "Points")
        for (text in headerTexts) {
            val textView = createTextView(text)
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        // Populate the table with sorted data
        for (document in documents) {
            val teamName = document.getString("countryName") ?: ""
            val continentName = document.getString("continentName") ?: ""
            val gamesPlayed = document.getLong("gamesPlayed") ?: 0
            val gamesWon = document.getLong("gamesWon") ?: 0
            val gamesDrawn = document.getLong("gamesDrawn") ?: 0
            val gamesLost = document.getLong("gamesLost") ?: 0
            val points = calculatePoints(gamesWon, gamesDrawn)

            // Add data row to the table
            val dataRow = TableRow(requireContext())
            val dataTexts = arrayOf(
                teamName,
                continentName,
                gamesPlayed.toString(),
                gamesWon.toString(),
                gamesDrawn.toString(),
                gamesLost.toString(),
                points.toString()
            )
            for (text in dataTexts) {
                val textView = createTextView(text)
                dataRow.addView(textView)
            }
            tableLayout.addView(dataRow)
        }
    }

    private fun populateTableFromDocuments2(
        documents: List<QueryDocumentSnapshot>,
        tableLayout: TableLayout
    ) {
        tableLayout.removeAllViews() // Clear existing rows from the table

        // Add table header row
        val headerRow = TableRow(requireContext())
        val headerTexts =
            arrayOf("Team  ", "Continent", "Played ", "Won ", "Drawn ", "Lost ", "Points")
        for (text in headerTexts) {
            val textView = createTextView(text)
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        // Populate the table with sorted data
        for (document in documents) {
            val teamName = document.getString("countryName") ?: ""
            val continentName = document.getString("continentName") ?: ""
            val gamesPlayed = document.getLong("gamesPlayed") ?: 0
            val gamesWon = document.getLong("gamesWon") ?: 0
            val gamesDrawn = document.getLong("gamesDrawn") ?: 0
            val gamesLost = document.getLong("gamesLost") ?: 0
            val points = calculatePoints(gamesWon, gamesDrawn)

            // Add data row to the table
            val dataRow = TableRow(requireContext())
            val dataTexts = arrayOf(
                teamName,
                continentName,
                gamesPlayed.toString(),
                gamesWon.toString(),
                gamesDrawn.toString(),
                gamesLost.toString(),
                points.toString()
            )
            for (text in dataTexts) {
                val textView = createTextView(text)
                dataRow.addView(textView)
            }
            tableLayout.addView(dataRow)
        }
    }


    private fun calculatePoints(won: Long, drawn: Long): Long {
        // You can adjust the point calculation logic as per your requirements
        // For example, you might assign more points for a win than for a draw
        return won * 3 + drawn
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DisplayResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}