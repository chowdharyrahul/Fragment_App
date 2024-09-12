package ca.sheridancollege.chowdrah.assignment2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteTeamFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var searchEditText: EditText
    private lateinit var teamTableLayout: TableLayout
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionReference = firestore.collection("user")
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_delete_team, container, false)
        val view = inflater.inflate(R.layout.fragment_delete_team, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        teamTableLayout = view.findViewById(R.id.teamTableLayout)

        // Set up search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Perform search
                val query = s.toString()
                searchTeams(query)
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })

        // Populate table with initial data (all teams)
        fetchDataAndPopulateTable()

        return view

    }

    private fun fetchDataAndPopulateTable() {
        collectionReference.get()
            .addOnSuccessListener { documents ->
                val teams = mutableListOf<Team>()
                for (document in documents) {
                    val teamName = document.getString("countryName") ?: ""
                    val continentName = document.getString("continentName") ?: ""
                    teams.add(Team(teamName, continentName, document.id))
                }
                populateTeamTable(teams)
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }


    }

    private fun populateTeamTable(teams: List<Team>) {
        // Clear existing rows
        teamTableLayout.removeAllViews()

        // Add header row
        val headerRow = TableRow(requireContext())
        headerRow.addView(createTextView("Team", true))
        headerRow.addView(createTextView("Continent", true))
        headerRow.addView(createTextView("Delete", true))
        teamTableLayout.addView(headerRow)

        // Add data rows
        for (team in teams) {
            val dataRow = TableRow(requireContext())
            dataRow.addView(createTextView(team.name))
            dataRow.addView(createTextView(team.continent))
            dataRow.addView(createDeleteButton(team.id))
            teamTableLayout.addView(dataRow)
        }
    }

    private fun searchTeams(query: String) {
        collectionReference.get()
            .addOnSuccessListener { documents ->
                val teams = mutableListOf<Team>()
                for (document in documents) {
                    val teamName = document.getString("countryName") ?: ""
                    val continentName = document.getString("continentName") ?: ""
                    val teamId = document.id
                    val team = Team(teamName, continentName, teamId)
                    teams.add(team)
                }
                // Filter teams based on the query
                val filteredTeams = teams.filter { team ->
                    team.name.contains(query, ignoreCase = true) || team.continent.contains(
                        query,
                        ignoreCase = true
                    )
                }
                // Populate the table with filtered teams
                populateTeamTable(filteredTeams)
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }


    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.setPadding(16, 8, 16, 8)
        if (isHeader) {
            textView.setBackgroundResource(R.drawable.table_header_bg)
        }
        return textView
    }

    private fun createDeleteButton(teamId: String): TextView {
        val deleteButton = TextView(requireContext())
        deleteButton.text = "DELETE"
        deleteButton.setPadding(16, 8, 16, 8)
        deleteButton.setBackgroundResource(R.drawable.delete_button_bg)
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(teamId)
        }
        return deleteButton
    }

    private fun showDeleteConfirmationDialog(teamId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this team?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteTeam(teamId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTeam(teamId: String) {
        collectionReference.document(teamId)
            .delete()
            .addOnSuccessListener {
                // Team deleted successfully
                fetchDataAndPopulateTable() // Refresh table
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }


    data class Team(val name: String, val continent: String, val id: String)
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteTeamFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeleteTeamFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}